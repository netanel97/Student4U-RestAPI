----------------------------------------------------------------------------------------------------------------------------
# Docker-Dekstop Installation and Setup Guide 

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).

### Installation Instructions

1. Go to [Docker-Desktop](https://www.docker.com/products/docker-desktop/), download and install the compatible version to your machine.

2. Once installation is complete, Docker-Desktop should be ready to use.

### Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/) (Provides free images, extension and plugins)

    If you encounter any issues during the installation process or have any further questions, please consult the official Docker documentation or seek assistance from the Docker community.
----------------------------------------------------------------------------------------------------------------------------
# MongoDB Installation and Setup Guide 

## Option 1: MongoDB GUI

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).

### Installation Instructions

1. Visit MongoDB Compass download link : [https://www.mongodb.com/try/download/compass](https://www.mongodb.com/try/download/compass)

2. Choose the appropriate MongoDB version for your operating system and and download MongoDB Compass Download (GUI).

3. Follow the installation instructions specific to your operating system. 

4. Once the installation is complete, MongoDB should be ready to use.

### Configuration and Setup

1. Open a MongoDBCompass application.

2. By default, MongoDB runs on port `27017`.

3. Press on the `connect` button.

4. MongoDB should now be running and ready to accept connections.

## Option 2: MongoDB Docker

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).
- You have Docker-Desktop installed.

### Installation Instructions

1. Go to [Install MongoDB Community with Docker](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-community-with-docker/) and follow the installation instructions.

2. Once complete, you should be able to see the `MongoDB` image and container in `Docker-Desktop`.
please note you should add `-p 27017:27017` when running the image as a container.
example: `docker run --name mongo -p 27017:27017 -d mongodb/mongodb-community-server:latest`

### Configuration and Setup

1. Open `Docker-Desktop`.

2. Go to `Containers` and you should see the container you made in the `Installation Instructions`, run it and it should be running and ready to accept connections.

### Additional Resources

