package org.cucumber.utilities;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoDBDumpAndLoad {

    public static void main(String[] args) throws IOException, ParseException {


        JSONParser jsonParser = new JSONParser();
        FileReader partnerList = new FileReader(BrowserUtils.getDownloadPath()+"/partnerList.json");
        JSONArray partnerArray = (JSONArray) jsonParser.parse(partnerList);

        System.out.println("How many partners will be copied = " + partnerArray.size());
        for (int k = 0; k < partnerArray.size(); k++) {

            JSONObject partnerJsonObj = (JSONObject) partnerArray.get(k);
            System.out.println("********************************************************************************************************************");
            System.out.println("******************************* Copy Collection Started for Partner = "+partnerJsonObj.get("target")+" ******************************");
            System.out.println("********************************************************************************************************************");
            System.out.println("Partners copy from/to = " + partnerJsonObj);
            String sourceDatabaseName = partnerJsonObj.get("source").toString();
            String targetDatabaseName = partnerJsonObj.get("target").toString();

            // Output directory for the dump
            String outputDir = BrowserUtils.getDownloadPath();

            // Collections to dump
            List<String> collectionsToDump = Arrays.asList(
///                   "aaaa_assigned",
//                    "file_assigned"
//                    "ledger_record_1"
//                    "users"
            );

            MongoClientURI uri = new MongoClientURI(ConfigurationReader.get("mongoURI"));
            MongoClient mongoClient = new MongoClient(uri);


            MongoDatabase sourceDatabase = mongoClient.getDatabase(sourceDatabaseName);
            MongoDatabase targetDatabase = mongoClient.getDatabase(targetDatabaseName);

            // Copy each specified collection
            for (String collectionName : collectionsToDump) {
                System.out.println("Copying " + collectionName + " to " + targetDatabaseName + "...");

                MongoCollection<Document> sourceCollection = sourceDatabase.getCollection(collectionName);
                List<Document> documents = sourceCollection.find().into(new ArrayList<>());

                // Delete old data in the target collection
                MongoCollection<Document> targetCollection = targetDatabase.getCollection(collectionName);
                targetCollection.deleteMany(new Document());

                // Insert new data into the target collection
                targetCollection.insertMany(documents);

            }

            System.out.println("Copying collection completed.");
        }

    }

}

