package org.together.pub.db;

import javax.inject.Inject;
import javax.inject.Singleton;

//ORM的入口
@Singleton
public class DBStore extends DBStoreHelper {



	@Inject
	public DBStore(DBbean db) {
		super(db);
	}
}
