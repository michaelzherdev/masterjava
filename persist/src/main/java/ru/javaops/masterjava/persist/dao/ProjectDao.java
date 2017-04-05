package ru.javaops.masterjava.persist.dao;

import one.util.streamex.IntStreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;
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

    @SqlQuery("SELECT nextval('proj_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE proj_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("TRUNCATE projects")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO projects (id, name, description) VALUES (:id, :name, :description)" +
            "ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<Project> projects, @BatchChunkSize int chunkSize);

    public List<String> insertAndGetConflictNames(List<Project> projects) {
        int[] result = insertBatch(projects, projects.size());
        return IntStreamEx.range(0, projects.size())
                .filter(i -> result[i] == 0)
                .mapToObj(index -> projects.get(index).getName())
                .toList();
    }
}
