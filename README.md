# Ever-changing Hierarchy helper 
This project aim to help Personia, an HR manager, to cope with the constants changes at Ever-changing Hierarchy GmbH.

This is a Spring Boot project using Java 11 and Apache Maven to build. There is a mvn wrapper included in the archive, but a JRE installed is required. 

### How to Test
You can run the unit and functional tests in the project, using:
 
`./mvnw test`  
You can use `mvn` command instead of using the wrapper command if Maven is installed in your machine.

### How to build
In order to build the project you need to run: 

`./mvnw package`

If you want to skip the tests you should run:

`./mvnw package -DskipTests`

The commands above will produce a JAR file located at `<project-root>/target` folder.

### How to run
After building the project it is time to run it. To do so fire the following command:  

`java -jar target/hierarchy-0.0.1-SNAPSHOT.jar`  
_This will start the application using `default` profile._

If you want to run the application using `prod` profile you can do so using:

`java -Dspring.profiles.active=prod -jar target/hierarchy-0.0.1-SNAPSHOT.jar`  

The `default` profile creates an in-memory H2 database. In `prod` the database is stored on a file and H2 console is disabled.

All endpoints in the application are under HTTP Basic Authentication. By default, the user `personia` and password `password` are set.  
If you want to start the application with different user and password you can do so passing the following arguments when running: 

```shell script
java \
 -Dspring.security.user.name=levi \
 -Dspring.security.user.password=pass \
 -jar target/hierarchy-0.0.1-SNAPSHOT.jar
```  
In this case the HTTP request will require user `levi` and password `pass` to be used.

### How to use
Finally, time to use our application. It has two main functions for now: **Saving the hierarchy** and **Querying an employee supervisors**.   
Detailed below is how to use them.

#### Saving hierarchy

To save a hierarchy, Personia can use the `POST /hierarchy` endpoint passing Chris' JSON.  
Following is an example using `curl`. 
```shell script
curl -iu personia:password --request POST 'http://localhost:8080/hierarchy' \
--header 'Content-type: application/json' \
--data-raw '{
    "Pete": "Nick",
    "Barbara": "Nick",
    "Nick": "Sophie",
    "Sophie": "Jonas"
}'
```  
_Notice the user and password needs to be provided, otherwise the request will respond `401 Unauthorized` status._  

If this is a valid input, it will save the employees and its relationship in the DB overwriting any past data if it exists. 

In case there is an issue in the input a `400 Bad Request` will return with a JSON response containing the errors. 
Try the following request as an example:
```shell script
curl -iu personia:password --request POST 'http://localhost:8080/hierarchy' \
--header 'Content-type: application/json' \
--data-raw '{
    "Pete": "Nick",
    "Nick": "Pete",
    "Sophie": "Jonas",
    "Adam": "Barbara"
}'
```
In case of an error nothing is stored to the DB.

#### Querying supervisors

After saving a valid hierarchy it is possible to see an employee's supervisors using `GET /employees/<name>/supervisors` endpoint.  
For this the employee's name must be provided in the path (case-insensitive):

```shell script
curl -iu personia:password --request GET 'http://localhost:8080/employees/nick/supervisors'
```
This will result in a JSON containing the employee's name, its supervisor's name and its supervisor's supervisor's name. 

If the user cannot be found, the endpoint will respond with `404 Not Found` status.

