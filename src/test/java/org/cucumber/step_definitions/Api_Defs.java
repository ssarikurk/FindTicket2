package org.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import org.cucumber.utilities.ApiUtils;

public class Api_Defs {
    @And("Get api response")
    public void getApiResponse() {
        JsonPath petJson = ApiUtils.getPetWithPetid(5);

        String petName = petJson.getString("category.name");
        System.out.println("petName = " + petName);


    }

    @Then("verify data")
    public void verifyData() {
        System.out.println("Wip");
    }
}
