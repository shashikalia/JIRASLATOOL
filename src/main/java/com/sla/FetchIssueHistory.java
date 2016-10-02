package com.sla;

import com.JsonParser.CreateJsonRequest;
import com.JsonParser.JsonParser;
import com.client.RestClient;
import com.properties.ReadProperties;
import com.sun.jersey.api.client.ClientResponse;

public class FetchIssueHistory {

	public static void GetParseIssues(RestClient client) {

		for (String project : ReadProperties.prop.projectList) {
			int count = 0;

			do {
				String input = CreateJsonRequest.getRequest(project, count);

				ClientResponse response = client.sendPostRequest(input);

				if (response.getStatus() == 200) {
					String responseJira = response.getEntity(String.class);
					//System.out.println("Good Response"+responseJira);
					JsonParser parser = new JsonParser(responseJira, project);

					int countOfIssue = parser.parseJsonString();

					if (countOfIssue < ReadProperties.prop.maxResults) {
						break;
					} else {
						count += countOfIssue;
					}

				} else {
					//System.out.println("Error Response" + response.getStatus());
					//System.out.println(response.getEntity(String.class));
					break;
				}
			} while (true);
		}
	}
}
