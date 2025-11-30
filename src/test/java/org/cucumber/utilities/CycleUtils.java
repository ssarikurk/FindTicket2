package org.cucumber.utilities;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import io.restassured.path.json.JsonPath;
import org.bson.Document;
import org.junit.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class CycleUtils {

    public static Document getTestCase2(String testCasesDB, String testCaseName) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        BasicDBObject query = BasicDBObject.parse("{testCaseName:'" + testCaseName + "'}");
//        System.out.println("query = " + query);
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, testCasesDB, "CycleTestCases");
        Document testDoc = eraTestColl.find(query).first();

        mongoClient.close();
        return testDoc;
    }

    public static Document getTestCase(String testCaseName, String collectionName, String testCasesDB) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        BasicDBObject query = BasicDBObject.parse("{testCaseName:'" + testCaseName + "'}");
//        System.out.println("query = " + query);
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, testCasesDB, collectionName);
        Document testDoc = eraTestColl.find(query).first();

        mongoClient.close();
        return testDoc;
    }

    public static void updateTestCaseDoc(String testId, String key, String value, String testCasesDB) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        Document filter = new Document("testId", testId);
        Document update = new Document("$set", new Document(key, value));
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, testCasesDB, "CycleTestCases");

        eraTestColl.updateMany(filter, update);
        System.out.println("document updated");
        mongoClient.close();
    }

    public static Map<String, Object> getExistingSettingFromPartner(String partner, String practice_id) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        BasicDBObject query = BasicDBObject.parse("{practice_id:'" + practice_id + "'}");
//        System.out.println("query = " + query);
        MongoCollection<Document> practice1Coll = MongoDBUtils.connectMongodb(mongoClient, partner, "practice_1");
        Document practiceDoc = practice1Coll.find(query).first();
//        System.out.println("practiceDoc.toJson() = " + practiceDoc.toJson());

        JsonPath jsonPath = JsonPath.from(practiceDoc.toJson());

        Map<String, Object> settings = jsonPath.getMap("settings.era");
