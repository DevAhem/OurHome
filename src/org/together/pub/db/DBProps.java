package org.together.pub.db;

import javax.xml.bind.annotation.XmlRootElement;

//机顶盒数据库参数
@XmlRootElement
public class DBProps {
	public String uri;
	public int port;
	public String dbName;
	public String user;
	public String pwd;
	public String driverClassName;
	
	public int initialSize = 5;
	public int MaxActive = 5;
	public int maxWait = 5;
	
	public String validateQuery;
	
	public String jobClassName;
}
