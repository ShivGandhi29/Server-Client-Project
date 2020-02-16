package nz.ac.vuw.swen301.assignment3.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class Resthome4LogsAppender extends AppenderSkeleton {


    private ArrayList<JSONObject> bufArray = new ArrayList<>();


    @Override
    protected void append(LoggingEvent loggingEvent) {

        UUID uuid = UUID.randomUUID();

        String id = uuid.toString();
        String message = loggingEvent.getRenderedMessage();
        String date =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(new Date(loggingEvent.getTimeStamp()));
        String thread = loggingEvent.getThreadName();
        String logger = loggingEvent.getLoggerName();
        Level level = loggingEvent.getLevel();
        String errorDetails = "";

        //adding all of the variables to json obj
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("message", message);
        obj.put("timestamp", date);
        obj.put("thread", thread);
        obj.put("logger", logger);
        obj.put("level", level);
        obj.put("errorDetails", errorDetails);

        bufArray.add(obj);
        if (bufArray.size() >= 10) {
            sendPost(new JSONArray(bufArray));
            this.bufArray.clear();
        }

    }

    /**
     * sends the JSONArray buffer to the server.
     * @param  buffer JSONArray
     */
    private void sendPost(JSONArray buffer){
        try {
            URI url = new URL("http://localhost:8080/resthome4logs/logs/addLogs").toURI();

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringEntity input = new StringEntity(buffer.toString());
            input.setContentType("application/json;charset=UTF-8");
            post.setEntity(input);
            HttpResponse response = client.execute(post);

            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append(System.getProperty("line.separator"));
            }


            System.out.println(result.toString());
            rd.close();

        } catch (Exception e) {e.fillInStackTrace(); }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
