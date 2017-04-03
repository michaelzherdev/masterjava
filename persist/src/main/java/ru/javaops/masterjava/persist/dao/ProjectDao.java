package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

/**
 * Created by Mikhail on 02.04.2017.
 */
public abstract class ProjectDao implements AbstractDao {

    public Project insert(Project project) {
        if (project.isNew()) {
            int id = insertGeneratedId(project);
            project.setId(id);
        } else {
            insertWithId(project);
        }
        return project;
    }

    @SqlUpdate("INSERT INTO projects (id, name, description) VALUES (:id, :name, :description)")
    protected abstract void insertWithId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (name, description) VALUES (:name, :description)")
    @GetGeneratedKeys
    protected abstract int insertGeneratedId(@BindBean Project project);


    @SqlQuery("SELECT * FROM projects ORDER BY id")
    public abstract List<Project> getAll();

    @SqlUpdate("TRUNCATE projects")
    @Override
    public abstract void clean();
}
