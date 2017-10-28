package com.wu.seckill;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;

/**
 * Servlet implementation class ReidsMatchListServlet
 */
public class RedisMatchListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static JedisPool pool = RedisAPI.getPool();;
	public static Jedis jedis;
	static {
		jedis = pool.getResource();
	}

	public RedisMatchListServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		list(request, response);
		try {
			response.sendRedirect("list.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	/**
	 * 查询抢购成功的人员数据列表
	 * 
	 * @param request
	 * @param response
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) {
		Set<?> set = jedis.smembers("userIdSet");
		Iterator<?> ite = set.iterator();
		System.out.println("中标名单--------------------userIdSet==" + JSON.toJSONString(set, true));
		int i = 0;
		Map<String, String> map = new HashMap<String, String>();
		while (ite.hasNext()) {
			i++;
			Object obj1 = ite.next();
			System.out.println("第" + i + "名：" + obj1);
			map.put("第" + i + "名：", obj1 + "");
		}
		request.getSession().setAttribute("user", map);
		System.out.println("中标名单----------------------map==" + JSON.toJSONString(map, true));
	}

}
