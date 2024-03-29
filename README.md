# Dispatcher

How to start the Dispatcher application
---

1. In your project directory run: `mvn clean install` to build your application
1. Start application with `java -jar target/dispatcher-1.0.0-SNAPSHOT.jar server config.yml`

### Requirements
On your local machine, PostgresSQL has to be be installed and a database has to be created.
The application's configuration file "config.yml" will have to be adjusted accordingly:

 database:  
  driverClass: org.postgresql.Driver  
  user: ***USERNAME***  
  password: ***PASSWORD***  
  url: jdbc:postgresql: ***DATABASE_NAME***  
  properties:  
    ***hibernate.hbm2ddl.auto: create***    
    
The last line *hibernate.hbm2ddl.auto: create* guarantees that the database tables will be created automatically by Hibernate.

### Creating an event:

`curl -H "Content-Type: application/json" -X POST -d '{"name":"eventA", "timestamp": "2022-08-03", "data": "This is some dummydata." }' http://localhost:8082/events`

### Deleting an event:


`curl -X DELETE "http://localhost:8082/events/1"`


### Accessing the Website

List events: http://localhost:8082/events

List events by Id: http://localhost:8082/events/{id}


Metrics
---

To see the application's metrics enter url `http://localhost:8084/metrics`
