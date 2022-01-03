## Automatic Cab Booking

> Note: I used this last in 2019 during my job to book the office cab. This might not work now :)

Program to auto book my office cab for the whole week .Built using Selenium automation framework for java.

This program opens NXT Trans website, wait for you to login and then book cab with adhoc request for the week.


#### Steps:
- build with `mvn clean package`
- run `java -jar target/automatic-cab-booking-1.0.0-jar-with-dependencies.jar`
- the NXT Trans page open in chrome
- enter User Id, password on the login page and click login
- the program opens new tabs and book for each day in the next week

##### Notes:
- It sets the chrome webdriver system property by copying the webdriver to temp folder and deleting on program exit.
- The chromedriver may needed to be updated manually.
- It automatically gets the date of the Monday for the next week and books pick and drop cabs for the entire next week.