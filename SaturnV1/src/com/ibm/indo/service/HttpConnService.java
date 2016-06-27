/**
 * 
 */
package com.ibm.indo.service;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author Adeeb
 *
 */
public interface HttpConnService {
	CloseableHttpClient getHttpClient();
}
