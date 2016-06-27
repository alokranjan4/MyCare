/**
 * 
 */
package com.ibm.indo.serviceImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.indo.service.LDAPService;
import com.ibm.indo.service.XMLService;
import com.ibm.indo.util.IndoServiceProperties;
import com.ibm.indo.util.IndoUtil;
import com.ibm.indo.util.IndoXMLParseUtil;
import com.ibm.indo.util.LDAPUtil;
import com.indosat.eai.catalist.mobileagent.CorporateProfileInputType;
import com.indosat.eai.catalist.mobileagent.CorporateProfileOutputType;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentService;
import com.isat.catalist.eai.mobileagent.ws.MobileAgentServiceServiceLocator;

/**
 * @author Adeeb
 *
 */
@Service
public class LDAPServiceImpl implements LDAPService{
	@Autowired
	LDAPUtil ldapUtil;
	@Autowired
	private XMLService xmlService;
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	IndoServiceProperties confProp=IndoServiceProperties.getInstance();
    Properties prop = confProp.getConfigSingletonObject();
	
	@Override
	public Map<String, Object> fetchUserDetails(String key, String value) {
		return null;
		/*
		List list = ldapUtil.getPersonNamesByLastName("");
		log.info("LDAPServiceImpl.fetchUserDetails() list "+list);
		return null;
	*/}
	
	@Override
	public String getUserTypeFromLDAP(String msisdn) {
		log.info("LDAPServiceImpl.getUser() - START");
		String type="";
		Properties env = new Properties();
		env.put( Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, "ldap://192.168.31.14:4032/o=subscribers,dc=vas,dc=indosat,dc=com" );
		env.put( Context.SECURITY_PRINCIPAL, "cn=wssldap,cn=Users,dc=vas,dc=indosat,dc=com" );
		env.put( Context.SECURITY_CREDENTIALS, "wssldappwd" );
		env.put("com.sun.jndi.ldap.read.timeout", "5");
		try {
			msisdn = IndoUtil.prefix62(msisdn);
			String card_type = "";
			// obtain initial directory context using the environment
			DirContext ctx = new InitialDirContext( env );
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(msisdn="+msisdn+"))";
			NamingEnumeration e = ctx.search("", filter, ctls);
			if (e.hasMore()) {
				SearchResult entry = (SearchResult) e.next();
				card_type = entry.getAttributes().get("card_type").get().toString();
				log.info("LDAPServiceImpl.getUser() card_type-"+card_type);
					
				if(card_type.equals("01")){
					type= "prepaid";
				}
				else if(card_type.equals("02")){
					type="postpaid";
				}
			}
			
			log.info("LDAPServiceImpl.getUser() type "+type);
			//log.info( "Retrieved i from directory with value: " + i.getAttributes("substype"));
		} catch ( NameAlreadyBoundException nabe ) {
			log.error("LDAPServiceImpl.getUser() nabe "+nabe);
		} catch ( Exception e ) {
			log.error("LDAPServiceImpl.getUser() ce "+IndoUtil.getFullLog(e));
		}finally{
			log.info("LDAPServiceImpl.getUser() - END");
		}
		return type;
	}

	@Override
	public Map<String, Object> getUser(String msisdn) {
		log.info("LDAPServiceImpl.getUser() - START");
		String userTypefromIcare="";
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			msisdn = IndoUtil.prefix62(msisdn);
			userTypefromIcare=getUserTypeFromLDAP(msisdn);
			log.info("User Type from LDAP  is "+userTypefromIcare);
			if(!userTypefromIcare.contains("paid")){
				log.info("Trying to retrieve data from ICARE");
				userTypefromIcare=fetchUserTypeFromIcare(msisdn);
				log.info("User Type from ICARE  is "+userTypefromIcare);
			}
			map.put("user_type", userTypefromIcare);
			map.put("Status", "SUCCESS");
		} catch ( Exception e ) {
			log.error("LDAPServiceImpl.getUser() ce "+IndoUtil.getFullLog(e));
		}finally{
			log.info("LDAPServiceImpl.getUser() - END");
		}
		return map;
	}
	
	String fetchUserTypeFromIcare(String msisdn){
		msisdn= IndoUtil.prefix62(msisdn);
		String userType="";
		log.info("ICareServiceImpl.customerProfile() - START");
		Map<String,Object> map = new HashMap<String,Object>();
		String url= "http://10.128.81.78:7800/WLI_EAI_PROCESS/ServletEAISyncExt";
		try{
			
			
				String xml="<?xml version=\"1.0\"?><evw:WSSMessage xmlns:evw=\"com/icare/eai/schema/evWSSGetUserProfile\"><evw:WSSGetUserProfileReq><evw:ServiceNumber>"+msisdn+"</evw:ServiceNumber></evw:WSSGetUserProfileReq></evw:WSSMessage>";
				//log.info("ICareServiceImpl.customerProfile() input xml "+xml);
				Map<String, Object>  data = xmlService.getRawXML(xml, url);
			//	log.info("ICareServiceImpl.customerProfile() evWSSGetUserProfile "+data);
				if(IndoUtil.isSuccess(data)){
					String response = (String) data.get("xml");
					response = response.replaceAll("evw:", "");
					Document xmlDoc = Jsoup.parse(response, "", Parser.xmlParser());
					Elements eles = xmlDoc.select("UserProfile");
					List<Map<String, String>> d = IndoXMLParseUtil.getParentChildXML(eles);
					if(null!= d && d.size()>0){
						if(null!=d.get(0).get("CustSegment") && !StringUtils.isEmpty(d.get(0).get("CustSegment").toString())){
							userType=d.get(0).get("CustSegment").toString().toLowerCase();
						}
						
						
					}else{
						//IndoUtil.populateErrorMap(map, "Saturn-700", "Saturn-109",0);
						log.info("ICareServiceImpl.customerProfileCorporate() START fetching profile from catalist for "+msisdn);
						MobileAgentServiceServiceLocator locator= new MobileAgentServiceServiceLocator();
							try {
								log.debug("URL is "+ prop.getProperty("CATALIST_URL"));
								MobileAgentService service = locator.getMobileAgentServiceSoapPort(new URL(prop.getProperty("CATALIST_URL")));
								CorporateProfileInputType inputType= new CorporateProfileInputType();
								inputType.setMsisdn(msisdn);
								CorporateProfileOutputType corpProfile= service.getCorporateProfile(inputType);
								if(null!=corpProfile && null!=corpProfile.getCustomerType()){
									if(corpProfile.getServiceType()!=null){
										log.info("Corporate "+corpProfile.getServiceType().toLowerCase());
										//userType=corpProfile.getServiceType().toLowerCase();
										userType="postpaid";
									}
									
								}
								log.info("ICareServiceImpl.customerProfileCorporate() - corpProfile "+corpProfile);
								
							} catch (MalformedURLException e) {
								
								log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e2"+e);
							} catch (ServiceException e) {
								
								log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e3"+e);
							} catch (RemoteException e) {
								
								log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e4"+e);
							}catch (Exception e) {
								
								log.error("Saturn-9012- ICareServiceImpl.customerProfileCorporate() e4"+e);
							}finally{
								log.info("ICareServiceImpl.customerProfileCorporate() - END");
							}
					}
					
					return userType;
				}
			
		}catch(Exception ce){
			log.error("ICareServiceImpl.customerProfile() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("ICareServiceImpl.customerProfile() - END");
		}
		return userType;
	}
}
