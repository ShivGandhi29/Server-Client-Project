# README

##assignment3-client

Log4J 1.2.17 appender Resthome4LogsAppender that uses the logs/ post service developed in the
server part to send log events to the server
##### @BEFORE

mvn clean compile

### Building the Jars
#### Standalone jar
To produce a standalone jar for CreateRandomLogs and LogMonitor you must have the server running and then
 use the command:

###### mvn package

The jars appear in the Target folder. They can be run with commands:

###### java -jar random.jar


###### java -jar logMonitor.jar

### JaCoCo
To run this:

mvn clean test

mvn jacoco:report

##### Analysis
The JaCoCo report details that nz.ac.vuw.swen301.assignment3.client.Resthome4LogsAppender has an overall of 97% coverage.

A further indepth analysis shows that :

- The only line that isnt being covered is Line 94 which is a catch exception.

### SpotBugs
To run this:

mvn compile site

View report at target/site/spotbugs.html

##### Analysis

After analysing the spot bugs report, I initially would 10 problems in my code. 7 of these were fixed by simply fixing the
warnings and closing methods properly. The remaining 3 bugs remained untouched as they will require n0 significant change
to my project.

The first bug is in nz.ac.vuw.swen301.assignment3.client.LogMonitor.sendGet(URI) The error is stating that it has
found reliance on default encoding for new java.io.InputStreamReader(InputStream). It falls under the I188N category which means: 
"Found a call to a method which will perform a byte to String (or String to byte) conversion, and will assume that the default
platform encoding is suitable. This will cause the application behaviour to vary between platforms.".
I was unable to fix this as the way I have written it makes it hard to change.

The second bug is in nz.ac.vuw.swen301.assignment3.client.LogMonitor.recieveExcel(HttpResponse). I recieve the message:
"may fail to clean up java.io.OutputStream on checked exception". I'm unsure of this bug has I cleaned up the outputStream
and closed it. This is deemed in the EXPERIMENTAL category.

The third bug is in the nz.ac.vuw.swen301.assignment3.client.Resthome4LogsAppender.sendPost(JSONArray). I recieve the error:
"Found reliance on default encoding: new java.io.InputStreamReader(InputStream)". This is unfixable this late in the project
development as it requires fundimental change to my code. It falls under the category: I18N: This method may fail to clean up
(close, dispose of) a stream, database object, or other resource requiring an explicit cleanup operation.

### JDepend

To run this:

mvn jdepend:generate

##### Analysis

My jdepend report returns a distance of 0.0%, therefore no discussion is required in accordance to the assignment handout.
