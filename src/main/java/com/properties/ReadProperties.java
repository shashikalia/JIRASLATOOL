package com.properties;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.main.Main;

public class ReadProperties {

	private static Logger logger = Logger.getLogger(ReadProperties.class);
	public static final String LOG4JFILE = "/jirasla.log4j.xml";
	public static final String PROPERTIES_FILE= "/SLA_Jira.properties";
	public static final long dayTimeInMillisecond = (24*3600*1000);
	public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	
	public static JiraProperties prop = new JiraProperties();
	
	public static final long LOG4JWATCHDOGPERIOD = 5000l;
	
	public static void loadProperties(){
		try {
			logger.info("Reading Property File");
			prop.projectList = new ArrayList<String>();
			prop.statesList = new ArrayList<String>();
			prop.firstResponse = new HashMap<String, Long>();
			prop.fixResponse = new HashMap<String,Long>();
			
			InputStream in_prop = ReadProperties.class.getResourceAsStream(PROPERTIES_FILE);
			Properties properties = new Properties();
			properties.load(in_prop);
			
			String projects = properties.getProperty("ACTIVE_PROJECTS");
			String[] project=projects.split(",");
			
			for(String pro:project) {
				prop.projectList.add(pro);
			}
			
			String states = properties.getProperty("STATES_FOR_SLA");
			String[] statesL = states.split(",");
			for(String stat:statesL) {
				prop.statesList.add(stat);
			}
			
			String firstresp = properties.getProperty("FIRST_RESPONSE_TIME");
			String[] firstresplist = firstresp.split(",");
			
			if(firstresplist.length == 4) {
				prop.firstResponse.put("Blocker", (Integer.parseInt(firstresplist[0])) * dayTimeInMillisecond);
				prop.firstResponse.put("Critical", (Integer.parseInt(firstresplist[1])) * dayTimeInMillisecond);
				prop.firstResponse.put("Major", (Integer.parseInt(firstresplist[2])) * dayTimeInMillisecond);
				prop.firstResponse.put("Minor", (Integer.parseInt(firstresplist[3])) * dayTimeInMillisecond);
			} else {
				logger.error("Incorrect Property File for Property FIRST_RESPONSE_TIME");
				System.exit(1);
			}
			
			
			String fixresp = properties.getProperty("FIX_RESPONSE_TIME");
			String[] fixresplist = fixresp.split(",");
			
			if(fixresplist.length == 4) {
				prop.fixResponse.put("Blocker", (Integer.parseInt(fixresplist[0])) * dayTimeInMillisecond);
				prop.fixResponse.put("Critical", (Integer.parseInt(fixresplist[1])) * dayTimeInMillisecond);
				prop.fixResponse.put("Major", (Integer.parseInt(fixresplist[2])) * dayTimeInMillisecond);
				prop.fixResponse.put("Minor", (Integer.parseInt(fixresplist[3])) * dayTimeInMillisecond);
			} else {
				logger.error("Incorrect Property File for Property FIX_RESPONSE_TIME");
				System.exit(1);
			}
			
			prop.UserName = properties.getProperty("USERNAME");
			prop.Password = properties.getProperty("PASSWORD");
			prop.jiraUrl = properties.getProperty("JIRA_URL");
			
			String nationalHolidays = properties.getProperty("NATIONAL_HOLIDAYS");
			String[] nationalHolidaysArray = nationalHolidays.split(",");
			
			for(String holiday:nationalHolidaysArray) {
				try {
					Date date = df.parse(holiday);
					Main.nationalHolidays.add(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}		
			}
			
			prop.maxResults = Integer.parseInt(properties.getProperty("MAX_RESULT_PER_REQUEST"));

		} catch (IOException e) {
			logger.error("Error loading properties file." + PROPERTIES_FILE, e);
		}
	}
}
