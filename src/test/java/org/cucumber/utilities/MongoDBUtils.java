package org.cucumber.utilities;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.restassured.path.json.JsonPath;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MongoDBUtils {

    private static final List<MongoClient> mongoClients = new ArrayList<>();
//    private static MongoClient mongoClient;

    public static MongoClient getMongoClient (){
        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        MongoClient mongoClient = new MongoClient(uri);
        return mongoClient;
    }

    public static MongoCollection<Document> connectMongodb(String databaseName, String collection){

        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        MongoClient mongoClient = new MongoClient(uri);
        mongoClients.add(mongoClient);

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collectionDoc = database.getCollection(collection);
        return collectionDoc;
    }
    public static MongoCollection<Document> connectMongodb(MongoClient mongoClient, String databaseName, String collection){

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collectionDoc = database.getCollection(collection);
        return collectionDoc;
    }

    public static MongoCollection<Document> connectMongodb(String URI, String databaseName, String collection){

        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get(URI));
        MongoClient mongoClient = new MongoClient(uri);
        mongoClients.add(mongoClient);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collectionDoc = database.getCollection(collection);
        return collectionDoc;
    }

    public static MongoCollection<Document> connectMongodb2(String databaseName, String collection) {

        MongoDatabase database = getMongoClient().getDatabase(databaseName);
        MongoCollection<Document> collectionDoc = database.getCollection(collection);
        return collectionDoc;

    }

    public static Document getPatientDoc (String URI, String databaseName, String practice, String patient){
            BasicDBObject query = BasicDBObject.parse("{'practice_id':'"+practice+"', 'patient_id':'"+patient+"'}");
            return connectMongodb(URI,databaseName,"patient_1").find(query).first();
    }


    public static Document getBenefitDoc (String URI, String databaseName, String practice, String benefitNum){
        BasicDBObject query = BasicDBObject.parse("{'practice_id':'"+practice+"', 'benefit_id':'"+benefitNum+"'}");
        return connectMongodb(URI,databaseName,"benefit_1").find(query).first();
    }

    public static Document getMongoDoc (String databaseName, String collection, String query){
        BasicDBObject queryObj = BasicDBObject.parse(query);
        MongoClient mongoClient = getMongoClient();
        Document document = connectMongodb(mongoClient,databaseName,collection).find(queryObj).first();
        mongoClient.close();
        return document ;
    }

    public static JsonPath getMongoDocJson (String databaseName, String collection, String query){
        BasicDBObject queryObj = BasicDBObject.parse(query);
        MongoClient mongoClient = getMongoClient();
        JsonPath jsonPath = JsonPath.from(connectMongodb(mongoClient,databaseName,collection).find(queryObj).first().toJson());
        mongoClient.close();
        return jsonPath;
    }

    public static Document getMongoDoc (String databaseName, String collection, BasicDBObject query){
        MongoClient mongoClient = getMongoClient();
        Document document = connectMongodb(mongoClient,databaseName,collection).find(query).first();
        mongoClient.close();
        return document ;
    }

    public static JsonPath getMongoDocJson (String databaseName, String collection, BasicDBObject query){
        MongoClient mongoClient = getMongoClient();
        JsonPath jsonPath = JsonPath.from(connectMongodb(mongoClient,databaseName,collection).find(query).first().toJson());
        mongoClient.close();
        return jsonPath;
    }

    public static Document getQueryResultDoc (String URI, String databaseName, String collection, BasicDBObject query){
        return connectMongodb(URI,databaseName,collection).find(query).first();
    }

    public static MongoDatabase connectMongodbDatabase(String URI, String databaseName){

        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get(URI));
        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database;
    }

    public static int iterDocCount (String URI, String partner, String practice, String collection, BasicDBObject query){
//        BasicDBObject query = BasicDBObject.parse("{'practice_id':'"+practice+"'}");
        int i = 0;
        FindIterable<Document> queryDoc = connectMongodb(URI,partner,collection).find(query);
        MongoCursor<Document> iterDoc = queryDoc.iterator();
        while (iterDoc.hasNext()){
            iterDoc.next();
            i++;
        }
        return i;
    }

    public static long documentCount (MongoCollection<Document> collection){
        long docCount = collection.countDocuments();
        return docCount;
    }


    public static List<Map<String, Object>> getCollectionMap(MongoCollection<Document> collection){

        List<Map<String, Object>> docList = new ArrayList<>();
        Map<String, Object> fieldKeyValueMap = new HashMap<>();
        Gson gson = new Gson();
        for (int i = 0; i < documentCount(collection); i++) {

            FindIterable<Document> iterDoc = collection.find().skip(i).limit(1);
            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                Document myDoc = (Document) it.next();
                fieldKeyValueMap= gson.fromJson(myDoc.toJson(),Map.class);
            }
            docList.add(fieldKeyValueMap);

        }

       return docList;
    }

    public static void executeUpdateQuery(MongoDatabase database, String collectionName, String filter, String update) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document filterDoc = Document.parse(filter);
        Document updateDoc = Document.parse(update);
