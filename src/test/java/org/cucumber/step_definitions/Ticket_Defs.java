package org.cucumber.step_definitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.cucumber.pages.TicketPage;
import org.cucumber.utilities.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Ticket_Defs {
    @Given("Navigate to {string}")
    public void navigate_to(String environment) {
        System.out.println("environment = " + environment);

        String url = ConfigurationReader.get(environment);
        //WebDriver driver = Driver.get();
        Driver.get().get(url);

        BrowserUtils.waitFor(0.2);

        BrowserUtils.waitFor(5);
    }



    TicketPage ticketPage = new TicketPage();

    @Then("search for flights from {string} to {string}")
    public void searchForFlightsFromTo(String from, String to) {

        ticketPage.fromText.clear();
        ticketPage.fromText.sendKeys(from);
        BrowserUtils.waitFor(0.5);
        ticketPage.fromText.sendKeys(Keys.ARROW_DOWN);
        BrowserUtils.waitFor(1);
        ticketPage.fromText.sendKeys(Keys.ENTER);
        ticketPage.toText.clear();
        ticketPage.toText.sendKeys(to);
        BrowserUtils.waitFor(1);
        ticketPage.toText.sendKeys(Keys.ARROW_DOWN + "" + Keys.ENTER);
        BrowserUtils.waitFor(4);
        ticketPage.searchFormSubmit.click();
        BrowserUtils.waitFor(20);

    }

    String dateStr;

    @And("select departure date as {string}")
    public void selectDepartureDateAs(String date) {
        Driver.get().get("https://www.ucuzabilet.com/dis-hat-arama-sonuc?from=ESB&to=DUS&toIsCity=1&ddate=" + date + "&adult=1&directflightsonly=on&flightType=2");
//        Driver.get().get("https://www.ucuzabilet.com/dis-hat-arama-sonuc?from=ESB&to=DUS&toIsCity=1&ddate="+date+"&adult=1&flightType=2");

        dateStr = date;
        BrowserUtils.waitFor(2);
    }

    @Then("collect flight list")
    public void collectFlightList() {
        List<WebElement> flightList = ticketPage.flightItem;
        System.out.println("Total flights found: " + flightList.size());
//        int n = 1;
        for (int i = 0; i < flightList.size(); i++) {
            String id = "item-" + (i + 1);
            System.out.println("id = " + id);
            WebElement itemLocater = Driver.get().findElement(By.id(id));
//            System.out.println("itemLocater = " + itemLocater);
//            System.out.println("itemLocater.isDisplayed() = " + itemLocater.isDisplayed());

            System.out.println("Rota = " + itemLocater.getAttribute("data-airports"));
            System.out.println("Fiyat = " + itemLocater.getAttribute("data-price") + " --> " + itemLocater.getAttribute("data-currency"));
//            String transactionAmount = Driver.get().findElement(By.cssSelector()
//            System.out.println(flight.getText());
            System.out.println("---------------------------------------------------");
//            n++;

        }
    }

    @And("select from {string} to {string} departure date as {string}")
    public void selectFromToDepartureDateAs(String from, String to, String dateStr) {
        Driver.get().get("https://www.ucuzabilet.com/dis-hat-arama-sonuc?from=" + from + "&to=" + to + "&toIsCity=1&ddate=" + dateStr + "&adult=1&directflightsonly=on&flightType=2");
        BrowserUtils.waitFor(2);
    }

    List<Map<String, Object>> csvRecords = new ArrayList<>();

    @Then("read search data from csv {string}")
    public void readSearchDataFromCsv(String csvFile) {
        csvRecords = ExcelUtil.readCSVtoListofMapWithPath(csvFile);
        // print csvRecords
//        for (Map<String, Object> record : csvRecords) {
//            System.out.println("Record: " + record);
//        }
    }

    List<Map<String, Object>> flights = new ArrayList<>();

    @And("search for each flight in flight list")
    public void searchForEachFlightInFlightList() {
        for (Map<String, Object> record : csvRecords) {
            Map<String, Object> flightMap = new HashMap<>();
            String from = (String) record.get("from");
//            System.out.println("from = " + from);
            String to = (String) record.get("to");
//            System.out.println("to = " + to);
            String dateStr = (String) record.get("date");
            System.out.println("Record: " + record);
            Driver.get().get("https://www.ucuzabilet.com/dis-hat-arama-sonuc?from=" + from + "&to=" + to + "&toIsCity=1&ddate=" + dateStr + "&adult=1&directflightsonly=on&flightType=2");
            BrowserUtils.waitFor(2);

            List<WebElement> flightList = ticketPage.flightItem;
            System.out.println("Total flights found: " + flightList.size());

            for (int i = 0; i < flightList.size(); i++) {
                String id = "item-" + (i + 1);
                System.out.println("id = " + id);
                WebElement itemLocater = Driver.get().findElement(By.id(id));
//            System.out.println("itemLocater = " + itemLocater);
//            System.out.println("itemLocater.isDisplayed() = " + itemLocater.isDisplayed());

                System.out.println("Rota = " + itemLocater.getAttribute("data-airports"));
                System.out.println("Fiyat = " + itemLocater.getAttribute("data-price") + " --> " + itemLocater.getAttribute("data-currency"));

                flightMap.put("Rota", itemLocater.getAttribute("data-airports"));
                flightMap.put("Fiyat", itemLocater.getAttribute("data-price"));
                flightMap.put("Para Birimi", itemLocater.getAttribute("data-currency"));
                flightMap.put("Tarih", dateStr);
                System.out.println("---------------------------------------------------");

            }
            flights.add(flightMap);
        }
        System.out.println("flights = " + flights);
        // export flights to html table file and assignt a unique name with timestamp
        StringBuilder htmlList = ExcelUtil.exportListofMapToHTMLTable("flight_search_results_" + System.currentTimeMillis() + ".html", flights);

        System.out.println("htmlList = " + htmlList);
        GmailUtil.sendHTMLEmail(String.valueOf(htmlList),"Uçuş Arama Sonuçları","gsarikurk@gmail.com", "suleymansarikurk@gmail.com");

    }



}