- [MongoDB Compass Documentation](https://www.mongodb.com/docs/compass/master/)
- [MongoDB University](https://university.mongodb.com/) (Provides free online courses on MongoDB)

    If you encounter any issues during the installation process or have any further questions, please consult the official MongoDB documentation or seek assistance from the MongoDB community.

----------------------------------------------------------------------------------------------------------------------------
# Postman Installation and Setup Guide 

### Prerequisites
Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).

### Installation Instructions

1. Visit Postman download link : [https://www.postman.com/downloads](https://www.postman.com/downloads/)

2. Choose the appropriate Postman version for your operating system and and download.

3. Follow the installation instructions specific to your operating system. 

4. Once the installation is complete, Postman should be ready to use.

### Configuration and Setup

1. Open a Postman application.

2. Login or skip.

3. Import the JSON collection given to you in the project folder.

4. Make sure your MongoDB and Server are running.

5. Postman should now be ready to send requests.

### Additional Resources

- [Postman Documentation](https://learning.postman.com/docs/introduction/overview/)
- [Postman Academy](https://academy.postman.com/)

    If you encounter any issues during the installation process or have any further questions, please consult the official Postman documentation or seek assistance from the Postman community.
----------------------------------------------------------------------------------------------------------------------------
# Android Studio Installation and Setup Guide 

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).
- You have Java JDK installed on your system.
- Your Android device has 'Debug by USB' enbabled.

### Installation Instructions

1. Visit Android Studio download link : [https://developer.android.com/studio/install](https://developer.android.com/studio/install) 
OR
[https://developer.android.com/studio](https://developer.android.com/studio) 

2. Make sure your system passes the system requirements specified in the first link. Follow the instructions and download the latest version.

3. Follow the installation instructions specific to your operating system. 

4. Once the installation is complete, Android Studio should be ready to use.

### Configuration and Setup

1. Open an Android Studio application.

2. A window should open asking if you would like to open a new project or import one, choose to import and select the project (Should have an Android icon as file image).

3. Once the project is loaded, click on `Sync Project with Gradle Files` (top right elephant icon with a blue arrow).  

4. Android Studio should be syncing the gradle files. This might take a few minutes.

5. Once finished, You can connect your phone to the system, click `Run 'filename'` run the app on your phone.

#### _note: There should be a pop-up on your device asking if to `Allow USB debugging?`. choose `Allow`._

### Additional Resources

- [Android Studio Documentation](https://developer.android.com/docs/)
- [Android Studio Courses](https://developer.android.com/courses/) (Provides free online courses on Android Studio)

    If you encounter any issues during the installation process or have any further questions, please consult the official Android Studio documentation or seek assistance from the Android community.
----------------------------------------------------------------------------------------------------------------------------
# Optional: Creating Jar file

#### If you wish to run the project without an IDE you can create a Jar file following these instructions:

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).
- You have Java JDK installed on your system.

### Configuration and Setup

1. Open file location.

2. Write cmd in address bar and press `Enter` key.

3. Write the command `gradlew build -x test`.

4. You can close the `Command Prompt` and go to the project location, open the new `build` folder and open the `libs` folder. You should see the created Jar files named after the project.

5. Follow step 2 again.

6. Write the command `java -jar {Jar_Name}.

7. Once done, the server should be running. To stop you can quit or press `CTRL`+`C`.
----------------------------------------------------------------------------------------------------------------------------
# Optional: Creating Docker Image & Container

### If you wish to run the project without an IDE you can create a Docker Container following these instructions:

### Prerequisites

Before starting, make sure that the following prerequisites are met:

- You have administrative access to your computer.
- You have a compatible operating system (Windows, macOS, or Linux).
- You have Java JDK installed on your system.
- You have Docker-Desktop installed.
- You have created a Jar file following `Creating Jar file` instructions.
- You have added the following line to `application.properties` 
  `spring.data.mongodb.uri=mongodb://mongoDb:27017/myDB`
- You have added the attached files in `Docker files.zip` provided in the folder to the project under the project's root: `Dockerfile` and `docker-compose.yml`

### Configuration and Setup

1. Open `Command Prompt` from the project location in File Explorer.

2. Write the command `docker-compose up --build`

3. When finished, the server should be running. To stop you can stop it from Docker Desktop or press `CTRL`+`C`.

* Please note: if adding the files causes the project to stop working, please revert the changes made.

----------------------------------------------------------------------------------------------------------------------------
# Optional: Adding SSL TLSv1.2 security 

### If you wish to add a layer of security to the code, follow these steps:

1. Open `Command Prompt as an administrator.

2. Navigate to `Desktop` or `Downloads` folders.

3. Run the command `keytool -genkey -alias integrativesecurity -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore integrativesecurity.p12 -validity 3650` and enter the password `secureproject1`. You will be requested to write your details, after filling them, enter `yes`.

4. You should have a file named `integrativesecurity.p12`. Add the file to the project's resources.

5. Change the project's `Application` class to 
```
package superapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    @Bean
    public ServletWebServerFactory servletContainer() {
        // Enable SSL Trafic
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        // Add HTTP to HTTPS redirect
        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());

        return tomcat;
    }

    /*
    We need to redirect from HTTP to HTTPS. Without SSL, this application used
    port 8082. With SSL it will use port 8443. So, any request for 8082 needs to be
    redirected to HTTPS on 8443.
     */
    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8084);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

}
```

6. In `application.properties` change the port from `8084` to `8443` and add the following lines to the file:
```
# Security

#enable/diable https
server.ssl.enabled=true
#server.port=8443
server.ssl.key-store=src/main/resources/integrativesecurity.p12
server.ssl.key-store-password=secureproject1
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=integrativesecurity

#ssl ciphers
server.ssl.ciphers=TLS_RSA_WITH_AES_128_CBC_SHA256, TLS_EMPTY_RENEGOTIATION_INFO_SCSV,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_RSA_WITH_AES_256_GCM_SHA384



# SSL protocol to use.
server.ssl.protocol=TLS

# Enabled SSL protocols.
server.ssl.enabled-protocols=TLSv1.2
```

7. In Postman, go to settings and disable `SSL Certificate verification`.

8. In Postman, click on `Add Certificate` and import the created `integrativesecurity.p12` file in the PFX field.

9. In the Host field write `localhost` and in port `8443`.

10. In `Passphrase` enter the password used in the `Command Prompt`.

11. The project should now be protected and secured.

* Please note: if these changed cause the project to stop working, please revert the changes made.

----------------------------------------------------------------------------------------------------------------------------
# Project Instructions

1. Launch your IDE.
2. Import the project into your IDE.
3. Right click your project --> Properties --> Project Facets --> Convert to faceted from... --> Apply and Close.
4. Right click your project --> Configure --> Add Gradle Nature.
5. Run the application file inside the project.
6. The server should be running.

----------------------------------------------------------------------------------------------------------------------------
# Forum Miniapp

### Prerequisites
Before starting, make sure that the following prerequisites are met:
 - The project is running (Server).
 - MongoDB Compass is connected.
 - Postman application is open and the `Forum_Final.postman_collection.json` file is imported.

### Instructions
1. The database includes premade useres and objects.
2. You can send any HTTP request by using postman with the json files that were provided to you.
3. When creating a new grade object add to the objectDetails the following attributes:

JSON example
```
    "objectDetails":{
        "subject" : "s",
        "description" : "d"
    }
```
### Special Operations

##### __1. Remove a specific forum thread:__
If you wish to remove a specific forum thread you need the following path: http://localhost:8084/superapp/miniapp/miniAppForum
- you need to write the command: "Remove Thread" as the command in the json that you are sending.
- You will need to provide the objectId of the thread you want to remove (you can get objectId from MongoDB Compass).

JSON example:
```
{
    "command" : "Remove Thread",
    "targetObject":{
        "objectId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "internalObjectId": "478c6fb9-b618-4099-9c24-aae661aa3c80"
        }
    },
    "invokedBy":{
        "userId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "email":"test@gmail.com"
        }
    }
    "commandAttributes":{}
}
```

##### __2. See all forum thread by a specific user:__
If you wish to See all forum thread by a specific user you need the following path: http://localhost:8084/superapp/miniapp/miniAppForum
- you need to write the command: "Get User Threads" as the command in the json that you are sending.
- You will need to provide the user id of the user you want to see all of his threads (you can get userId from MongoDB Compass). you need to write it in the commandAttributes with key "creator".
- You can choose how many threads to show per page and what page to view by adding the fields 'page' and 'size' to the commandAttributes. By default it will show the first 15 threads.

JSON example:
```
{
    "command" : "Get User Threads",
    "targetObject":{
        "objectId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "internalObjectId": "8b94fa00-de84-43d9-8a5b-51a20424d8fd"
        }
    },
    "invokedBy":{
        "userId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "email":"test@gmail.com"
        }
    },
    "commandAttributes":{"creator":"2023b.Liran.Sorokin-Student4U_adam@gmail.com","page":0,"size":15}
}
```

##### __3. See all forum thread after a certain date:__
If you wish to See all forum thread after a certain date you need the following path: http://localhost:8084/superapp/miniapp/miniAppForum
- you need to write the command: "Get Threads After" as the command in the json that you are sending.
- You will need to provide the date (you can get date format from MongoDB Compass). you need to write it in the commandAttributes with key "date".
- You can choose how many threads to show per page and what page to view by adding the fields 'page' and 'size' to the commandAttributes. By default it will show the first 15 threads.

JSON example:
```
{
    "command" : "Get Threads After",
    "targetObject":{
        "objectId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "internalObjectId": "dde9236d-dcd3-451e-bc46-3fc7f813b3d8"
        }
    },
    "invokedBy":{
        "userId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "email":"test@gmail.com"
        }
    },
    "commandAttributes":{"date":"2023-06-02T08:56:01.490+00:00"}
}
```
----------------------------------------------------------------------------------------------------------------------------
# Grades Avarage Miniapp

### Prerequisites
Before starting, make sure that the following prerequisites are met:
 - The project is running (Server).
 - MongoDB Compass is connected.
 - Postman application is open and the `Grades.postman_collection.json` file is imported.

### Instructions
1. The database includes premade useres and objects.
2. You can send any HTTP request by using postman with the json files that were provided to you.[
3. When creating a new grade object add to the objectDetails the following attributes:

JSON example
```
"objectDetails":{
        "course" : "Physics",
        "grade" : 97,
        "points" : 5
    }
