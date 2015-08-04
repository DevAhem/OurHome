package org.together.pub.db;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.together.pub.po.PersonIncomePO;
import org.together.pub.po.UserPO;

//ORM的入口
@Singleton
public class DBStore extends DBStoreHelper {

	public DBTable<UserPO, String> t_user;
	public DBTable<PersonIncomePO, String> t_person_income;

	@Inject
	public DBStore(DBbean db) {
		super(db);
	}
}
