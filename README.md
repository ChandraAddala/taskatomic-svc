taskatomic-svc
==============

Services to create and manage tasks.


## Technologies used:

*   Scala
*   Scalatra
*   Gradle
*   Flyway
*   Slick


## To build this component, we require:

*   Gradle version 2.2 or above
*   JDK 7
*   Scala 2.10 or above

## Setup

For development purpose, repo comes with a sample H2 database which should be copied to your home directory using the below command. 

```
cp services/src/main/db/taskatomic-db.mv.db ~/ 
```

### Sample build

```
gradle clean build
```

### Build executable jar and zip for database configurations

```
gradle clean build fatjar buildZip
```

### How to run the app

Run the build to create executable jar and complete instructions under setup. That should generate an executable jar file in ```services/build/libs/```. Execute the jar file using the below command.
 
```
java -jar services/build/libs/services-1.0-SNAPSHOT.jar  
```

### How to run the app by building a new database

To build your own database, use database configurations in zip file located at ```database/build/distributions/database-1.0-SNAPSHOT.zip ```. Unzip the file and use the below command.  

```
sh flyway -url=jdbc:h2:file:~/taskatomic-db-flywaytest -user=root -password= clean migrate
```

To run the app using the newly created database above

```
java -Dc3p0.jdbcUrl=jdbc:h2:file:~/taskatomic-db-flywaytest -jar services/build/libs/services-1.0-SNAPSHOT.jar
```

### How to access the app

The db is pre-loaded with some test data and can be accessed using the below urls when running the app for development purpose.

http://localhost:8080/users/cdivvela   
http://localhost:8080/users/cdivvela/projects/1   
http://localhost:8080/users/cdivvela/projects/1/tasks/1
  
### API detail  

| URL's                                               | Description
| ----------------------------------------------------|:---------------------------------------------------------------------
| GET: /users/:handle                                 | Gets user info for :handle   
| GET: /users/:handle/all                             | Gets user info for :handle along with projects associated with the user. Trying to avoid extra hop with this call.  
| GET: /users/:handle/projects                        | Gets all the projects associated with the user :handle. User should be either an owner on the project or atleast have a task assigned on that project.
| GET: /users/:handle/projects/:id                    | Gets project for :id associated with the user :handle. User should be either an owner on the project or atleast have a task assigned on that project.
| GET: /users/:handle/projects/:projectId/tasks       | Gets all tasks for project :projectId associated with the user :handle. User should be either an owner on the project or atleast have a task assigned on that project.
| GET: /users/:handle/projects/:projectId/tasks/:id   | Gets task for :id of project :projectId associated with the user :handle. User should be either an owner on the project or atleast have a task assigned on that project.
| POST: /users                                        | Creates a user and returns a http status code of 201. If user already exists, returns the existing user with status code of 200.
| PUT: /users/:handle                                 | Updates a user. Request has to contain the complete user object (not just the fields being updated)
| POST: /users/:handle/projects                       | Creates a project with user handle being the owner and returns a http status code of 201.
| PUT: /users/:handle/projects/:id                    | Updates the project with :id who has :handle as the owner. Request has to contain the complete project object (not just the fields being updated)
| POST: /users/:handle/projects/:projectId/tasks      | Creates a task associated with project :projectId and returns a http status code of 201.
| PUT: /users/:handle/projects/:projectId/tasks/:id   | Updates the task wth :id belonging to the project with :projectId. Request has to contain the complete task object (not just the fields being updated)