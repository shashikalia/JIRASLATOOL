package com.sla;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class SLAUtils {

	public static HashSet<String> SLAState = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("Open");
			add("Reopened");
			add("In Progress");
			add("Resolved");
		}
	};
	
	public static ArrayList<String> ReportHeading = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("Project");
			add("Issue Id");
			add("Priority");
			add("Issue Type");
			add("Detection Phase");
			add("Summary");
			add("Assignee");
			add("Status");
			add("First Response Remaining Time");
			add("Fix Response Remaining Time");
			add("Fix Response Time Spent");
			add("Hold Time");
			add("Holidays");
			add("FirstResponse");
			add("FixResponse");
		}
	};
	
	public static String convertLongToStringDateFormat(long timeToConvert, boolean checkNegative) {
		if(checkNegative == true && timeToConvert <=0) {
			return "Expired";
		} else if(checkNegative == false && timeToConvert ==0) {
			return "0Day";
		}
		timeToConvert /= 1000;
		int seconds = (int) (timeToConvert%60);timeToConvert /= 60;
		int minutes = (int) (timeToConvert%60);timeToConvert /= 60;
		int hour = (int) (timeToConvert%24);timeToConvert /= 24;
		
		int days = (int) (timeToConvert);

		StringBuilder sb = new StringBuilder();
		if(days != 0) {
			sb.append(days+"Day ");
		}
		if(hour != 0) {
			sb.append(hour+"Hour ");
		}
		if(minutes!=0) {
			sb.append(minutes+"Min ");
		} else if(seconds>0) {
			sb.append("1Min ");
		}
		
		return sb.toString();
	}
	
	public static long convertStringDateToLong(String dateToConvert) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		try {
			Date date = df.parse(dateToConvert);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		return 0;
	}
	
}
