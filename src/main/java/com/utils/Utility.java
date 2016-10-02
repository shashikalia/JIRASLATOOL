package com.utils;

import com.properties.ReadProperties;

public class Utility {

	public String GetJqlQuery(String Project) {
		boolean firstRequest = true; 
		StringBuilder sb = new StringBuilder();
		sb.append("Project="+Project+" AND (");
		for(String state: ReadProperties.prop.statesList) {
			if(firstRequest) {
				sb.append(" Status="+state);
				firstRequest=false;
			} else {
				sb.append(" OR Status=\""+state+"\"");
			}
		}
		sb.append(")");
		
		return sb.toString();
	}
	
}
