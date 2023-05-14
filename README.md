# DummyTest

This is a test project, with UI tests for Cypromarketcap built using selenium and cucumber

***
**Running Tests**

Run UI tests using the below mvn command 

````
mvn test '-Dcucumber.filter.tags="@ui"'
````

Run API tests using the below mvn command

````
mvn test '-Dcucumber.filter.tags="@api"'
````

***

**Reports**

Cucumber html report is generated in the project build directory at below path

````
/target/cucumber-report/report.html
````

***
