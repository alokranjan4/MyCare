/**
 * 
 */
package com.ibm.indo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.ibm.services.vo.ServiceType;

/**
 * @author Adeeb
 *
 */
public class ServiceTypeSorter  implements Comparator<ServiceType>{
	protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
		@Override
		public int compare(ServiceType o1, ServiceType o2) {
			Date d1 = IndoUtil.parseDate(o1.getStartDate(), "dd.MM.yyyy");
			Date d2 = IndoUtil.parseDate(o2.getStartDate(), "dd.MM.yyyy");
			return DATE_FORMAT.format(d1).compareTo(DATE_FORMAT.format(d2));
		}
}
