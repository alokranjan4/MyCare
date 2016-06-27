/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibm.indo.util;

import java.io.Serializable;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Aadam
 */
@Repository
public class LDAPUtil implements Serializable{/*
	
	private static final long serialVersionUID = -6203751104015962909L;
	@Autowired
	LdapTemplate ldapTemplate;
	
	private static Logger log = Logger.getLogger("saturnLoggerV1");
	
	public List<String> getPersonNamesByLastName(String lastName) {
		 log.info("LDAPUtil.getPersonNamesByLastName() START ");
		try{
			LdapQuery query = LdapQueryBuilder.query()
			         .attributes("card_type", "msisdn")
			         .where("objectclass").is("Users")
			         .and("msisdn").is("6281585078806");
				log.info("LDAPUtil.getPersonNamesByLastName() query "+query.toString());
			      return ldapTemplate.search(query,
			         new AttributesMapper<String>() {
			            public String mapFromAttributes(Attributes attrs)throws NamingException {
			            	log.info("LDAPUtil.getPersonNamesByLastName(...) IN");
			               return (String) attrs.get("cn").get();
			            }
			         });
	      }catch(Exception ce){
	    	  log.info("LDAPUtil.getPersonNamesByLastName() e "+IndoUtil.getFullLog(ce));
	      }
		log.info("LDAPUtil.getPersonNamesByLastName() END ");
		return null;
	   }
*/}