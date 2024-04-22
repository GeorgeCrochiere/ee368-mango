Release Notes 
Team Crochiere
George Crochiere, Jake D'Esposito, Vincent Cifone, Kanaiym Abdigaparova, and Elise Almgren

GitHub link: https://github.com/GeorgeCrochiere/ee368-mango

Before using the Mango software the user must have a correct build.properties file in the mangoSource folder. Be sure to have the correct Tomcat location, DB location, and the correct username and password inputted into the file. A sample file is provided in the repository; be sure to edit it to be accurate.

Overall Compilation:
In order to compile the program, once being located in the repository’s directory, the following commands need to be executed:
	ant fullDeploy
	ant reload

Afterwards, the Mango application can be accessed via the browser at localhost:8080/test/.

FR3 Change Password Bug 
Author: Elise Almgren
Completion Date: April 10, 2024
Files Affected: 
UserDao.java
Feature Change:
When updating and changing an admin password, the system now successfully edits the password without an error. 

FR4 Landing Page
Author: Kanaiym Abdigaparova 
Completion Date: April 20, 2024
Files Affected:
MiscDwr.java
messages_en.properties
landing.jsp
springDispatcher-servlet.xml
page.tag
Feature Changes
This feature will allow the user to have a specific ‘landing’ page to get to when entering into the Mango software. This feature will add an icon to the navigation bar that will allow the user to return to their designed home page, and Mango will open to this home page as well. 

FR5 Horizontal CSV
Author: Vincent Cifone and Mathew Griffith
Completion Date: April 20th, 2024
Files Affected 
ReportExportBase.java
ChartExportServlet.java
ReportChartCreator.java
ReportDao.java
ReportCsvStreamer.java
Feature Change:
This feature sorts the sensor information alphabetically and prints them on separate columns in a CSV file for exportation.
 
FR7 More Report Charts
Author: Jake D'Esposito and George Crochiere
Completion Date: April 18th, 2024
Files Affected
createTables-derby.sql
createTables-mysql.sql
createTables-mssql.sql
scripts.sql
ReportDao.java
UserDao.java
Upgrade1_12_1.java
Upgrade1_12_2.java
Upgrade1_12_3.java
ReportWorkItem.java
ImageChartUtil.java
ReportChartCreator.java
ReportPointInfo.java
ReportPointVO.java
ReportsDwr.java
messages_en.properties
reports.jsp

Feature Changes
This task will improve the behavior and generate a correct scatter plot. Before running and compiling this portion of the software it is necessary to submit specific queries on a page in the Mango software using the built-in SQL page. Enter the queries seen below and select the ‘submit update’ field at the bottom of the page. Be sure to update each query individually.

Queries: 
alter table reportInstancePoints add column plotType integer
            alter table reportInstancePoints add column title varchar(50)
            alter table reportInstancePoints add column xAxisLabel varchar(50)
            alter table reportInstancePoints add column yAxisLabel varchar(50)
            alter table reportInstancePoints add column useYRef char(1)
            alter table reportInstancePoints add column yRefVal double

