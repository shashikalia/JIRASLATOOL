package com.pojo;

import java.util.ArrayList;

public class IssueRequest {

	public String jql;
	public int startAt;
	public int maxResults;
	public ArrayList<String> fields;
	public ArrayList<String> expand;
	
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}
	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public ArrayList<String> getFields() {
		return fields;
	}
	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
	public ArrayList<String> getExpand() {
		return expand;
	}
	public void setExpand(ArrayList<String> expand) {
		this.expand = expand;
	}
}
