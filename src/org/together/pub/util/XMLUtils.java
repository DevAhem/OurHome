package org.together.pub.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLUtils {
	public static void ObjectToXML(Object obj, String filePath) {
		try {
				JAXBContext context = JAXBContext.newInstance(obj.getClass());
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
				marshaller.marshal(obj, new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T XMLToObject(Class<T> c, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				JAXBContext context = JAXBContext.newInstance(c);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				return (T) unmarshaller.unmarshal(file);
			}else {
				return c.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T XMLToObject(Class<T> c, InputStream is) {
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
