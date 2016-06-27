/**
 * 
 */
package com.ibm.indo.util;

import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.ibm.indo.controller.OrionJob;

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
		scheduler = new StdSchedulerFactory().getScheduler("OrionScheduler");
		if(null!=scheduler){
			scheduler.shutdown(false);
			log.info("*****************Shutdown Orion Scheduler*************");
			} 
		}catch (SchedulerException e) {
			e.printStackTrace();
		}
		try {
		scheduler = new StdSchedulerFactory().getScheduler();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				   String jobName = jobKey.getName();
				   String jobGroup = jobKey.getGroup();
				   //get job's trigger
				   List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				   Date nextFireTime = triggers.get(0).getNextFireTime(); 
				   System.out.println("[jobName] : " + jobName + " [groupName] : " + jobGroup + " - " + nextFireTime);
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
 
        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("Orion.contextInitialized()");
		InputStream businessIS = null;
		try {
			servletContext = arg0.getServletContext();
			log.debug("Orion.contextInitialized() servletContext " + servletContext);	
			businessIS =this.getClass().getClassLoader().getResourceAsStream("indo.properties");
					//servletContext.getResource(configDir+"indo.properties").openStream();
			PROPERTY.load(businessIS);
			PropertyConfigurator.configure(PROPERTY);
			log.info("Orion.contextInitialized() properties file loaded Successfully.");
			if (null != businessIS) {
				businessIS.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Orion.contextInitialized - e " +IndoUtil.getFullLog(e));
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
		            		SchedulerProp.setProperty("org.quartz.scheduler.instanceName", "OrionScheduler");
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
							JobDetail job = JobBuilder.newJob(OrionJob.class)
					        		.withIdentity("OrionJob", "OrionAction")
					        		.build();
							Trigger trigger = newTrigger()
								    .withIdentity("OrionTrigger", "OrionAction")
								    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * * * ?"))
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