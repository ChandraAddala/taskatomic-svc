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

### Build executable jar

```
gradle clean build fatjar
```

### How to run the app

Run the build to create executable jar and complete instructions under setup. That should generate an executable jar file in ```services/build/libs/```. Execute the jar file using the below command.
 
```
java -jar services/build/libs/services-1.0-SNAPSHOT.jar
```

### How to access the app

The db is pre-loaded with some test data and can be accessed using the below urls when running the app for development purpose.

http://localhost:8080/users/cdivvela   
http://localhost:8080/users/cdivvela/projects/spacex-landing-pad   
http://localhost:8080/users/cdivvela/projects/spacex-landing-pad/tasks/dock-cargo-ship
  
### API detail  

| URL's                                               | Description
| ----------------------------------------------------|:---------------------------------------------------------------------
| GET: /users/:handle                                 | Gets user info for :handle   
| GET: /users/:handle/all                             | Gets user info for :handle along with projects associated with the user. Trying to avoid extra hop with this call.  
| GET: /users/:handle/projects                        | Gets all the projects associated with the user :handle. User should be either an owner on the project or have atleast has a task assigned to him/her on that project.
| GET: /users/:handle/projects/:id                    | Gets project for :id associated with the user :handle. User should be either an owner on the project or have atleast has a task assigned to him/her on that project.
| GET: /users/:handle/projects/:projectId/tasks       | Gets all tasks for project :projectId associated with the user :handle. User should be either an owner on the project or have atleast has a task assigned to him/her on that project.
| GET: /users/:handle/projects/:projectId/tasks/:id   | Gets task for :id of project :projectId associated with the user :handle. User should be either an owner on the project or have atleast has a task assigned to him/her on that project.