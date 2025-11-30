package org.cucumber.pages;

import org.cucumber.utilities.BrowserUtils;
import org.cucumber.utilities.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public abstract class BasePage {

    @FindBy(css = "div[class='loader-mask shown']")
    @CacheLookup
    protected WebElement loaderMask;

    @FindBy(css = "h1[class='oro-subtitle']")
    public WebElement pageSubTitle;

    @FindBy(css = "#user-menu > a")
    public WebElement userName;

    @FindBy (xpath = "(//*[contains(text(),'Providers')])[1]/../..")
    public WebElement providersTab;

    @FindBy (xpath = "(//*[contains(text(),'Settings')])[1]/../..")
    public WebElement settingsTab;

    @FindBy (xpath = "(//*[contains(text(),'Analytics')])[1]/../..")
    public WebElement analyticsTab;

    @FindBy (xpath = "(//*[contains(text(),'Data')])[1]/../..")
    public WebElement dataTab;

    @FindBy (xpath = "(//*[contains(text(),'Member')])[1]/../..")
    public WebElement memberTab;

    @FindBy (xpath = "(//*[contains(text(),'Cases')])[1]/../..")
    public WebElement casesTab;

    @FindBy (xpath = "(//*[contains(text(),'Studio')])[1]/../..")
    public WebElement studioTab;

    @FindBy (xpath = "(//*[contains(text(),'Policies')])[1]/../..")
    public WebElement policiesTab;

    @FindBy (xpath = "(//*[contains(text(),'Cases')])[1]/../..")
    public WebElement SIUTab;

    @FindBy (xpath = "(//*[contains(text(),'Claims')])[1]/../..")
    public WebElement claimsTab;

    @FindBy (xpath = "(//*[contains(text(),'Reporting')])[1]/../..")
    public WebElement reportingTab;

    @FindBy (xpath = "(//*[contains(text(),'Portal')])[1]/../..")
    public WebElement portalTab;

    @FindBy (id="uM0N5YSrdBUgDP-9S2iqA")
    public List<WebElement> leftPanel;

    @FindBy (id="clearinghouses.tesia.payer_name")
    public List<WebElement> clearinHousePayerName;

    @FindBy (css = ".ant-spin-dot ant-spin-dot-spin")  //ant-spin-dot.ant-spin-dot-spin
    @CacheLookup
    protected WebElement spinningDots;

    @FindBy (css = "._1xTp6N90zTOy394Mu2ZGLX")
    @CacheLookup
    protected WebElement spinningSquare;

    @FindBy (css = "._1xTp6N90zTOy394Mu2ZGLX")
    @CacheLookup
    protected WebElement spinningBigDots;

    @FindBy (xpath = "//*[.=' Filter']")
    public WebElement filter;

    @FindBy (xpath = "(//input[@placeholder='Separate by comma'])[2]")
    public WebElement claimKeyFilter;

    @FindBy (xpath = "//input[@placeholder='Start date']")
    public WebElement startDate;

    @FindBy (xpath = "//input[@placeholder='End date']")
    public WebElement endDate;

    @FindBy (xpath = "//button[.='OK']//span")
    public WebElement filterOk;

    @FindBy (xpath = "//td[@title='2020-09-21']")
    public WebElement clickDate;

    @FindBy(css = ".loader_fullscreen__YnFOX")
    public List<WebElement> loaderDotsBig;



    public BasePage() {
        PageFactory.initElements(Driver.get(), this);
    }


    /**
     * @return page name
     */
    public String getPageSubTitle() {
        //ant time we are verifying page name, or page subtitle, loader mask appears
        waitUntilSpinningDotsDisappear();
//        BrowserUtils.waitForStaleElement(pageSubTitle);
        return pageSubTitle.getText();
    }


    /**
     * Waits until loader screen present. If loader screen will not pop up at all,
     * NoSuchElementException will be handled  bu try/catch block
     * Thus, we can continue in any case.
     */
    public void waitUntilSpinningDotsDisappear() {
        try {
            WebDriverWait wait = new WebDriverWait(Driver.get(), 5);
            wait.until(ExpectedConditions.invisibilityOf(spinningDots));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void waitUntilSpinningSquareDisappear() {
        try {
            WebDriverWait wait = new WebDriverWait(Driver.get(), 10);
            wait.until(ExpectedConditions.invisibilityOf(spinningSquare));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void waitUntilSpinningBigDotsDisappear() {
        try {
            WebDriverWait wait = new WebDriverWait(Driver.get(), 5);
            wait.until(ExpectedConditions.invisibilityOf(spinningBigDots));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void navigateToTab(String tab) {
      //  String tabLocator = "//span[normalize-space()='" + tab + "' and contains(@class, 'title title-level-1')]";
//        String tabLocator = "(//div[@class='provider_navbar_navbarItem__QqLOY']//span[contains(text(), '"+tab+"')])[1]";
        String tabLocator = "//div[.='"+tab+"']";

//        try {
//            BrowserUtils.waitForClickablility(By.xpath(tabLocator), 5);
//            WebElement tabElement = Driver.get().findElement(By.xpath(tabLocator));
//            new Actions(Driver.get()).moveToElement(tabElement).pause(200).doubleClick(tabElement).build().perform();
//        } catch (Exception e) {
//            BrowserUtils.clickWithWait(By.xpath(tabLocator), 5);
//        }

//        (//*[contains(text(),'Data')])[1]

        try {
            BrowserUtils.waitForPresenceOfElement(By.xpath(tabLocator), 5);
            BrowserUtils.waitForVisibility(By.xpath(tabLocator), 5);
          //  BrowserUtils.scrollToElement(Driver.get().findElement(By.xpath(tabLocator)));
            Driver.get().findElement(By.xpath(tabLocator)).click();
            BrowserUtils.waitFor(3);
        } catch (Exception e) {
//            BrowserUtils.waitForStaleElement(Driver.get().findElement(By.xpath(moduleLocator)));
            BrowserUtils.clickWithTimeOut(Driver.get().findElement(By.xpath(tabLocator)),  5);
            BrowserUtils.waitFor(3);
        }
    }
//*[contains(text(),'Groups')]

    public void navigatePro(String tab) {
        //  String tabLocator = "//span[normalize-space()='" + tab + "' and contains(@class, 'title title-level-1')]";
        String tabLocator;
        //System.out.println(tab);
        if(tab.equals("Claims") || tab.equals("Preauthorizations") || tab.equals("Dashboards")) {
            tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[2]";
        }else{
            tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[1]";
        }
//        try {
//            BrowserUtils.waitForClickablility(By.xpath(tabLocator), 5);
//            WebElement tabElement = Driver.get().findElement(By.xpath(tabLocator));
//            new Actions(Driver.get()).moveToElement(tabElement).pause(200).doubleClick(tabElement).build().perform();
//        } catch (Exception e) {
//            BrowserUtils.clickWithWait(By.xpath(tabLocator), 5);
//        }

//        (//*[contains(text(),'Data')])[1]

        try {
            BrowserUtils.waitForPresenceOfElement(By.xpath(tabLocator), 5);
            BrowserUtils.waitForVisibility(By.xpath(tabLocator), 5);
            //  BrowserUtils.scrollToElement(Driver.get().findElement(By.xpath(tabLocator)));
            Driver.get().findElement(By.xpath(tabLocator)).click();
            BrowserUtils.waitFor(3);
        } catch (Exception e) {
//            BrowserUtils.waitForStaleElement(Driver.get().findElement(By.xpath(moduleLocator)));
            BrowserUtils.clickWithTimeOut(Driver.get().findElement(By.xpath(tabLocator)),  5);
            BrowserUtils.waitFor(3);
        }
    }

public void navigateProMenu(String tab) {
    //  String tabLocator = "//span[normalize-space()='" + tab + "' and contains(@class, 'title title-level-1')]";
    String tabLocator;
    if(tab.equals("Claims") || tab.equals("Preauthorizations") || tab.equals("Dashboards")) {
        tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[2]";
    }else{
        tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[1]";
    }
//        try {
//            BrowserUtils.waitForClickablility(By.xpath(tabLocator), 5);
//            WebElement tabElement = Driver.get().findElement(By.xpath(tabLocator));
//            new Actions(Driver.get()).moveToElement(tabElement).pause(200).doubleClick(tabElement).build().perform();
//        } catch (Exception e) {
//            BrowserUtils.clickWithWait(By.xpath(tabLocator), 5);
//        }

//        (//*[contains(text(),'Data')])[1]

    try {
        BrowserUtils.waitForPresenceOfElement(By.xpath(tabLocator), 5);
        BrowserUtils.waitForVisibility(By.xpath(tabLocator), 5);
        //  BrowserUtils.scrollToElement(Driver.get().findElement(By.xpath(tabLocator)));
        Driver.get().findElement(By.xpath(tabLocator)).click();
        BrowserUtils.waitFor(3);
    } catch (Exception e) {
//            BrowserUtils.waitForStaleElement(Driver.get().findElement(By.xpath(moduleLocator)));
        BrowserUtils.clickWithTimeOut(Driver.get().findElement(By.xpath(tabLocator)),  5);
        BrowserUtils.waitFor(3);
    }
}



    public void navigateToModule(String tab, String module ) {
        //  String tabLocator = "//span[normalize-space()='" + tab + "' and contains(@class, 'title title-level-1')]";

        String tabLocator;
        if(tab.equals("Claims") || tab.equals("Preauthorizations") || tab.equals("Dashboards")) {
            tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[2]";
        } else if(tab.equals("Studio")) {
            tabLocator = "//div[(text()='" + tab + "')]";
        }else{
            tabLocator = "(//div[contains(@class,'titleHstack')] [(text()='" + tab + "')])[1]";
        }
        String moduleLocator = "//*[text()='"+ module +"']";


        try {
            BrowserUtils.waitForPresenceOfElement(By.xpath(tabLocator), 5);
            BrowserUtils.waitForVisibility(By.xpath(tabLocator), 5);
            //  BrowserUtils.scrollToElement(Driver.get().findElement(By.xpath(tabLocator)));
            Driver.get().findElement(By.xpath(tabLocator)).click();
            BrowserUtils.waitFor(3);
        } catch (Exception e) {
//            BrowserUtils.waitForStaleElement(Driver.get().findElement(By.xpath(moduleLocator)));
            BrowserUtils.clickWithTimeOut(Driver.get().findElement(By.xpath(tabLocator)),  5);
            BrowserUtils.waitFor(3);
        }


        try {
            BrowserUtils.waitForPresenceOfElement(By.xpath(moduleLocator), 5);
            BrowserUtils.waitForVisibility(By.xpath(moduleLocator), 5);
            //  BrowserUtils.scrollToElement(Driver.get().findElement(By.xpath(tabLocator)));
            WebElement moduleElement = Driver.get().findElement(By.xpath(moduleLocator));
//            new Actions(Driver.get()).moveToElement(moduleElement).pause(200).build().perform();
            JavascriptExecutor jse = (JavascriptExecutor) Driver.get();

            jse.executeScript("arguments[0].click();", moduleElement);
            //Driver.get().findElement(By.xpath(moduleLocator)).click();
            BrowserUtils.waitFor(3);
        } catch (Exception e) {
//            BrowserUtils.waitForStaleElement(Driver.get().findElement(By.xpath(moduleLocator)));
            BrowserUtils.clickWithTimeOut(Driver.get().findElement(By.xpath(moduleLocator)),  5);
            BrowserUtils.waitFor(3);
        }




    }

    public String getFieldInput(WebElement element){
        int inputLenght = element.getText().length();
        String inputData;
        if (inputLenght>0){
            inputData=element.getText();
        }else{
            inputData="empty";
        }
        return inputData;

    }

    ///// this method for JSON UPLOAD tests/////
    public String getFieldInput2(WebElement element){
        int inputLenght = element.getText().length();
        String inputData;
        if (inputLenght>0){
            inputData=element.getText();
        }else{
            inputData=null;
        }
        return inputData;
    }

    public String emptyNullConvert(String value){
        int inputLenght = value.length();
        String inputData;
        if (inputLenght>0){
            inputData=value;
        }else{
            inputData=null;
        }
        return inputData;
    }



    public String getFieldInputValue(WebElement element, String attribute){
        int inputLenght = element.getAttribute(attribute).length();
        String inputData;
        if (inputLenght>0){
            inputData=element.getAttribute(attribute);
        }else{
            inputData="empty";
        }
        return inputData;

    }

    ///// this method for JSON UPLOAD tests/////

    public String getFieldInputValue2(WebElement element, String attribute){
        int inputLenght = element.getAttribute(attribute).length();
        String inputData;
        if (inputLenght>0){
            inputData=element.getAttribute(attribute);
        }else{
            inputData=null;
        }
        return inputData;

    }
    public void fieldDelete(WebElement element){
        element.click();
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
    }
    public void waitUntilBlueBigDotsDisappear() {
        do {
            BrowserUtils.waitFor(3);
        }while (loaderDotsBig.size()>0);

    }


}
