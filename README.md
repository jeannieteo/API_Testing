REST Assured API Automation Framework
1. RestAssured
2. TestNg
3. Log4J
4. Maven
5. allure report 

How to execute tests:
mvn clean test
or 
mvn clean test -Dsuitefilename="restAssured_apitest.xml"
then 
allure serve

How to put allure in your system
1. Download zip file
https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/
2. Unzip into folder and copy pathname of bin.
3. add pathname to $Path environment variable.
