package org.cucumber.utilities;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ETestFramework {

    private static String testCasesDB;

    public static void main(String[] args) {
        testCasesDB = "qa-clinic3Era";

        // Get the list of test case IDs from the database
        List<String> testCaseIds = getTestCaseList();


        for (String testCaseId : testCaseIds) {
            System.out.println("testCaseId = " + testCaseId);
            String[] command = {"mvn", "test", "-Dcucumber.filter.tags=@ecat" + testCaseId, "-Dbrowser=docker_chrome"};
//            runMavenCommand(command);
        }


    }


    // Utility method to run a Maven command
    public static void runMavenCommand(String[] command) {
        Process process = null;
        try {
            String projectPath = System.getProperty("user.dir");
            System.out.println("Running test case in project path: " + projectPath);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(projectPath));

            process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Maven script executed successfully.");
            } else {
                System.out.println("Error executing Maven script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    // This method fetches the list of test case IDs from MongoDB
    public static List<String> getTestCaseList() {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> testCasesColl = MongoDBUtils.connectMongodb(mongoClient, testCasesDB, "eraCycleTestCases");
        BasicDBObject query = BasicDBObject.parse("{isTestCaseReady:true, testStatus:'waitingForTest'}");
        MongoIterable<Document> testCases = testCasesColl.find(query);

        List<String> testCaseList = new ArrayList<>();

        for (Document testCase : testCases) {
            testCaseList.add(testCase.getString("testId"));
        }

        return testCaseList;
    }
}
