package org.together.web.httpserver;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;

public class BaseService {
//	public enum Status {
//		SUCCESS,
//
//		FAIL,
//
//		DB_ERROR
//		
//
//	}
	//STATUS
	 public static String SUCCESS = "success";
	 public static String FAIL = "fail";
	 public static String DB_ERROR = "db_error";

	public static String RESULT = "result";
	public static String STATUS = "status";
	public static String JSON_STR = "json";

	public byte[] buildResult(String status, Object obj) {
		HashMap<String, Object> result = new HashMap<>();
		result.put(STATUS, status);
		result.put(RESULT, obj);
		return JSON.toJSONBytes(result);
	}
}
