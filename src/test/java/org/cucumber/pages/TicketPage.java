package org.cucumber.pages;

import org.cucumber.utilities.BrowserUtils;
import org.cucumber.utilities.Driver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class TicketPage extends BasePage{

    public TicketPage(){PageFactory.initElements(Driver.get(), this);}

    @FindBy(id="from_text")
    public  WebElement fromText;
    @FindBy(id="to_text")
    public  WebElement toText;
    @FindBy(id="searchFormSubmit")
    public  WebElement searchFormSubmit;
    @FindBy(css = ".ant-collapse-item")
    public List<WebElement> practiceSelectList;
    @FindBy(css = ".flight-item")
    public List<WebElement> flightItem;



}
