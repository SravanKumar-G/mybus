## mybus
#Developer Setup
* Install Java8 or later -- http://www.oracle.com/technetwork/java/javase/downloads/index.html

  command to verify installation 
  >javac -version
  >java -version
* Install mongodb 3.0.7 or later -- https://docs.mongodb.org/manual/installation/
  
  command to verify installation 
  > mongod -version
* Install Apache Maven 3.2.3 or later -- https://maven.apache.org/install.html

  command to verify installation 
  >mvn -version
* Install git -- https://help.github.com/articles/set-up-git/

  command to verify installation 
  >git --version
  
#Start database
We will be using mongodb for application database. Start the mongo deamon using the below command in terminal/command prompt
>mongod &

#Checkout project source from github

>git clone git@github.com:srinikandula/mybus.git

#Run the app
You should have a folder created with name 'mybus'. Open a terminal window/command prompt and run the below command
>mvn clean install tomcat7:run

For development I recommend using IDE i.e. eclipse, intelliJ etc. Download you favorite IDE. If you plan to use Eclipse run the below command to create eclise related artifacts.
  >mvn eclipse:eclipse

This will clean the previous build artifacts and lauch an embedded tomcat server with the application deployed to it. The log should hint you with the URL to access the web application via browser. Usually it would be on http://localhost:8081/

Happy coding :)

#Lombok setup
The project uses lombok(https://projectlombok.org/) for leveraging various features of it. If you are using an IDE for development you need to install respective plugin to have source code compiled successfully. If you are using eclipse please use this link for instructions configuring eclipse.
http://stackoverflow.com/questions/22310414/how-to-configure-lombok-in-eclipse-luna

If you are using intelliJ install lombok plugin from marketplace.







