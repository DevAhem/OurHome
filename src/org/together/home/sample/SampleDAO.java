package org.together.home.sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.together.pub.db.DBUtils;
import org.together.pub.db.DBbean;

public class SampleDAO {
	@Inject
	DBbean db;

	public String queryStyle0(Connection conn) {
		try (PreparedStatement pst = conn
				.prepareStatement("select sysdate from dual");
				ResultSet rs = pst.executeQuery();) {
			rs.next();
			return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public List<String> queryStyle1() {
		// style 1
		// modify list
		List<String> list = new ArrayList<>();

		// only query
		db.executeQuery(

		"select * from t_cxl_xml_type where type_id = ?",

		pst -> {
			pst.setString(1, "2");
			pst.setString(2, "1000");
		},

		rs -> {
			list.add(rs.getString("TYPE_ID"));
			list.add(rs.getString("TYPE_NAME"));
		});
		return list;
	}

	public List<String> queryStyle2() {
		// style 2
		// query by pst and close rs
		List<String> list = new ArrayList<>();

		db.executeQueryWithParam(

		"select * from t_cxl_xml_type where type_id = ?",

		pst -> DBUtils.queryForMapList(pst),

		"2", "1000");
		return list;
	}

	public Map<String, Object> queryStyle3() {
		// style 3
		// execute and return by pst
		// close Result by DBUtils
		return db.executePst(

		"select * from t_cxl_xml_type where type_id = ?",

		pst -> {
			pst.setString(1, "2");
			pst.setString(2, "1000");
			return DBUtils.queryForMapFirst(pst);
		});
	}

	public List<String> queryStyle4() {
		// style 4
		// close Result ourselves
		return db.executePst(

		"select * from t_cxl_xml_type where type_id = ?",

		pst -> {
			pst.setString(1, "2");
			List<String> list2 = new ArrayList<>();
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					list2.add(rs.getString("TYPE_ID"));
					list2.add(rs.getString("TYPE_NAME"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return list2;
		});

	}

	public void modifyStyle1() {
		// sytle 1
		db.executeUpdateWithParam(

		"update table_1 set value_1 = ? where id = ?",

		"1", "1000");
	}

	public void modifyStyle2() {
		// style 2
		db.executePst("update table_1 set value_1 = ? where id = ?",

		pst -> {
			pst.setString(1, "1");
			pst.setString(2, "1000");
			return pst.executeUpdate();
		});
	}

	public void modifyStyle3() {
		// style 3
		// similar to ibatis
		// TODO
	}

}
