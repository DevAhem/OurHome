package org.together.pub.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.together.pub.util.BeanUtils;

public class DBUtils {
	public static class ResultTable {
		public Object[][] records;
		public Object[] heads;
	}

	static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("rawtypes")
	private static Map<Class, Method> pstSetterMethods = new HashMap<>();

	@SuppressWarnings("rawtypes")
	private static Map<Class, Method> rsGetterMethods = new HashMap<>();

	@SuppressWarnings("rawtypes")
	private static void addPstSetterMethod(Class c, String methodName) {
		try {
			pstSetterMethods
					.put(c, PreparedStatement.class.getMethod(methodName,
							int.class, c));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private static void addRsGetterMethod(Class c, String methodName) {
		try {
			rsGetterMethods.put(c,
					ResultSet.class.getMethod(methodName, int.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		addPstSetterMethod(String.class, "setString");
		addPstSetterMethod(int.class, "setInt");
		addPstSetterMethod(long.class, "setLong");
		addPstSetterMethod(double.class, "setDouble");
		addPstSetterMethod(BigDecimal.class, "setBigDecimal");
		addPstSetterMethod(Timestamp.class, "setTimestamp");

		addRsGetterMethod(String.class, "getString");
		addRsGetterMethod(int.class, "getInt");
		addRsGetterMethod(long.class, "getLong");
		addRsGetterMethod(double.class, "getDouble");
		addRsGetterMethod(Integer.class, "getInt");
		addRsGetterMethod(Long.class, "getLong");
		addRsGetterMethod(Double.class, "getDouble");
		addRsGetterMethod(BigDecimal.class, "getBigDecimal");
		addRsGetterMethod(Timestamp.class, "getTimestamp");
	}

	public static Connection conn(String url, String userName, String pwd)
			throws Exception {
		return DriverManager.getConnection(url, userName, pwd);
	}

	public static Connection conn(String url) throws Exception {
		return DriverManager.getConnection(url);
	}

	public static void closeAll(AutoCloseable... cs) {
		for (int i = 0; i < cs.length; i++) {
			try {
				if (cs[i] != null) {
					cs[i].close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void rollbackAll(Connection... cons) {
		for (Connection con : cons) {
			try {
				con.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> queryForList(PreparedStatement pst, Class<T> c)
			throws Exception {
		List<T> resultList = new ArrayList<T>();

		List<Class> fieldTypeList = new ArrayList<Class>();
		List<Field> fieldList = new ArrayList<Field>();
		List<Class> dbTypeList = new ArrayList<Class>();

		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			for (int i = 0; i < md.getColumnCount(); i++) {
				try {
					Field field = c.getField(md.getColumnName(i + 1));
					fieldList.add(field);
					fieldTypeList.add(field.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
				dbTypeList.add(getRecordClass(md, i));
			}

			while (rs.next()) {
				T obj = c.newInstance();
				for (int i = 0; i < md.getColumnCount(); i++) {

					Object value = rs.getObject(i + 1);
					Class dbType = dbTypeList.get(i);
					Class fieldType = fieldTypeList.get(i);
					if (dbType.equals(fieldType)) {
						fieldList.get(i).set(obj, value);
					} else {
						fieldList.get(i)
								.set(obj,
										BeanUtils.convertType(value, dbType,
												fieldType));
					}
				}
				resultList.add(obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return resultList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void queryFirstToObject(PreparedStatement pst, Object obj) {
		Class<? extends Object> c = obj.getClass();
		List<Class> fieldTypeList = new ArrayList<Class>();
		List<Field> fieldList = new ArrayList<Field>();
		List<Class> dbTypeList = new ArrayList<Class>();

		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			for (int i = 0; i < md.getColumnCount(); i++) {
				try {
					Field field = c.getField(md.getColumnName(i + 1));
					fieldList.add(field);
					fieldTypeList.add(field.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
				dbTypeList.add(getRecordClass(md, i));
			}

			if (rs.next()) {
				for (int i = 0; i < md.getColumnCount(); i++) {
					Object value = rs.getObject(i + 1);
					Class dbType = dbTypeList.get(i);
					Class fieldType = fieldTypeList.get(i);
					if (dbType.equals(fieldType)) {
						fieldList.get(i).set(obj, value);
					} else {
						fieldList.get(i)
								.set(obj,
										BeanUtils.convertType(value, dbType,
												fieldType));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getRecordClass(ResultSetMetaData md, int i)
			throws ClassNotFoundException, SQLException {
		return Class.forName(md.getColumnClassName(i + 1));
	}

	public static List<Map<String, Object>> queryForMapList(
			PreparedStatement pst) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> record = new LinkedHashMap<>();
				for (int i = 0; i < md.getColumnCount(); i++) {
					if (md.getColumnTypeName(i + 1).toLowerCase()
							.indexOf("date") != -1) {
						record.put(md.getColumnName(i + 1),
								rs.getTimestamp(i + 1));
					} else {
						record.put(md.getColumnName(i + 1), rs.getObject(i + 1));
					}
				}
				resultList.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static List<Map<String, Object>> queryForMapList(
			PreparedStatement pst, Map<String, String> transMap) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> record = new LinkedHashMap<>();
				for (int i = 0; i < md.getColumnCount(); i++) {
					String columnName = md.getColumnName(i + 1);
					if (transMap.containsKey(columnName)) {
						columnName = transMap.get(columnName);
					}

					if (md.getColumnTypeName(i + 1).toLowerCase()
							.indexOf("date") != -1) {
						record.put(columnName, rs.getTimestamp(i + 1));
					} else {
						record.put(columnName, rs.getObject(i + 1));
					}
				}
				resultList.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static Map<String, Object> queryForMapFirst(PreparedStatement pst)
			throws Exception {
		Map<String, Object> record = new HashMap<>();
		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			if (rs.next()) {
				record = new HashMap<>();
				for (int i = 0; i < md.getColumnCount(); i++) {
					record.put(md.getColumnName(i + 1), rs.getObject(i + 1));
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return record;
	}

	public static Map<String, Object> queryForMapFirst(PreparedStatement pst,
			Map<String, String> transMap) throws Exception {
		Map<String, Object> record = new HashMap<>();
		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			if (rs.next()) {
				record = new HashMap<>();
				for (int i = 0; i < md.getColumnCount(); i++) {
					String columnName = md.getColumnName(i + 1);
					if (transMap.containsKey(columnName)) {
						columnName = transMap.get(columnName);
					}

					record.put(columnName, rs.getObject(i + 1));
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return record;
	}

	public static List<Map<String, Object>> queryForMapList(Connection con,
			String sql) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		try (PreparedStatement pst = con.prepareStatement(sql);) {
			resultList = queryForMapList(pst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static Map<Object, Object> queryForKVMap(Connection con, String sql) {
		Map<Object, Object> results = new HashMap<>();
		try (PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery();) {
			while (rs.next()) {
				results.put(rs.getObject(1), rs.getObject(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings("rawtypes")
	public static Method getPstSetterMethod(Class c) {
		Method method = pstSetterMethods.get(c);
		if (method == null) {
			try {
				method = PreparedStatement.class.getMethod("setObject",
						int.class, Object.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return method;
	}

	@SuppressWarnings("rawtypes")
	public static Method getRsGetterMethod(Class c) {
		Method method = rsGetterMethods.get(c);
		if (method == null) {
			try {
				method = ResultSet.class.getMethod("getObject", int.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return method;
	}

	public static List<Method> getPstMethods(List<Field> fieldList) {
		List<Method> methodList = new ArrayList<Method>();
		try {
			for (Field field : fieldList) {
				methodList.add(getRsGetterMethod(field.getType()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return methodList;
	}

	public static void executeUpdate(Connection con, String sql, Object... os)
			throws Exception {
		try (PreparedStatement pst = con.prepareStatement(sql)) {
			for (int i = 0; i < os.length; i++) {
				getPstSetterMethod(os[i].getClass()).invoke(pst, i + 1, os[i]);
			}
			pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<String> getColumnNames(ResultSetMetaData md) {
		List<String> list = new ArrayList<>();
		try {
			int c = md.getColumnCount() + 1;
			for (int i = 1; i < c; i++) {
				list.add(md.getColumnName(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set getDateColumn(ResultSetMetaData md) {
		Set set = new HashSet();
		try {
			int c = md.getColumnCount() + 1;
			for (int i = 1; i < c; i++) {
				String typeName = md.getColumnTypeName(i);
				if ("Timestamp".equals(typeName)) {
					set.add(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return set;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<?> queryForSingleColList(PreparedStatement pst) {
		List resultList = new ArrayList<>();
		try (ResultSet rs = pst.executeQuery();) {
			ResultSetMetaData md = rs.getMetaData();
			boolean isTime = md.getColumnTypeName(1).toLowerCase()
					.indexOf("date") != -1;
			while (rs.next()) {
				if (isTime) {
					resultList.add(rs.getTimestamp(1));
				} else {
					resultList.add(rs.getObject(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

}