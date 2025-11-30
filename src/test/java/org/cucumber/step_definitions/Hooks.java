package org.cucumber.step_definitions;

import org.cucumber.utilities.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Hooks implements EventListener {

    @Before (value = "@webApp", order = 1)
    public void setUp(Scenario scenario){

        System.out.println("----------------------------------------------------------------------\nScenario Started = " + scenario.getName()+"\n----------------------------------------------------------------------");
        Driver.get().manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        Driver.get().manage().window().maximize();
       // BrowserUtils.waitFor(5);
    }

    @After (value = "@webApp", order = 0)
    public void tearDown(Scenario scenario){
        if(scenario.isFailed()){
            final byte[] screenshot = ((TakesScreenshot) Driver.get()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot,"image/png","screenshot");
            System.out.println("***FAILED*** \n----------------------------------------------------------------------");
            System.out.println(scenario.getName()+" ----> "+scenario.getStatus());
        }else {
            System.out.println("***PASSED*** \n----------------------------------------------------------------------");
            System.out.println(scenario.getName()+" ----> "+scenario.getStatus());
        }

        Driver.closeDriver();

    }

}