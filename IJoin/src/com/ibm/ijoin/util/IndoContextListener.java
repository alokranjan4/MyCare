/**
 * 
 */
package com.ibm.ijoin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/** 
 * @author Aadam
 * 
 */
public class IndoContextListener implements ServletContextListener{
	
	private static ServletContext servletContext = null;
	private static Logger log = Logger.getLogger("ijoinLogger");
	public static Properties		PROPERTY	= new Properties();
	public static Properties		LANGPROPERTY	= new Properties();
	Scheduler scheduler = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("Saturn.contextDestroyed()- ServletContextListener destroyed");
		if(null!=scheduler){
			try {
				scheduler.shutdown(false);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("Saturn.contextInitialized()- ServletContextListener started");
		InputStream businessIS = null;
		try {
			servletContext = arg0.getServletContext();
			log.debug("Saturn.contextInitialized() servletContext " + servletContext);	
			businessIS =this.getClass().getClassLoader().getResourceAsStream("indo.properties");
					//servletContext.getResource(configDir+"indo.properties").openStream();
			PROPERTY.load(businessIS);
			PropertyConfigurator.configure(PROPERTY);
			log.info("Saturn.contextInitialized() properties file loaded Successfully.");
			businessIS =this.getClass().getClassLoader().getResourceAsStream("saturn.properties");
			LANGPROPERTY.load(businessIS);
			PropertyConfigurator.configure(LANGPROPERTY);
			log.info("Saturn.contextInitialized() lang properties file loaded Successfully.");
			if (null != businessIS) {
				businessIS.close();
			}
		} catch (Exception e) {
			log.error("Saturn.contextInitialized - e " +IndoUtil.getFullLog(e));
		}finally{
			if (null != businessIS) {
				try {businessIS.close();} catch (IOException e) {}
			}
		}
		/*new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
					try {
						JobDetail job = JobBuilder.newJob(OrderSatusJob.class)
				        		.withIdentity("job1", "group1")
				        		.build();
						Trigger trigger = newTrigger()
							    .withIdentity("trigger1", "group1")
							    .startNow()
							   // .withSchedule(SimpleScheduleBuilder.simpleSchedule()
							   // .withIntervalInMinutes(5).repeatForever())
							    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 1-7 * * ?"))
							    .build();
					    scheduler = new StdSchedulerFactory().getScheduler();
						scheduler.start();    	
						scheduler.scheduleJob(job, trigger);
					} catch (Exception e) {
						log.error("AfterSpringLoad.onApplicationEvent - scheduler exception " +IndoUtil.getFullLog(e));
					}
		       }
		      },5000 
		);*/
	}
	
	public static ServletContext getServletContext(){
		return servletContext;
	}
}