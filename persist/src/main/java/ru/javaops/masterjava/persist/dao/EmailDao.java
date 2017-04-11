package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Email;

import java.util.List;

/**
 * Created by mikhail on 11.04.17.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class EmailDao implements AbstractDao {

    @SqlUpdate("TRUNCATE groups CASCADE ")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM emails ORDER BY date DESC")
    public abstract List<Email> getAll();

    @SqlUpdate("INSERT INTO emails (from_name, subject, body, result, date)  VALUES (:from, :subject, :body, :result, :date)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean Email email);

    public void insert(Email email) {
        int id = insertGeneratedId(email);
        email.setId(id);
    }
}
