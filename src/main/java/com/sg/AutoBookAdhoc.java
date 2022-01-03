package com.sg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

public class AutoBookAdhoc {

	private static final String pickUp = "Pick Up";
	private static final String drop = "Drop";

	public static void main(String[] args) {
		try {
			System.out.println("Start");
			getNextMondayFromToday();
			String pickUpTime="12:00";
			String dropTime="19:30";

			placeChromeDriverInTempAndSetSystemProperty();
			WebDriver driver = new ChromeDriver();
			driver.manage().window().maximize();

			//get user session specific url
			String adHocUrl = waitForLoginAndGetAdhocUrl(driver);
			
			//get Next monday's date from today
			String startDateForWeek = getNextMondayFromToday();
			
			//get dates for week starting next monday
			List<String> dates = getDatesForWeek(startDateForWeek,5);
			
			//for each data, book a pick and drop trip
			for(String date:dates) {
				openTabAndBookAdhoc(driver,adHocUrl,pickUp,date,pickUpTime);
				openTabAndBookAdhoc(driver,adHocUrl,drop,date,dropTime);
			}
		}catch(TimeoutException te) {
			System.err.println("Login timed out, please login within 3 minutes");
		}catch(Exception e ) {
			e.printStackTrace();
		}
	}

	private static void placeChromeDriverInTempAndSetSystemProperty() throws IOException {
		final File tempFile = File.createTempFile("chromedriver", ".exe");
		InputStream is = AutoBookAdhoc.class.getClassLoader().getResourceAsStream("chromedriver.exe");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int nread;
		while((nread = is.read(data,0,data.length))!=-1) {
			buffer.write(data,0,nread);
		}
		buffer.flush();
		byte[] arr = buffer.toByteArray();
		Files.write(arr,tempFile);
		is.close();
		buffer.close();
		tempFile.deleteOnExit();
		System.out.println("Chromedriver --> "+tempFile.toString());
		System.setProperty("webdriver.chrome.driver",tempFile.toPath().toString());
		
	}

	private static void openTabAndBookAdhoc(WebDriver driver,String adHocUrl,String pickUpOrDrop,String date,String time ) {
		checkDateFormat(date);

		openAnsSwitchToNewTab(driver);
		driver.get(adHocUrl);

		//pick up or drop
		(new Select(driver.findElement(By.id("lstTripType")))).selectByVisibleText(pickUpOrDrop);

		//reason for adhoc
		(new Select(driver.findElement(By.id("lstReason")))).selectByVisibleText("Others");

		//date setting
		((JavascriptExecutor) driver).executeScript("argument[0].setAttribute(arguments[1],arguments[2]);",driver.findElement(By.id("txtDate")),"value",date);

		//cab time
		(new Select(driver.findElement(By.id("lstShift")))).selectByVisibleText(time);

		//some comments
		driver.findElement(By.id("txtComments")).sendKeys("booking adhoc");

		//select employee
		driver.findElement(By.id("chk_0")).click();

		//#MAGIC
		driver.findElement(By.id("btnSubmit")).click();

		String status = driver.findElement(By.id("lblMsg")).getText();
		System.out.println("Booking for "+pickUpOrDrop+" for date "+date+" for time "+time+" status is: "+status);

	}

	private static String waitForLoginAndGetAdhocUrl(WebDriver driver) {
		driver.get("https://www.nxttrans.com/smtw/default.aspx");
		
		//driver.findElement(By.xpath("//imput[@id='txtLoginName']")).sendKeys(user);
		//driver.findElement(By.xpath("//imput[@id='txtPWD']")).sendKeys(pass);
		//driver.findElement(By.xpath("//imput[@type='submit']")).click();
		
		
		WebDriverWait webDriverWait= new WebDriverWait(driver,180);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("navbarCollapse")));
		
		WebElement we = driver.findElement(By.id("navbarCollapse"));
		WebElement we1 = we.findElement(By.cssSelector("a[href*='SmtAdhocRequest']"));
		
		String adHocUrl = we1.getAttribute("href");
		return adHocUrl;
	}

	private static void checkDateFormat(String date) {
		try {
			new SimpleDateFormat("dd/MM/yyyy").parse(date);
		}catch(ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void openAnsSwitchToNewTab(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.open()");
		List<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(tabs.size()-1));
	}
	
	private static String getNextMondayFromToday() {
		LocalDate ld = LocalDate.now();		
		ld = ld.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return ld.format(formatter);
	}
	private static final SimpleDateFormat finalForm = new SimpleDateFormat("dd/MM/yyyy"); //27/07/2020
	
	private static List<String> getDatesForWeek(String startDate, int numOfDays) throws ParseException {
		List<String> daysRange = new ArrayList<>();
		Date myDate = finalForm.parse(startDate);
		Calendar c = Calendar.getInstance();
		c.setTime(myDate);

		daysRange.add(startDate);
		daysRange.addAll(IntStream.range(1, numOfDays).boxed().map(i -> {
			c.add(Calendar.DATE, 1);
			return finalForm.format(c.getTime());
		}).collect(Collectors.toList()));
		
		return daysRange;
	}
}
