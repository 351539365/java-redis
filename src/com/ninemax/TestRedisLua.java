package com.ninemax;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestRedisLua {

	public static final String REDIS_LUA = "redis.call('select',1);local key = KEYS[1];local expire = tonumber(KEYS[2]);"
			+ "local number = tonumber(KEYS[3]);local count = tonumber(redis.call('GET',key));"
			+ "if count == nil then redis.call('SETEX',KEYS[1],expire,'100');return 0;"
			+ "else if count +1 >= number then redis.call('SETEX',KEYS[1],expire,'1');return 0;"
			+ "else redis.call('INCR',KEYS[1]);return 1;end;end;";

	public static final String REDIS_LUA2 = "local key = KEYS[1];return redis.call('INCR',KEYS[1]);";

	private static String redisScript = null;

	private static JedisPool jedisPool = null;

	static {
		/*
		 * try { Conf.load(); } catch (Exception e) { e.printStackTrace(); }
		 */
		System.out.println("====");
		jedisPool = getPool();
		redisScript = loadLuaScript();
	}

	/**
	 * ����redis���ӳ�
	 * 
	 * @param ip
	 * @param port
	 * @return JedisPool
	 */
	public static JedisPool getPool() {
		if (jedisPool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// ����һ��pool�ɷ�����ٸ�jedisʵ����ͨ��pool.getResource()����ȡ��
			// �����ֵΪ-1�����ʾ�����ƣ����pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
			config.setMaxActive(500);
			// ����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����
			config.setMaxIdle(5);
			// ��ʾ��borrow(����)һ��jedisʵ��ʱ�����ĵȴ�ʱ�䣬��������ȴ�ʱ�䣬��ֱ���׳�JedisConnectionException��
			config.setMaxWait(1000 * 100);
			// ��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�
			config.setTestOnBorrow(true);
			jedisPool = new JedisPool(config, "127.0.0.1");
		}
		return jedisPool;
	}

	private static String loadLuaScript() {
		Jedis jedis = null;
		String redisScript = null;
		try {
			jedis = jedisPool.getResource();
			redisScript = jedis.scriptLoad(REDIS_LUA2);
		} catch (Exception e) {
			e.printStackTrace();
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return redisScript;
	}

	public void exce() {
		Jedis jedis = jedisPool.getResource();
		jedis.select(1);
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("hostcan", "1");
		// jedis.hmset("topic", hash);
		jedis.hincrBy("topic", "topic110:192", 1);
		//String ip = "192";
		// Object result = jedis.evalsha(redisScript, 1, "topic110:192", "100",
		// "3");
		// jedis.hset("topic", "topic110:192", result.toString());
		System.out.println(jedis.hget("topic", "topic110:192"));

		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}

	public static void main(String[] args) {
		// ���ӱ��ص� Redis ����
		/*
		 * Jedis jedis = jedisPool.getResource(); Object result =
		 * jedis.evalsha(redisScript, 3 , "zhangsan", "100", "3");
		 */
		// System.out.println(result);
		
		/**counter:user
        ->  ip_1001: 21
        ->  ip_1002: 10
        ->  ip_1003: 32
        ->  ip_1004: 203
            .......
        ->  ip_9999: 130
        
        **/
		
		/**counter:topic
        ->  ID_1001: 21
        ->  ID_1002: 10
        ->  ID_1003: 32
        ->  ID_1004: 203
            .......
        ->  ID_9999: 130
        
        jedis.hincrBy("topic", "ID_1001", 1);
        **/
		

		TestRedisLua test = new TestRedisLua();
		test.exce();
	}

}
