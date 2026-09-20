###  SelfHeal

### Spring Boot Runtime Health Monitoring & Self-Healing Framework

SelfHeal is a reusable **Spring Boot Starter** that provides runtime health
monitoring, failure detection, automated recovery, circuit breaking,
failure fingerprinting, dependency monitoring, recovery escalation,
event notifications, audit trails, and dependency topology visualization.

The goal is simple:

> Detect runtime problems, attempt recovery automatically, record what happened,
> and expose the system state through APIs and a management dashboard.

---

## ✨ Features

SelfHeal currently provides:

- ✅ Runtime health monitoring
- ✅ Circuit Breaker
- ✅ Dependency Failure Detection
- ✅ Automated Recovery
- ✅ Recovery Escalation
- ✅ Failure Fingerprinting
- ✅ Alert & Notification System
- ✅ Event Listener API
- ✅ Recovery Audit Trail
- ✅ Health Dependency Graph
- ✅ Recovery History
- ✅ Runtime Metrics
- ✅ Management REST APIs
- ✅ Built-in Management Dashboard
- ✅ In-memory persistence
- ✅ Spring Boot AutoConfiguration

---

# 🏗️ Architecture

SelfHeal is implemented as a reusable Spring Boot Starter.

```text
                    Spring Boot Application
                             │
                             ▼
                  SelfHeal AutoConfiguration
                             │
             ┌───────────────┼────────────────┐
             │               │                │
             ▼               ▼                ▼
       Health Monitor   Failure Detection   Dependencies
             │               │                │
             └───────────────┼────────────────┘
                             ▼
                   Failure Classification
                             │
                             ▼
                     Circuit Breaker
                             │
                             ▼
                     Recovery Engine
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
                Recovery          Escalation
                    │                 │
                    ▼                 ▼
              Audit Trail       Notifications
                    │
                    ▼
              Management APIs
                    │
                    ▼
               Dashboard
```

The application does not need to manually create SelfHeal beans for basic usage.

After the starter dependency is available to the application, Spring Boot loads the SelfHeal AutoConfiguration automatically.


### 📦 Project Structure

``` text

SelfHeal/
│
├── selfheal-spring-boot-starter/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │
│   └── pom.xml
│
├── selfheal-demo-app/
│   │
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   │
│   └── pom.xml
│
└── README.md

```

## selfheal-spring-boot-starter

The reusable Spring Boot Starter containing the SelfHeal framework.

## selfheal-demo-app

A demonstration Spring Boot application used to verify SelfHeal features, simulate failures, test recovery, register dependencies, and demonstrate the management dashboard.

### 🚀 Quick Start:

## Requirements
----------------
Java 21+
Maven 3.9+
Spring Boot application

The current project is developed with:

Java       : 21+ target
Spring Boot: 4.1.1
Maven      : 3.9+

The runtime development environment used during testing may use a newer installed JDK while the project compiler targets Java 21.

### 1. Add SelfHeal to Your Application

  -> Add the following dependency to your Spring Boot application's pom.xml:
  
  ```
                              <dependency>
                              <groupId>io.github.satyabrata11x</groupId>
                              <artifactId>selfheal-spring-boot-starter</artifactId>
                              <version>0.1.1</version>
                              </dependency>
```

### The Maven coordinates are:

      Group ID    : com.selfheal
      Artifact ID : selfheal-spring-boot-starter
      Version     : 0.1.0
      
### 2. Start Your Spring Boot Application

 ->Start the application normally:

         mvn spring-boot:run

SelfHeal initializes automatically through Spring Boot AutoConfiguration.

You should see SelfHeal initialization information in the application logs.

### Example:

          =================================
                 SELFHEAL INITIALIZED
          =================================
          
          You may also see monitoring information such as:
          
          [SELFHEAL] Monitor started
          
### 3. Open the SelfHeal Dashboard

  > Once the application is running, open:

        http://localhost:8080/selfheal/dashboard

The dashboard provides a runtime view of the SelfHeal system.

## It displays:

            System health
            Health-check metrics
            Failed health checks
            Detected failures
            Recovery processes
            Successful recoveries
            Failed recoveries
            Recovery success rate
            Response time
            Dependencies
            Dependency topology
            Circuit breakers
            Recovery escalations
            Recovery audit trail

The dashboard automatically refreshes runtime information.

### ❤️ Health Monitoring

  > SelfHeal continuously monitors the health of the configured application component.

The monitoring system tracks information such as:

                    Health Status
                    Response Time
                    Failure Count
                    Recovery State
                    Recovery History

> Supported primary health states:

                UP
                DOWN

