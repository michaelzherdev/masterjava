package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.IntStreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

/**
 * Created by Mikhail on 02.04.2017.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("INSERT INTO cities (id, value) VALUES (:id, :value) ON CONFLICT DO NOTHING")
    public abstract void insert(@BindBean City city);

    @SqlQuery("SELECT * FROM cities ORDER BY id")
    public abstract List<City> getAll();

    @SqlUpdate("TRUNCATE cities")
    @Override
    public abstract void clean();
    @SqlQuery("SELECT nextval('group_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE group_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlBatch("INSERT INTO cities (id, id_str, value) VALUES (:id, :idStr, :value)" +
            "ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);

    public List<String> insertAndGetConflictIds(List<City> cities) {
        int[] result = insertBatch(cities, cities.size());
        return IntStreamEx.range(0, cities.size())
                .filter(i -> result[i] == 0)
                .mapToObj(index -> cities.get(index).getIdStr())
                .toList();
    }
}
