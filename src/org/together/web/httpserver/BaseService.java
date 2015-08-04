package org.together.web.httpserver;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;

public class BaseService {
	public static String SUCCESS = "success";
	public static String FAIL = "fail";
	public static String DB_ERROR = "db_error";
	public static String JSON_STR = "json";

	public byte[] buildResult(String status, Object obj) {
		HashMap<String, Object> result = new HashMap<>();
		result.put(status, obj);
		return JSON.toJSONBytes(result);
	}
}
