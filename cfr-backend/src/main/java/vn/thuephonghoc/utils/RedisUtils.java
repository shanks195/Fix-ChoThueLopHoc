package vn.thuephonghoc.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings({"unchecked","all"})
public class RedisUtils {

    private RedisTemplate<Object, Object> redisTemplate;
    @Value("${jwt.online-key}")
    private String onlineKey;

    public RedisUtils(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =============================common============================

    /**
     * Specify the cache expiration time
     * @param key key
     * @param time time (seconds)
     */
    public boolean expire(String key, long time) {
        try {
            if (time> 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get expiration time according to key
     * @param key key cannot be null
     * @return time (seconds) return 0 for permanent validity
     */
    public long getExpire(Object key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Find matching key
     * @param pattern key
     * @return /
     */
    public List<String> scan(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = rc.scan(options);
        List<String> result = new ArrayList<>();
        while (cursor.hasNext()) {
            result.add(new String(cursor.next()));
        }
        try {
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Paging query key
     * @param patternKey key
     * @param page page number
     * @param size per page
     * @return /
     */
    public List<String> findKeysForPage(String patternKey, int page, int size) {
        ScanOptions options = ScanOptions.scanOptions().match(patternKey).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = rc.scan(options);
        List<String> result = new ArrayList<>(size);
        int tmpIndex = 0;
        int fromIndex = page * size;
        int toIndex = page * size + size;
        while (cursor.hasNext()) {
            if (tmpIndex >= fromIndex && tmpIndex <toIndex) {
                result.add(new String(cursor.next()));
                tmpIndex++;
                continue;
            }
            // After obtaining the data that meets the conditions, you can exit
            if(tmpIndex >=toIndex) {
                break;
            }
            tmpIndex++;
            cursor.next();
        }
        try {
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Determine whether the key exists
     * @param key key
     * @return true exists false does not exist
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete cache
     * @param key can pass one value or multiple
     */
    public void del(String... key) {
        if (key != null && key.length> 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=================== ==========

    /**
     * Ordinary cache acquisition
     * @param key key
     * @return value
     */
    public Object get(String key) {
        return key == null? null: redisTemplate.opsForValue().get(key);
    }

    /**
     * Batch acquisition
     * @param keys
     * @return
     */
    public List<Object> multiGet(List<String> keys) {
        Object obj = redisTemplate.opsForValue().multiGet(Collections.singleton(keys));
        return null;
    }

    /**
     * Put in ordinary cache
     * @param key key
     * @param value
     * @return true success false failure
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put the normal cache and set the time
     * @param key key
     * @param value
     * @param time time (seconds) time must be greater than 0, if time is less than or equal to 0, it will be set indefinitely
     * @return true success false failure
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time> 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put the normal cache and set the time
     * @param key key
     * @param value
     * @param time time
     * @param timeUnit type
     * @return true success false failure
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time> 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================Map=============== ==================

    /**
     * HashGet
     * @param key key cannot be null
     * @param item item cannot be null
     * @return value
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * Get all the key values ​​corresponding to hashKey
     * @param key key
     * @return corresponding multiple key values
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);

    }

    /**
     * HashSet
     * @param key key
     * @param map corresponds to multiple key values
     * @return true success false failure
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet and set the time
     * @param key key
     * @param map corresponds to multiple key values
     * @param time time (seconds)
     * @return true success false failure
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time> 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put data into a hash table, if it does not exist, it will be created
     *
     * @param key key
     * @param item item
     * @param value
     * @return true success false failure
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put data into a hash table, if it does not exist, it will be created
     *
     * @param key key
     * @param item item
     * @param value
     * @param time Time (seconds) Note: If the existing hash table has time, the original time will be replaced here
     * @return true success false failure
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time> 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the value in the hash table
     *
     * @param key key cannot be null
     * @param item items can be multiple and cannot be null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * Determine whether there is a value in the hash table
     *
     * @param key key cannot be null
     * @param item item cannot be null
     * @return true exists false does not exist
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash increment If it does not exist, it will create one and return the newly added value
     *
     * @param key key
     * @param item item
     * @param by how many to increase (greater than 0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash decreasing
     *
     * @param key key
     * @param item item
     * @param by should be reduced (less than 0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=================== ==========

    /**
     * Get all values ​​in Set according to key
     *
     * @param key key
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query from a set according to value, whether it exists
     *
     * @param key key
     * @param value
     * @return true exists false does not exist
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put data into set cache
     *
     * @param key key
     * @param values ​​can be multiple
     * @return number of successes
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Put the set data into the cache
     * @param key key
     * @param time time (seconds)
     * @param values ​​can be multiple
     * @return number of successes
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time> 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get the length of the set cache
     * @param key key
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Remove the value
     * @param key key
     * @param values ​​can be multiple
     * @return removed number
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=============== =================

    /**
     * Get the contents of the list cache
     * @param key key
     * @param start start
     * @param end end 0 to -1 represent all values
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the length of the list cache
     * @param key key
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get the value in the list by index
     * @param key key
     * @param index When index>=0, 0 header, 1 second element, and so on; when index<0, -1, end of the table, -2 the penultimate element, and so on
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Put the list into the cache
     * @param key key
     * @param value
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put the list into the cache
     * @param key key
     * @param value
     * @param time time (seconds)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time> 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put the list into the cache
     * @param key key
     * @param value
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put the list into the cache
     * @param key key
     * @param value
     * @param time time (seconds)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time> 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modify a piece of data in the list according to the index
     * @param key key
     * @param index index
     * @param value
     * @return /
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove N values ​​as value
     * @param key key
     * @param count how many to remove
     * @param value
     * @return removed number
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