```

### Special Operations

##### __1. Remove a specific grade:__
If you wish to remove a specific grade you need the following path: http://localhost:8084/superapp/miniapp/miniAppGradeAVG
- you need to write the command: "Remove Grade" as the command in the json that you are sending.
- You will need to provide the objectId of the grade you want to remove (you can get objectId from MongoDB Compass).

JSON example:
```
{
    "command" : "Remove Grade",
    "targetObject":{
        "objectId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "internalObjectId": "478c6fb9-b618-4099-9c24-aae661aa3c80"
        }
    },
    "invokedBy":{
        "userId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "email":"test@gmail.com"
        }
    },
    "commandAttributes":{}
}
```

##### __2. Calculate grades avarage:__
If you wish to calculate user's avarage grades you need the following path: http://localhost:8084/superapp/miniapp/miniAppGradeAVG
- you need to write the command: "Calculate AVG" as the command in the json that you are sending.
- You will need to provide the user id of the user you want to calculate his grades' avarage (you can get userId from MongoDB Compass). you need to write it in the commandAttributes with key "creator".

JSON example:
```
{
    "command" : "Calculate AVG",
    "targetObject":{
        "objectId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "internalObjectId": "3e7fe352-6367-4870-9b3a-d89b57078f48"
        }
    },
    "invokedBy":{
        "userId":{
            "superapp":"2023b.Liran.Sorokin-Student4U",
            "email":"test@gmail.com"
        }
    },
    "commandAttributes":{"creator":"2023b.Liran.Sorokin-Student4U_adam@gmail.com","page":0,"size":15}
}
```
----------------------------------------------------------------------------------------------------------------------------
# Calendar Miniapp

### Prerequisites
Before starting, make sure that the following prerequisites are met:
 - The project is running (Server).
 - MongoDB Compass is connected.
 - Calendar Miniapp is installed on Android device.
 - Both the device and the server are running on the same network (open Command Prompt on the system -> enter command `ipconfig` -> search for `Wi-Fi` table or `Ethernet` table and compare the `IPv4 Address` to the `retroFitIP` constant in the `CONSTANTS` java file under Utils).
 - If not copy the `IPv4 Address` to the specified location above and re-run the application on the device.

### Instructions
1. The database includes premade useres and objects.
2. You can send any HTTP request by using the client application project that wes provided to you.

### Special Operations

#### _Please note that there is no need for the user to change it's role, this done by the system._

##### __1. Login:__
If you already have an account and you wish to login. This page is the default page when you start the application.
If you don't have an account yet, please press on `Don't have an account yet?` in the button of the screen.
- In the textbox under `Username:` please insert the requested username
- In the textbox under `Email:` pleae insert the requested email
- When all is set - click on `LOGIN`

