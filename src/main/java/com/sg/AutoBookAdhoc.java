package com.sg;

import org.apache.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AutoBookAdhoc {

	static Logger logger= Logger.getLogger(AutoBookAdhoc.class);

	private static final String pickUp = "Pick Up";
	private static final String drop = "Drop";

	public static void main(String[] args) {
		try {
			System.out.println("Start");
			getNextMondayFromToday();
			String pickUpTime="";
			String dropTime="";

			WebDriver driver = new ChromeDriver();
			driver.manage().window().maximize();

			String adHocUrl = waitForLoginAndGetAdhocUrl();

			List<String> dates = getDatesForWeek(startDateForWeek,days);
			for(String date:dates) {
				openTabAndBookAdhoc(driver,adHocUrl,pickUp,date,pickUpTime);
				openTabAndBookAdhoc(driver,adHocUrl,drop,date,dropTime);
			}
		}catch(TimeoutException te) {
		}catch(Exception e ) {
			e.printStackTrace();
		}
	}

}
