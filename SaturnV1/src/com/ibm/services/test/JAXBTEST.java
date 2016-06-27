package com.ibm.services.test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ibm.indo.util.IndoUtil;

public class JAXBTEST {
	 private static String pojo2Xml(Object object, JAXBContext context) throws JAXBException {

	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        StringWriter writer = new StringWriter();
	        marshaller.marshal(object, writer);
	        String xmlStringData = writer.toString();
	        return xmlStringData;
	    }
	 public static void main(String args[]) throws JAXBException{
			JAXBContext jaxbContext = JAXBContext.newInstance(Person.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Person><First-Name>1</First-Name><Last-Name>2</Last-Name><First-Name>3</First-Name><Last-Name>4</Last-Name></Person>";
			StringReader reader = new StringReader(xml);
			List<Person> p = (List<Person>) unmarshaller.unmarshal(reader);
			String d = IndoUtil.convertToJSON(p);
			System.out.println(d);
			/*Person p = new Person();
			p.setFirstName("1");
			p.setLastName("2");
			System.out.println(pojo2Xml(p, jaxbContext));
			String d = IndoUtil.convertToJSON(p);
			System.out.println(d);*/
		}
}
