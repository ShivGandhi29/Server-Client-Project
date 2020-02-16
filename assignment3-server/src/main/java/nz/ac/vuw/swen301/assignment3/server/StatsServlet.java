package nz.ac.vuw.swen301.assignment3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;


public class StatsServlet extends HttpServlet {

    private List<String> loggers = new ArrayList<>();
    private List<Level> levels = new ArrayList<>();
    private List<String> threads = new ArrayList<>();


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Log Stats");
        Map<String, Object[]> data = new TreeMap<>();


        List<LogEvent> logs = new ArrayList<>(LogsServlet.getLogs());


        for (LogEvent l: logs){
            if (!loggers.contains(l.getLogger()))
                loggers.add(l.getLogger());
            if (!levels.contains(l.getLevel()))
                levels.add(l.getLevel());
            if (!threads.contains(l.getThread()))
                threads.add(l.getThread());
        }


        List<String> rowNames = new ArrayList<>();
        List<Level> levelList = new ArrayList<>(levels);
        List<String> threadList = new ArrayList<>(threads);
        StringBuilder sb = new StringBuilder();

        while (loggers.size() > 0) {
            while (threadList.size() > 0) {
                while (levelList.size() > 0) {
                    sb.append(loggers.get(0)).append(", ");
                    sb.append(threads.get(0)).append(", ");
                    sb.append(levelList.get(0));
                    levelList.remove(0);
                    if (!rowNames.contains(sb.toString()))
                        rowNames.add(sb.toString());
                    sb.setLength(0);
                }
                levelList = new ArrayList<>(levels);
                threadList.remove(0);
            }
            threadList = new ArrayList<>(threads);
            loggers.remove(0);
        }


        //build the data rows
        for (int i = 0; i < (rowNames.size()); i++) {

            Map<String, int[]> rows = new HashMap<>();
            for (String name: rowNames){
                rows.put(name, new int[]{0});
            }

            String dateOfLogs = "";

            for (LogEvent l: logs){
                for (String currName : rowNames){
                    System.out.println(currName);
                    String currLogger = l.getLogger();
                    String currLevel = l.getLevel().toString();




                    if (currName.contains(currLogger)){

                        if (currName.contains(currLevel)){

                            String[] split = l.getTimeStamp().split(" ");
                            String day = split[0];
                            String month = split[1];
                            String year = split[5];

                            dateOfLogs = day + month + year;
                            rows.put(dateOfLogs, new int[]{0});
                            System.out.println(dateOfLogs);
                            for (String row : rows.keySet()) {
                                if (row.contains(currLevel)){
                                    rows.get(row)[0]++;
                                }
                            }

                        }
                    }


                }
            }

            String cur = rowNames.get(i);

            if (i == 0)
                data.put("0", new Object[]{"Log Information", dateOfLogs});
            else
                data.put(Integer.toString(i), new Object[]{cur,rows.get(cur)[0]});
                cur = rowNames.get(0);
                data.put(Integer.toString(i + 1), new Object[]{cur, rows.get(cur)[0]});
        }



        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {

            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {

                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
            }
        }

        response.setStatus(200);
        response.setContentType("application/vnd.ms-excel"); // should be excel format
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

    }


}
