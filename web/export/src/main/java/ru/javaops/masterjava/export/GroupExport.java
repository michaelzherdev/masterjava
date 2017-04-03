package ru.javaops.masterjava.export;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Mikhail on 02.04.2017.
 */
@Slf4j
public class GroupExport {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    @Value
    public static class FailedName {
        public String nameOrRange;
        public String reason;

        @Override
        public String toString() {
            return nameOrRange + " : " + reason;
        }
    }

//        public List<Group> process(final InputStream is, int chunkSize) throws XMLStreamException {
//            final StaxStreamProcessor processor = new StaxStreamProcessor(is);
//            List<Group> groups = new ArrayList<>();
//
//            int id = groupDao.getSeqAndSkip(chunkSize);
//
//            while (processor.doUntil(XMLEvent.START_ELEMENT, "Group")) {
//                final String name = processor.getAttribute("name");
//                final GroupType type = GroupType.valueOf(processor.getAttribute("type"));
//                final Group group = new Group(id, name, type);
//                executorService.submit(() -> groupDao.insert(group));
//                groups.add(group);
//            }
//            return groups;
//        }

    public List<FailedName> process(final InputStream is, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<List<FailedName>>() {
            class ChunkFuture {
                String nameRange;
                Future<List<String>> future;

                public ChunkFuture(List<Group> chunk, Future<List<String>> future) {
                    this.future = future;
                    this.nameRange = chunk.get(0).getName();
                    if (chunk.size() > 1) {
                        this.nameRange += '-' + chunk.get(chunk.size() - 1).getName();
                    }
                }
            }

            @Override
            public List<FailedName> call() throws XMLStreamException {
                List<ChunkFuture> futures = new ArrayList<>();

                int id = groupDao.getSeqAndSkip(chunkSize);
                List<Group> chunk = new ArrayList<>(chunkSize);
                final StaxStreamProcessor processor = new StaxStreamProcessor(is);

                while (processor.doUntil(XMLEvent.START_ELEMENT, "Group")) {
                    final String name = processor.getAttribute("name");
                    final GroupType type = GroupType.valueOf(processor.getAttribute("type"));
                    final Group group = new Group(id, name, type);

                    chunk.add(group);
                    if (chunk.size() == chunkSize) {
                        futures.add(submit(chunk));
                        chunk = new ArrayList<>(chunkSize);
                        id = groupDao.getSeqAndSkip(chunkSize);
                    }
                }

                if (!chunk.isEmpty()) {
                    futures.add(submit(chunk));
                }

                List<FailedName> failed = new ArrayList<>();
                futures.forEach(cf -> {
                    try {
                        failed.addAll(StreamEx.of(cf.future.get()).map(name -> new FailedName(name, "already present")).toList());
                        log.info(cf.nameRange + " successfully executed");
                    } catch (Exception e) {
                        log.error(cf.nameRange + " failed", e);
                        failed.add(new FailedName(cf.nameRange, e.toString()));
                    }
                });
                return failed;
            }

            private ChunkFuture submit(List<Group> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(chunk,
                        executorService.submit(() -> groupDao.insertAndGetConflictNames(chunk))
                );
                log.info("Submit " + chunkFuture.nameRange);
                return chunkFuture;
            }
        }.call();
    }
}
