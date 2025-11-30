package org.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.cucumber.pages.LoginPage;
import org.cucumber.utilities.BrowserUtils;
import org.cucumber.utilities.ConfigurationReader;
import org.cucumber.utilities.Driver;

public class Login_Defs {

    @Given("the user logged in as {string} to {string} as partner {string}")
    public void the_user_logged_in_as_to_as_partner(String userName, String environment) {

        String url = ConfigurationReader.get(environment);
        //WebDriver driver = Driver.get();
        Driver.get().get(url);

        String username =null;
        String password =null;
        String partner =null;

        if(userName.equals("superuser")){
            username = ConfigurationReader.get("Superuser");
            password = ConfigurationReader.get("Superpassword");

        }else if(userName.equals("Super")){
            username = ConfigurationReader.get("Super");
            password = ConfigurationReader.get("Superpassword");

        }else if(userName.equals("testroles")){
            username = ConfigurationReader.get("testroles");
            password = ConfigurationReader.get("Superpassword");

        }
        LoginPage loginPage = new LoginPage();
        BrowserUtils.waitFor(1);
        loginPage.login(username,password);

        BrowserUtils.waitFor(1);
    }
    @When("Scenario Started {string}")
    public void scenario_Started(String message) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Scenario Name: "+message);
//        System.out.println("------------------------------------------------------------------------");
    }


    @Given("User logs in to {string}")
    public void userLogsInTo(String environment) {
        System.out.println("environment = " + environment);

        String url = ConfigurationReader.get(environment);
        //WebDriver driver = Driver.get();
        Driver.get().get(url);

        String username =null;
        String password =null;

        if(environment.equals("sauceLabs")){
            username = ConfigurationReader.get("sauceLabsStandartUser");
            password = ConfigurationReader.get("sauceLabsPass");

        }else if(environment.equals("skyscanner")){
            username = ConfigurationReader.get("Super");
            password = ConfigurationReader.get("Superpassword");

        }else if(environment.equals("testroles")){
            username = ConfigurationReader.get("testroles");
            password = ConfigurationReader.get("Superpassword");

        }
        LoginPage loginPage = new LoginPage();
        BrowserUtils.waitFor(0.2);
//        System.out.println("username = " + username);
//        System.out.println("password = " + password);
        loginPage.login(username,password);

        BrowserUtils.waitFor(5);
    }


}