### A simplified monitoring flow is:
```
                        Health Check
                             │
                             ▼
                        Healthy?
                         ┌───┴────┐
                         │        │
                        Yes       No
                         │        │
                         ▼        ▼
                        Continue  Failure Detection
                                      │
                                      ▼
                               Failure Classification
                                      │
                                      ▼
                                   Recovery
                        🔌 Dependency Monitoring
```

### SelfHeal provides dependency registration and health monitoring.

## Supported dependency types include:

                    DATABASE
                    CACHE
                    REST_API
                    MESSAGE_BROKER
                    EXTERNAL_SERVICE
                    FILE_SYSTEM
                    UNKNOWN

## Supported dependency states:

                UP
                DOWN
                DEGRADED
                UNKNOWN

> Example dependency environment:
```
                    selfheal-demo-component
                            │
                            ├── postgresql
                            │
                            ├── redis
                            │
                            └── payment-api
```

### Each dependency can expose:

                  Name
                  Type
                  Status
                  Response time
                  Health message
                  🔄 Automated Recovery

SelfHeal provides automated recovery through configurable recovery strategies.

### urrently supported strategies include:

                          RETRY
                          IMMEDIATE

The default recovery configuration used by the demo application is:

                      Strategy           : retry
                      Maximum Attempts   : 3
                      Initial Delay      : 1000 ms
                      Backoff Multiplier : 2.0
                      Maximum Delay      : 10000 ms
                      Cooldown           : 10000 ms

The retry strategy uses exponential backoff.

## Example:
```
                          Failure
                             │
                             ▼
                          Attempt 1
                             │
                             └── wait 1 second
                                    │
                                    ▼
                                 Attempt 2
                                    │
                                    └── wait 2 seconds
                                              │
                                              ▼
                                           Attempt 3
                          
                          If recovery succeeds:
                          
                          Failure
                             ↓
                          Recovery
                             ↓
                          Health Check
                             ↓
                          SUCCESS
                          
                          If recovery fails after the configured number of attempts:
                          
                          Failure
                             ↓
                          Recovery Attempt
                             ↓
                          Recovery Attempt
                             ↓
                          Recovery Attempt
                             ↓
                          Recovery Failed
                             ↓
                          Escalation
                          🛡️ Circuit Breaker
```

SelfHeal includes a circuit breaker to protect components from repeated recovery attempts and continuous failures.

## Supported states:

CLOSED
OPEN
HALF_OPEN

### Circuit breaker flow:
```
                 ┌──────────────┐
                 │    CLOSED    │
                 └──────┬───────┘
                        │
                     failures
                        │
                        ▼
                 ┌──────────────┐
                 │     OPEN     │
                 └──────┬───────┘
                        │
                    cooldown
                        │
                        ▼
                 ┌──────────────┐
                 │  HALF_OPEN   │
                 └──────┬───────┘
                        │
               ┌────────┴────────┐
               │                 │
            success            failure
               │                 │
               ▼                 ▼
            CLOSED              OPEN
```


## The current circuit breaker state can be inspected through:

          GET /selfheal/management/circuit-breakers

## A circuit breaker can also be reset manually:

         POST /selfheal/management/circuit-breakers/{componentName}/reset
         
## 🚨 Recovery Escalation

If SelfHeal cannot recover a failure after the configured number of attempts, the failure is escalated.

## An escalation records information such as:

                        Component name
                        Failure type
                        Recovery strategy
                        Number of attempts
                        Recovery duration
                        Reason
                        Timestamp

## Example:

                  [SELFHEAL-ESCALATION]
                  
                  component = selfheal-demo-component
                  attempts  = 3
                  reason    = Recovery exhausted after 3 attempts

## Escalations can be viewed using:

        GET /selfheal/management/escalations

## The number of escalations can be obtained using:

        GET /selfheal/management/escalations/count
        
## 🔍 Failure Fingerprinting:

SelfHeal generates a deterministic fingerprint for runtime failures.

## The fingerprint is generated from structured failure information including:

                                  Component
                                  Exception Type
                                  Failure Type
                                  Package
                                  Class
                                  Method

The fingerprint allows recurring failures to be represented using a consistent identifier.

## Conceptually:
```

              Runtime Failure
                     │
                     ▼
              Failure Context
                     │
                     ▼
              Fingerprint Generator
                     │
                     ▼
              SHA-256 Fingerprint
```

## Example:

    07432305a7c83201339cc246965cf40d34820ee584127ab7d2323f6e69cccbbd

## Failure context can include:

                Application name
                Component name
                Package
                Class
                Method
                File
                Line number
                Exception type
                Failure type
                Message
                Stack trace
                Timestamp
                Fingerprint
                Alert & Notification System

SelfHeal includes an event-driven alert and notification system.

## Supported alert severities:

                INFO
                WARNING
                CRITICAL

