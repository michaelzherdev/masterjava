package ru.javaops.masterjava.export;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.*;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * gkislin
 * 14.10.2016
 */
@Slf4j
public class UserExport {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    @Value
    public static class FailedEmail {
        public String emailOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailOrRange + " : " + reason;
        }
    }

    public List<FailedEmail> process(final InputStream is, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<List<FailedEmail>>() {
            class ChunkFuture {
                String emailRange;
                Future<List<String>> future;

                public ChunkFuture(List<User> chunk, Future<List<String>> future) {
                    this.future = future;
                    this.emailRange = chunk.get(0).getEmail();
                    if (chunk.size() > 1) {
                        this.emailRange += '-' + chunk.get(chunk.size() - 1).getEmail();
                    }
                }
            }

            @Override
            public List<FailedEmail> call() throws XMLStreamException {
                List<ChunkFuture> futures = new ArrayList<>();

                int id = userDao.getSeqAndSkip(chunkSize);
                List<User> chunk = new ArrayList<>(chunkSize);
                final StaxStreamProcessor processor = new StaxStreamProcessor(is);

                Map<User, List<String>> map = new ConcurrentHashMap<>();
                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    final String email = processor.getAttribute("email");
                    final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                    City city = cityDao.getByIdStr(processor.getAttribute("city"));
                    final int cityIdStr = city.getId();

                    final String groupRefs = processor.getAttribute("groupRefs");
                    final String fullName = processor.getReader().getElementText();
                    final User user = new User(id++, fullName, email, flag, cityIdStr);
                    if (groupRefs != null) {
//                        groups.addAll(Arrays.asList(groupRefs.split(" ")));
                        map.put(user, Arrays.asList(groupRefs.split(" ")));
                    }

                    chunk.add(user);
                    if (chunk.size() == chunkSize) {
                        futures.add(submit(chunk));
                        chunk = new ArrayList<>(chunkSize);
                        id = userDao.getSeqAndSkip(chunkSize);
                    }

                }

                if (!chunk.isEmpty()) {
                    futures.add(submit(chunk));
                }

                List<FailedEmail> failed = new ArrayList<>();
                futures.forEach(cf -> {
                    try {
                        failed.addAll(StreamEx.of(cf.future.get()).map(email -> new FailedEmail(email, "already present")).toList());
                        log.info(cf.emailRange + " successfully executed");

                        cf.future.get().forEach(email -> {
                            map.forEach((k, v) -> {
                                if (k.getEmail().equals(email))
                                    map.remove(k);

                            });
                        });

                        map.forEach((k, v) -> {
                            for (String s : map.get(k)) {
                                Group group = groupDao.getByName(s);
                                userDao.insertUsersGroups(new UserGroupDTO(k.getId(), group.getId()));

                            }
                        });
                    } catch (Exception e) {
                        log.error(cf.emailRange + " failed", e);
                        failed.add(new FailedEmail(cf.emailRange, e.toString()));
                    }
                });

                return failed;
            }

            private ChunkFuture submit(List<User> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(chunk,
                        executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk))
                );
                log.info("Submit " + chunkFuture.emailRange);
                return chunkFuture;
            }
        }.call();
    }
}
