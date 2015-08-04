package org.together.home.money.family;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.together.pub.db.DBStore;
import org.together.pub.db.DBbean;
import org.together.pub.util.MD5Utils;
import org.together.web.httpserver.BaseService;

public class FamilyService extends BaseService {

	@Inject
	DBbean db;

	@Inject
	DBStore dbStore;

	@Inject
	private FamilyDAO dao;

	// 添加收入
	public byte[] addIncome(HttpServletRequest req) {
		return buildResult(SUCCESS, "");
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
}
