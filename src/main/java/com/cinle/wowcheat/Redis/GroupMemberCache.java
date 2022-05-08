package com.cinle.wowcheat.Redis;

import com.cinle.wowcheat.Dao.GroupMemberDao;
import com.cinle.wowcheat.Model.GroupMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author JunLe
 * @Time 2022/4/26 22:28
 */
@Service
public class GroupMemberCache {
    private final static String prefix_KEY_MEMBER = "GROUP_MEMBERS:";
    private final static String prefix_KEY_ADMIN = "GROUP_ADMINS:";
    private final static long expireTime = 2 * 60 * 60;
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private GroupMemberDao groupMemberDao;


    public Set<GroupMember> getGroupMembersByGroupId(String groupId) {
        String key = prefix_KEY_MEMBER + groupId;
        Set<GroupMember> set = redisTemplate.opsForSet().members(key);
        return set.size() > 0 ? set : initMembers(groupId);
    }

    public Set<String> getGroupAdminIdsByGroupId(String groupId) {
        String key = prefix_KEY_ADMIN + groupId;
        Set<String> set = redisTemplate.opsForSet().members(key);
        return set.size() > 0 ? set : initAdminIds(groupId);
    }

    public long updateGroupMembers(String groupId, List<GroupMember> groupMembers) {
        String key = prefix_KEY_MEMBER + groupId;
        long count = 0;
        for (GroupMember member : groupMembers) {
            redisTemplate.opsForSet().add(key, member);
            count++;
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return count;
    }

    public long deleteGroupMember(String groupId, List<GroupMember> groupMembers) {
        String key = prefix_KEY_MEMBER + groupId;
        long count = 0;
        for (GroupMember member : groupMembers) {
            redisTemplate.opsForSet().remove(key, member);
            count++;
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return count;
    }

    public long updateGroupAdminIds(String groupId, List<String> adminIds) {
        String key = prefix_KEY_ADMIN + groupId;
        long count = 0;
        for (String uuid : adminIds) {
            redisTemplate.opsForSet().add(key, uuid);
            count++;
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return count;
    }

    public long deleteGroupAdminIds(String groupId, List<String> adminIds) {
        String key = prefix_KEY_ADMIN + groupId;
        long count = 0;
        for (String uuid : adminIds) {
            redisTemplate.opsForSet().remove(key, uuid);
            count++;
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return count;
    }

    public void removeAllCache(String groupId){
        redisTemplate.delete(prefix_KEY_MEMBER + groupId);
        redisTemplate.delete(prefix_KEY_ADMIN + groupId);
    }

    private Set<GroupMember> initMembers(String groupId) {
        String key = prefix_KEY_MEMBER + groupId;
        Set<GroupMember> set = redisTemplate.opsForSet().members(key);
        if (set != null && set.size() > 0) {
            return set;
        } else {
            synchronized (key) {
                Set<GroupMember> set1 = redisTemplate.opsForSet().members(key);
                if (set != null && set.size() > 0) {
                    return set1;
                }
                List<GroupMember> members = groupMemberDao.getGroupMembersForSendMessage(groupId);
                if (members == null || members.size() <= 0) {
                    //群组不存在或者无组员，随便存一个，防止非法请求
                    members = Arrays.asList(new GroupMember());
                }
                updateGroupMembers(groupId, members);
            }
        }
        return redisTemplate.opsForSet().members(key);
    }

    private Set<String> initAdminIds(String groupId) {
        String key = prefix_KEY_ADMIN + groupId;
        Set<String> set = redisTemplate.opsForSet().members(key);
        if (set != null && set.size() > 0) {
            return set;
        } else {
            synchronized (key) {
                Set<String> set1 = redisTemplate.opsForSet().members(key);
                if (set != null && set.size() > 0) {
                    return set1;
                }
                List<String> ids = groupMemberDao.getAdminIdListByGroupId(groupId);
                if (ids == null || ids.size() <= 0) {
                    //群组不存在或者无组员，随便存一个，防止非法请求
                    ids = Arrays.asList("default");
                }
                updateGroupAdminIds(groupId, ids);
            }
        }
        return redisTemplate.opsForSet().members(key);
    }


}
