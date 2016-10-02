package com.JsonParser;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.main.Main;
import com.pojo.ChangeHistory;
import com.pojo.JiraIssueChangeHistory;

public class JsonParser {

	private String stringToParse;
	private String projectName;
	private final String ISSUES = "issues";
	private final String STATUS = "status";
	private final String VALUE = "value";
	private final String KEY = "key";
	private final String FIELDS = "fields";
	private final String CREATED = "created";
	private final String NAME = "name";
	private final String CHANGELOG = "changelog";
	private final String TOTAL = "total";
	private final String HISTORIES = "histories";
	private final String ITEMS = "items";
	private final String FIELD = "field";
	private final String FROMSTIRNG = "fromString";
	private final String TOSTRING = "toString";
	private final String DESCRIPTION = "summary";
	private final String PRIORITY = "priority";
	private final String ASSIGNEE = "assignee";
	private final String ISSUETYPE= "issuetype";
	private final String DETECTION_PHASE= "customfield_11202";
	
	
	/*
	 * private final String FROM = "from"; private final String TO = "to";
	 */

	private static Logger logger = Logger.getLogger(JsonParser.class);
	public ArrayList<JiraIssueChangeHistory> issueList;

	public JsonParser(String parseStr, String project) {
		stringToParse = parseStr;
		projectName = project;
	}

	public int parseJsonString() {
		/* check if list already exist for continued loop */
		if(Main.projectIssueMap.containsKey(projectName)) {
			issueList = Main.projectIssueMap.get(projectName);
		} else {
			issueList = new ArrayList<JiraIssueChangeHistory>();
		}
		int total = 0;
		try {
			JSONObject jsonObj = new JSONObject(stringToParse);
			JSONArray issueArray = jsonObj.getJSONArray(ISSUES);

			if (issueArray.length() != 0) {
				total = issueArray.length();
				for (int issueCount = 0; issueCount < total; issueCount++) {
					JiraIssueChangeHistory issue = new JiraIssueChangeHistory();
					try {

						JSONObject jsonIssue = issueArray
								.getJSONObject(issueCount);
						issue.issueId = jsonIssue.getString(KEY);
						/* parsing for fields in issue start */
						JSONObject fields = jsonIssue.getJSONObject(FIELDS);
						issue.createdOn = fields.getString(CREATED);
						issue.issueDescription = fields.getString(DESCRIPTION);
						
						JSONObject priority = fields.getJSONObject(PRIORITY);
						issue.priority = priority.getString(NAME);
						 
						JSONObject assignee = null;
						try {
							assignee = fields.getJSONObject(ASSIGNEE);
						} catch(JSONException e) {
							logger.debug("No Assignee for Issue "+issue.issueId);
						}
						if(assignee != null) {
							issue.assignee = assignee.getString(NAME);
						}
						
						JSONObject issueType = fields.getJSONObject(ISSUETYPE);
						issue.issueType = issueType.getString(NAME);
						
						JSONObject detectionPhase = null;
						try {
							detectionPhase = fields.getJSONObject(DETECTION_PHASE);
						} catch(JSONException e) {
							logger.debug("No Detection phase for Issue "+issue.issueId);
						}
						
						if(detectionPhase != null) {
							issue.detectionPhase = detectionPhase.getString(VALUE);
						}
						
						JSONObject status = fields.getJSONObject(STATUS);
						issue.status = status.getString(NAME);
						/* parsing for fields in issue ends */

						/* parsing for changelog in issue start */
						JSONObject changelog = jsonIssue
								.getJSONObject(CHANGELOG);
						int historiesCount = changelog.getInt(TOTAL);
						JSONArray historyArray = changelog
								.getJSONArray(HISTORIES);

						if (historiesCount == 0) {
							issue.changeHistory = null;
						} else {
							ArrayList<ChangeHistory> changeHistoryList = new ArrayList<ChangeHistory>();

							for (int indexHistory = 0; indexHistory < historiesCount; indexHistory++) {
								JSONObject history = historyArray
										.getJSONObject(indexHistory);
								JSONArray itemsArray = history
										.getJSONArray(ITEMS);
								for (int indexItems = 0; indexItems < itemsArray
										.length(); indexItems++) {
									JSONObject items = itemsArray
											.getJSONObject(indexItems);
									String fieldType = items.getString(FIELD);

									if (fieldType.equalsIgnoreCase("status")) {
										ChangeHistory changeHistory = new ChangeHistory();
										changeHistory.oldState = items
												.getString(FROMSTIRNG);
										changeHistory.newState = items
												.getString(TOSTRING);
										changeHistory.changeDate = history
												.getString(CREATED);
										changeHistoryList.add(changeHistory);
									}
								}
							}
							if (!changeHistoryList.isEmpty()) {
								issue.changeHistory = changeHistoryList;
							}
						}
						/* parsing for changelog in issue ends */
					} catch (JSONException e) {
						logger.error(e, e);
					}
					issueList.add(issue);
				}
				/* Adding issue to main */
				Main.projectIssueMap.put(projectName, issueList);
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}
		return total;
	}

}
