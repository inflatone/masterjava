package ru.masterjava.persist.dao;

import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import ru.masterjava.persist.model.UserGroup;

import java.util.List;
import java.util.Set;

public abstract class UserGroupDao implements AbstractDao {
    @Override
    @SqlUpdate("TRUNCATE user_group CASCADE")
    public abstract void clean();

    @SqlBatch("INSERT INTO user_group (user_id, group_id) VALUES (:userId, :groupId)")
    public abstract void insertBatch(@BindBean List<UserGroup> userGroups);

    @SqlQuery("SELECT user_id FROM user_group WHERE group_id=:it")
    public abstract Set<Integer> getUserIds(@Bind int groupId);

    public static List<UserGroup> toUserGroups(int userId, Integer... groupsIds) {
        return StreamEx.of(groupsIds).map(groupId -> new UserGroup(userId, groupId)).toList();
    }

    public static Set<Integer> getByGroupId(int groupId, List<UserGroup> userGroups) {
        return StreamEx.of(userGroups).filterBy(UserGroup::getGroupId, groupId).map(UserGroup::getUserId).toSet();
    }
}
