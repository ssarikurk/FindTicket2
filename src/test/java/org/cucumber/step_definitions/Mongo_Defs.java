package org.cucumber.step_definitions;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.bson.Document;
import org.cucumber.utilities.MongoDBUtils;

public class Mongo_Defs {

    @When("Scenario Started {string} - Browser Not Necessary")
    public void scenarioStartedBrowserNotNecessary(String scenarioName) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Scenario Name: "+scenarioName);
    }


    @Given("Get data from {string} db and {string} collection")
    public void getDataFromDbAndCollection(String db, String collection) {
        MongoClient mongoClient = MongoDBUtils.getMongoClient();
        MongoCollection<Document> moviesCollection = MongoDBUtils.connectMongodb(mongoClient,db,collection);
        Document query = Document.parse("{directors:'Winsor McCay'}");

        Document movieDoc = moviesCollection.find(query).first();
        System.out.println("movieDoc.toJson() = " + movieDoc.toJson());

        JsonPath movieJson = JsonPath.from(movieDoc.toJson());
        movieJson.prettyPrint();

    }


}
