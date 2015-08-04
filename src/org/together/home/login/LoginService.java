package org.together.home.login;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.together.pub.db.DBStore;
import org.together.pub.db.DBbean;
import org.together.pub.util.MD5Utils;
import org.together.web.httpserver.BaseService;

public class LoginService extends BaseService {

	@Inject
	DBbean db;

	@Inject
	DBStore dbStore;

	@Inject
	private LoginDAO dao;

	private final String USER_ID = "userId";
	private final String PASSWORD = "password";

	enum LoginResult {
		SUCCESS,

		FAIL,

		NO_USER
	}

	private LoginResult checkLogin(String userId, String password) {
		String md5Pwd = MD5Utils.getMD5Str(password).toUpperCase();
		String pwd = db.withConnection((conn) -> {
			return dao.getPwd(userId);
		});
		if (pwd == null) {
			return LoginResult.NO_USER;
		} else {
			if (md5Pwd.equals(pwd)) {
				return LoginResult.SUCCESS;
			} else {
				return LoginResult.FAIL;
			}
		}
	}

	public byte[] login(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("登录....");
		/*HttpSession session = req.getSession();
		String userId = req.getParameter(USER_ID);
		String password = req.getParameter(PASSWORD);
		LoginResult res = checkLogin(userId, password);
		if (LoginResult.SUCCESS == LoginResult.valueOf(res.name())) {
			// 设置session
			session.setAttribute("user", userId + System.currentTimeMillis());

			// 设置cookie
			resp.addCookie(new Cookie("userId", userId));

			return buildResult(SUCCESS, res);
		} else {
			return buildResult(FAIL, res);
		}*/
		return buildResult(SUCCESS, "服务器");
	}
}