//        System.out.println("updateDoc = " + updateDoc);
        collection.updateMany(filterDoc, updateDoc);
        System.out.println("document updated");
    }

    public static void deleteFieldFromDocuments(MongoCollection<Document> collection, String filter, String fieldToDelete) {
        Document filterDoc = Document.parse(filter);
        Document updateDoc = new Document("$unset", new Document(fieldToDelete, ""));
        collection.updateMany(filterDoc, updateDoc);
        System.out.println("Field '" + fieldToDelete + "' deleted from documents.");
    }

    public static void executeUpdateQuery(MongoCollection<Document> collection, String filter, String update) {
        Document filterDoc = Document.parse(filter);
        Document updateDoc = Document.parse(update);
        collection.updateMany(filterDoc, updateDoc);
        System.out.println(filter+" ---> document updated");
    }
    public static void deleteFieldsFromDocuments(MongoCollection<Document> collection, String filter, List<String> fieldsToDelete) {
        Document filterDoc = Document.parse(filter);

        Document unsetFields = new Document();
        for (String field : fieldsToDelete) {
            unsetFields.append(field, "");
        }

        Document updateDoc = new Document("$unset", unsetFields);
        collection.updateMany(filterDoc, updateDoc);
        System.out.println("Fields " + fieldsToDelete + " deleted from documents.");
    }
    public static void executeUpdateQuery(MongoCollection<Document> collection, Document filter, Document update) {
        collection.updateMany(filter, update);
        System.out.println(filter.toJson()+" ---> document updated");
    }
    public static void executeUpdateQuery(MongoDatabase database, String collectionName, Document filter, Document update) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.updateMany(filter, update);
        System.out.println(filter.toJson()+" ---> document updated");
    }

    public static void executeDeleteQuery(MongoCollection<Document> collection, String filter) {
        Document filterDoc = Document.parse(filter);
//        System.out.println("filterDoc.toJson() = " + filterDoc.toJson());
        collection.deleteMany(filterDoc);
        if (filter.length()>100){
            System.out.println(filter.substring(0,100)+"....(too Long) ---> document deleted");
        } else System.out.println(filter+" ---> document deleted");
    }

    public static void executeDeleteQuery(MongoDatabase database, String collectionName, String filter) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document filterDoc = Document.parse(filter);
        collection.deleteMany(filterDoc);
        System.out.println(filter+" ---> document deleted");
    }

    public static void insertDocument(MongoCollection<Document> collection, Document document) {
        collection.insertOne(document);
        System.out.println("Document inserted successfully!");
    }

    public static void importPayorCrosswalkJSintoMongoCollection (String jsFilePath, String databaseName,String collectionName){

        // Read JavaScript file and extract array content
        String arrayContent = extractArrayContent(jsFilePath);

        if (arrayContent != null) {
            // Convert extracted array content to JSONArray

            JSONArray jsonArray = new JSONArray("[" + arrayContent + "]");

            // Import JSON data into MongoDB collection
            importIntoMongoDB(jsonArray, databaseName, collectionName);
        }

    }

    private static void importIntoMongoDB(JSONArray jsonArray, String databaseName ,String collectionName) {
        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(ConfigurationReader.get("mongoURI")));
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            database.getCollection(collectionName).drop(); // Drop existing collection

            for (int i = 0; i < jsonArray.length(); i++) {
                Document document = Document.parse(jsonArray.get(i).toString());
                database.getCollection(collectionName).insertOne(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String extractArrayContent(String filePath) {
        StringBuilder arrayContent = new StringBuilder();
        boolean insideArray = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (insideArray) {
                    arrayContent.append(line.trim());
                    if (line.contains("];")) {
                        break;
                    }
                }
                if (line.contains("const records = [")) {
                    insideArray = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println("arrayContent = " + arrayContent);
        return arrayContent.toString();
    }


    public static void importDentalPayorJSintoMongoCollection (String jsFilePath, String databaseName){
        try {
            // Read the JavaScript file and extract the dentalPayorArray
            List<JSONObject> dentalPayorArray = extractDentalPayorArray(jsFilePath);

            // Import dentalPayorArray into MongoDB collection
            importDentalPayorArray(dentalPayorArray, databaseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static List<JSONObject> extractDentalPayorArray(String filePath) throws Exception {
        List<JSONObject> dentalPayorArray = new ArrayList<>();
        List<String> clearinghouseList = Arrays.asList(
                "availity",
                "dxc",
                "changehealthcare",
                "tesia"
//                "liberty",
//                "avesis",
//                "envolve",
//                "synthetic"
        );
        for (String clearinghouse : clearinghouseList) {
            System.out.println("clearinghouse = " + clearinghouse);
            String filePathNew = filePath+"dental_payor_"+clearinghouse+".js";
//            System.out.println("filePath = " + filePathNew);
            // Open and read the JavaScript file
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePathNew), StandardCharsets.UTF_8))){
                String line;
                StringBuilder jsonBuilder = new StringBuilder();
                boolean insideObject = false;
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("dentalPayorArray.push(")) {
                        jsonBuilder.setLength(0); // Clear the StringBuilder
                        jsonBuilder.append(line.substring(line.indexOf("{"))); // Start appending from the opening curly brace
                        insideObject = true;
                    } else if (insideObject) {
                        jsonBuilder.append(line.trim()); // Append the current line
                        if (line.trim().endsWith(");")) { // Check if the JSON object is complete
                            insideObject = false;
                            String jsonString = jsonBuilder.toString();
                            JSONObject jsonObject = new JSONObject(jsonString);
                            dentalPayorArray.add(jsonObject);
                        }
                    }
                }
            }
        }

        return dentalPayorArray;
    }

    private static void importDentalPayorArray(List<JSONObject> dentalPayorArray, String dbName) {
        try {
            MongoCollection<Document> dentalPayorColl = MongoDBUtils.connectMongodb("mongoURI",dbName,"dental_payor_1");

            dentalPayorColl.deleteMany(new Document());

            for (JSONObject dentalPayor : dentalPayorArray) {
                Document document = Document.parse(dentalPayor.toString().replace("â€“","–").replace("â€™","’"));
                dentalPayorColl.insertOne(document);
            }
            System.out.println("Data imported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void closeAllClients() {
        int i = 0;

        if (mongoClients.size()>0){
            try {
                for (MongoClient client : mongoClients) {
                    i++;
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Closed Mongo Connection Count = " + i);
        }
        mongoClients.clear();
    }

    public static void loadJsScript() {
        Process process = null;
        try {
            // Define the command to execute the .bat file
            String command = "cmd.exe /c start /wait C:\\Users\\Administrator\\Desktop\\mongosh-2.2.1\\bin\\mongoshScript.bat";

            // Create ProcessBuilder instance
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

            // Start the process
            process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();

            // Print the exit code
            System.out.println("Exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (process!=null){
                process.destroy();
            }
        }
    }

    public static void loadJsScript3(String databaseName) {
        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        try (MongoClient mongoClient = new MongoClient(uri)) {
            MongoDatabase db = mongoClient.getDatabase("qa-test");

            // Read the JavaScript query from the file
            String filePath = "C:\\Users\\Administrator\\vscodeRet\\fullstack\\ret-fullstack\\scripts\\directorydb\\dental_payor_dxc.js";
            String query = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("query = " + query);

            // Execute the JavaScript query using $eval
            Document evalResult = db.runCommand(new Document("$eval", query));

            // Process the result if needed
            System.out.println("Evaluation result: " + evalResult.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void loadJsScript2(String databaseName) {
        // Connect to MongoDB
        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        MongoClient mongoClient = new MongoClient(uri);
//        mongoClients.add(mongoClient);
        MongoDatabase database = mongoClient.getDatabase(databaseName);


        List<String> clearinghouseList = Arrays.asList(
                "availity",
                "dxc",
                "changehealthcare",
                "tesia"
//                "liberty",
//                "avesis",
//                "envolve",
//                "synthetic"
        );
        for (String clearinghouse : clearinghouseList) {

            // Path to your JavaScript file
            String filePath = "C:\\Users\\Administrator\\vscodeRet\\fullstack\\ret-fullstack\\scripts\\directorydb"+clearinghouse+".js";

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                StringBuilder commandBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    // Execute each line of the JavaScript file as a command
                    database.runCommand(Document.parse(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Close the connection
            mongoClient.close();
        }
    }

    public static void deleteSingletonRecord (String processName){
        MongoCollection<Document> singletonDoc = MongoDBUtils.connectMongodb(getMongoClient(),"operations","singleton_process_1");
        Bson filter = Filters.and(Filters.eq("name", processName), Filters.eq("ip_address", "xx.xxx.xx.111"));
        singletonDoc.deleteMany(filter);
    }

    public static void setSingletonSettings (boolean settingsValue, String testCaseName, String note, String databaseName){
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient,databaseName,"keepSettings");
        Document keepSettingFilter = new Document("settingsName", "eraCycle");
        Document keepSettingsUpdate = new Document("$set", new Document("keepSettings", settingsValue)
                .append("whoIsKepping",testCaseName)
                .append("note", note));
        try {
            executeUpdateQuery(keepSettingsColl, keepSettingFilter, keepSettingsUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    public static boolean getSingletonSettings (){
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient,"qa-test","keepSettings");
        Document keepSettingFilter = new Document("settingsName", "eraCycle");
//        Document keepSettingsUpdate = new Document("$set", new Document("keepSettings", settingsValue));
        boolean keepSettingsValue = true;
        try {
            Document keepSetDoc = keepSettingsColl.find(keepSettingFilter).first();
            keepSettingsValue = (boolean) keepSetDoc.get("keepSettings");
            System.out.println("keepSettingsValue = " + keepSettingsValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return keepSettingsValue;
    }

    public static void setVersionChangedLocked (boolean settingsValue){
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient,"qa-test","keepSettings");
        Document keepSettingFilter = new Document("settingsName", "retAppVersion");
        Document keepSettingsUpdate = new Document("$set", new Document("versionChangedLocked", settingsValue));
        try {
            executeUpdateQuery(keepSettingsColl, keepSettingFilter, keepSettingsUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }



    public static boolean keepSettingsValue (){
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient,"qa-test","keepSettings");
        Document keepSettingFilter = new Document("settingsName", "eraCycle");
        Document keepSettingsDoc = keepSettingsColl.find(keepSettingFilter).first();
        boolean value = keepSettingsDoc.getBoolean("keepSettings");
        return value;
    }


    public static void pushAndKeepSettings (String partner, String practiceId, Logger logger, String testCaseName, String note, String testCasesDB){
        MongoClient mongoClient = getMongoClient();
        MongoCollection<Document> practiceColl = connectMongodb(mongoClient,partner,"practice_1");
        MongoCollection<Document> testDataColl = connectMongodb(mongoClient,testCasesDB,"eraCycleTestCases");
        MongoCollection<Document> partnerColl = connectMongodb(mongoClient,"directory","partner_1");

        Bson testCaseQuery = Filters.eq("testCaseName", testCaseName.replace("_"," "));
        System.out.println("query executed = " + testCaseQuery);

        try {
            Document testDataDoc = testDataColl.find(testCaseQuery).first();
//            System.out.println("testDataDoc.toJson() = " + testDataDoc.toJson());

            Object settingsObj = testDataDoc.get("settings");
            Document settings = (Document) settingsObj;
//            Document settings = (Document) testDataDoc.get("settings");

            Object denialPartnerSettingsObj = testDataDoc.get("denial_partner_settings");
            Document denialPartnerSettings = (Document) denialPartnerSettingsObj;

            Object denialPracticeSettingsObj = testDataDoc.get("denial_practice_settings");
            Document denialPracticeSettings = (Document) denialPracticeSettingsObj;

            System.out.println("settings = " + settings.toJson());
            System.out.println("denialPracticeSettings = " + denialPracticeSettings.toJson());
            System.out.println("denialPartnerSettings = " + denialPartnerSettings.toJson());

            Document filter = new Document("practice_id", practiceId);
//            Document update = new Document("$set", new Document()
//                    .append("settings", denialPracticeSettings)
//                    .append("settings.era", settings)
//            );

            // Fetch the existing document to keep current settings fields
            Document existingPracticeDoc = practiceColl.find(filter).first();
            Document existingSettings = existingPracticeDoc != null && existingPracticeDoc.containsKey("settings")
                    ? (Document) existingPracticeDoc.get("settings")
                    : new Document();

// Merge existing settings with new values
            existingSettings.put("era", settings); // Update "era" field inside "settings"

// Add/update denialPracticeSettings fields inside settings
            for (String key : denialPracticeSettings.keySet()) {
                existingSettings.put(key, denialPracticeSettings.get(key));
            }

// Create update document
            Document update = new Document("$set", new Document("settings", existingSettings));
//            System.out.println("update.toJson() = " + update.toJson());

// Execute update
            executeUpdateQuery(practiceColl, filter, update);


            // update partner_1 denial settings

            Document filterPartner = new Document("partner_id", partner);
            Document updatePartner = new Document()
                    .append("$set", denialPartnerSettings); // Directly inserting at root level

            executeUpdateQuery(partnerColl, filterPartner, updatePartner);

            setSingletonSettings(true, testCaseName, note, testCasesDB);

        } catch (Exception e) {
            logger.error("error "+e.getMessage());
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }


    public static void copyCollections(String sourceDatabaseName, String targetDatabaseName, List<String> collectionsToCopy) {
        System.out.println("********************************************************************************************************************");
        System.out.println("******************************* Copy Collection Started for Partner = " + targetDatabaseName + " ******************************");
        System.out.println("********************************************************************************************************************");
        System.out.println("Partners copy from/to: Source = " + sourceDatabaseName + ", Target = " + targetDatabaseName);

        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase sourceDatabase = mongoClient.getDatabase(sourceDatabaseName);
        MongoDatabase targetDatabase = mongoClient.getDatabase(targetDatabaseName);

        // Copy each specified collection
        for (String collectionName : collectionsToCopy) {
            System.out.println("Copying " + collectionName + " to " + targetDatabaseName + "...");

            MongoCollection<Document> sourceCollection = sourceDatabase.getCollection(collectionName);
            MongoCollection<Document> targetCollection = targetDatabase.getCollection(collectionName);

            // Delete old data in the target collection
            targetCollection.deleteMany(new Document());

            // Fetch documents from the source collection
            FindIterable<Document> findIterable = sourceCollection.find();
            MongoCursor<Document> cursor = findIterable.iterator();

            // Process documents in batches
            List<Document> batch = new ArrayList<>();
            int batchSize = 1000;

            while (cursor.hasNext()) {
                batch.add(cursor.next());

                if (batch.size() == batchSize) {
                    // Send the batch to the target collection
                    targetCollection.insertMany(batch);

                    // Clear the batch and free up memory
                    batch.clear();
                }
            }

            // Process any remaining documents
            if (!batch.isEmpty()) {
                targetCollection.insertMany(batch);
            }

            System.out.println("Copying collection " + collectionName + " completed.");
        }

        mongoClient.close();
    }

    public static void copyCollection(String databaseName, String sourceCollectionName, String targetCollectionName) {
        System.out.println("******************************************************************************************************");
        System.out.println("********** Copying Collection: " + sourceCollectionName + " -> " + targetCollectionName + " **********");
        System.out.println("******************************************************************************************************");

        // Connect to MongoDB
        MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
        MongoClient mongoClient = new MongoClient(uri);

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> sourceCollection = database.getCollection(sourceCollectionName);
            MongoCollection<Document> targetCollection = database.getCollection(targetCollectionName);

            // Drop or delete data in target collection
            if (targetCollection.countDocuments() > 0) {
                System.out.println("Dropping existing target collection: " + targetCollectionName);
                targetCollection.drop(); // Drops the entire collection
                targetCollection = database.getCollection(targetCollectionName); // Recreate collection
            }

            // Copy documents in batches
            List<Document> batch = new ArrayList<>();
            int batchSize = 1000;
            MongoCursor<Document> cursor = sourceCollection.find().iterator();

            while (cursor.hasNext()) {
                batch.add(cursor.next());

                if (batch.size() == batchSize) {
                    targetCollection.insertMany(batch);
                    batch.clear(); // Clear memory
                }
            }

            // Insert any remaining documents
            if (!batch.isEmpty()) {
                targetCollection.insertMany(batch);
            }

            System.out.println("Successfully copied data from " + sourceCollectionName + " to " + targetCollectionName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }


}
