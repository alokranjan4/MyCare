/**
 * 
 */
package com.ibm.indo.util;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * @author Adeeb
 *
 */
@ManagedBean(name = "Config", eager = true)
@ApplicationScoped
public class CacheUtil {
	//@Resource(name = "java:jboss/infinispan/container/test")
    private static CacheContainer container;

    private Cache<String, Object> entityCache = null;

    private static CacheUtil configInstance = null;
    private static Logger log = Logger.getLogger("saturnLoggerV1");
    public CacheUtil() {
        configInstance = this;
    }

    @PostConstruct
    public void start() {
    	if(container == null){
    		createInstance();
    	}
        entityCache = container.getCache("entity");
    }

    public static CacheUtil getInstance() {
    	log.info("CacheUtil.getInstance() configInstance "+configInstance);
        if (configInstance == null) {
            createInstance();
        }
        return configInstance;
    }

    public Cache<String, Object> getEntityCache() {
        return entityCache;
    }

    private static void createInstance() {
        configInstance = new CacheUtil();
        if (container == null) {
        	log.info("CacheUtil.createInstance()--- creating instance");
            ConfigurationBuilder confBuilder = new ConfigurationBuilder();
            confBuilder.clustering().cacheMode(CacheMode.LOCAL);
            EmbeddedCacheManager cacheManager = new DefaultCacheManager(confBuilder.build());
            cacheManager.start();
            container = cacheManager;
        }else{
        	log.info("CacheUtil.createInstance()--- not creating instance");
        }
        configInstance.start();
    }
}