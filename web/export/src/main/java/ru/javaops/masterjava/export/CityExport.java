package ru.javaops.masterjava.export;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
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
public class CityExport {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    @Value
    public static class Failed {
        public String idOrRange;
        public String reason;

        @Override
        public String toString() {
            return idOrRange + " : " + reason;
        }
    }

    public List<Failed> process(final InputStream is, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<List<Failed>>() {
            class ChunkFuture {
                String idRange;
                Future<List<String>> future;

                public ChunkFuture(List<City> chunk, Future<List<String>> future) {
                    this.future = future;
                    this.idRange = chunk.get(0).getIdStr();
                    if (chunk.size() > 1) {
                        this.idRange += '-' + chunk.get(chunk.size() - 1).getIdStr();
                    }
                }
            }

            @Override
            public List<Failed> call() throws XMLStreamException {
                List<ChunkFuture> futures = new ArrayList<>();

                int id = cityDao.getSeqAndSkip(chunkSize);
                List<City> chunk = new ArrayList<>(chunkSize);
                final StaxStreamProcessor processor = new StaxStreamProcessor(is);

                while (processor.doUntil(XMLEvent.START_ELEMENT, "City")) {
                    final String idStr = processor.getAttribute("id");
                    final String value = processor.getText();
                    final City city = new City(id++, idStr, value);

                    chunk.add(city);
                    if (chunk.size() == chunkSize) {
                        futures.add(submit(chunk));
                        chunk = new ArrayList<>(chunkSize);
                        id = cityDao.getSeqAndSkip(chunkSize);
                    }
                }

                if (!chunk.isEmpty()) {
                    futures.add(submit(chunk));
                }

                List<Failed> failed = new ArrayList<>();
                futures.forEach(cf -> {
                    try {
                        failed.addAll(StreamEx.of(cf.future.get()).map(name -> new Failed(name, "already present")).toList());
                        log.info(cf.idRange + " successfully executed");
                    } catch (Exception e) {
                        log.error(cf.idRange + " failed", e);
                        failed.add(new Failed(cf.idRange, e.toString()));
                    }
                });
                return failed;
            }

            private ChunkFuture submit(List<City> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(chunk,
                        executorService.submit(() -> cityDao.insertAndGetConflictIds(chunk))
                );
                log.info("Submit " + chunkFuture.idRange);
                return chunkFuture;
            }
        }.call();
    }
}
