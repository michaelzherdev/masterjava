package ru.javaops.masterjava.persist.dao;

import one.util.streamex.IntStreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

/**
 * Created by Mikhail on 02.04.2017.
 */
public abstract class GroupDao implements AbstractDao {

    public Group insert(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertWithId(group);
        }
        return group;
    }

    @SqlUpdate("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS GROUP_TYPE))")
    protected abstract void insertWithId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (name, type) VALUES (:name, CAST(:type AS GROUP_TYPE))")
    @GetGeneratedKeys
    protected abstract int insertGeneratedId(@BindBean Group group);

    @SqlBatch("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS GROUP_TYPE))" +
            "ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<Group> groups, @BatchChunkSize int chunkSize);

    @SqlQuery("SELECT nextval('group_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE group_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlQuery("SELECT * FROM groups ORDER BY id")
    public abstract List<Group> getAll();

    @SqlUpdate("TRUNCATE groups")
    @Override
    public abstract void clean();

    public List<String> insertAndGetConflictNames(List<Group> groups) {
        int[] result = insertBatch(groups, groups.size());
        return IntStreamEx.range(0, groups.size())
                .filter(i -> result[i] == 0)
                .mapToObj(index -> groups.get(index).getName())
                .toList();
    }
}