##### __2. Signup:__
If you don't have an account yet or if you wish to create a new account. 
- In the textbox under `Username:` please insert the requested username
- In the textbox under `Email:` please insert the requested email
- Choose an avater by swiping through the avatar images available
- When all is set - click on `SIGN UP`

##### __3. Sign out:__
If you wish to exit the application, please press on the upper-left corner icon

### Under "CALENDAR tab"

All of the features described below are communicating with the server 

##### __4. Get All Event By Chosen Date:__
In the buttom of the screen you have a slider with months and numbers.
- Please select the desired `Month` and `day` (number). #in the date `15-04-2023` there is already one built-in event
- Click the `refresh` button
#### _*note that if there are no events in that specific date nothing will be shown._

##### __5. Add An Event:__
In the buttom of the screen you have a slider with months and numbers.
- After Selecting a specific date and pressed on refresh button
- Click on the Plus `+` button 
- Insert new event's details in the form popup
- Click on `SUBMIT`
- By selecting again on the refresh button it will appear
* Please notice: 'participants' field must be non-null and should be an existing user in the database (dummy@gmail.com always exists)

##### __6. Delete An Event:__
In the buttom of the screen you have a slider with months and numbers.
- After Selecting a specific date and pressed on `refresh` button
- Select on the event the you wish to delete
- In the popup window, press on the `trash` icon
- By selecting again on the `refresh` button this event will disappear

##### __7. Edit An Event:__
In the buttom of the screen you have a slider with months and numbers.
- After Selecting a specific date and pressed on `refresh` button
- Click on the event the you wish to edit
- In the popup window, press on the `pencil` icon
- Insert updated event's details in the form popup
- Click on `SUBMIT`
- By selecting again on the `refresh` button it will appear
*Please notice: 'participants' field must be non-null and should be an existing user in the database (dummy@gmail.com always exists)


### Under "DAILY TASKS tab"

All of the features described below are operated localy

##### 8. Add A New Task:
- Click on the plus `+` button in the buttom-right corner of the screen
- Please enter the task 
- Click on `V` button to save

##### 9. Delete A Task:
Assuming there are at least one task created
- `Long press` on the task you wish to delete

##### 10. Mark Task As Done:
Assuming there are at least one task created
- Pressing on the `square` next to the task will mark it as done

#### _*note that if there is an error during application runtime, please open Postman and use the attached `Calendar.postman_collection.json` file._

## Known issues:
1. In `CalendarMiniApp` - after `login`/`signup`, when the user didn't choose a specific date yet and pressed on `refresh` button, the application crashes.
2. In `CalenadarMiniApp` - when trying to `add` a large number of events and then `delete` large number of events, the application crashes.
3. In `CalenadarMiniApp` - when searcing for a specific date, there is no filter by user and all events will be shown.
