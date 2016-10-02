package com.main;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.client.RestClient;
import com.pojo.JiraIssueChangeHistory;
import com.properties.ReadProperties;
import com.report.ExcelReportBuilder;
import com.sla.FetchIssueHistory;
import com.sla.SLAData;
import com.sla.SLAImpl;

public class Main {

	public static Logger logger = null;
	public static Map<String, ArrayList<JiraIssueChangeHistory>> projectIssueMap = new HashMap<String, ArrayList<JiraIssueChangeHistory>>();
	public static ArrayList<Date> nationalHolidays = new ArrayList<Date>();
	
	public static void main(String[] args) throws IOException {

		
		String logfile = Main.class.getResource(ReadProperties.LOG4JFILE)
				.getFile();
		DOMConfigurator.configureAndWatch(logfile,
				ReadProperties.LOG4JWATCHDOGPERIOD);
		logger = Logger.getLogger(Main.class);
		
		logger.info("Starting SLA Tool..");
		
		ReadProperties.loadProperties();

		RestClient client = null;
		try {
			client = new RestClient(ReadProperties.prop.jiraUrl,
					ReadProperties.prop.UserName, ReadProperties.prop.Password);
		} catch (GeneralSecurityException e) {
			logger.error(e,e);
			System.exit(1);
		}

		/* Get Issue History for Listed Projects */
		logger.info("Parsing Rest response received from Jira..");
		FetchIssueHistory.GetParseIssues(client);

		SLAImpl imp = new SLAImpl();
		List<SLAData> slaList = new ArrayList<SLAData>();
		imp.calculateSLA(slaList);
		
		logger.info("Creating Excel file for the issues");
		ExcelReportBuilder reportBuilder = new ExcelReportBuilder();
		reportBuilder.createExcelReport(slaList);
	}
}