## Current event-to-severity mapping includes:

                Event	Severity
                Failure detected	WARNING
                Recovery started	INFO
                Recovery successful	INFO
                Recovery failed	CRITICAL

## Example console notification:

                  [SELFHEAL-ALERT]
                  severity=WARNING
                  component=selfheal-demo-component
                  event=FAILURE_DETECTED
                  message=...

The notification system is extensible through the AlertNotifier interface.

## 📡 Event Listener API

Applications can register listeners for SelfHeal events.

## Example:

            SelfHealEventListener listener =
                    event -> System.out.println(
                            event.getType()
                                    + " | "
                                    + event.getComponentName()
                                    + " | "
                                    + event.getMessage()
                    );
            
            eventPublisher.registerListener(listener);

## SelfHeal events include:

                FAILURE_DETECTED
                RECOVERY_STARTED
                RECOVERY_ATTEMPT
                RECOVERY_SUCCESS
                RECOVERY_FAILED
                FAILURE_CLASSIFIED

Applications can use these events to integrate SelfHeal with their own logging, notification, or monitoring systems.

## 📝 Recovery Audit Trail

SelfHeal records recovery operations in an audit trail.

## Each audit entry contains:

                    Component
                    Failure Type
                    Recovery Strategy
                    Attempts
                    Success / Failure
                    Duration
                    Message
                    Timestamp

## Example response:

                  [
                    {
                      "componentName": "selfheal-demo-component",
                      "failureType": "COMPONENT_FAILURE",
                      "strategy": "RETRY",
                      "attempts": 1,
                      "successful": true,
                      "duration": 6,
                      "message": "Recovery completed successfully",
                      "timestamp": "2026-09-19T18:03:13.588607Z"
                    }
                  ]

## Retrieve the audit trail:

      GET /selfheal/management/audit

## Clear the audit trail:

     DELETE /selfheal/management/audit
### 🕸️ Health Dependency Graph

SelfHeal represents application dependency relationships using a health-aware dependency graph.

Example:

                 selfheal-demo-component
                         │
              ┌──────────┼──────────┐
              │          │          │
              ▼          ▼          ▼
          PostgreSQL   Redis    Payment API

## The graph tracks:

              Nodes
              Relationships
              Dependency types
              Dependency health
              Response time
              Health messages

## Example graph:

      Nodes: 4
      Links: 3

The root application component can be represented in the graph even when it does not have a corresponding dependency health record.

### 📡 Management REST API

## SelfHeal exposes management endpoints under:

              /selfheal/management
              System Status
              GET /selfheal/management/status

Returns the current SelfHeal/application status.

    ->  Health
          GET /selfheal/management/health
          
          Returns current health information.
          
    -> Metrics
          GET /selfheal/management/metrics
          
          Returns runtime monitoring and recovery metrics.
          
    ->  Dependencies
          GET /selfheal/management/dependencies
          
          Returns all registered dependencies.
          
          GET /selfheal/management/dependencies/failed
          
          Returns failed dependencies.
          
          GET /selfheal/management/dependencies/healthy
          
          Returns healthy dependencies.
          
    ->  Dependency Graph
          GET /selfheal/management/dependency-graph
          
          Returns the complete dependency graph.
          
          GET /selfheal/management/dependency-graph/{componentName}
          
          Returns the graph associated with a component.
          
    -> Circuit Breakers
          GET /selfheal/management/circuit-breakers
          
          Returns circuit breaker states.
          
          POST /selfheal/management/circuit-breakers/{componentName}/reset

Resets a circuit breaker.

        Recovery History
        GET /selfheal/management/history
        
        Returns recovery history.
        
        GET /selfheal/management/statistics
        
        Returns recovery statistics.
        
        DELETE /selfheal/management/history
        
        Clears recovery history.
        
        Recovery Audit
        GET /selfheal/management/audit
        
        Returns recovery audit entries.
        
        DELETE /selfheal/management/audit
        
        Clears the audit trail.
        
        Recovery Escalations
        GET /selfheal/management/escalations
        
        Returns recovery escalations.
        
        GET /selfheal/management/escalations/count
        
        Returns the escalation count.
        
        DELETE /selfheal/management/escalations

Clears escalation records.

## 🧪 Testing SelfHeal

The demo application contains test endpoints used to demonstrate the framework.

These endpoints are part of the demo application and are intended for testing. They are not required by the SelfHeal Starter itself.

          Simulate Component Failure
          POST /selfheal/test/fail

Using cURL:

        curl -X POST http://localhost:8080/selfheal/test/fail

This triggers the demo component failure path.

      Trigger Recovery
      POST /selfheal/test/recover

Using cURL:

      curl -X POST http://localhost:8080/selfheal/test/recover
      Force Recovery Failures
      POST /selfheal/test/recovery-failures/{count}

