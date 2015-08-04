package pub;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Resource {
	@XmlAttribute
	public String type;

	@XmlAttribute
	public String username;

	@XmlAttribute
	public String password;

	@XmlAttribute
	public String factory;

	@XmlAttribute
	public String driverClassName;

	@XmlAttribute
	public String url;

	@XmlAttribute
	public String maxActive;

	@XmlAttribute
	public String maxIdle;

	@XmlAttribute
	public String maxWait;
}
