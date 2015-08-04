package pub;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Context")
public class Context {
	public String WatchedResource;
	public List<Resource> Resource = new ArrayList<Resource>();
}
