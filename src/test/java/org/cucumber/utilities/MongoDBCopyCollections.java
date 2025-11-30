package org.cucumber.utilities;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.bson.types.Decimal128;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class MongoDBCopyCollections {


    public static void main(String[] args) {
        new MongoDBCopyCollections().moveCollections();
    }

    public void moveCollections() {
        String partnerShort = "pdc";
        String partner = "pdc-all-dev-test-a";
        boolean trial1 = true;  // Adjust this value as needed

        try (MongoClient mongoClient = MongoDBUtils.getMongoClient()) {
            String sourceOpDb = "operations";
            String opDbOld = "operations-trial-1-" + partnerShort;
            String opDbNew = "operations-trial-2-" + partnerShort;

            String partDbOld = "partners-trial-1-" + partnerShort;
            String partDbNew = "partners-trial-2-" + partnerShort;

            String[] collectionsOperations = {
                    "aaaa_assigned",
                    "file_assigned",
                    "ledger_record"
            };

            String[] collectionsPartner = {
                    "aaaa_assigned",
                    "file_assigned",
                    "ledger_record"
            };

            if (trial1) {
                createAndCleanDatabase(mongoClient, opDbOld, collectionsOperations);
                createAndCleanDatabase(mongoClient, partDbOld, collectionsPartner);
                copyCollections(mongoClient, sourceOpDb, opDbOld, collectionsOperations);
                copyCollections(mongoClient, partner, partDbOld, collectionsPartner);
            } else {
                createAndCleanDatabase(mongoClient, opDbNew, collectionsOperations);
                createAndCleanDatabase(mongoClient, partDbNew, collectionsPartner);
                copyCollections(mongoClient, sourceOpDb, opDbNew, collectionsOperations);
                copyCollections(mongoClient, partner, partDbNew, collectionsPartner);
            }
        }
    }

    private static void createAndCleanDatabase(MongoClient mongoClient, String dbName, String[] collections) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoIterable<String> existingCollections = database.listCollectionNames();
        Set<String> existingCollectionsSet = new HashSet<>();
        for (String name : existingCollections) {
            existingCollectionsSet.add(name);
        }

        for (String collectionName : collections) {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            if (existingCollectionsSet.contains(collectionName)) {
                // Clean the existing collection
                collection.deleteMany(new Document());
                System.out.println("Cleaned collection " + collectionName + " in database " + dbName);
            } else {
                // Create the collection
                database.createCollection(collectionName);
                System.out.println("Created collection " + collectionName + " in database " + dbName);
            }
        }
    }

    private static void copyCollections(MongoClient mongoClient, String sourceDb, String destinationDb, String[] collections) {
        MongoDatabase sourceDatabase = mongoClient.getDatabase(sourceDb);
        MongoDatabase destinationDatabase = mongoClient.getDatabase(destinationDb);

        for (String collectionName : collections) {
            MongoCollection<Document> sourceCollection = sourceDatabase.getCollection(collectionName);
            MongoCollection<Document> destinationCollection = destinationDatabase.getCollection(collectionName);

            // Copy documents to destination database collection in batches
            List<Document> batch = new ArrayList<>();
            final int BATCH_SIZE = 1000; // Adjust batch size as needed

            for (Document doc : sourceCollection.find()) {
                Document sanitizedDoc = sanitizeDocument(doc);
                batch.add(sanitizedDoc);
                if (batch.size() == BATCH_SIZE) {
                    destinationCollection.insertMany(batch);
                    batch.clear();
                }
            }

            // Insert remaining documents
            if (!batch.isEmpty()) {
                destinationCollection.insertMany(batch);
            }

            System.out.println("Copied collection " + collectionName + " to " + destinationDb);
        }
    }
    private static Document sanitizeDocument(Document document) {
        Document sanitizedDoc = new Document();
        for (Entry<String, Object> entry : document.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check for and handle keys starting with '$'
            if (key.startsWith("$")) {
                key = key.replace("$", "DOLLAR_");
            }

            if (value instanceof Document) {
                value = sanitizeDocument((Document) value);
            } else if (value instanceof List) {
                value = sanitizeList((List<?>) value);
            } else if (value instanceof Decimal128) {
                value = ((Decimal128) value).bigDecimalValue();
            }

            sanitizedDoc.put(key, value);
        }
        return sanitizedDoc;
    }

    private static List<?> sanitizeList(List<?> list) {
        List<Object> sanitizedList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Document) {
                sanitizedList.add(sanitizeDocument((Document) item));
            } else if (item instanceof List) {
                sanitizedList.add(sanitizeList((List<?>) item));
            } else {
                sanitizedList.add(item);
            }
        }
        return sanitizedList;
    }
}