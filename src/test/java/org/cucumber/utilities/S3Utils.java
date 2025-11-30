package org.cucumber.utilities;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class S3Utils {

    public static int listS3ResultList(S3Client s3, String bucketName, String filePath ) {
        int objectSize = 0;
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .prefix(filePath)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            objectSize = objects.size();
            System.out.println("objects.size() = " + objects.size());
            for (S3Object myValue : objects) {
                System.out.println("\n The name of the key is " + myValue.key());
//                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
//                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return objectSize;
    }




    public static void deleteFilesFromS3(String folderName, String s3BucketName ) {
        // Define your credentials
        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
        );

        // Define your region
        Regions clientRegion = Regions.US_EAST_1;

        // Folder to delete inside the bucket
//        String folderName = "Mike/";  // Replace with your folder name (include trailing slash)
//        String s3BucketName = "your-bucket-name";  // Replace with your S3 bucket name

        try {
            // Create an Amazon S3 client
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            // List all objects in the specified folder
            ObjectListing objectListing = s3Client.listObjects(s3BucketName, folderName);
            List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

            // Collect the keys of all objects in the folder
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                keys.add(new DeleteObjectsRequest.KeyVersion(objectSummary.getKey()));
            }

            // Check if there are objects to delete
            if (!keys.isEmpty()) {
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(s3BucketName).withKeys(keys);
                s3Client.deleteObjects(deleteObjectsRequest);
                System.out.println("All files in the folder '" + folderName + "' have been deleted.");
            } else {
                System.out.println("The folder '" + folderName + "' is already empty.");
            }

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it and returned an error response
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response or the client couldn't parse the response from Amazon S3
            e.printStackTrace();
        }
    }

    public static void deleteSpecificFileFromS3(String fileName, String folderName, String s3BucketName) {
        // Define your credentials
        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
        );

        // Define your region
        Regions clientRegion = Regions.US_EAST_1;

        try {
            // Create an Amazon S3 client
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            String fileKey = folderName + fileName;

            s3Client.deleteObject(s3BucketName, fileKey);

            System.out.println("The file '" + fileKey + "' has been deleted from S3 successfully.");
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }


    public static void uploadToS3(String fileName, String s3Path, String s3BucketName ) {

        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
//                    ConfigurationReader.get("accessKeyDev"),
//                    ConfigurationReader.get("secretKeyDev")
        );

        Regions clientRegion = Regions.US_EAST_1;

        String stringObjKeyName = fileName;
        String fileObjKeyName = s3Path+fileName;
        String filePath = BrowserUtils.getDownloadPath()+"/"+fileName;


        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(s3BucketName, fileObjKeyName, new File(filePath));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
            System.out.println("------------------------- "+fileName+" Uploaded to S3 ------------------------");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();
        }

    }

    public static void upload820ToS3(String fileName, String s3Path, String s3BucketName, String collectionName) {

        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
//                    ConfigurationReader.get("accessKeyDev"),
//                    ConfigurationReader.get("secretKeyDev")
        );

        Regions clientRegion = Regions.US_EAST_1;

        String stringObjKeyName = fileName;
        String fileObjKeyName = s3Path+fileName;
        String filePath = BrowserUtils.getDownloadPath()+"/"+fileName;

        //upload820ToS3Success
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        String update = "error";
        String filter = "{fileName820:'"+fileName+"'}";
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, "qa-test", collectionName);
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(s3BucketName, fileObjKeyName, new File(filePath));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
            System.out.println("------------------------- "+fileName+" Uploaded to S3 ------------------------");

            String updateMessage = fileName+" uploaded to server succesfully";
            update = "{$set:{upload820ToS3Success:'"+updateMessage+"'}}";

            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();

            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();
            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        }finally {
            mongoClient.close();
        }

    }
    public static void upload820ToS3_2(String fileName, String s3Path, String s3BucketName, String collectionName, String testCaseDB) {

        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
//                    ConfigurationReader.get("accessKeyDev"),
//                    ConfigurationReader.get("secretKeyDev")
        );

        Regions clientRegion = Regions.US_EAST_1;

        String stringObjKeyName = fileName;
        String fileObjKeyName = s3Path+fileName;
        String filePath = BrowserUtils.getDownloadPath()+"/"+fileName;

        //upload820ToS3Success
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        String update = "error";
        String filter = "{fileName820:'"+fileName+"'}";
        MongoCollection<Document> eraTestColl = MongoDBUtils.connectMongodb(mongoClient, testCaseDB, collectionName);
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(s3BucketName, fileObjKeyName, new File(filePath));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            s3Client.putObject(request);
            System.out.println("------------------------- "+fileName+" Uploaded to S3 ------------------------");

            String updateMessage = fileName+" uploaded to server succesfully";
            update = "{$set:{upload820ToS3Success:'"+updateMessage+"'}}";

            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();

            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("------------------------- !!!File Upload ERROR!!! ------------------------");
            e.printStackTrace();
            update = "{$set:{upload820ToS3Success:'failed'}}";
            MongoDBUtils.executeUpdateQuery(eraTestColl, filter, update);
        }finally {
            mongoClient.close();
        }

    }

    public static void downloadFromS3(String fileName, String s3Path, String s3BucketName ) {

        String keyName = s3Path+fileName; // The S3 object key you want to download
        String localFilePath = BrowserUtils.getDownloadPath()+"/"+fileName; // Local file path to save the downloaded file

        Regions clientRegion = Regions.US_EAST_1;

        AWSCredentials credentials = new BasicAWSCredentials(
                ConfigurationReader.get("accessKeyStaging"),
                ConfigurationReader.get("secretKeyStaging")
//                    ConfigurationReader.get("accessKeyDev"),
//                    ConfigurationReader.get("secretKeyDev")
        );
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        try {
            com.amazonaws.services.s3.model.S3Object s3Object = s3Client.getObject(new com.amazonaws.services.s3.model.GetObjectRequest(s3BucketName, keyName));
            S3ObjectInputStream objectContent = s3Object.getObjectContent();

            File file = new File(localFilePath);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                int read;
                byte[] buffer = new byte[1024];
                while ((read = objectContent.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }
            }

            System.out.println(" -------------- File downloaded successfully to: " + localFilePath+" --------------");
        } catch (AmazonClientException | IOException e) {
            e.printStackTrace();
        }
    }

}


