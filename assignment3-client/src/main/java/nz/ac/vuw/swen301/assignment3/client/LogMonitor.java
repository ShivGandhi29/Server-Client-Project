package nz.ac.vuw.swen301.assignment3.client;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class LogMonitor extends JFrame implements ActionListener {

    /**
     * uses doGet from Server
     */

    private static JFrame frame = new JFrame();
    private JTextField textField;
    private static String storeLimit = "10";
    private static String minLevel = "DEBUG";

    private static DefaultTableModel tableModel = new DefaultTableModel(0, 0);
    private static JSONArray localData;
    private static JButton submitButton;
    private static URI uri;

    private LogMonitor() {
        super("LOG Monitor");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
    }

    private static void sendGet(URI uri) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        System.out.println("\nSending 'GET' request to URL : " + uri);


        if (response.getEntity().getContentType().toString().contains("application/vnd.ms-excel")) {
            recieveExcel(response);
            return;
        }

        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        if (response.getStatusLine().getStatusCode() == 400){
            System.out.println(result.toString());
            return;
        }


        localData = new JSONArray(result.toString());
        displayData();
        localData = new JSONArray();
    }

    private static void displayData() {

        clearCells();

        for (int i = 0; i < localData.length(); i++) {
            String id = localData.getJSONObject(i).get("id").toString();
            String message = localData.getJSONObject(i).get("message").toString();
            String timestamp = localData.getJSONObject(i).get("timestamp").toString();
            String thread = localData.getJSONObject(i).get("thread").toString();
            String logger = localData.getJSONObject(i).get("logger").toString();
            String level = localData.getJSONObject(i).get("level").toString();
            tableModel.setValueAt(id, i, 0);
            tableModel.setValueAt(message, i, 1);
            tableModel.setValueAt(timestamp, i, 2);
            tableModel.setValueAt(thread, i, 3);
            tableModel.setValueAt(logger, i, 4);
            tableModel.setValueAt(level, i, 5);
            tableModel.fireTableDataChanged();
        }

    }

    private static void clearCells() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("", i, 0);
            tableModel.setValueAt("", i, 1);
            tableModel.setValueAt("", i, 2);
            tableModel.setValueAt("", i, 3);
            tableModel.setValueAt("", i, 4);
            tableModel.setValueAt("", i, 5);
            tableModel.fireTableDataChanged();
        }
    }

    private static void recieveExcel(HttpResponse response) {
        if (response.getEntity() != null) {
            try {
                InputStream is = response.getEntity().getContent();
                String path = System.getProperty("user.dir");
                path = path + "/logs.xlsx";




                FileOutputStream outputStream = new FileOutputStream(new File(path));
                byte[] buffer = new byte [5600];
                int inbyte;
                while ((inbyte =is.read(buffer)) > 0)
                    outputStream.write(buffer,0,inbyte);
                is.close();
                outputStream.close();
                System.out.println(path);

                System.out.println("Your excel file has been downloaded");
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private boolean isValid(String textField) {
        try {
            Integer.parseInt(textField);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void init() {
        this.getContentPane().setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel(new GridLayout());
        
        JLabel logLabel = new JLabel("min level:");
        logLabel.setHorizontalAlignment(SwingConstants.CENTER);


        String[] logChoices = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL", "TRACE"};
        final JComboBox<String> dropDownLvlButton = new JComboBox<>(logChoices);


        dropDownLvlButton.addActionListener(e -> {
            minLevel = (String) dropDownLvlButton.getSelectedItem();
            try {
                refreshUri(0);
                sendGet(uri);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        //limit textfield that only allows numbers
        JLabel textLabel = new JLabel("limit:");
        textField = new JTextField(storeLimit,1);
        // textField.getPreferredSize();


        //buttons
        JButton fetchDataButton = new JButton("FETCH DATA");
        fetchDataButton.addActionListener(e -> {
            storeLimit = textField.getText();

            if (!isValid(storeLimit)){
                JOptionPane.showMessageDialog(frame,"Invalid request input, request is not an Integer");
                return;
            } else if (Integer.parseInt(storeLimit) > 50) {
                JOptionPane.showMessageDialog(frame,"Invalid request input, request is greater than 50");
            } else if (Integer.parseInt(storeLimit) < 0 )
                JOptionPane.showMessageDialog(frame,"Invalid request input, request is less than 0");
            refreshUri(0);
            try {
                sendGet(uri);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // jTable.getModel().setValueAt(storeLimit, 0, 0);
            // System.out.println(textField.getText());
        });
        submitButton = fetchDataButton;

        //DOWNLOAD STATS
        JButton downloadStatsButton = new JButton("DOWNLOAD STATS");
        downloadStatsButton.addActionListener(e -> {
            refreshUri(1);

            try {
                sendGet(uri);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // Desktop.getDesktop().browse(new URL("http://localhost:8080/resthome4logs/stats").toURI());

        });


        panel1.add(logLabel);
        panel1.add(dropDownLvlButton);
        panel1.add(textLabel);
        panel1.add(textField);
        panel1.add(fetchDataButton);
        panel1.add(downloadStatsButton);


        JPanel panel2 = new JPanel(new BorderLayout());


        String[] tableHeader = {"id", "message", "time", "thread", "logger", "level"};


        /*tableData, tableHeader*/
        JTable jTable = new JTable(/*tableData, tableHeader*/);

        //Build the COLUMNS
        tableModel.setColumnIdentifiers(tableHeader);
        jTable.setModel(tableModel);

        //jTable.setModel(new DefaultTableModel(0,0));

        for (int i = 0; i < 50; i++) {
            tableModel.addRow(new String[]{});
        }


        TableColumnModel colModel = jTable.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100);
        colModel.getColumn(1).setPreferredWidth(20);
        colModel.getColumn(2).setPreferredWidth(100);
        colModel.getColumn(3).setPreferredWidth(50);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(50);


        //  jTable.setFocusable(false);
        jTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(jTable);
        panel2.add(scrollPane);


        //Here goes the interesting code
        this.getContentPane().add(panel1);
        //don't know what this does, but it orks.
        this.getContentPane().add(panel2, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2,
                2, 2), 0, 0));
        this.setPreferredSize(new Dimension(1000, 500));
        this.pack();
    }

    private static void refreshUri(int i) {
        switch (i) {
            case 0:
                try {
                    uri = new URIBuilder()
                            .setScheme("http")
                            .setHost("localhost")
                            .setPort(8080)
                            .setPath("resthome4logs/logs")
                            .addParameter("limit", storeLimit)
                            .addParameter("level", minLevel)
                            .build();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                System.out.println("case 1");
                System.out.println(uri.toString());
                try {
                    uri=new URIBuilder()
                            .setScheme("http")
                            .setHost("localhost")
                            .setPort(8080)
                            .setPath("resthome4logs/stats")
                            .build();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                System.out.println(uri.toString());
                break;
                default:break;
        }
    }


    public static void main(String[] args) throws IOException {
        frame = new LogMonitor();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.getRootPane().setDefaultButton(submitButton);
        refreshUri(0);
        sendGet(uri);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {


    }
}
