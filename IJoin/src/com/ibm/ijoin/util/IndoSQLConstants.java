package com.ibm.ijoin.util;

public interface IndoSQLConstants {
	String GET_USER		=	"SELECT MSISDN,LASTLOGINDATE,UNSUCCESSFULATTEMPS,CHANGEPASSWORDFLAG,USERID,FIRSTNAME,MIDDLENAME,LASTNAME,ACTIVE,EMPLOYEE_ID,EMAIL_ID,TOKEN,REG_DATE,USER_PASSWORD,USERTYPE,IDCHECKFLAG FROM im2_users WHERE USERID=? or EMAIL_ID=? ";
	String UPDATE_LOGIN	=	"update tbl_agent_info set LASTLOGINDATE=sysdate, UNSUCCESSFULATTEMPS=0, DEVICE_INFO=?, VERSION_INFO=? WHERE USERID=?";
	String FAILED_LOGIN	=	"update TBL_AGENT_INFO set UNSUCCESSFULATTEMPS = UNSUCCESSFULATTEMPS+1, DEVICE_INFO=?, VERSION_INFO=? WHERE USERID=?";
}
