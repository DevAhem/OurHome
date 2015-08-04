package org.together.home.money.person;

import java.util.Map;

import javax.inject.Inject;

import org.together.pub.db.DBUtils;
import org.together.pub.db.DBbean;

public class PersonDAO {
	@Inject
	DBbean db;

	/**
	 * 获取用户密码
	 * @param userId
	 * @return
	 */
	public String getPwd(String userId) {
		return db.executePst(

		"SELECT PASSWORD FROM LoginUser l where l.USERID=?",

		pst -> {
			pst.setString(1, userId);
			Map<String, Object> map = DBUtils.queryForMapFirst(pst);
			return (String) map.get("PASSWORD");
		});
	}

}
