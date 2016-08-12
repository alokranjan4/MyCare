package com.ibm.ijoin.util;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;

import org.apache.log4j.Logger;



public class EmailService {
	private static EmailService mailService = null;
	private static Logger logger = Logger.getLogger("ijoinLogger");

	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties props = confProp.getConfigSingletonObject();

	private EmailService() throws AddressException, NamingException, Exception {
		logger.debug("flag is   ||" + props);
		/*		if (props == null) {
			props = new Properties();
			logger.info("before fetching property file");
			props.put("mail.smtp.host", props.getProperty("EMAILSMTPHOST"));
			logger.info("Property file  ||   "+props.getProperty("EMAILSMTPHOST"));
			logger.info("Property file  ||   "+props.getProperty("PORT"));
			props.put("mail.smtp.port", props.getProperty("PORT"));
			props.put("mail.smtp.user", "flase");
			props.put("mail.smtp.password", "false");
		}*/

	}

	public static EmailService getInstance() throws Exception {

		if (mailService == null) {
			mailService = new EmailService();
		}
		return mailService;
	}

	public static boolean sendEmailWithHtmlAttachment(String recipients,
			String subject, String message, String from, String fileName,
			String filePath) throws MessagingException, AddressException,
			NamingException, Exception {
		boolean mailSent = false;
		try {
			Properties props1 = new Properties();
			props1.put("mail.smtp.host", "smtpgw.indosatooredoo.com");
			props1.put("mail.smtp.port", "25");
		//	props1.put("mail.smtp.user", false);
		//	props1.put("mail.smtp.password", false);
			props1.put("mail.transport.protocol","smtp"); 
			Session session = Session.getDefaultInstance(props1);
			session.setDebug(true);
			Message msg = new MimeMessage(session);
			logger.info("Msg is  || " + msg);
			InternetAddress addressFrom = new InternetAddress(from);
			logger.debug("addressFrom is  || " + addressFrom);
			logger.debug("Msg is *** || " + msg);
			addressFrom.setPersonal("INDOSAT");
			msg.setText(message);
			msg.setFrom(addressFrom);
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
			msg.setSubject(subject);
			Transport.send(msg);
			mailSent = true;
			return mailSent;
		} catch (Exception e) {
			logger.error("Eception Occured  In EmailMagr ||" +IndoUtil.getFullLog(e));
		}
		return mailSent;

	}

}
