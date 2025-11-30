package org.cucumber.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExportUtil {

    public static void toCSV(String fileName, List<LinkedHashMap<String, String>> listToExport) {

        List<String> headers = listToExport.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());

//        String path= "src/test/resources/Downloads/eligibiltyErrorList.csv";

        File[] files = new File(BrowserUtils.getDownloadPath()).listFiles();
        for (File file : files) {
            if ((file.isFile() && file.getName().contains(fileName))) {
                file.delete();
            }
        }

        String filepath =  BrowserUtils.getDownloadPath(fileName);

        try(FileWriter writer= new FileWriter(filepath, true);){
            for (String string : headers) {
                writer.write(string);
                writer.write(",");
            }
            writer.write("\r\n");

            for (LinkedHashMap<String, String> lmap : listToExport) {
                for (Map.Entry<String, String> string2 : lmap.entrySet()) {
                    writer.write(string2.getValue());
                    writer.write(",");
                }
                writer.write("\r\n");
            }
            System.out.println("Created file exported to '" + filepath + "'");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void toCSV2(String fileName, List<Map<String, Object>> listToExport) {

        List<String> headers = listToExport.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());

//        String path= "src/test/resources/Downloads/eligibiltyErrorList.csv";

        File[] files = new File(BrowserUtils.getDownloadPath()).listFiles();
        for (File file : files) {
            if ((file.isFile() && file.getName().contains(fileName))) {
                file.delete();
            }
        }

        String filepath =  BrowserUtils.getDownloadPath(fileName);

        try(FileWriter writer= new FileWriter(filepath, true);){
            for (String string : headers) {
//                System.out.println("string = " + string);
                writer.write(string);
                writer.write(",");
            }
            writer.write("\r\n");

            for (Map<String, Object> lmap : listToExport) {
                for (Map.Entry<String, Object> string2 : lmap.entrySet()) {
//                    System.out.println("string2 = " + string2);
                    writer.write(String.valueOf(string2.getValue()));
                    writer.write(",");
                }
                writer.write("\r\n");
            }
            System.out.println("Created file exported to '" + filepath + "'");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void toCSV3(String fileName, List<LinkedHashMap<String, String>> listToExport) {
        List<String> headers = listToExport.stream()
                .flatMap(map -> map.keySet().stream())
                .distinct()
                .collect(Collectors.toList());

        // Remove existing file if it exists
        File[] files = new File(BrowserUtils.getDownloadPath()).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().contains(fileName)) {
                file.delete();
            }
        }

        String filepath = BrowserUtils.getDownloadPath(fileName);

        try (FileWriter writer = new FileWriter(filepath, true)) {
            // Write headers
            for (String header : headers) {
                writer.write(header);
                writer.write(",");
            }
            writer.write("\r\n");

            // Write data
            for (LinkedHashMap<String, String> map : listToExport) {
                for (String header : headers) {
                    String value = map.get(header);
                    if (value != null) {
                        writer.write(value);
                    }
                    writer.write(",");
                }
                writer.write("\r\n");
            }
            System.out.println("Created file exported to '" + filepath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void toJSON(String fileName, List<LinkedHashMap<String, String>> listToExport) throws IOException {

        String filepath =  BrowserUtils.getDownloadPath(fileName);
        Writer writer = new FileWriter(filepath);
        new Gson().toJson(listToExport, writer);
        writer.close();

        System.out.println("Created file exported to '" + filepath + "'");

    }

    public static void toJSON2(String fileName, List<LinkedHashMap<String, Object>> listToExport) throws IOException {

        String filepath =  BrowserUtils.getDownloadPath(fileName);
        Writer writer = new FileWriter(filepath);
        new Gson().toJson(listToExport, writer);
        writer.close();

        System.out.println("Created file exported to '" + filepath + "'");

    }

    public static void toJSON3(String fileName, List<Map<String, Object>> listToExport) throws IOException {

        String filepath =  BrowserUtils.getDownloadPath(fileName);
        Writer writer = new FileWriter(filepath);
        new Gson().toJson(listToExport, writer);
        writer.close();

        System.out.println("Created file exported to '" + filepath + "'");

    }

    public static void prepareJSON(String fileName, List<Map<String, Object>> listToExport) throws IOException, ParseException {

        boolean JSONExist = false;
        File[] files = new File(BrowserUtils.getDownloadPath()).listFiles();
        JSONParser jsonParser = new JSONParser();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().contains(fileName)) {
                    FileReader oldEligList = new FileReader(BrowserUtils.getDownloadPath() + "/"+fileName);
                    JSONArray eligArray = (JSONArray) jsonParser.parse(oldEligList);
//                    System.out.println("eligArray.size() = " + eligArray.size());
                    if (eligArray.size() > 0) {
                        JSONExist = true;
                    } else {
                        file.delete();
                    }
                }
            }
        }
//        System.out.println("JSONExist = " + JSONExist);

        if (JSONExist) {

//                JSONArray newResult = (JSONArray) jsonParser.parse(succesEligList);
//                JSONObject newResult = new JSONObject(succesEligList);

            FileReader oldEligList = new FileReader(BrowserUtils.getDownloadPath()  + "/"+fileName);
            JSONArray eligArray = (JSONArray) jsonParser.parse(oldEligList);

            JSONObject eligJsonObj = (JSONObject) eligArray.get(0);
            String oldDate = eligJsonObj.get("date").toString();
//            System.out.println("oldDate = " + oldDate);

            String today = DateUtils.getFormatedToday("yyyyMMdd");
            if (oldDate.equals(today)) {
                eligArray.addAll(listToExport);
                try {
                    FileWriter fileWriter = new FileWriter(BrowserUtils.getDownloadPath()  + "/"+fileName);         // writing back to the file
                    fileWriter.write(eligArray.toJSONString());
                    fileWriter.flush();
                    fileWriter.close();
                    System.out.println("Updated file exported to '" + BrowserUtils.getDownloadPath() + "/" +fileName+ "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                toJSON3(fileName, listToExport);
            }
        }else {
            toJSON3(fileName, listToExport);
        }
    }


    public static void convertJsonToCsv(String jsonFileName, String csvFileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Read JSON file and convert to List of Maps
        List<LinkedHashMap<String, String>> listOfMaps = objectMapper.readValue(
                new File(BrowserUtils.getDownloadPath() + "/"+jsonFileName),
                new TypeReference<List<LinkedHashMap<String, String>>>() {
                }
        );

        // Print the result
        for (LinkedHashMap<String, String> map : listOfMaps) {
            System.out.println(map);
        }

        toCSV3(csvFileName, listOfMaps);
    }




}





