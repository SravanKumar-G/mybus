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
  
For development I recommend using IDE i.e. eclipse, intelliJ etc. Download you favorite IDE and get ready to ROCK!!

#Start database
We will be using mongodb for application database. Start the mongo deamon using the below command in terminal/command prompt
>mongod &

#Checkout project source from github

>git clone git@github.com:srinikandula/mybus.git

#Run the app
You should have a folder created with name 'mybus'. Open a terminal window/command prompt and run the below command
>mvn clean install tomcat7:run

This will clean the previous build artifacts and lauch an embedded tomcat server with the application deployed to it. The log should hint you with the URL to access the web application via browser. Usually it would be on http://localhost:8081/

Happy coding :)








