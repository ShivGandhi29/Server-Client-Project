package nz.ac.vuw.swen301.assignment3.server;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;


public class WhiteBoxTests {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private LogsServlet logsServlet;
    private StatsServlet statsServlet;


    @Before
    public void initialize() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        logsServlet = new LogsServlet();
        statsServlet = new StatsServlet();

    }



    /**
     * doGet method test
     */
    @Test
    public void testValidGetRequestResponseCode() throws IOException {
        request.setParameter("limit","10");
        request.setParameter("level","ALL");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testValidGetRequestResponseCode2() throws IOException {
        request.setParameter("limit","50");
        request.setParameter("level","WARN");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testInvalidGetRequestResponseCode2() throws IOException {
        request.setServletPath("searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(400, response.getStatus());
    }

    @Test
    public void testInvalidGetRequestResponseCode3() throws IOException {
        logsServlet.doGet(request, response);

        assertEquals(400, response.getStatus());
    }



    @Test
    public void testInvalidRequestResponseCode5() throws IOException {
        request.setParameter("limit","51");
        request.setParameter("level","WARN");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(400, response.getStatus());
    }
    @Test
    public void testInvalidRequestResponseCode6() throws IOException {
        request.setParameter("limit","-1");
        request.setParameter("level","WARN");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(400, response.getStatus());
    }

    @Test
    public void testValidGetPathInfo() throws IOException {
        request.setParameter("limit","10");
        request.setParameter("level","ALL");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals("/searchlogs",request.getPathInfo());
    }


    @Test
    public void testGetDataSizeReturned() throws IOException {
        MockHttpServletRequest req2 = new MockHttpServletRequest();
        req2.setPathInfo("/addlog");
        logsServlet.doPost(createValidMultiplePostRequest(req2),new MockHttpServletResponse());

        request.setParameter("limit","10");
        request.setParameter("level","ALL");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        String s = response.getContentAsString();

        System.out.println(response.getContentAsString());
        JSONArray array = new JSONArray(s);
        assertEquals(3,array.length());
    }


    @Test
    public void testWrongNameParameter() throws IOException {
        request.addParameter("wrongName","10");
        request.addParameter("wrongName","ALL");
        request.setPathInfo("/searchlogs");
        logsServlet.doGet(request, response);

        assertEquals(400,response.getStatus());
    }

    /**
     * doPost method test
     */
    @Test
    public void testValidPostPathInfo() throws IOException {
        request.setPathInfo("/addlog");
        logsServlet.doPost(createValidPostRequest(request), response);

        assertEquals("/addlog",request.getPathInfo());
    }

    @Test
    public void test400PostPath() throws IOException{
        String s = null;
        request.setPathInfo(s);
        logsServlet.doPost(request, response);
        assertEquals(400, response.getStatus());
    }
    @Test
    public void testGetLogs(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        logsServlet = new LogsServlet();
        request.setPathInfo("/searchlogs");

        LogsServlet.getLogs().clear();

        assertEquals("[]", LogsServlet.getLogs().toString());
    }
    @Test
    public void testDuplicatePostRequest() throws  IOException {
        request = createValidPostRequest(request);
        JSONArray dupeArray = new JSONArray(request.getContentAsString());
        String id = (String) dupeArray.getJSONObject(0).get("id");
        logsServlet.doPost(request, response);
        request = createValidPostRequest(request);
        dupeArray = new JSONArray(request.getContentAsString());
        dupeArray.getJSONObject(0).put("id", id);
        byte[] contentBody = dupeArray.toString().getBytes();
        request.setContent(contentBody);
        logsServlet.doPost(request, response);
        assertEquals(409, response.getStatus());
    }

    /**
     * LogEvent toString tests
     */
    @Test
    public void testLogEvent() throws IOException{
        request = createValidPostRequest(request);
        JSONArray array = new JSONArray(request.getContentAsString());
        logsServlet.doPost(request, response);
        request = createValidPostRequest(request);

        String s = "{" + "\"level\":\"" + array.getJSONObject(0).get("level") + "\"" +
                    ",\"logger\":\"" + array.getJSONObject(0).get("logger") + "\"" +
                    ",\"id\":\"" + array.getJSONObject(0).get("id") + "\"" + ",\"thread\":\"thread-test\"" +
                    ",\"message\":\"" + array.getJSONObject(0).get("message") + "\"" +
                    ",\"timestamp\":\"" + array.getJSONObject(0).get("timestamp") + "\"" +
                    ",\"errorDetails\":\"" + array.getJSONObject(0).get("errorDetails") + "\"" + '}';

        assertEquals(array.getJSONObject(0).toString(), s );
    }
    @Test
    public void TestLogContentType() throws IOException {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setParameter("limit", "50");
        request.setParameter("level", "debug");
        response.getContentType();
        // query parameter missing
        LogsServlet service = new LogsServlet();

        service.doGet(request,response);

        assertEquals("application/json", response.getContentType());

    }


    @Test
    public void testStats1() throws IOException {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setParameter("limit", "50");
        request.setParameter("level", "debug");
        response.getStatus();
        // query parameter missing
        StatsServlet service = new StatsServlet();
        service.doGet(request,response);

        assertEquals(200, response.getStatus());

    }


    @Test
    public void testStats2() throws IOException {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setParameter("limit", "50");
        request.setParameter("level", "debug");
        response.getContentType();
        // query parameter missing
        StatsServlet service = new StatsServlet();

        service.doGet(request,response);


        assertEquals("application/vnd.ms-excel", response.getContentType());

    }

    @Test
    public void testValidStatsServlet() throws Exception{
        logsServlet.doPost(createValidPostRequest(new MockHttpServletRequest()), new MockHttpServletResponse());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setServletPath("/stats");
        statsServlet.doGet(request,response);
        assertEquals(response.getStatus(), 200);
        assertEquals(response.getContentType(), "application/vnd.ms-excel");

    }




    private MockHttpServletRequest createValidPostRequest(MockHttpServletRequest request){

        JSONObject testLog = new JSONObject();
        testLog.put("id", UUID.randomUUID().toString());
        testLog.put("message", "test");
        testLog.put("timestamp", "2019-06-14T01:37:35.628Z");
        testLog.put("thread", "thread-test");
        testLog.put("logger", "white box test");
        testLog.put("level", Level.DEBUG);
        testLog.put("errorDetails", "");

        JSONArray testObjArray = new JSONArray();
        testObjArray.put(testLog);
        byte[] contentBody = testObjArray.toString().getBytes();
        request.addHeader("Content-Type", "application/json");
        request.setPathInfo("/addlog");
        request.setContent(contentBody);
        request.setCharacterEncoding("UTF-8");

        return request;
    }
    private MockHttpServletRequest createValidMultiplePostRequest(MockHttpServletRequest request){

        JSONObject testLog = new JSONObject();
        JSONObject testLog2 = new JSONObject();
        JSONObject testLog3 = new JSONObject();
        JSONArray testObjArray = new JSONArray();

        testLog.put("id", UUID.randomUUID().toString());
        testLog.put("message", "test");
        testLog.put("timestamp", "2019-06-14T01:37:35.628Z");
        testLog.put("thread", "thread-test1");
        testLog.put("logger", "white box test");
        testLog.put("level", Level.INFO);
        testLog.put("errorDetails", "");
        testObjArray.put(testLog);

        testLog2.put("id", UUID.randomUUID().toString());
        testLog2.put("message", "test");
        testLog2.put("timestamp", "2019-06-14T01:37:35.628Z");
        testLog2.put("thread", "thread-test2");
        testLog2.put("logger", "white box test");
        testLog2.put("level", Level.WARN);
        testLog2.put("errorDetails", "");
        testObjArray.put(testLog2);

        testLog3.put("id", UUID.randomUUID().toString());
        testLog3.put("message", "test");
        testLog3.put("timestamp", "2019-06-14T01:37:35.628Z");
        testLog3.put("thread", "thread-test3");
        testLog3.put("logger", "white box test");
        testLog3.put("level", Level.DEBUG);
        testLog3.put("errorDetails", "");
        testObjArray.put(testLog3);

        byte[] contentBody = testObjArray.toString().getBytes();
        request.addHeader("Content-Type", "application/json");
        request.setPathInfo("/addlog");
        request.setCharacterEncoding("UTF-8");
        request.setContent(contentBody);

        return request;
    }

    @After
    public void reset(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
}
