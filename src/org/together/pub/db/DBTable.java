package org.together.pub.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTable<PO, ID> {
	private String insertSQL;
	private String createSQL;
	private String storeSQL;
	private String deleteSQL;
	private String loadSQL;
	private String selectAllSQL;

	private Class<PO> classPO;
	private Field idField;
	private List<Field> fieldList = new ArrayList<Field>();
	private final Logger logger = LoggerFactory.getLogger(DBTable.class);

	private DBbean db;
	private String tableName;

	private GenerationType genType = GenerationType.AUTO;
	private String sequenceName;

	public DBTable(DBbean db, String tableName, Class<PO> classPO, Class<ID> classID) {
		this.db = db;
		this.tableName = tableName;
		this.classPO = classPO;

		Field[] fields = classPO.getFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getAnnotation(Id.class) != null) {
				idField = field;

				GeneratedValue genValue = idField.getAnnotation(GeneratedValue.class);

				if (genValue != null) {
					genType = genValue.strategy();
					if (genType == GenerationType.SEQUENCE) {
						sequenceName = genValue.generator();
					}
				}
			} else {
				fieldList.add(field);
			}
		}

		selectAllSQL = buildSelectAllSQL();
		loadSQL = buildLoadSQL();
		insertSQL = buildInsertSQL();
		createSQL = buildCreateSQL();
		storeSQL = buildStoreSQL();
		deleteSQL = buildDeleteSQL();

		// logger.debug(selectAllSQL);
		// logger.debug(loadSQL);
		// logger.debug(insertSQL);
		// logger.debug(createSQL);
		// logger.debug(storeSQL);
		// logger.debug(deleteSQL);
	}

	private String getTableName() {
		return tableName;
	}

	private String buildSelectAllSQL() {
		StringBuilder buf = new StringBuilder();

		buf.append("select ").append(idField.getName());

		for (Field field : fieldList) {
			buf.append(",").append(field.getName());
		}
		buf.append(" from ").append(getTableName());
		return buf.toString();
	}

	private String buildInsertSQL() {
		StringBuilder buf1 = new StringBuilder();
		StringBuilder buf2 = new StringBuilder();

		buf1.append(idField.getName());
		buf2.append("?");

		for (int i = 0; i < fieldList.size(); i++) {
			buf1.append(",").append(fieldList.get(i).getName());
			buf2.append(",").append("?");
		}
		return "insert into " + getTableName() + " (" + buf1 + ")" + " values (" + buf2 + ")";
	}

	private String buildCreateSQL() {
		StringBuilder buf1 = new StringBuilder();
		StringBuilder buf2 = new StringBuilder();
		for (int i = 0; i < fieldList.size(); i++) {
			if (i != 0) {
				buf1.append(",");
				buf2.append(",");
			}
			buf1.append(fieldList.get(i).getName());
			buf2.append("?");
		}
		// idField
		if (genType == GenerationType.SEQUENCE) {
			buf1.append(",").append(idField.getName());
			buf2.append(",").append(sequenceName).append(".nextval");
		} else if (genType == GenerationType.IDENTITY) {
			buf1.append(",").append(idField.getName());
			buf2.append(",").append("sys_guid()");
		}
		return "insert into " + getTableName() + " (" + buf1 + ")" + " values (" + buf2 + ")";
	}

	private String buildStoreSQL() {
		StringBuilder buf = new StringBuilder("update ").append(getTableName()).append(" set ");
		for (int i = 0; i < fieldList.size(); i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(fieldList.get(i).getName() + " = ?");
		}
		buf.append(" where ").append(idField.getName()).append(" = ?");
		return buf.toString();
	}

	private String buildDeleteSQL() {
		StringBuilder buf = new StringBuilder();
		buf.append("delete ").append(getTableName()).append(" where ").append(idField.getName()).append(" = ?");
		return buf.toString();
	}

	private String buildLoadSQL() {
		StringBuilder buf = new StringBuilder(selectAllSQL).append(" ").append("where ").append(idField.getName())
				.append(" = ?");
		return buf.toString();
	}

	public void insert(PO po) {
		Connection con = db.getConnection();
		try (PreparedStatement pst = con.prepareStatement(insertSQL);) {
			Field field;
			DBUtils.getPstSetterMethod(idField.getType()).invoke(pst, 1, idField.get(po));
			for (int i = 0; i < fieldList.size(); i++) {
				field = fieldList.get(i);
				DBUtils.getPstSetterMethod(field.getType()).invoke(pst, i + 2, field.get(po));
			}
			pst.executeUpdate();
		} catch (Exception e) {
			logger.error("DBError", e);
		}
	}

	public void batchInsert(List<PO> poList) {
		Connection con = db.getConnection();
		try (PreparedStatement pst = con.prepareStatement(insertSQL);) {
			Field field;

			for (PO po : poList) {
				DBUtils.getPstSetterMethod(idField.getType()).invoke(pst, 1, idField.get(po));
				for (int i = 0; i < fieldList.size(); i++) {
					field = fieldList.get(i);
					DBUtils.getPstSetterMethod(field.getType()).invoke(pst, i + 2, field.get(po));
				}
				pst.addBatch();
			}
			pst.executeBatch();
		} catch (Exception e) {
			logger.error("DBError", e);
		}
	}

	public void create(PO po) {
		Connection con = db.getConnection();
		try (PreparedStatement pst = con.prepareStatement(createSQL);) {
			Field field;
			for (int i = 0; i < fieldList.size(); i++) {
				field = fieldList.get(i);
				DBUtils.getPstSetterMethod(field.getType()).invoke(pst, i + 1, field.get(po));
			}
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DBError", e);
		}
	}

	public void store(PO po) {
		Connection con = db.getConnection();

		try (PreparedStatement pst = con.prepareStatement(storeSQL);) {
			Field field;
			for (int i = 0; i < fieldList.size(); i++) {
				field = fieldList.get(i);
				DBUtils.getPstSetterMethod(field.getType()).invoke(pst, i + 1, field.get(po));
			}
			DBUtils.getPstSetterMethod(idField.getType()).invoke(pst, fieldList.size() + 1, idField.get(po));

			pst.executeUpdate();
		} catch (Exception e) {
			logger.error("DBError", e);
		}
	}

	public void delete(ID id) {
		Connection con = db.getConnection();
		try (PreparedStatement pst = con.prepareStatement(deleteSQL);) {
			DBUtils.getPstSetterMethod(idField.getType()).invoke(pst, 1, id);
			pst.executeUpdate();
		} catch (Exception e) {
			logger.error("DBError", e);
		}
	}

	// TODO TYPE PST
	public PO load(ID id) {
		PO po = null;
		Connection con = db.getConnection();
		try (PreparedStatement pst = con.prepareStatement(loadSQL);) {
			DBUtils.getPstSetterMethod(idField.getType()).invoke(pst, 1, id);
			try (ResultSet rs = pst.executeQuery();) {
				if (rs.next()) {
					po = classPO.newInstance();
					idField.set(po, rs.getObject(1));
					for (int i = 0; i < fieldList.size(); i++) {
						fieldList.get(i).set(po, rs.getObject(i + 2));
					}
				}
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			logger.error("DBError", e);
		}
		return po;
	}

	public PO findFirstBy(String whereClause, Object... objs) {
		PO po = null;
		Connection con = db.getConnection();
		StringBuilder buf = new StringBuilder(selectAllSQL).append(" ").append(whereClause);
		String sql = buf.toString();
		try (PreparedStatement pst = con.prepareStatement(sql);) {
			for (int i = 0; i < objs.length; i++) {
				DBUtils.getPstSetterMethod(objs.getClass()).invoke(pst, i + 1, objs[i]);
			}
			try (ResultSet rs = pst.executeQuery();) {
				if (rs.next()) {
					po = classPO.newInstance();
					idField.set(po, rs.getObject(1));
					for (int i = 0; i < fieldList.size(); i++) {
						fieldList.get(i).set(po, rs.getObject(i + 2));
					}
				}
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			logger.error("DBError", e);
		}
		return po;
	}

	public List<PO> findBy(String whereClause, Object... objs) {
		PO po = null;
		List<PO> list = new ArrayList<>();
		Connection con = db.getConnection();

		StringBuilder buf = new StringBuilder(selectAllSQL).append(" ").append(whereClause);
		String sql = buf.toString();

		try (PreparedStatement pst = con.prepareStatement(sql);) {
			for (int i = 0; i < objs.length; i++) {
				DBUtils.getPstSetterMethod(objs[i].getClass()).invoke(pst, i + 1, objs[i]);
			}
			try (ResultSet rs = pst.executeQuery();) {
				while (rs.next()) {
					po = classPO.newInstance();
					idField.set(po, rs.getObject(1));
					for (int i = 0; i < fieldList.size(); i++) {
						fieldList.get(i).set(po, rs.getObject(i + 2));
					}
					list.add(po);
				}
			} catch (Exception e) {

				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DBError", e);
		}
		return list;
	}

	public void executeBy(String exeSQL, String whereClause, Object... objs) {
		Connection con = db.getConnection();
		String sql = exeSQL + " " + whereClause;

		try (PreparedStatement pst = con.prepareStatement(sql);) {
			for (int i = 0; i < objs.length; i++) {
				DBUtils.getPstSetterMethod(objs.getClass()).invoke(pst, i + 1, objs[i]);
			}
			pst.executeUpdate();
		} catch (Exception e) {
			logger.error("DBError", e);
		}
	}

	public void deleteBy(String whereClause, Object... objs) {
		executeBy("delete " + getTableName(), whereClause, objs);
	}
}
