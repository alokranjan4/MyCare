/**
 * 
 */
package com.ibm.ijoin.serviceImpl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ibm.ijoin.service.HttpConnService;
import com.ibm.ijoin.util.IdleConnectionMonitorThread;
import com.ibm.ijoin.util.IndoUtil;

/**
 * @author Adeeb
 *
 */
@Service
public class HttpConnServiceImpl implements HttpConnService{
	private static Logger log = Logger.getLogger("ijoinLogger");
	@Override
	public CloseableHttpClient getHttpClient() {
		log.info("HttpConnServiceImpl.getHttpClient() - START");
		try{
			SSLContext context = null;
	    	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
	
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };
	
			try {
				context = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
			try {
				context.init(null, trustAllCerts, new java.security.SecureRandom());
			} catch (KeyManagementException e1) {
				e1.printStackTrace();
			}
			
			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					  .register("https", sslConnectionFactory)
					  .register("http", new PlainConnectionSocketFactory()).build();
			
		    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			 // Increase max total connection to 200
			 cm.setMaxTotal(50);
			 // Increase default max connection per route to 20
			 cm.setDefaultMaxPerRoute(20);
			 RequestConfig defaultRequestConfig = RequestConfig.custom()
					    .setSocketTimeout(8000)
					    .setConnectTimeout(8000)
					    .setConnectionRequestTimeout(8000)
					    .build();
			 CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setConnectionManagerShared(true)
					 .setDefaultRequestConfig(defaultRequestConfig).build();
			 IdleConnectionMonitorThread staleMonitor = new IdleConnectionMonitorThread(cm);
			 staleMonitor.start();
			 staleMonitor.join(1000);
		    return httpClient;
		}catch(Exception ce){
			log.error("HttpConnServiceImpl.getHttpClient() ce "+IndoUtil.getFullLog(ce));
		}finally{
			log.info("HttpConnServiceImpl.getHttpClient() - END");
		}
		return null;
	}
	
}
