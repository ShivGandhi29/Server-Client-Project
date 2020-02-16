package nz.ac.vuw.swen301.assignment3.client;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Questions:
 * stats, how does it need to be formatted
 * is message fine, or does it need to print something else
 * how do i test appender
 * assignment 2 marks
 */

public class CreateRandomLogs {

    private static Logger logger = Logger.getLogger(CreateRandomLogs.class);


    public static void main(String[] args) {

        Random r = new Random();
        logger.setLevel(Level.ALL);
        logger.addAppender(new Resthome4LogsAppender());

        TimerTask timerTask = new TimerTask() {
            public void run() {

                int lvl = r.nextInt(6);
                switch (lvl) {
                    case 0:
                        logger.debug("debug message");
                        break;
                    case 1:
                        logger.info("info message");
                        break;
                    case 2:
                        logger.warn("warn message");
                        break;
                    case 3:
                        logger.error("error message");
                        break;
                    case 4:
                        logger.fatal("fatal message");
                        break;
                    case 5:
                        logger.trace("trace message");
                        break;
                    default:
                        break;
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 500);

    }

}
