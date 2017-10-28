package com.wu.seckill;

import java.util.UUID;

public class StringUtils {
	private StringUtils() {
	}

	/**
	 * 生成一个8位长度的唯一字符串，作为开盘项目标识
	 * 
	 * @return
	 */
	public static synchronized String projectCode() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
	}
	
	/**
	 * 生成一个10位长度的唯一字符串，作为开盘项目标识
	 * 
	 * @return
	 */
	public static synchronized String userId() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
	}

}