## Example:

    curl -X POST http://localhost:8080/selfheal/test/recovery-failures/3

### This can be used to demonstrate:

                    ```
                    Recovery Attempts
                           ↓
                    Recovery Failure
                           ↓
                    Circuit Breaker
                           ↓
                    Escalation
                           ↓
                    Audit Trail
                    Simulate a Specific Failure Type
                    POST /selfheal/test/failure/{type}
                    ```

## Example:

     curl -X POST http://localhost:8080/selfheal/test/failure/DATABASE_FAILURE
     
## 🔌 Dependency Failure Testing

The demo application provides dependency testing endpoints.

                -> Mark Dependency Down
                        POST /selfheal/test/dependencies/{name}/down

## Example:

        curl -X POST http://localhost:8080/selfheal/test/dependencies/postgresql/down
        Recover Dependency
        POST /selfheal/test/dependencies/{name}/recover

## Example:

       curl -X POST http://localhost:8080/selfheal/test/dependencies/postgresql/recover
        -> Failure Fingerprinting Test

## The demo application contains an exception simulation endpoint:

             GET /selfheal/test/exception/database

## Example:

           curl http://localhost:8080/selfheal/test/exception/database

The endpoint deliberately produces an application exception so that the SelfHeal exception interception and failure fingerprinting pipeline can be demonstrated.

## 📈 Example Self-Healing Flow

## A typical SelfHeal failure lifecycle looks like:

```

                Application
                     │
                     ▼
               Health Check
                     │
                     ▼
              Failure Detected
                     │
                     ▼
             Failure Classified
                     │
                     ▼
              Circuit Breaker
                     │
                     ▼
              Recovery Engine
                     │
              ┌──────┴──────┐
              │             │
           SUCCESS        FAILURE
              │             │
              ▼             ▼
          Recovered      Retry
                            │
                            ▼
                       More Attempts
                            │
                     ┌──────┴──────┐
                     │             │
                  SUCCESS        FAILED
                     │             │
                     ▼             ▼
                 Recovered      Escalation
                                   │
                                   ▼
                              Audit Trail
```

At the same time, SelfHeal can publish events and generate alerts.

## ⚙️ Default Runtime Configuration

The current demo runtime uses:

                    Monitoring Interval : 5000 ms
                    Latency Threshold   : 1000 ms
                    
                    Recovery Strategy   : retry
                    Maximum Attempts    : 3
                    Initial Delay       : 1000 ms
                    Backoff Multiplier  : 2.0
                    Maximum Delay       : 10000 ms
                    Cooldown            : 10000 ms
                    
                    Persistence Provider: memory

The current persistence implementation uses in-memory storage.

This means runtime history and audit information are stored in memory and are not intended to survive application restarts.

### 💾 Persistence

The current implementation provides:

memory

as the active persistence provider.

## At startup, SelfHeal reports the active provider:

     [SELFHEAL-PERSISTENCE] Active provider: memory

The persistence architecture is designed around a persistence abstraction so that additional persistence implementations can be introduced later.

### 🧰 Building the Starter Locally

## Clone the repository:

          git clone <YOUR-GITHUB-REPOSITORY-URL>

## Move into the starter:

         cd SelfHeal/selfheal-spring-boot-starter

## Build and install:

        mvn clean install

This creates the SelfHeal JAR and installs it into the local Maven repository.

The artifact can then be used by the demo application or another local Maven project.

### ▶️ Running the Demo Application

## After installing the starter:

       cd ../selfheal-demo-app

Run:

     mvn clean spring-boot:run

## The demo application starts on:

       http://localhost:8080

## Open the dashboard:

      http://localhost:8080/selfheal/dashboard
## 🧪 Test Results

The implemented SelfHeal starter has been tested across the major framework components.

The latest completed starter build includes:

                82 source files
                19 tests passed
                BUILD SUCCESS

The demo application also builds successfully.

The implemented feature set has been exercised through the demo application's runtime APIs and dashboard.

### 📸 Demonstration

The SelfHeal project can be demonstrated through:

## 1. Dashboard Overview

Shows:

      System health
      Runtime metrics
      Failure counts
      Recovery statistics
      2. Dependency Graph

Shows:
```
        
        Application
             │
         ┌───┼──────────┐
         ▼   ▼          ▼
        DB  Redis   Payment API

```


### 3. Recovery & Escalation

Shows:

        Circuit breaker state
        Recovery attempts
        Escalations
        
## 4. Audit Trail

Shows:

        Recovery strategy
        Attempts
        Duration
        Success/failure
        Timestamp

### 📄 License

License information will be added before the first public release.

### 👨‍💻 Author

Satyabrata Behera

SelfHeal

Spring Boot Runtime Health Monitoring & Self-Healing Framework


                                                                  Thank You 