//        System.out.println("settings = " + settings);

        mongoClient.close();
        return settings;
    }

    public static boolean isAutomationSettingsMatching(String partner, String practice_id, String testCasesDB, String testCaseName) {

        if (testCaseName.contains("_")) {
            testCaseName = testCaseName.replace("_", " ");
        }
        Map<String, Object> settingsExisting = getExistingSettingFromPartner(partner, practice_id);
        System.out.println("settingsExisting = " + settingsExisting);

        Document testDoc = getTestCase2(testCasesDB, testCaseName);
        Map<String, Object> settingsExpected = JsonPath.from(testDoc.toJson()).getMap("settings");
        System.out.println("settingsExpected = " + settingsExpected);

        boolean settingsMatched = settingsExisting.equals(settingsExpected);
        System.out.println("settingsMatched = " + settingsMatched);
        return settingsMatched;
    }


    public static void setRestoreStatus(boolean settingsValue) {
//        System.out.println("settingsValue = " + settingsValue);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "restore");
        Document keepSettingsUpdate = new Document("$set", new Document("isRestoreInprogress", settingsValue).append("timeStamp", new Date()));
        try {
            MongoDBUtils.executeUpdateQuery(keepSettingsColl, keepSettingFilter, keepSettingsUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    public static void setCycleReadiness(boolean settingsValue) {
//        System.out.println("settingsValue = " + settingsValue);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "restore");
        Document keepSettingsUpdate = new Document("$set", new Document("isReadyToCycle", settingsValue).append("timeStamp", new Date()));
        try {
            MongoDBUtils.executeUpdateQuery(keepSettingsColl, keepSettingFilter, keepSettingsUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    public static boolean getCycleReadiness() {
//        System.out.println("settingsValue = " + settingsValue);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "restore");
        boolean isReadyToCycle = false;
        try {
            Document settingDoc = keepSettingsColl.find(keepSettingFilter).first();
            isReadyToCycle = settingDoc.getBoolean("isReadyToCycle");
            System.out.println("isReadyToCycle = " + isReadyToCycle);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return isReadyToCycle;
    }


    public static boolean getRestoreStatus() {

//        System.out.println("partner = " + partner);
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "restore");
        boolean isRestoreInprogress = true;
        try {
            Document keepSetDoc = keepSettingsColl.find(keepSettingFilter).first();
            isRestoreInprogress = (boolean) keepSetDoc.get("isRestoreInprogress");
            System.out.println("isRestoreInprogress = " + isRestoreInprogress);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return isRestoreInprogress;
    }

    public static boolean getCycleStatus(String partner) {

        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "CycleStatus").append("partner", partner);
        boolean isCycleInprogress = true;
        try {
            Document keepSetDoc = keepSettingsColl.find(keepSettingFilter).first();
            isCycleInprogress = (boolean) keepSetDoc.get("isCycleInprogress");
            System.out.println("isCycleInprogress = " + isCycleInprogress);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return isCycleInprogress;
    }


    public static void setCycleProgressStatus(boolean settingsValue, String partner) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> keepSettingsColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", "keepSettings");
        Document keepSettingFilter = new Document("settingsName", "CycleStatus")
                .append("partner", partner);
        Document keepSettingsUpdate = new Document("$set", new Document("isCycleInprogress", settingsValue)
                .append("startTime", new Date()));
        try {
            MongoDBUtils.executeUpdateQuery(keepSettingsColl, keepSettingFilter, keepSettingsUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    public static void checkECATStatus() {
        String partner = "qa-clinic3";
        boolean isRestoreInprogress = CycleUtils.getRestoreStatus();
        boolean isCycleInprogress = CycleUtils.getCycleStatus(partner);

        boolean isEveryThingOkForECAT = !isCycleInprogress && !isRestoreInprogress;
        System.out.println("isEveryThingOkForCycleNewTest = " + isEveryThingOkForECAT);
        Assert.assertTrue("!!!!!An Environment Restore or Cycle test is Inprogress you need to wait until it finishes!!!!!", isEveryThingOkForECAT);

    }

    public static Document getEraSplitFileAssigned(String partner, String checkNumber) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> eraSplitFileAssignedColl = MongoDBUtils.connectMongodb(mongoClient, partner, "era_split_file_assigned");
        String filter = "{check_number:'" + checkNumber + "'}";
        BasicDBObject query = BasicDBObject.parse(filter);
        Document eraSplitFileDoc = eraSplitFileAssignedColl.find(query).first();
        mongoClient.close();
        return eraSplitFileDoc;
    }

    public static void setWorkableDenialEntry(String partner, String practiceId, String codeNumber, boolean codeStatus) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> workableDenialEntryColl = MongoDBUtils.connectMongodb(mongoClient, partner, "workable_denial_entry_1");

        // Create the query filter
        BasicDBObject query = BasicDBObject.parse("{practice_id:'"+practiceId+"', code:'" + codeNumber + "'}");

        // Define the update operation
        BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("workable", codeStatus));
//
//        // Execute the update operation
        workableDenialEntryColl.updateOne(query, update);

        Document workableDenialEntryDoc = workableDenialEntryColl.find(query).first();
        String changedCode = workableDenialEntryDoc.getString("code");
        System.out.println("changedCode = " + changedCode);
        boolean workableStatus = workableDenialEntryDoc.getBoolean("workable");
        System.out.println("workableStatus = " + workableStatus);


        System.out.println("Updated 'workable' to " + workableStatus + " for code: " + changedCode);


        mongoClient.close();

    }

    public static Document getEraSplitFileAssignedAdjustmentTableData(Document eraSplitFileDoc) {

        List<Document> writebackRecords = (List<Document>) eraSplitFileDoc.get("writeback_records", List.class);
        Document data = null;
        for (Document record : writebackRecords) {
            // Check if the table_name is "adjustment"
            if ("adjustment".equals(record.getString("table_name"))) {
                // Retrieve the data object
                data = record.get("data", Document.class);
                break; // Exit the loop after finding the first matching record
            }
        }
        return data;
    }

    public static List<Document> getOpendentalWritebackArchive_1(String partner, String transactionId) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> OpendentalWritebackArchiveColl = MongoDBUtils.connectMongodb(mongoClient, partner, "opendental_writeback_archive_1");
        String filter = "{transaction_id:'" + transactionId + "'}";
        BasicDBObject query = BasicDBObject.parse(filter);
        List<Document> listOpendentalWritebackArchiveDoc = (List<Document>) OpendentalWritebackArchiveColl.find(query);
        mongoClient.close();
        return listOpendentalWritebackArchiveDoc;
    }

    public static Document getOpendentalWritebackArchive_1Table(String partner, String transactionId, String tablename) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> OpendentalWritebackArchiveColl = MongoDBUtils.connectMongodb(mongoClient, partner, "opendental_writeback_archive_1");
        String filter = "{transaction_id:'" + transactionId + "', table_name:'" + tablename + "'}";
        BasicDBObject query = BasicDBObject.parse(filter);
        Document OpendentalWritebackArchive1TableDoc = OpendentalWritebackArchiveColl.find(query).first();
        mongoClient.close();
        return OpendentalWritebackArchive1TableDoc;
    }

    public static Document getPlbAdjustment835Assigned(String partner, String stHash) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> plbAdjustment835AssignedColl = MongoDBUtils.connectMongodb(mongoClient, partner, "plb_adjustment_835_assigned");
        String filter = "{st_hash:'" + stHash + "'}";
        BasicDBObject query = BasicDBObject.parse(filter);
        Document plbAdjustment835AssignedDoc = plbAdjustment835AssignedColl.find(query).first();
        mongoClient.close();
        return plbAdjustment835AssignedDoc;
    }


    public static Document getAdjustment1(String partner, String practiceId, String adjId) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> adjustment_1Coll = MongoDBUtils.connectMongodb(mongoClient, partner, "adjustment_1");
        String filter = "{practice_id:'" + practiceId + "', adjustment_id:'" + adjId + "'}";
        BasicDBObject query = BasicDBObject.parse(filter);
        Document adjustment_1Doc = adjustment_1Coll.find(query).first();
        mongoClient.close();
        return adjustment_1Doc;
    }

    public static Document getSvc835Assigned(String partner, String contentHash, String claimIdentifier) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> svc835AssignedColl = MongoDBUtils.connectMongodb(mongoClient, partner, "svc_835_assigned");
        BasicDBObject querySvc = BasicDBObject.parse("{st_hash:'" + contentHash + "','matched_claim_1.claim_identifier':'" + claimIdentifier + "'}");
        Document svc835AssignedDoc = svc835AssignedColl.find(querySvc).first();
        mongoClient.close();
        return svc835AssignedDoc;
    }

    public static Document svc835UserEditLog(String partner, String svcHash) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> svc835UserEditLogColl = MongoDBUtils.connectMongodb(mongoClient, partner, "svc_835_user_edit_log");
        BasicDBObject querySvc = BasicDBObject.parse("{svc_hash:'" + svcHash + "'}");
        Document svc835UserEditLogDoc = svc835UserEditLogColl.find(querySvc).first();
        mongoClient.close();
        return svc835UserEditLogDoc;
    }


    public static String getInterchangeEndpoint(String interchangeId, String endpointName) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> interchangeColl = MongoDBUtils.connectMongodb(mongoClient, "directory", "interchange_1");
        BasicDBObject query = BasicDBObject.parse("{interchange_id : '" + interchangeId + "'}");
        System.out.println("query = " + query);
        Document interchangeDoc = interchangeColl.find(query).first();
        JsonPath interchangeJson = JsonPath.from(interchangeDoc.toJson());
        String uploadPath = interchangeJson.getString("endpoints." + endpointName + ".ftp.path");
        return uploadPath;
    }

    public static String getInterchangeEndpointByName( String interchangeName, String endpointName){
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> interchangeColl = MongoDBUtils.connectMongodb(mongoClient,"directory","interchange_1");
        BasicDBObject query = BasicDBObject.parse("{name : '"+interchangeName+"'}");
        System.out.println("query = " + query);
        Document interchangeDoc = interchangeColl.find(query).first();
        JsonPath interchangeJson = JsonPath.from(interchangeDoc.toJson());
        String uploadPath = interchangeJson.getString("endpoints."+endpointName+".ftp.path");
        return uploadPath;
    }

}





