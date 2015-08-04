package org.together.pub.po;

import java.sql.Timestamp;

import javax.persistence.Id;

public class UserPO {
	@Id
	public String pid;
	
	public String user_name;
	public String password;
	public String email;
	public String real_name;
	public Timestamp birthday;
	public String id_card;
	public Timestamp last_login_time;
	public String is_online;
	public Timestamp register_time;
	public String telphone;
	public String address;
	public String remark;

}
