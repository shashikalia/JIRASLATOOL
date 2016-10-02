package com.sla;

public class SLAData {

	public String project;
	public String issueId;
	public String IssueType;
	public String priority;
	public String issueDescription;
	public String assignee;
	public String status;
	public String firstResponseOk;
	public String fixResponseOk;
	public String remainingTimeFixResponse;
	public String timeSpentFixResponse;
	public String remainingTimeFirstResponse;
	public Integer holidays;
	public String HoldTime;
	public String detectionPhase;
	
	public String toString() {
		
		return project+","+issueId+","+priority+","+IssueType+","+detectionPhase+","+issueDescription+
				","+assignee+","+status+","+remainingTimeFirstResponse+","+remainingTimeFixResponse+
				","+timeSpentFixResponse+","+HoldTime+","+holidays+","+firstResponseOk+","+fixResponseOk;
	}
}
