package org.together.pub.db;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Named;
import javax.inject.Singleton;

//ORM的入口
@Singleton
public class DBStoreHelper {

	public DBStoreHelper(@Named("53") DBbean db) {
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			if (field.getType().equals(DBTable.class)) {
				initTable(field, db);
			}
		}
	}

	// 对某一个表模型进行初始化
	@SuppressWarnings("rawtypes")
	private void initTable(Field field, DBbean db) {
		try {
			Type genericType = field.getGenericType();
			if (genericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericType;
				Type[] types = parameterizedType.getActualTypeArguments();
				TableName annoName = field.getAnnotation(TableName.class);
				String tableName = annoName != null ? annoName.value() : field.getName();
				DBTable dbTable = DBTable.class.getConstructor(DBbean.class, String.class, Class.class, Class.class)
						.newInstance(db, tableName, (Class) types[0], (Class) types[1]);
				field.set(this, dbTable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
