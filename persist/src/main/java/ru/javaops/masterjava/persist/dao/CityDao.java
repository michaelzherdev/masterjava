package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

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
}
