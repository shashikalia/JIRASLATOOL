package com.pojo;

import java.util.List;

public class JiraIssueChangeHistory {

	public String issueId;
	public String status;
	public String createdOn;
	public String priority;
	public String issueDescription;
	public String assignee;
	public String issueType;
	public String detectionPhase;
	
	public List<ChangeHistory> changeHistory;
	
}
