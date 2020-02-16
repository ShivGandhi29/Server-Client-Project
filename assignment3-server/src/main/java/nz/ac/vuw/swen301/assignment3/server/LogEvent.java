package nz.ac.vuw.swen301.assignment3.server;

import org.json.JSONObject;

import java.util.Date;

public class LogEvent implements Comparable {

    private String id;

    private String message;

    private Date timestamp;

    private String thread;

    private String logger;

    private Level level;

    private String errorDetails;



    LogEvent(String uuid, String message, Date timestamp, String thread, String logger, Level level, String errorDetails){
        this.id = uuid;
        this.message = message;
        this.timestamp = timestamp;
        this.thread = thread;
        this.logger = logger;
        this.level = level;
        this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", thread='" + thread + '\'' +
                ", logger='" + logger + '\'' +
                ", level=" + level +
                ", errorDetails='" + errorDetails + '\'' +
                '}';
    }

    String getId() {
        return this.id;
    }

    Level getLevel(){
        return this.level;
    }

    String getLogger(){
        return this.logger;
    }

    String getThread(){
        return this.thread;
    }



    /**
     * Comparing timestamps to get the lastest one.
     * @param o takes object o
     * @return returns the timestamps to get the latest one
     */
    @Override
    public int compareTo(Object o) {
        LogEvent log = (LogEvent) o;
        return log.timestamp.compareTo(this.timestamp);
    }

    JSONObject convertToJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("message", this.message);
        obj.put("timestamp", this.timestamp);
        obj.put("thread", this.thread);
        obj.put("logger", this.logger);
        obj.put("level", this.level.toString());
        obj.put("errorDetails", this.errorDetails);
        return obj;
    }


    public String getTimeStamp() {
        return this.timestamp.toString();
    }
}
