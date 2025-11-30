package org.cucumber.utilities;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JSONUtils {


    public static JSONArray readJSON (String fileName) {

        JSONParser jsonParser = new JSONParser();
        String filepath = BrowserUtils.getDownloadPath()+File.separator+ fileName;
//        System.out.println("filepath = " + filepath);
        JSONArray jsonArray = new JSONArray();
        try {
            FileReader reader0 = new FileReader(filepath);
            jsonArray = (JSONArray) jsonParser.parse(reader0);
//        System.out.println("patientArray = " + patientArray);

            //to get one of json array
            JSONObject jsonObj = (JSONObject) jsonArray.get(0);
//        System.out.println("eraJsonObj = " + eraJsonObj);
        
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    public static List<String> readJsonToList(String filePath) {
        Gson gson = new Gson();
        List<String> list = null;
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            list = gson.fromJson(reader, listType);
            System.out.println("File names read from JSON successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static JSONObject readJSONObject(String fileName) {
        JSONParser jsonParser = new JSONParser();
        String filepath = BrowserUtils.getDownloadPath() + File.separator + fileName;
        JSONObject jsonObject = null;

        try {
            FileReader reader = new FileReader(filepath);
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

            // Get the first JSONObject from the JSONArray
            if (!jsonArray.isEmpty()) {
                jsonObject = (JSONObject) jsonArray.get(0);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static List<Document> JsonArrayToBson2(String filePath) {
        List<Document> bsonDocuments = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> jsonArray = objectMapper.readValue(
                    new File(filePath), new TypeReference<List<Map<String, Object>>>() {}
            );
            for (Map<String, Object> jsonMap : jsonArray) {
                Document bsonDocument = processExtendedJson2(jsonMap);
                bsonDocuments.add(bsonDocument);
            }
            bsonDocuments.forEach(doc -> System.out.println(doc.toJson()));

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        }

        return bsonDocuments;
    }

    private static Document processExtendedJson2(Map<String, Object> jsonMap) {
        Document bsonDocument = new Document();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;

                if (valueMap.containsKey("$oid")) {
                    String oidValue = valueMap.get("$oid").toString();
                    if (ObjectId.isValid(oidValue)) {
                        value = new ObjectId(oidValue);
                    } else {
                        System.err.println("Warning: Invalid ObjectId format for key " + key + ": " + oidValue);
                        continue;
                    }
                } else if (valueMap.containsKey("$date")) {
                    Object dateValue = valueMap.get("$date");

                    if (dateValue instanceof Map && ((Map<?, ?>) dateValue).containsKey("$numberLong")) {
                        value = new Date(Long.parseLong(((Map<?, ?>) dateValue).get("$numberLong").toString()));
                    } else if (dateValue instanceof Long) {
                        value = new Date((Long) dateValue);
                    }
                } else {
                    value = processExtendedJson(valueMap);
                }
            } else if (value instanceof List) {
                value = processList((List<Object>) value);
            }

            bsonDocument.put(key, value);
        }
        return bsonDocument;
    }

    private static List<Object> processList(List<Object> list) {
        List<Object> processedList = new ArrayList<>();

        for (Object item : list) {
            if (item instanceof Map) {
                processedList.add(processExtendedJson((Map<String, Object>) item));
            } else {
                processedList.add(item);
            }
        }

        return processedList;
    }



    public static List<Document> JsonArrayToBson(String filePath) {
        List<Document> bsonDocuments = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse JSON file into a List of Maps
            List<Map<String, Object>> jsonArray = objectMapper.readValue(
                    new File(filePath), new TypeReference<List<Map<String, Object>>>() {}
            );

            // Convert each Map to a BSON Document and add it to the list
            for (Map<String, Object> jsonMap : jsonArray) {
                Document bsonDocument = new Document(jsonMap);
                bsonDocuments.add(bsonDocument);
            }

            // Print each BSON Document
            bsonDocuments.forEach(doc -> System.out.println(doc.toJson()));

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        }

        return bsonDocuments;
    }


    public static Document JsonToBsonConverter(String filePath) {
        // Path to the JSON file
        Document bsonDocument = new Document();
        // Read and convert JSON to BSON Document
        try {
            // ObjectMapper to read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON file into a List of Maps
            Map<String, Object> jsonMap = objectMapper.readValue(
                    new File(filePath), new TypeReference<Map<String, Object>>() {}
            );

            // Convert the Map to a BSON Document
            bsonDocument = processExtendedJson(jsonMap);

            // Print the BSON Document
            System.out.println(bsonDocument.toJson());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bsonDocument;
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static Document processExtendedJson(Map<String, Object> jsonMap) {
        Document document = new Document();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                Map<String, Object> nestedMap = (Map<String, Object>) value;

                if (nestedMap.containsKey("$date")) {
                    // Convert $date to java.util.Date from ISO 8601 string
                    String dateString = (String) nestedMap.get("$date");
                    try {
                        value = dateFormat.parse(dateString);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle the parsing exception
                    }
                } else if (nestedMap.containsKey("$numberDecimal")) {
                    // Convert $numberDecimal to Decimal128
                    value = new Decimal128(new java.math.BigDecimal((String) nestedMap.get("$numberDecimal")));
                } else {
                    // Recursively process nested maps
                    value = processExtendedJson(nestedMap);
                }
            }

            document.put(key, value);
        }

        return document;
    }
}
