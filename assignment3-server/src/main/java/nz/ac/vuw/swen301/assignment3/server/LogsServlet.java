package nz.ac.vuw.swen301.assignment3.server;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.sort;

public class LogsServlet extends HttpServlet {


    private static List<LogEvent> logs = new ArrayList<>();
    private static final int MINIMUM_LIMIT = 0;
    private static final int MAXIMUM_LIMIT = 50;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);



    /**
     * searches the logs
     */
    private JSONArray searchLogs(HttpServletRequest request) {

        int limit = Integer.parseInt(request.getParameter("limit"));
        Level currentLevel = Level.stringToLevel(request.getParameter("level"));


        List<JSONObject> foundLogs = new ArrayList<>(limit);
        System.out.println(limit);
        sort(logs);
        for (LogEvent log : logs) {


            assert currentLevel != null;
            if (foundLogs.size() < limit && limit <= MAXIMUM_LIMIT && log.getLevel().getPriority() >= Objects.requireNonNull(currentLevel).getPriority()) {
                foundLogs.add(log.convertToJSON());
            }
        }
        System.out.println(foundLogs.size());

        return new JSONArray(foundLogs);
    }

    static List<LogEvent> getLogs(){
        return logs;
    }



    /**
     * Adds logs to log array
     * @param requestObj request
     */

    private void addLog(JSONObject requestObj) {
        String id = (String) requestObj.get("id");
        String message = (String) requestObj.get("message");
        Date timestamp = null;
        try {
            timestamp = dateFormat.parse((String) requestObj.get("timestamp"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String thread = (String) requestObj.get("thread");
        String logger = (String) requestObj.get("logger");
        Level level = Level.stringToLevel((String) requestObj.get("level"));
        String errorDetails = (String) requestObj.get("errorDetails");

        logs.add(new LogEvent(id, message, timestamp, thread, logger, level, errorDetails));
    }


    /**
     * standard doGet Method
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {



        if (request.getParameter("limit") == null){
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            response.sendError(HttpStatus.BAD_REQUEST_400);
            return;
        }
        if (Integer.parseInt(request.getParameter("limit")) > MAXIMUM_LIMIT || Integer.parseInt(request.getParameter("limit")) < MINIMUM_LIMIT ) {
            response.setContentType("text/plain");
            response.setStatus(400);
            response.getWriter().println("ERROR 400: bad input parameter: limit greater than MAXIMUM, limit: "+ MAXIMUM_LIMIT + ", actual: "
                    + Integer.parseInt(request.getParameter("limit")));
            System.out.println("Rsponse status -------------->"+response.getStatus());
            return;
        }


        JSONArray jArrayLogs = searchLogs(request);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jArrayLogs);

        out.flush();
        out.close();
        response.setStatus(201);
        response.getWriter().println("search results matching criteria");

    }

    /**
     * Standard doPost method
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(201);
        response.getWriter().println("NOT A BAD REQUEST");
        if (request.getPathInfo() == null) {
            System.out.println(request.getPathInfo());
            response.setStatus(400);
            response.getWriter().println("ERROR 400: invalid input, object invalid");

            return;
        }

        JSONArray requestArray;


        StringBuilder stringBuffer = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                stringBuffer.append(line);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            requestArray = new JSONArray(stringBuffer.toString());
        } catch (JSONException e) {

            throw new IOException("Error parsing JSON request string");
        }
        for (int i = 0; i < requestArray.length(); i++) {
            JSONObject requestObj = requestArray.getJSONObject(i);



            for (LogEvent l : logs){
                if (l.getId().equals(requestObj.get("id"))){
                    response.setStatus(409);
                    response.getWriter().println("ERROR 409: a log event with this id already exists");
                    return;
                }
            }try {

                addLog(requestObj);
            }catch (Exception e){}
        }

        response.setStatus(201);
        response.getWriter().print(requestArray.length() + " items created\n" + logs.size() + " logs in server");
    }
}
