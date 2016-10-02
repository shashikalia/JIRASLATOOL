package com.sla;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.main.Main;
import com.pojo.ChangeHistory;
import com.pojo.JiraIssueChangeHistory;
import com.properties.ReadProperties;

public class SLAImpl {

	public final long dayTimeInMillisecond = (24*3600*1000);
	public void calculateSLA(List<SLAData> slaList) {
		
		/* Iterate through all the project to calculate SLA */
		for(Entry<String, ArrayList<JiraIssueChangeHistory>> e: Main.projectIssueMap.entrySet()) {
			String project = e.getKey();
			ArrayList<JiraIssueChangeHistory> issueList = e.getValue();
			
			for(JiraIssueChangeHistory issueHistory:issueList) {
				long firstTimeResponse = 0;
				long fixTimeResponse = 0;
				long holdTime = 0;
				boolean stateChanged = false;
				SLAData slaData = new SLAData();
				slaData.project = project;
				populateSLAData(slaData,issueHistory);
			
				String startDate = issueHistory.createdOn;
				String endDate = null;
				
				if(issueHistory.changeHistory != null && !issueHistory.changeHistory.isEmpty()) {
					for (ChangeHistory his : issueHistory.changeHistory) {
						endDate = his.changeDate;
						/* Get Hold time */
						if(his.oldState.equalsIgnoreCase("Hold")) {
							holdTime += getTimeInState(startDate,endDate,slaData, true);
						}
						if (SLAUtils.SLAState.contains(his.oldState)) {
							long time = getTimeInState(startDate, endDate,slaData, false);
							/* Check if State is Open and it is new ticket to get first response time */
							if (his.oldState.equalsIgnoreCase("Open")
									&& startDate
											.equals(issueHistory.createdOn)) {
								firstTimeResponse = time;
								stateChanged= true;
							}
							fixTimeResponse += time;
						}
						startDate = endDate;
					}
					/* Get the time from Last changed state till now */
					if(SLAUtils.SLAState.contains(slaData.status)) {
						fixTimeResponse += getTimeInState(startDate, null,slaData, false);
					}
					if(slaData.status.equalsIgnoreCase("Hold")) {
						holdTime += getTimeInState(startDate,null,slaData, true);
					}
					slaData.timeSpentFixResponse=SLAUtils.convertLongToStringDateFormat(fixTimeResponse, false);
					slaData.remainingTimeFirstResponse = 
							SLAUtils.convertLongToStringDateFormat
							(ReadProperties.prop.firstResponse.get(slaData.priority)-firstTimeResponse, true);
					slaData.remainingTimeFixResponse = 
							SLAUtils.convertLongToStringDateFormat
							(ReadProperties.prop.fixResponse.get(slaData.priority)-fixTimeResponse, true);
					slaData.HoldTime = SLAUtils.convertLongToStringDateFormat(holdTime, false);
					
				} else {
					firstTimeResponse = getTimeInState(startDate, endDate,slaData, false);
					/* first and Fix time are same in here */
					slaData.timeSpentFixResponse = SLAUtils.convertLongToStringDateFormat(firstTimeResponse, false);
					slaData.remainingTimeFirstResponse = 
							SLAUtils.convertLongToStringDateFormat
							(ReadProperties.prop.firstResponse.get(slaData.priority)-firstTimeResponse, true);
					slaData.remainingTimeFixResponse = 
							SLAUtils.convertLongToStringDateFormat
							(ReadProperties.prop.fixResponse.get(slaData.priority)-firstTimeResponse, true);
					
					slaData.HoldTime = SLAUtils.convertLongToStringDateFormat(holdTime, false);
				}
				getFirstFixTimeStatus(slaData, stateChanged);
				slaList.add(slaData);
			}	
		}	
	}



	private void populateSLAData(SLAData slaData,
			JiraIssueChangeHistory issueHistory) {
		
		slaData.issueId = issueHistory.issueId;
		slaData.status = issueHistory.status;
		slaData.issueDescription = issueHistory.issueDescription;
		slaData.assignee = issueHistory.assignee;
		slaData.priority = issueHistory.priority;
		slaData.IssueType = issueHistory.issueType;
		slaData.detectionPhase = issueHistory.detectionPhase;
		slaData.holidays = 0;
		
	}



