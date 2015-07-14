package org.together.pub.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {
	public void copyParameterIgnoreCase(Object dest, HttpServletRequest req) {
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : dest.getClass().getFields()) {
			fieldMap.put(field.getName().toLowerCase(), field);
		}

		Map<String, String> mapKeyMap = new HashMap<>();
		Map<String, String[]> src = req.getParameterMap();
		for (Object key : src.keySet()) {
			mapKeyMap.put(key.toString().toLowerCase(), key.toString());
		}

		Set<String> joinedKeys = BeanUtils.joinSet(fieldMap.keySet(),
				mapKeyMap.keySet());

		for (String key : joinedKeys) {
			Field field = fieldMap.get(key);
			try {
				String[] values = (String[]) src.get(mapKeyMap.get(key));
				if (values != null) {
					if (values.length == 1) {
						String value = values[0];
						field.set(dest, BeanUtils.convertType(value,
								value.getClass(), field.getType()));
					} else {
						// 数组暂时只支持字符串
						field.set(dest, values);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
