package org.together.pub.db;

import java.util.HashMap;
import java.util.Map;

import org.together.pub.util.BeanUtils;

@SuppressWarnings("rawtypes")
public class SQLParams extends HashMap<String, ParamProp> {
	private static final long serialVersionUID = 5037340458937262801L;

	public Object put(String key, Object value, Class clazz) {
		ParamProp prop = new ParamProp();
		prop.value = value;
		prop.clazz = clazz;
		return super.put(key, prop);
	}

	public Object put(String key, Object value) {
		Class clazz = null;
		if (value != null) {
			clazz = value.getClass();
		}
		return put(key, value, clazz);
	}

	public String notNull(String key, String text) {
		if (get(key) != null) {
			return text;
		} else {
			return "";
		}
	}

	public static SQLParams loadFromMap(Map map) {
		SQLParams params = new SQLParams();
		for (Object key : map.keySet()) {
			params.put(String.valueOf(key), map.get(key));
		}
		return params;
	}

	public static SQLParams load(Object obj) {
		Map map = BeanUtils.pojo2Map(obj);
		return loadFromMap(map);
	}

}
