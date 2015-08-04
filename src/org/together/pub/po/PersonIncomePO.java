package org.together.pub.po;

import java.sql.Timestamp;

import javax.persistence.Id;

public class PersonIncomePO {
	@Id
	public String pid;
	public String user_name;
	public Float sum;
	public String type_id;
	public Timestamp income_time;
	public Timestamp system_time;
	public String remark;

}
