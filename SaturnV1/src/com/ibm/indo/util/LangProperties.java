/**
 * 
 */
package com.ibm.indo.util;

import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;





/**
 * @author IBM_ADMIN
 *
 */
public class LangProperties implements Serializable {
	 static final long serialVersionUID = 10000000001L;
	 private static Logger log = Logger.getLogger("saturnLoggerV1");
	 private static Properties configProp   =   null;
	 private static  volatile LangProperties  uniqueInstance;
	 private LangProperties(){ }
	    /**
		 * @Purpose to get  Instantance of ErrorIdeaSingleTon
	     * @Type    Method
		 * @return ErrorIdeaSingleTon
		 */
	 public static   LangProperties  getInstance()
	    {
	        if (uniqueInstance ==null )
	        {
	            synchronized(LangProperties.class)
	            {
	                if (uniqueInstance ==null )
	                {
	                    uniqueInstance=new LangProperties();
	                }
	            }
	        }
	        return uniqueInstance ;
	    }
	    
	    /**
		 * @Purpose to get  Instantance of ErrorIdeaSingleTon
	     * @Type    Method
		 * @return ErrorIdeaSingleTon
		 */
	    public Properties getConfigSingletonObject(){
	    	if(configProp!=null)
	    		return configProp;
	    	try {
	    		configProp = IndoContextListener.LANGPROPERTY;
	    		log.info("Properties loaded in configProperties.");
			} catch (Exception e) {
				log.info("Exception from PROPERTIES. Properties is not found."+e.getMessage());
			}
	        return configProp;
	    }

	    public static void main(String[] args) {
	    	LangProperties confProp=LangProperties.getInstance();
	        Properties prop = confProp.getConfigSingletonObject();
	    	log.info(prop.getProperty("Testconfig"));
		}
}
