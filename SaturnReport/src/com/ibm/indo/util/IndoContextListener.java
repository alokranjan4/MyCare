/**
 * 
 */
package com.ibm.indo.util;

import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ibm.indo.serviceImpl.ReportJob;

/** 
 * @author Aadam
 * 
 */
public class IndoContextListener implements ServletContextListener{
	
	private static ServletContext servletContext = null;
	private static Logger log = Logger.getLogger("saturnLogger");
	public static Properties		PROPERTY	= new Properties();
	public static Properties		SchedulerProp	= new Properties();
	Scheduler scheduler = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			scheduler = new StdSchedulerFactory().getScheduler("SaturnScheduler");
			if(null!=scheduler){
				scheduler.shutdown(false);
				log.info("*****************Shutdown Saturn Scheduler*************");
				} 
			}catch (SchedulerException e) {
				e.printStackTrace();
			}
	}
 
        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("SaturnReports.contextInitialized()");
		InputStream businessIS = null;
		try {
			servletContext = arg0.getServletContext();
			log.debug("SaturnReports.contextInitialized() servletContext " + servletContext);	
			businessIS =this.getClass().getClassLoader().getResourceAsStream("indo.properties");
					//servletContext.getResource(configDir+"indo.properties").openStream();
			PROPERTY.load(businessIS);
			PropertyConfigurator.configure(PROPERTY);
			log.info("SaturnReports.contextInitialized() properties file loaded Successfully.");
			if (null != businessIS) {
				businessIS.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SaturnReports.contextInitialized - e " +IndoUtil.getFullLog(e));
		}finally{
			if (null != businessIS) {
				try {businessIS.close();} catch (IOException e) {}
			}
		}
		try{
			new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	try{
		            		SchedulerProp.setProperty("org.quartz.scheduler.instanceName", "SaturnScheduler");
		            		SchedulerProp.setProperty("org.quartz.scheduler.instanceId", "AUTO");
		            		SchedulerProp.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		            		SchedulerProp.setProperty("org.quartz.threadPool.threadCount", "5");
		            		StdSchedulerFactory sf = new StdSchedulerFactory();
		            		sf.initialize(SchedulerProp);
			            	scheduler = sf.getScheduler();
			            	if(null!=scheduler && scheduler.isStarted()){
			            		scheduler.shutdown(false);
			            	}
							scheduler.start();   
		            	} catch (Exception e) {
							log.error("ReportsContextListener.onApplicationEvent - scheduler0 exception " +IndoUtil.getFullLog(e));
						}		            	
						try {
							JobDetail job = JobBuilder.newJob(ReportJob.class)
					        		.withIdentity("reportjob", "action1")
					        		.build();
							Trigger trigger = newTrigger()
								    .withIdentity("trigger1", "action1")
								    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 07 * * ?"))
								    .build();
							scheduler.scheduleJob(job, trigger);
		            	} catch (Exception e) {
		            		log.error("ReportsContextListener.onApplicationEvent - scheduler1 exception " +IndoUtil.getFullLog(e));
		            	}
		            }
		        },5000 
			);
		}catch(Exception e){
			log.error("ReportsContextListener.getServletContext()"+IndoUtil.getFullLog(e));
		}
	}
	
	public static ServletContext getServletContext(){
		
		return servletContext;
	}
}