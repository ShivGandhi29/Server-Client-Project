package nz.ac.vuw.swen301.assignment3.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Resthome4LogsAppenderTest {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private static final String TEST_PATH = "/resthome4logs"; // as defined in pom.xml
    private static final String SERVICE_PATH = TEST_PATH + "/logs"; // as defined in pom.xml and web.xml

    private static Logger logger = Logger.getLogger(Resthome4LogsAppenderTest.class);

    @Before
    public void init(){
        logger = Logger.getLogger(Resthome4LogsAppenderTest.class);
        logger.addAppender(new Resthome4LogsAppender());
        logger.setLevel(Level.ALL);
    }
//set 9 of the same level, and change the last one. - note from david
    @After
    public void after(){
        logger.removeAllAppenders();
    }
    private HttpResponse get(URI uri) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        return httpClient.execute(request);
    }
    private boolean isServerReady() throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(TEST_PATH);
        URI uri = builder.build();
        try {
            HttpResponse response = get(uri);
            boolean success = response.getStatusLine().getStatusCode() == 200;

            if (!success) {
                System.err.println("Check whether server is up and running, request to " + uri + " returns " + response.getStatusLine());
            }

            return success;
        }
        catch (Exception x) {
            x.printStackTrace();
            System.err.println("Encountered error connecting to " + uri + " -- check whether server is running and application has been deployed");
            return false;
        }
    }


    @Test
    public void testServerConnection() throws Exception{
        assert isServerReady();
    }

    @Test
    public void testAValidAppend5() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 10 ; i++ ) {
            logger.trace("test trace");
        }

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "trace");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(10, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "TRACE");
        }
    }
    @Test
    public void testAValidAppend6() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 10 ; i++ ) {
            logger.fatal("test fatal");
        }

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "fatal");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(10, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "FATAL");
        }
    }



    @Test
    public void testAValidAppend4() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 9 ; i++ ) {
            logger.debug("test debug");
        }
        logger.error("test error");
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "error");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(1, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "ERROR");
        }
    }

    @Test
    public void testAValidAppend3() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 9 ; i++ ) {
            logger.debug("test debug");
        }
        logger.warn("test warn");
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "warn");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(1, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "WARN");
        }
    }

    @Test
    public void testAValidAppend2() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 9 ; i++ ) {
            logger.debug("test debug");
        }
        logger.info("test info");
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "info");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(1, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "INFO");
        }
    }

    /**
     * Tests that 10 Debug level logs get sent to the server
     * after this total debug level logs in server is 10
     */
    @Test
    public void testAValidAppend1() throws Exception {
        Assume.assumeTrue(isServerReady());

        for (int i = 0 ; i < 10 ; i++ ) {
            logger.debug("test debug");
        }
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "DEBUG");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        JSONArray array = new JSONArray(streamAsString(response.getEntity()));

        Assert.assertEquals(10, array.length());
        for (int i = 0 ; i < array.length() ; i++) {
            Assert.assertEquals(array.getJSONObject(i).get("level"), "DEBUG");
        }
    }


    @Test
    public void requiresLayoutTest() throws Exception{
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level", "DEBUG");


        Resthome4LogsAppender appender = new Resthome4LogsAppender();
        assertFalse(appender.requiresLayout());

    }

    @Test
    public void setLevelForAllLogsTest(){

        Logger debugLogger = Logger.getLogger("DEBUG");
        Logger warnLogger = Logger.getLogger("WARN");
        Logger allLogger = Logger.getLogger("ALL");
        Logger fatalLogger = Logger.getLogger("FATAL");
        Logger traceLogger = Logger.getLogger("TRACE");
        Logger infoLogger = Logger.getLogger("INFO");
        Logger errorLogger = Logger.getLogger("ERROR");
        Logger offLogger = Logger.getLogger("OFF");

        debugLogger.setLevel(Level.DEBUG);
        warnLogger.setLevel(Level.WARN);
        allLogger.setLevel(Level.ALL);
        fatalLogger.setLevel(Level.FATAL);
        traceLogger.setLevel(Level.TRACE);
        infoLogger.setLevel(Level.INFO);
        errorLogger.setLevel(Level.ERROR);
        offLogger.setLevel(Level.OFF);


        assertSame(debugLogger.getLevel(), Level.DEBUG);
        assertSame(warnLogger.getLevel(), Level.WARN);
        assertSame(allLogger.getLevel(), Level.ALL);
        assertSame(fatalLogger.getLevel(), Level.FATAL);
        assertSame(traceLogger.getLevel(), Level.TRACE);
        assertSame(infoLogger.getLevel(), Level.INFO);
        assertSame(errorLogger.getLevel(), Level.ERROR);
        assertSame(offLogger.getLevel(), Level.OFF);

    }

    @Test
    public void logsInOrderTest(){
        Resthome4LogsAppender appender = new Resthome4LogsAppender();

        Logger debugLogger = Logger.getLogger("DEBUG");
        Logger warnLogger = Logger.getLogger("WARN");

        debugLogger.addAppender(appender);
        warnLogger.addAppender(appender);

        debugLogger.setLevel(Level.DEBUG);
        warnLogger.setLevel(Level.WARN);

        java.util.List<Logger> logs = new ArrayList<>();

        try{
            debugLogger.debug("DEBUG");
            warnLogger.warn("WARN");

            logs.add(debugLogger);
            logs.add(warnLogger);


        }catch (Exception e){
            debugLogger.error("Error", e);
            warnLogger.error("Error", e);
        }

        assertEquals(logs.get(0), debugLogger);
        assertEquals(logs.get(1), warnLogger);

    }





    @Test
    public void close() {
    }

    @Test
    public void requiresLayout() {
    }













    /**
     * returns an entity content as a string
     */
    private String streamAsString(HttpEntity entity) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(entity.getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
