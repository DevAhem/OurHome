package org.together.home.money.person;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.together.pub.db.DBStore;
import org.together.pub.db.DBbean;
import org.together.pub.po.PersonIncomePO;
import org.together.pub.util.BeanUtils;
import org.together.web.httpserver.BaseService;

import com.alibaba.fastjson.JSON;

public class PersonService extends BaseService {

	@Inject
	DBbean db;

	@Inject
	DBStore dbStore;

	@Inject
	private PersonDAO dao;

	// 添加收入
	public byte[] addIncome(HttpServletRequest req) {
		HashMap<String, String> map = new HashMap<>();
		map.put("user_name", "admin");
		map.put("password", "123456");
		String json = JSON.toJSONString(map);
		// String json = req.getParameter(JSON_STR);
		PersonIncomePO income = JSON.parseObject(json, PersonIncomePO.class);
		income.pid = BeanUtils.getUuid();
		db.withConnection((conn) -> {
			dbStore.t_person_income.insert(income);
			return "";
		});
		return buildResult(SUCCESS, "添加成功");
	}

	// 删除收入
	public byte[] removeIncome(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
	}

	// 修改收入
	public byte[] modifyIncome(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
	}

	// 查询收入
	public byte[] queryIncome(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
	}

	// 添加支出
	public byte[] addExpend(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
	}

	// 删除支出
	public byte[] removeExpend(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
	}

	// 修改支出
	public byte[] modifyExpend(HttpServletRequest req) {
		return buildResult(SUCCESS, "");

	}

	// 查询支出
	public byte[] queryExpend(HttpServletRequest req) {
		return buildResult(SUCCESS, "");

	}

	public void test() {
		addIncome(null);
	}
}
