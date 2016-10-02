package com.properties;

import java.util.List;
import java.util.Map;

public class JiraProperties {

	public String jiraUrl;
	public List<String> projectList;
	public List<String> statesList;
	public Map<String,Long> firstResponse;
	public Map<String,Long> fixResponse;
	public String UserName;
	public String Password;
	public int maxResults;
}
