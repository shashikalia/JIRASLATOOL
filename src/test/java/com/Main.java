package com;

import com.sla.SLAUtils;

public class Main {

	public static void main(String[] args) {
		
		long tiem = SLAUtils.convertStringDateToLong("2016-07-04T14:00:24.868+0200");
		System.out.println(tiem);
		
		//String form = SLAUtils.convertLongToStringDateFormat(System.currentTimeMillis()-tiem);
		String form = SLAUtils.convertLongToStringDateFormat(66000,false);
		System.out.println(form);
	}
	
}
