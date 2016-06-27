/**
 * 
 */
package com.ibm.ijoin.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Adeeb
 *
 */
public class DateComparator implements Comparator<Date> {
	 protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy h:mm a");

	    public int compare(Date d1, Date d2) {
	        return DATE_FORMAT.format(d1).compareTo(DATE_FORMAT.format(d2));
	    }	
}
