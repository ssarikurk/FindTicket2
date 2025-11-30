package org.cucumber.utilities;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import org.junit.Assert;

public class SyncUtils {

    public static void waitForSync (String ip, String partner, String practice, String mysqlDB) {
//        mysqlDB = ConfigurationReader.get(mysqlDB);
        System.out.println("mysqlDB = " + mysqlDB);
        BasicDBObject queryWritebackCount = BasicDBObject.parse("{'practice_id':'" + practice + "'}");
        System.out.println("queryWritebackCount = " + queryWritebackCount);
        int writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
        System.out.println("Writeback count = " + writebackCount);
        int waitCount = 150;
        String error = "";




        try {
            SqlUtils.createConnection(ip, mysqlDB, "root", "");
            String queryChangelogCount = "select count(*) from ret_change_log;";
            int changeLogCount = Integer.parseInt(SqlUtils.getCellValue(queryChangelogCount).toString());
            System.out.println("changeLogCount = " + changeLogCount);
            for (int i = 0; i < waitCount; i++) {
//            System.out.println("i = " + i);
                changeLogCount = Integer.parseInt(SqlUtils.getCellValue(queryChangelogCount).toString());

                if (changeLogCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("ChangeLog Table sync isn't finished or sync isn't working");
                        System.out.println("Remaining changeLogCount = " + changeLogCount);
//                        Assert.assertEquals("ChangeLog Table sync isn't finished or sync isn't working", 0, changeLogCount);
                    }
                    BrowserUtils.waitFor(3);
                } else {
                    System.out.println("ChangeLog table synced to Mongo");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Mysql changelog table sync failed because of Mongo");
        } catch (Exception e) {
            error = "Mysql changelog table not finished";
            System.out.println("error = " + error);
        }

        BrowserUtils.waitFor(10);

        //  opendental_writeback_1
        try {
            for (int i = 0; i < waitCount; i++) {
//            System.out.println("i = " + i);
                writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
                if (writebackCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("Writeback collection sync isn't finished or sync isn't working");
                        System.out.println("Remaining Writeback Count = " + writebackCount);
                    }
                    BrowserUtils.waitFor(2);
                } else {
                    System.out.println("All changed data written back to Opendantal");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Writeback_1 collection failed because of Mongo");
        } catch (Exception e) {
            error = "Writeback_1 collection not finished";
            System.out.println("error = " + error);
        }




        //opendental_received_record_1
        try {
            BasicDBObject queryReceivedRecordCount = BasicDBObject.parse("{'practice_id':'" + practice + "'}");
            System.out.println("queryReceivedRecordCount = " + queryReceivedRecordCount);
            int receivedRecordCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_received_record_1", queryReceivedRecordCount);
            System.out.println("Received Record count = " + receivedRecordCount);
            for (int i = 0; i < waitCount; i++) {
                System.out.println("i = " + i);
                receivedRecordCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_received_record_1", queryReceivedRecordCount);
                if (receivedRecordCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("Received record collection process isn't finished or process isn't working");
                        System.out.println("Remaining Received Record count = " + receivedRecordCount);
                        Assert.assertEquals("Received record collection process isn't finished or process isn't working", 0, receivedRecordCount);
                    }
                    BrowserUtils.waitFor(3);
                } else {
                    System.out.println("Received record collection is processed");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Received record collection process failed because of Mongo");
        } catch (Exception e) {
            error = "Received record collection process not finished";
            System.out.println("error = " + error);
        }

    }
    public static void waitForSyncOdToMongo (String ip, String partner, String practice, String mysqlDB) {
//        mysqlDB = ConfigurationReader.get(mysqlDB);
        int waitCount = 150;
        String error = "";




        BrowserUtils.waitFor(2);
        try {
            SqlUtils.createConnection(ip, mysqlDB, "root", "xxxx123");
            String queryChangelogCount = "select count(*) from ret_change_log;";
            int changeLogCount = Integer.parseInt(SqlUtils.getCellValue(queryChangelogCount).toString());
            System.out.println("changeLogCount = " + changeLogCount);
            for (int i = 0; i < waitCount; i++) {
//            System.out.println("i = " + i);
                changeLogCount = Integer.parseInt(SqlUtils.getCellValue(queryChangelogCount).toString());

                if (changeLogCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("ChangeLog Table sync isn't finished or sync isn't working");
                        System.out.println("Remaining changeLogCount = " + changeLogCount);
//                        Assert.assertEquals("ChangeLog Table sync isn't finished or sync isn't working", 0, changeLogCount);
                    }
                    BrowserUtils.waitFor(3);
                } else {
                    System.out.println("ChangeLog table synced to Mongo");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Mysql changelog table sync failed because of Mongo");
        } catch (Exception e) {
            error = "Mysql changelog table not finished";
            System.out.println("error = " + error);
        }

        BrowserUtils.waitFor(10);



        //opendental_received_record_1
        try {
            BasicDBObject queryReceivedRecordCount = BasicDBObject.parse("{'practice_id':'" + practice + "'}");
            System.out.println("queryReceivedRecordCount = " + queryReceivedRecordCount);
            int receivedRecordCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_received_record_1", queryReceivedRecordCount);
            System.out.println("Received Record count = " + receivedRecordCount);
            for (int i = 0; i < waitCount; i++) {
                System.out.println("i = " + i);
                receivedRecordCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_received_record_1", queryReceivedRecordCount);
                if (receivedRecordCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("Received record collection process isn't finished or process isn't working");
                        System.out.println("Remaining Received Record count = " + receivedRecordCount);
                        Assert.assertEquals("Received record collection process isn't finished or process isn't working", 0, receivedRecordCount);
                    }
                    BrowserUtils.waitFor(3);
                } else {
                    System.out.println("Received record collection is processed");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Received record collection process failed because of Mongo");
        } catch (Exception e) {
            error = "Received record collection process not finished";
            System.out.println("error = " + error);
        }

    }

    public static void waitForWritebackSync (String partner, String practice) {
        BasicDBObject queryWritebackCount = BasicDBObject.parse("{'practice_id':'" + practice + "'}");
        System.out.println("queryWritebackCount = " + queryWritebackCount);
        int writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
        System.out.println("Writeback count = " + writebackCount);
        int waitCount = 150;
        String error = "";

        //  opendental_writeback_1
        try {
            for (int i = 0; i < waitCount; i++) {
//            System.out.println("i = " + i);
                writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
                if (writebackCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("Writeback collection sync isn't finished or sync isn't working");
                        System.out.println("Remaining Writeback Count = " + writebackCount);
                    }
                    BrowserUtils.waitFor(2);
                } else {
                    System.out.println("All changed data written back to Opendantal");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Writeback_1 collection failed because of Mongo");
        } catch (Exception e) {
            error = "Writeback_1 collection not finished";
            System.out.println("error = " + error);
        }


        BrowserUtils.waitFor(10);


    }


    public static void waitForMongoOdWriteback1Sync (String partner, String practice) {

        BasicDBObject queryWritebackCount = BasicDBObject.parse("{'practice_id':'" + practice + "'}");
//        System.out.println("queryWritebackCount = " + queryWritebackCount);
        int writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
        System.out.println("Writeback count = " + writebackCount);
        int waitCount = 150;
        String error = "";
        try {
            for (int i = 0; i < waitCount; i++) {
//            System.out.println("i = " + i);
                writebackCount = MongoDBUtils.iterDocCount("mongoURI", partner, practice, "opendental_writeback_1", queryWritebackCount);
                if (writebackCount > 0) {
                    if (i == (waitCount - 1)) {
                        System.out.println("Writeback collection sync isn't finished or sync isn't working");
                        System.out.println("Remaining Writeback Count = " + writebackCount);
                    }
                    BrowserUtils.waitFor(2);
                } else {
                    System.out.println("All changed data written back to Opendantal");
                    break;
                }
            }
        } catch (MongoSocketException | MongoTimeoutException m) {
            m.printStackTrace();
            error = "Mongo error - " + m.getMessage();
            System.out.println("error = " + error);
            System.out.println("Writeback_1 collection failed because of Mongo");
        } catch (Exception e) {
            error = "Writeback_1 collection not finished";
            System.out.println("error = " + error);
        }

    }


}
