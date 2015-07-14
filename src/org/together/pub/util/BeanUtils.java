package org.together.pub.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

public class BeanUtils {
	public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	private static ConvertUtilsBean convertUtils = BeanUtilsBean.getInstance()
			.getConvertUtils();

	public static <S, D> D convertType(Object obj, Class<S> srcClass,
			Class<D> destClass) {
		Converter converter = convertUtils.lookup(srcClass, destClass);
		return converter.convert(destClass, obj);
	}

	// @SuppressWarnings("rawtypes")
	// public static Object convertType(Object obj, Class srcClass, Class
	// destClass) {
	// try {
	// if (obj == null) {
	// return null;
	// } else if (srcClass.equals(Timestamp.class)
	// && destClass.equals(String.class)) {
	// return sdf.format(obj);
	// } else if (destClass.equals(String.class)) {
	// return obj.toString();
	// } else {
	// String strObj = obj.toString();
	// if (int.class.equals(destClass)) {
	// return Integer.parseInt(strObj);
	// } else if (Integer.class.equals(strObj)) {
	// return Integer.valueOf(strObj);
	// } else if (long.class.equals(destClass)) {
	// return Long.parseLong(strObj);
	// } else if (Long.class.equals(strObj)) {
	// return Long.valueOf(strObj);
	// } else if (double.class.equals(destClass)) {
	// return Double.parseDouble(strObj);
	// } else if (Double.class.equals(strObj)) {
	// return Double.valueOf(strObj);
	// } else if (Date.class.equals(destClass)) {
	// return sdf.parse(strObj);
	// } else if (Timestamp.class.equals(destClass)) {
	// return new Timestamp(sdf.parse(strObj).getTime());
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return obj;
	// }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map pojo2Map(Object model) {
		Map m = new LinkedHashMap();
		Field[] fields = model.getClass().getFields();
		for (Field field : fields) {
			try {
				m.put(field.getName(), field.get(model));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return m;
	}

	public static void copy(Object dest, Object src) {
		Field[] fields = src.getClass().getFields();
		for (Field field : fields) {
			try {
				BeanUtilsBean.getInstance().setProperty(dest, field.getName(),
						field.get(src));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void copy(Object dest, Map src) {
		Field[] fields = dest.getClass().getFields();
		for (Field field : fields) {
			try {
				String name = field.getName();

				Object value = src.get(name);
				if (value != null) {
					field.set(
							dest,
							convertType(value, value.getClass(),
									field.getType()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 交集
	public static <T> Set<T> joinSet(Set<T> set1, Set<T> set2) {
		Set<T> result = new HashSet<T>();
		result.addAll(set1);
		result.retainAll(set2);
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static void copyIgnoreCase(Object dest, Map src) {
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : dest.getClass().getFields()) {
			fieldMap.put(field.getName().toLowerCase(), field);
		}

		Map<String, String> mapKeyMap = new HashMap<>();

		for (Object key : src.keySet()) {
			mapKeyMap.put(key.toString().toLowerCase(), key.toString());
		}

		Set<String> joinedKeys = joinSet(fieldMap.keySet(), mapKeyMap.keySet());

		for (String key : joinedKeys) {
			Field field = fieldMap.get(key);
			try {
				Object value = src.get(mapKeyMap.get(key));
				if (value != null) {
					field.set(
							dest,
							convertType(value, value.getClass(),
									field.getType()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] values(Class<T> c) {
		try {
			return (T[]) c.getMethod("values").invoke(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] objectToData(Object obj) {
		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bOut);
			out.writeObject(obj);
			out.close();
			bOut.close();
			return bOut.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}