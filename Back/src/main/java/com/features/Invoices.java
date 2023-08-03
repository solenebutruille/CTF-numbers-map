package com.features;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Invoices {

    SessionCookies sessionCookies;

    public Invoices() {
        sessionCookies = new SessionCookies();
    }

    /* Solution OS Command Injection
    File directory = new File("./src/main/resources/invoice/");
       File[] matchingFiles = directory.listFiles(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               if (substring.equalsIgnoreCase("''")) return true;
               return name.contains(substring);
           }
       });
       // Convert the matching files to a list of filenames
       for (File file : matchingFiles) {
           invoices.add(file.getName());
       }
    */

    public String allowedString(String substring){
        String[] allowList = {"cat", "less", "vi", "vim", "&&", "../flag/flag.flg", "flag.flg", "flag", "ls", "..", "cd"};
        String[] words = substring.split("\\s+");
        List<String> allowedWords = new ArrayList<>(Arrays.asList(words));

        for (int i = 1; i < allowedWords.size(); i++) {
            if (!Arrays.asList(allowList).contains(allowedWords.get(i))) {
                allowedWords.remove(i);
                i--;
            }
        }

        StringBuilder result = new StringBuilder();
        for (String word : allowedWords) {
            result.append(word).append(" ");
        }

        return result.toString().trim();
    }

    public List<String> getInvoicesInDatabase(String substring) throws IOException {
        List<String> invoices = new ArrayList<>();
        substring = allowedString(substring);
        String[] commands = {"/bin/sh", "-c", "ls | grep " + substring};
        System.out.println("OS commands to run : " + Arrays.toString(commands));
        ProcessBuilder builder = new ProcessBuilder(commands);

        builder.directory(new File("./src/main/resources/invoice/"));
        Process process = builder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         while ((substring = reader.readLine()) != null) { invoices.add(substring); }
        reader.close();
        return invoices;
    }

    /* Solution Direct Object Refereence

    String pattern = "Invoice_number(\\d+)";
    String replacement = "$1";
    String invoiceID = markerId.replaceAll(pattern, replacement);
    if (! checkAccess(invoiceID, sessionCookie)) return ; */

    public void downloadInvoice(String markerId, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {

        File file = new File("./src/main/resources/invoice/" + markerId);
        Scanner scanner = new Scanner(file);
        String res = "";
        while (scanner.hasNextLine()) {
            res += scanner.nextLine();
        }
        scanner.close();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+markerId);

        // Stream the file content to the response output stream
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] bytes = res.getBytes();
        outputStream.write(bytes);
        outputStream.flush();
    }


    public List<String> searchInvoiceFromUserID(int userID, String substring) {
        String pattern = "Invoice_number(\\d+)";
        String replacement = "$1";
        List<String> markersID = null;
        Connection connection = null;
        try {
            markersID = getInvoicesInDatabase(substring);
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");
        } catch (Exception e) {
            e.printStackTrace();
            return markersID;
        }
        markersID = markersID.stream()
                .map(str -> str.replaceAll(pattern, replacement))
                .collect(Collectors.toList());

        for (int i = 0; i < markersID.size(); i ++){
            try {
                String num = markersID.get(i);
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID FROM MARKERS WHERE BUYER = ? AND ID = ?");
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, Integer.parseInt(num));
                ResultSet resultSet = preparedStatement.executeQuery();
                markersID.remove(i);
                i--;
                while (resultSet.next()) {
                    markersID.add("Invoice_number" + num);
                }
            } catch (Exception e){
                System.out.println("this error is expected " +e);
            }
        }
        return markersID;
    }

    public boolean createInvoice(int markerID, String userID) throws IOException {
        String filePath = "./src/main/resources/invoice/Invoice_number" +markerID;
        LocalDate today = LocalDate.now();
        File file = new File(filePath);
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) { file.createNewFile(); }
            FileWriter writer = new FileWriter(file);
            writer.write("This number was bought on the " +today+ " by the user " +userID);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
