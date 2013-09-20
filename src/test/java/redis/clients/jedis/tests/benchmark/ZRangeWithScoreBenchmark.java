package redis.clients.jedis.tests.benchmark;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.tests.HostAndPortUtil;
import redis.clients.jedis.tests.HostAndPortUtil.HostAndPort;

public class ZRangeWithScoreBenchmark {
	private static HostAndPort hnp = HostAndPortUtil.getRedisServers().get(0);
	private static final int TOTAL_OPERATIONS = 10000;

	private static void setupData(Jedis jedis, byte[] key) {
		jedis.del(key);

		Random random = new Random();

		for (int i = 0; i < 1000; ++i) {
			double score = random.nextDouble();

			byte[] member = new byte[36];
			for (int j = 0; j < 36; ++j) {
				member[j] = (byte) ('0' + random.nextInt(10));
			}

			jedis.zadd(key, score, member);
		}
	}

	private static void cleanUp(Jedis jedis, byte[] key) {
		jedis.del(key);
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Jedis jedis = new Jedis(hnp.host, hnp.port);
		jedis.connect();
		jedis.auth("foobared");
		jedis.flushAll();

		byte[] testkey = "zrangewithscorekey".getBytes();
		setupData(jedis, testkey);

		long begin = Calendar.getInstance().getTimeInMillis();

		for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
			jedis.zrangeWithScores(testkey, 0, 200);
		}

		long elapsed = Calendar.getInstance().getTimeInMillis() - begin;

		cleanUp(jedis, testkey);
		jedis.disconnect();

		System.out.println(((1000 * TOTAL_OPERATIONS) / elapsed) + " ops");
	}
}