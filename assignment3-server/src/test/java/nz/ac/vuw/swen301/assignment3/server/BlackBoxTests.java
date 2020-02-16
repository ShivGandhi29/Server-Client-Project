package nz.ac.vuw.swen301.assignment3.server;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlackBoxTests {
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private static final String TEST_PATH = "/resthome4logs"; // as defined in pom.xml
    private static final String SERVICE_PATH = TEST_PATH + "/logs"; // as defined in pom.xml and web.xml
    private static final String STATS_SERVICE_PATH = TEST_PATH + "/stats"; // as defined in pom.xml and web.xml

    /**
     *          Test to do
     *   409 post
     *   Data return
     *
     */



    @BeforeClass
    public static void startServer() throws Exception {
        Runtime.getRuntime().exec("mvn jetty:run");
        Thread.sleep(10000);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        Runtime.getRuntime().exec("mvn jetty:stop");
        Thread.sleep(4000);
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

    /**
     * Test Status codes 200
     */
    @Test
    public void testStatusCode200 () throws Exception {
       // startServer();
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("level","all")
                .setParameter("limit", "40");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
        //stopServer();
    }

    @Test
    public void testStatusCode200Stats () throws Exception {
        // startServer();
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STATS_SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
        //stopServer();
    }

    /**
     * Test Status codes 201
     */
    @Test
    public void testStatusCode201() throws Exception{
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH+"/addLogs");
        URI uri = builder.build();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = createValidPostRequest(new HttpPost(uri));

        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine().getReasonPhrase());

        System.out.println(response);
        assertEquals(201, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testStatusCode400 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("level","all")
                .setParameter("limit", "51");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testPost409() throws Exception {
        Assume.assumeTrue(isServerReady());
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH+"/addLogs");
        URI uri = builder.build();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(uri);
        HttpResponse response1 = get(uri);



        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();

        obj.put("id", "Ca5c6631-1be8-42c1-9008-be3f276518c7");
        obj.put("message", " message");
        obj.put("timestamp", format.format(new Date().getTime()));
        obj.put("thread", "thread");
        obj.put("logger", "logger");
        obj.put("level", "ALL");
        obj.put("errorDetails", "error details");
        array.put(obj);

        obj2.put("id", "Ca5c6631-1be8-42c1-9008-be3f276518c7");
        obj2.put("message", "message");
        obj2.put("timestamp", format.format(new Date().getTime()));
        obj2.put("thread", "thread");
        obj2.put("logger", "logger");
        obj2.put("level", "ALL");
        obj2.put("errorDetails", "error details");
        array.put(obj2);

        postRequest.setHeader("Content-Type", "application/json");
        StringEntity se = new StringEntity(array.toString());
        postRequest.setEntity(se);
        HttpResponse response = httpClient.execute(postRequest);


        String resp = EntityUtils.toString(response.getEntity());

        assertEquals(409,response.getStatusLine().getStatusCode());

    }

    /**
     * COMPLETE TEST AFTER
     */

    @Test
    public void testStatusCode400Post () throws Exception {

        //startServer();
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("level","all")
                .setParameter("limit", "51");
        URI uri = builder.build();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = createValidPostRequest(new HttpPost(uri));
        HttpResponse response = client.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
    }




    /**
     * End test status codes
     */

    /**
     * Test Content types
     */
    @Test
    public void testJSONContentType() throws Exception{
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
        .setParameter("limit", "50")
        .setParameter("level", "debug");
        URI uri = builder.build();

        HttpRequest request = new HttpGet(uri);
        HttpResponse response = get(uri);
        assertEquals("Content-Type: application/json;charset=utf-8", response.getEntity().getContentType().toString());
    }

    @Test
    public void testExcelContentType() throws Exception{
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STATS_SERVICE_PATH);
        URI uri = builder.build();

        HttpRequest request = new HttpGet(uri);
        HttpResponse response = get(uri);
        assertEquals("Content-Type: application/vnd.ms-excel", response.getEntity().getContentType().toString());
    }

    @Test
    public void testReturnedValues() throws Exception{
      // startServer();
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("level","info")
                .setParameter("limit", "2");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());
        String[] logs = content.split(" ");
        Set<String> set = Arrays.stream(logs).collect(Collectors.toSet());

        assertTrue(!set.isEmpty());
        System.out.println(set);
        //stopServer();

    }

    /**
     * data returned tests
     **/


    private HttpPost createValidPostRequest(HttpPost post){

        List<JSONObject> list = new ArrayList<>();

        JSONObject testLog = new JSONObject();
        testLog.put("id", UUID.randomUUID().toString());
        testLog.put("message", "test");
        testLog.put("timestamp", "2019-06-14T01:37:35.628Z");
        testLog.put("thread", "thread-test");
        testLog.put("logger", "black box test");
        testLog.put("level", "DEBUG");
        testLog.put("errorDetails", "");
        list.add(testLog);
        JSONArray array = new JSONArray(list);
        StringEntity input = new StringEntity(array.toString(), ContentType.APPLICATION_JSON);
        input.setContentType("application/json;charset=UTF-8");

        post.setEntity(input);
        return post;
    }




}
