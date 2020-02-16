package nz.ac.vuw.swen301.assignment3.server;

public enum Level {
    ALL(0),TRACE(5), DEBUG(10), INFO(15), WARN(20), ERROR(25), FATAL(30), OFF(35);

    private int value;

    Level(int val) {
        this.value = val;
    }

    public int getPriority() {
        return value;
    }


    static Level stringToLevel(String s) {
        s = s.toLowerCase();
        switch (s) {
            case "all":
                return Level.ALL;
            case "trace":
                return Level.TRACE;
            case "debug":
                return Level.DEBUG;
            case "info":
                return Level.INFO;
            case "warn":
                return Level.WARN;
            case "error":
                return Level.ERROR;
            case "fatal":
                return Level.FATAL;
            case "off":
                return Level.OFF;
            default:
                break;
        }
        return null;
    }

}
