package com.JsonParser;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import com.properties.ReadProperties;
import com.utils.Utility;


public class CreateJsonRequest {

	public static JsonBuilderFactory factory = Json.createBuilderFactory(null);
	
	public static  String getRequest(String project, int index) {
		
		Utility util = new Utility();
		JsonObjectBuilder issue = factory.createObjectBuilder();
		issue.add("jql", util.GetJqlQuery(project));
		issue.add("startAt", index);
		issue.add("maxResults", ReadProperties.prop.maxResults);
		
		JsonArrayBuilder fields = factory.createArrayBuilder();
		fields.add("id");
		fields.add("created");
		fields.add("status");
		fields.add("summary");
		fields.add("priority");
		fields.add("assignee");
		fields.add("issuetype");
		fields.add("customfield_11202");
		issue.add("fields", fields);
		
		JsonArrayBuilder expand = factory.createArrayBuilder();
		expand.add("changelog");
		
		issue.add("expand", expand);
		
		return issue.build().toString();
		
	}
}
