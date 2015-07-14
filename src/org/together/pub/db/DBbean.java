package org.together.pub.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.jooq.lambda.fi.util.function.CheckedConsumer;
import org.jooq.lambda.fi.util.function.CheckedFunction;

@Singleton
public class DBbean {
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

	@Inject
	private DataSource datasource;

	public Connection conn() {
		Connection conn = null;
		try {
			conn = datasource.getConnection();
			threadLocal.set(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Connection getConnection() {
		return threadLocal.get();
	}

	public void close() {
		try {
			getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void WithTransaction(CheckedConsumer<Connection> connConsumer) {
		try (Connection conn = conn()) {
			conn.setAutoCommit(false);
			try {
				connConsumer.accept(conn);
				conn.commit();
			} catch (Throwable e) {
				e.printStackTrace();
				if (conn != null) {
					conn.rollback();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public <R> R WithTransactionQuery(
			CheckedFunction<Connection, R> connFunction) {
		try (Connection conn = conn()) {
			conn.setAutoCommit(false);
			try {
				R result = connFunction.apply(conn);
				conn.commit();
				return result;
			} catch (Throwable e) {
				e.printStackTrace();
				if (conn != null) {
					conn.rollback();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public <R> R withConnection(CheckedFunction<Connection, R> connFunction) {
		try (Connection conn = conn();) {
			return connFunction.apply(conn);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param sql
	 * @param pstConsumer
	 * @param rsConsumer
	 */
	public <T, R> void executeQuery(String sql,
			CheckedConsumer<PreparedStatement> pstConsumer,
			CheckedConsumer<ResultSet> rsConsumer) {
		Connection conn = getConnection();
		try (PreparedStatement pst = conn.prepareStatement(sql);) {
			if (pstConsumer != null) {
				pstConsumer.accept(pst);
			}
			try (ResultSet rs = pst.executeQuery();) {
				while (rs.next()) {
					rsConsumer.accept(rs);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * if you executeQuery, close ResultSet!
	 * 
	 * @param sql
	 * @param pstFunction
	 * @return
	 */
	public <R> R executePst(String sql,
			CheckedFunction<PreparedStatement, R> pstFunction) {
		Connection conn = getConnection();
		try (PreparedStatement pst = conn.prepareStatement(sql);) {
			return pstFunction.apply(pst);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param exeSQL
	 * @param whereClause
	 * @param objs
	 */
	public void executeUpdateWithParam(String exeSQL, Object... objs) {
		Connection con = getConnection();

		try (PreparedStatement pst = con.prepareStatement(exeSQL);) {
			for (int i = 0; i < objs.length; i++) {
				DBUtils.getPstSetterMethod(objs.getClass()).invoke(pst, i + 1,
						objs[i]);
			}
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <R> R executeQueryWithParam(String exeSQL,
			CheckedFunction<PreparedStatement, R> rsFunction, Object... objs) {
		Connection con = getConnection();
		try (PreparedStatement pst = con.prepareStatement(exeSQL)) {
			for (int i = 0; i < objs.length; i++) {
				DBUtils.getPstSetterMethod(objs.getClass()).invoke(pst, i + 1,
						objs[i]);
			}
			return rsFunction.apply(pst);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
