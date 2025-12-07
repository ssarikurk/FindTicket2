package org.cucumber.step_definitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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


    @When("Scenario Started {string} - Browser Not Necessary")
    public void scenarioStartedBrowserNotNecessary(String scenarioName) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Scenario Name: "+scenarioName);
    }




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

            String from = (String) record.get("from");
//            System.out.println("from = " + from);
            String to = (String) record.get("to");
//            System.out.println("to = " + to);
            String dateStr = (String) record.get("date");
            System.out.println("Record: " + record);
            Driver.get().get("https://www.ucuzabilet.com/dis-hat-arama-sonuc?from=" + from + "&to=" + to + "&toIsCity=1&ddate=" + dateStr + "&adult=1&directflightsonly=on&flightType=2");
            BrowserUtils.waitFor(5);

            List<WebElement> flightList = ticketPage.flightItem;
            System.out.println("Total flights found: " + flightList.size());

            for (int i = 0; i < flightList.size(); i++) {
                Map<String, Object> flightMap = new LinkedHashMap<>();
                String id = "item-" + (i + 1);
                String airlineStr = "#item-" + (i + 1)+" .airline";
//                String flightDetail = "flight-detail-bar-" + (i + 1);
                String baggageDetail = "#flight-detail-bar-" + (i + 1)+" .col-10.text-left.pl-3";
                System.out.println("id = " + id);
                WebElement itemLocater = Driver.get().findElement(By.id(id));
                WebElement airlineLocater = Driver.get().findElement(By.cssSelector(airlineStr));
                WebElement baggageLocater = Driver.get().findElement(By.cssSelector(baggageDetail));
                String airline = airlineLocater.getText().trim();
                System.out.println("airline = " + airline);
//                System.out.println("baggageLocater.getText() = " + baggageLocater.getText());
//                System.out.println("flightLocater.getText() = " + flightLocater.getText());
//            System.out.println("itemLocater = " + itemLocater);
//            System.out.println("itemLocater.isDisplayed() = " + itemLocater.isDisplayed());
                String priceRaw = itemLocater.getAttribute("data-price");

                // normalize and parse price to Double (safe)
                double priceValue = 0.0;
                if (priceRaw != null) {
                    String priceClean = priceRaw.replaceAll("[^\\d,\\.]", "").replace(",", ".");
                    if (!priceClean.isEmpty()) {
                        try {
                            priceValue = Double.parseDouble(priceClean);
                        } catch (NumberFormatException ignored) {
                            priceValue = 0.0;
                        }
                    }
                }
                System.out.println("Rota = " + itemLocater.getAttribute("data-airports"));
                System.out.println("Fiyat = " + itemLocater.getAttribute("data-price") + " --> " + itemLocater.getAttribute("data-currency"));
                flightMap.put("Tarih", dateStr);
                flightMap.put("Havayolu", airline);
                flightMap.put("Rota", itemLocater.getAttribute("data-airports"));
                flightMap.put("Fiyat", priceValue);
                flightMap.put("Para Birimi", itemLocater.getAttribute("data-currency"));
//                flightMap.put("HavaYolu", itemLocater.getAttribute("airline"));
                flightMap.put("Bagaj", baggageLocater.getText().replace("Diğer bagaj seçenekleri", "").trim());

                flightMap.put("url", Driver.get().getCurrentUrl());

                System.out.println("---------------------------------------------------");
//                flight-detail-bar-2
                flights.add(flightMap);
                BrowserUtils.waitFor(1);

            }
        }
//        System.out.println("flights = " + flights);
        for (Map<String, Object> flight : flights) {
            System.out.println("flight = " + flight);
        }

        // sort flights by numeric price ascending; change to reversed(...) for descending
        flights.sort(Comparator.comparingDouble(m -> ((Number) m.get("Fiyat")).doubleValue()));
//        flights.sort(Comparator.comparingDouble(m -> ((Number) m.get("Fiyat")).doubleValue()));

        // export flights to html table file and assignt a unique name with timestamp
        StringBuilder htmlList = ExcelUtil.exportListofMapToHTMLTable("flight_search_results_" + System.currentTimeMillis() + ".html", flights);

//        System.out.println("htmlList = " + htmlList);
//        GmailUtil.sendHTMLEmail(String.valueOf(htmlList),"Uçuş Arama Sonuçları","gsarikurk@gmail.com", "suleymansarikurk@gmail.com");

    }


    @And("search for each flight in flight list on {string}")
    public void searchForEachFlightInFlightListOn(String arg0) {
        https://booking.kayak.com/flights/ESB-FRA/2026-03-24?fs=stops%3D0&sort=bestflight_a#dialog

        for (Map<String, Object> record : csvRecords) {
            System.out.println("record.toString() = " + record.toString());

            String from = (String) record.get("from");
//            System.out.println("from = " + from);
            String to = (String) record.get("to");
//            System.out.println("to = " + to);
            String dateStr = (String) record.get("date");
            String formatedDate = DateUtils.getNewFormatedDate3(dateStr, "dd.MM.yyyy", "yyyy-MM-dd");

            String url = "https://booking.kayak.com/flights/"+from+"-"+to+"/"+formatedDate+"?fs=stops%3D0&sort=bestflight_a#dialog";
            System.out.println("url = " + url);
            Driver.get().get(url);
            System.out.println("Driver.get().getCurrentUrl() = " + Driver.get().getCurrentUrl());
            BrowserUtils.waitFor(10);

            List<WebElement> flightListBooking = ticketPage.flightListBooking;
            for (WebElement webElement : flightListBooking) {
                System.out.println("webElement.getText() = " + webElement.getText());

            }

//    #flight-results-list-wrapper .Fxw9-result-item-container









        }




    }
}