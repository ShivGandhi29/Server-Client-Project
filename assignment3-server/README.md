# README

##assignment3-server

A server application that provides the services specified in the swagger API

##### @BEFORE

mvn clean compile

### JaCoCo
To run this:

mvn clean test

mvn jacoco:report

##### Analysis
The JaCoCo report details that nz.ac.vuw.swen301.assignment3.server has an overall of 90% coverage.

A further indepth analysis shows that :

- StatsServlet has 95% coverage

    - Line 117 in the doGet(HttpServlet, HttpServletResponse) was not covered as my test for some reason decided to
    skip it and cover the 2 lines below it instead. I
    discovered this very late in the development process and was unable to complete this due to time constraints.

- LogsServlet has a 94% coverage

    - addLog(JSONObject) Line 73/74 catch exception missing coverage

    - searchLogs(HttpServletRequest) Line 46/47 branches missed. This is due to an oversight and time constraints.

    - doPost(HttpServletRequest, HttpServletResponse) Line 149/151 catch exception missing coverage

 - Level has 92% coverage

    - Level only has 44% of branches covered due to me not testing ERROR, FATAL, TRACE and OFF Levels. This was an
    oversight on my part due to time constraints.

  - LogEvent(String, String, Date, String, String, Level, String) has 65% coverage

    - toString method has 0% covered. I am not sure why this is as I have written a test for it. Every other method
    has 100% code coverage.

Overall, I believe that my tests have sufficient coverage for the Logs and Stats Servlet.

### SpotBugs
To run this:

mvn compile site

View report at target/site/spotbugs.html

##### Analysis

After analysing the spot bugs report, I initially would 8 problems in my code. 6 of these were fixed by simply fixing the
warnings. The remaining 2 bugs remained untouched as they will require na significant change to my project.

The first bug is in nz.ac.vuw.swen301.assignment3.server.LogEvent and is defined as compareTo(Object) and uses Object.equals().
This is deemed in the BAD_PRACTISE category. I was unable to fix this as the way I have written it makes it hard to change.

The second bug is in nz.ac.vuw.swen301.assignment3.server.StatsServlet and is in the doGet(HttpServletRequest, HttpServletResponse)
method. This makes inefficient use of keySet iterator instead of entrySet iterator. I had attempted to fix this, but replacing
keySet with entrySet results in a failure to display the logs in the GUI. This is deemed in the PERFORMANCE category

### JDepend

To run this:

mvn jdepend:generate

##### Analysis

My jdepend report returns a distance of 0.0%, therefore no discussion is required in accordance to the assignment handout.
