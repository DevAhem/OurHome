package org.together.home.sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import org.together.pub.db.DBbean;

public class SampleService {
	@Inject
	DBbean db;

	@Inject
	private SampleDAO dao;

	public String scan(ServletRequest req) {
		// modify style 1
		db.WithTransaction(conn -> dao.modifyStyle1());

		// query style 0
		String result = db.withConnection(conn -> dao.queryStyle0(conn));

		// query style 1
		List<String> list1 = db.withConnection(conn -> dao.queryStyle1());
		System.out.println(list1);

		// query style 2
		List<String> list2 = db.withConnection(conn -> dao.queryStyle2());
		System.out.println(list2);

		// query style 3
		Map<String, Object> map3 = db.withConnection(conn -> dao.queryStyle3());
		System.out.println(map3);

		return result;
	}

	public List<String> queryByCode(ServletRequest req) {
		// Connection不应该被close，在with方法会关闭

		// 直接使用Connection，不推荐，应该改成使用conn作为参数的DAO的方法，看更下面的例子
		db.withConnection(conn -> {
			try (PreparedStatement pst = conn
					.prepareStatement("select sysdate from dual");
					ResultSet rs = pst.executeQuery();) {
				rs.next();
				return rs.getString(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		});

		// 推荐
		db.withConnection(conn -> dao.queryStyle0(conn));

		// 使用DAO做修改，并且有返回值，但是这种做法不推荐，应该尽量减少使用事务的时间，看更下面的例子
		db.WithTransactionQuery(conn -> {
			dao.modifyStyle1();
			return dao.queryStyle1();
		});

		// 推荐
		// 使用DAO做修改
		// 再使用DAO做查询
		db.WithTransaction(conn -> {
			dao.modifyStyle1();
		});
		List<String> list1 = db.withConnection(conn -> dao.queryStyle1());

		return list1;
	}
}