	public long getTimeInState(String sDate, String eDate,SLAData slaData, boolean isHoldTime) {
		
		long startTimeInMilli = SLAUtils.convertStringDateToLong(sDate);
		long endTimeInMilli = System.currentTimeMillis();
		if(eDate != null) {
			endTimeInMilli = SLAUtils.convertStringDateToLong(eDate);
		}
		
		/* Excluding Weekend Saturday and Sunday Time Start */
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(startTimeInMilli);
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endTimeInMilli);
		long weekendsTimeInMilliseconds =0;
		long holidayTimeInMillisecond =0;
		
		if(!((endTimeInMilli - startTimeInMilli) < dayTimeInMillisecond) &&
				startCal.get(Calendar.DAY_OF_WEEK) == startCal.get(Calendar.DAY_OF_WEEK)) {
			weekendsTimeInMilliseconds = getWeekendsTimeInMilliseconds(startCal,endCal,startTimeInMilli,endTimeInMilli);
		
			/* Excluding Weekend Saturday and Sunday Time end */
		}
		/* Excluding National Holidays */
		
		/* Resetting again as modified in getWeekendsTimeInMilliseconds function */
		startCal.setTimeInMillis(startTimeInMilli);
		endCal.setTimeInMillis(endTimeInMilli);
		holidayTimeInMillisecond = getHolidayTimeInMilliseconds(startCal,endCal);
		int numberOfHolidays = (int) (holidayTimeInMillisecond/dayTimeInMillisecond);
		if(isHoldTime == false) {
			slaData.holidays = slaData.holidays + numberOfHolidays;
		}
		
		return (endTimeInMilli - startTimeInMilli - weekendsTimeInMilliseconds - holidayTimeInMillisecond);
	}
	
	private long getHolidayTimeInMilliseconds(Calendar startCal, Calendar endCal) {
		long holidayTime = 0;
		for (Date date : Main.nationalHolidays) {
			long holidayStartTime = date.getTime();
			Calendar isWeekend = Calendar.getInstance();
			isWeekend.setTimeInMillis(holidayStartTime + 2000); // adding 2000 just to be sure in that day
			if (isWeekend.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& isWeekend.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				if ((startCal.getTimeInMillis() <= holidayStartTime)
						&& (endCal.getTimeInMillis() >= (date.getTime() + dayTimeInMillisecond))) {
					holidayTime += dayTimeInMillisecond;
				}
			}
		}
		return holidayTime;
	}



	private long getWeekendsTimeInMilliseconds(Calendar startCal, Calendar endCal, long startTimeInMilli, long endTimeInMilli) {
		int weekendDays = 0;
		long weekendTimeInMill = 0;
		
		/* Taken into consideration if start date is weekend day */
		if(startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			weekendTimeInMill += startTimeInMilli % (dayTimeInMillisecond);
		}
		
		/* Taken into consideration if end date is weekend day */
		if(endCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				endCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			weekendTimeInMill -= (dayTimeInMillisecond - (endTimeInMilli % (dayTimeInMillisecond)));
		}
		
		
		do {
			startCal.add(Calendar.DAY_OF_MONTH, 1);
	        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
	        		startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
	            ++weekendDays;
	        }
			
		} while(startCal.getTimeInMillis() < endCal.getTimeInMillis());
		
		return weekendTimeInMill+(weekendDays*dayTimeInMillisecond);
	}


	private void getFirstFixTimeStatus(SLAData slaData, boolean stateChanged) {
		if(stateChanged == false) {
			if (slaData.remainingTimeFirstResponse.equalsIgnoreCase("Expired")) {
				slaData.firstResponseOk="NOK";
				slaData.fixResponseOk="Not Started yet";
			} else {
				slaData.firstResponseOk="Not Started Yet";
				slaData.fixResponseOk="Not Started yet";
			}
		} else {
			if(slaData.remainingTimeFirstResponse.equalsIgnoreCase("Expired")) {
				slaData.firstResponseOk="NOK";
			} else {
				slaData.firstResponseOk="OK";
			}
			if(slaData.remainingTimeFixResponse.equalsIgnoreCase("Expired")) {
				slaData.fixResponseOk="NOK";
			} else if(slaData.status.equalsIgnoreCase("Hold") || slaData.status.equalsIgnoreCase("External")) {
				slaData.fixResponseOk="On Hold";
			} else if(slaData.status.equalsIgnoreCase("Verified") || slaData.status.equalsIgnoreCase("Closed")) {
				slaData.fixResponseOk="OK";
			} else {
				slaData.fixResponseOk="Ongoing";
			}
		}
	}
}
