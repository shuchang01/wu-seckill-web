package com.wu.seckill;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import com.alibaba.fastjson.JSON;

/**
 * Servlet implementation class ReidsMatchServlet
 */
public class RedisMatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static JedisPool pool = RedisAPI.getPool();

	// 初始化操作
	// RedisAPI.set("accountBalance", "9999");  // 标还剩9999块钱
	
	public RedisMatchServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Jedis jedis = pool.getResource();
		long start = System.currentTimeMillis();
		int flag = 0;
		try {
			flag = bid(request, response, jedis);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("fail buy");
		} finally {
			pool.returnBrokenResource(jedis);
			RedisAPI.returnResource(pool, jedis);
		}
		if (flag == 1) {
			response.getWriter().write("success buy");
		} else if (flag == 2) {
			response.getWriter().write("have buy");
		} else if (flag == 0) {
			response.getWriter().write("bid is zero ,you can not buy");
		} else {
			response.getWriter().write("fail buy");
		}
		long end = System.currentTimeMillis();
		System.out.println("--------------------------------------------请求耗时：" + (end - start) + "毫秒");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	/**
	 * 开始抢购
	 * 
	 * @param request
	 * @param response
	 * @param jedis
	 * @return
	 * @throws Exception
	 */
	private int bid(HttpServletRequest request, HttpServletResponse response, Jedis jedis) throws Exception {
		int flag = 0;// 1成功 , 2 已经购买, 3已经抢购完了，其他异常
		// 每个请求对应一个userId
//		int userId = new Random().nextInt(999999);
		String userId = com.wu.seckill.StringUtils.userId();
		
		// 观察 总标值，每人抢购一元
		String watchReply = jedis.watch("accountBalance");
		System.out.println("=================抢购watchReply==" + watchReply);
		while ("OK".equals(jedis.watch("accountBalance"))) {
			// 判断是否购买过
			Boolean isBuy = RedisAPI.sismember("userIdSet", userId + "");
			if (isBuy) {
				flag = 2;
				return flag;
			}
			// 投资额
			int r = 1;// new Random().nextInt(2);
			int lastAccount = 0;
			String balance = RedisAPI.get("accountBalance");
			if (StringUtils.isNotBlank(balance)) {
				lastAccount = Integer.valueOf(balance) - r;
			}
			if (lastAccount < 0) {
				flag = 3;
				break;
			}
			Transaction tx = jedis.multi();
			tx.set("accountBalance", lastAccount + "");
			List<Object> result = tx.exec();
			System.out.println("================set accountBalance over, result==" + JSON.toJSONString(result, true));
			if (result == null || result.isEmpty()) {
				String unwatchReply = jedis.unwatch();
				System.out.println("=================抢购unwatchReply==" + unwatchReply);
			} else {
				System.out.println("恭喜您，" + userId + "已经中标" + r + "元，标余额" + lastAccount + "元");
				RedisAPI.set(Thread.currentThread().getName(), r + "");
				RedisAPI.sadd("userIdSet", userId + "");
				flag = 1;
				break;
			}
		}
		System.out.println("===================flag [1 该用户抢购成功，2 该用户已经抢购， 3 该产品已被抢购完了， 其他情况]==" + flag);
		// 异步操作其他，比如accountBalance最新值刷新入db
		return flag;
	}
}