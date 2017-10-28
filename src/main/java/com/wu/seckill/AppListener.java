package com.wu.seckill;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("=======================应用系统已挂掉");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("=======================应用系统启动，开始初始化数据...");
		RedisAPI.set("accountBalance", "9999");  // 标还剩9999套房
		System.out.println("=======================应用系统启动，初始化数据完毕.");
	}

}
