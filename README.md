# Project Name: Stock Alarms 

Technologies used: Spring Boot, Hibernate, Thymeleaf  

Database: PostgreSQL (name: stock_alarms)

Application runs on http://localhost:8080/

Note: In order to run the application you need to set the following environment variables:
```   
STOCK_DATA_API_KEY = <api key value>
STOCK_ALARMS_EMAIL = <email address from which the applications sends emails>
STOCK_ALARMS_PASSWORD = <password for the email address above>
```


# Problem statement

 You need to design and implement an application that should allow the users to receive price changes alarms when the Stock price changes reach their defined thresholds. 
 
 ## Main use cases: 
 
**UC1: User Registration:** 
- new user can register within the application providing: o first name o last name o email o pass/pass check As a result of registration process, the user can login in the application. 
 
**UC2: Define Alarms:**
As logged in user, the user can: 
- add new Stocks to be monitored 
- for each Stock he can define ONE alarm(small restriction for first implementation) 
 
Alarm means: 
- Let’s say that the current price for the stock A is 20 USD. The user could define as an alarm, the fact that the price of stock A is over +10% or is less than -20%. 
 
**UC3: Manage Alarms:** 
As logged in user, the user can: 
- list the already defined alarms 
- edit existing alarms 
- delete alarms For each stock listed, the user should see the price when the alarm was defined, the current price, the variance as percentage and the target alarm percentage, active/inactive. 
 
**UC4: Send Alarm:** 
The system should constantly scan the stock prices for the defined alarms and in case that the alarm conditions are met, the system should send an email to that user with information about the original stock price and the new price. Once an alarm is triggered, the system is not monitoring anymore that alarm conditions and the alarm is marked as inactive. 
 
 
 
 
## Technical requirements: 
- Spring boot should be used 
- Alarms and users should be persisted in DB 
- Spring Data / Hibernate / JDBC can be used for DB operations (just choose the preferred technology or technologies) 
- FreeMarker templates or Thymeleaf templates can be used for UI rendering - UI design should look OK, no fancy design is needed 
- One possible source of stocks data is: https://www.alphavantage.co (if not suitable, any other WS which provides reliable data can be used) 
- Checking stocks prices should be done either using polling or using data stream; it depends on the WS used. If polling is used, the polling interval must be configurable via application configuration. 
- Checking of the stocks should be optimized to avoid retrieving several times the same stock data if multiple alarms for that stock are configured 
- Integration tests for checking the 3rd party library integration should be implemented 
- For email sending you can use any email server you have access. Gmail, yahoo 
