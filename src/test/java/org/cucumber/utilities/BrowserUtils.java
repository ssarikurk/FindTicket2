package org.cucumber.utilities;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BrowserUtils {

    /**
     * Switches to new window by the exact title. Returns to original window if target title not found
     * @param targetTitle
     */
    public static void switchToWindow(String targetTitle) {
        String origin = Driver.get().getWindowHandle();
        for (String handle : Driver.get().getWindowHandles()) {
            Driver.get().switchTo().window(handle);
            if (Driver.get().getTitle().equals(targetTitle)) {
                return;
            }
        }
        Driver.get().switchTo().window(origin);
    }

    /**
     * Moves the mouse to given element
     *
     * @param element on which to hover
     */
    public static void hover(WebElement element) {
        Actions actions = new Actions(Driver.get());
        actions.moveToElement(element).perform();
    }

    /**
     * Moves the mouse to given element and click
     *
     * @param element1 on which to hover and goes
     * @param element2 and click
     */
    public static void hoverAndClick(WebElement element1, WebElement element2) {
        Actions actions = new Actions(Driver.get());
        actions.moveToElement(element1).moveToElement(element2).click().perform();
    }

    /**
     * Moves the mouse to given element and click
     *
     * @param element1 on which to hover and click
     *
     */
    public static void hoverAndClick(WebElement element1) {
        Actions actions = new Actions(Driver.get());
        actions.moveToElement(element1).click().perform();
    }
    /**
     * return a list of string from a list of elements
     *
     * @param list of webelements
     * @return list of string
     */
    public static List<String> getElementsText(List<WebElement> list) {
        List<String> elemTexts = new ArrayList<>();
        for (WebElement el : list) {
            elemTexts.add(el.getText());
        }
        return elemTexts;
    }

    /**
     * return a list of string from a list of elements
     *
     * @param list of webelements
     * @return list of string
     */
    public static List<String> getElementsAttribute(List<WebElement> list,String attribute) {
        List<String> elemAttrib = new ArrayList<>();
        for (WebElement el : list) {
            elemAttrib.add(el.getAttribute(attribute));
        }
        return elemAttrib;
    }


    /**
     * Extracts text from list of elements matching the provided locator into new List<String>
     *
     * @param locator
     * @return list of strings
     */
    public static List<String> getElementsText(By locator) {

        List<WebElement> elems = Driver.get().findElements(locator);
        List<String> elemTexts = new ArrayList<>();

        for (WebElement el : elems) {
            elemTexts.add(el.getText());
        }
        return elemTexts;
    }

    /**
     * Performs a pause
     *
     * @param seconds
     */
    public static void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void waitFor(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for the provided element to be visible on the page
     *
     * @param element
     * @param timeToWaitInSec
     * @return
     */
    public static WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeToWaitInSec);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits for element matching the locator to be visible on the page
     *
     * @param locator
     * @param timeout
     * @return
     */
    public static WebElement waitForVisibility(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeout);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for provided element to be clickable
     *
     * @param element
     * @param timeout
     * @return
     */
    public static WebElement waitForClickablility(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeout);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits for element matching the locator to be clickable
     *
     * @param locator
     * @param timeout
     * @return
     */
    public static WebElement waitForClickablility(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeout);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * waits for backgrounds processes on the browser to complete
     *
     * @param timeOutInSeconds
     */
    public static void waitForPageToLoad(long timeOutInSeconds) {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        try {
            WebDriverWait wait = new WebDriverWait(Driver.get(), timeOutInSeconds);
            wait.until(expectation);
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    /**
     * Verifies whether the element matching the provided locator is displayed on page
     *
     * @param by
     * @throws AssertionError if the element matching the provided locator is not found or not displayed
     */
    public static void verifyElementDisplayed(By by) {
        try {
            Assert.assertTrue("Element not visible: " + by, Driver.get().findElement(by).isDisplayed());
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            Assert.fail("Element not found: " + by);

        }
    }

    /**
     * Verifies whether the element matching the provided locator is NOT displayed on page
     *
     * @param by
     * @throws AssertionError the element matching the provided locator is displayed
     */
    public static void verifyElementNotDisplayed(By by) {
        try {
            Assert.assertFalse("Element should not be visible: " + by, Driver.get().findElement(by).isDisplayed());
        } catch (NoSuchElementException e) {
            e.printStackTrace();

        }
    }


    /**
     * Verifies whether the element matching the provided locator is NOT displayed on page
     *
     * @param element
     * @throws AssertionError the element matching the provided locator is displayed
     */
    public static void verifyElementNotDisplayed(WebElement element) {
        try {
            Assert.assertFalse("Element should not be visible: " + element, element.isDisplayed());
        } catch (NoSuchElementException e) {
            e.printStackTrace();

        }
    }


    /**
     * Verifies whether the element is displayed on page
     *
     * @param element
     * @throws AssertionError if the element is not found or not displayed
     */
    public static void verifyElementDisplayed(WebElement element) {
        try {
            Assert.assertTrue("Element not visible: " + element, element.isDisplayed());
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            Assert.fail("Element not found: " + element);

        }
    }


    /**
     * Waits for element to be not stale
     *
     * @param element
     */
    public static void waitForStaleElement(WebElement element) {
        int y = 0;
        while (y <= 15) {
            if (y == 1)
                try {
                    element.isDisplayed();
                    break;
                } catch (StaleElementReferenceException st) {
                    y++;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (WebDriverException we) {
                    y++;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    /**
     * Clicks on an element using JavaScript
     *
     * @param element
     */
    public static void clickWithJS(WebElement element) {
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].click();", element);
    }


    /**
     * Scrolls down to an element using JavaScript
     *
     * @param element
     */
    public static void scrollToElement(WebElement element) {
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Performs double click action on an element
     *
     * @param element
     */
    public static void doubleClick(WebElement element) {
        new Actions(Driver.get()).doubleClick(element).build().perform();
    }

    /**
     * Changes the HTML attribute of a Web Element to the given value using JavaScript
     *
     * @param element
     * @param attributeName
     * @param attributeValue
     */
    public static void setAttribute(WebElement element, String attributeName, String attributeValue) {
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attributeName, attributeValue);
    }

    /**
     * Highlighs an element by changing its background and border color
     * @param element
     */
    public static void highlight(WebElement element) {
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
        waitFor(1);
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].removeAttribute('style', 'background: yellow; border: 2px solid red;');", element);
    }

    /**
     * Checks or unchecks given checkbox
     *
     * @param element
     * @param check
     */
    public static void selectCheckBox(WebElement element, boolean check) {
        if (check) {
            if (!element.isSelected()) {
                element.click();
            }
        } else {
            if (element.isSelected()) {
                element.click();
            }
        }
    }

    /**
     * attempts to click on provided element until given time runs out
     *
     * @param element
     * @param timeout
     */
    public static void clickWithTimeOut(WebElement element, int timeout) {
        for (int i = 0; i < timeout; i++) {
            try {
                element.click();
                return;
            } catch (WebDriverException e) {
                waitFor(1);
            }
        }
    }

    /**
     * executes the given JavaScript command on given web element
     *
     * @param element
     */
    public static void executeJScommand(WebElement element, String command) {
        JavascriptExecutor jse = (JavascriptExecutor) Driver.get();
        jse.executeScript(command, element);

    }

    /**
     * executes the given JavaScript command on given web element
     *
     * @param command
     */
    public static void executeJScommand(String command) {
        JavascriptExecutor jse = (JavascriptExecutor) Driver.get();
        jse.executeScript(command);

    }


    /**
     * This method will recover in case of exception after unsuccessful the click,
     * and will try to click on element again.
     *
     * @param by
     * @param attempts
     */
    public static void clickWithWait(By by, int attempts) {
        int counter = 0;
        //click on element as many as you specified in attempts parameter
        while (counter < attempts) {
            try {
                //selenium must look for element again
                clickWithJS(Driver.get().findElement(by));
                //if click is successful - then break
                break;
            } catch (WebDriverException e) {
                //if click failed
                //print exception
                //print attempt
                e.printStackTrace();
                ++counter;
                //wait for 1 second, and try to click again
                waitFor(1);
            }
        }
    }

    /**
     *  checks that an element is present on the DOM of a page. This does not
     *    * necessarily mean that the element is visible.
     * @param by
     * @param time
     */
    public static void waitForPresenceOfElement(By by, long time) {
        new WebDriverWait(Driver.get(), time).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static void waitUntilOpens(List<WebElement> element, int seconds){

        for (int i = 0; i < (seconds*2); i++) {
            if (element.size()==0){
                BrowserUtils.waitFor(0.1);
            }else{
                break;
            }
        }
        BrowserUtils.waitFor(0.7);
    }


    public static void DropFile(File filePath, WebElement target, int offsetX, int offsetY) {
        if(!filePath.exists())
            throw new WebDriverException("File not found: " + filePath.toString());

        WebDriver driver = ((RemoteWebElement)target).getWrappedDriver();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);


        String JS_DROP_FILES = "'var c=arguments,b=c[0],k=c[1];c=c[2];for(var d=b.ownerDocument||document,l=0;;){var e=b.getBoundingClientRect(),g=e.left+(k||e.width/2),h=e.top+(c||e.height/2),f=d.elementFromPoint(g,h);if(f&&b.contains(f))break;if(1<++l)throw b=Error('Element not interactable'),b.code=15,b;b.scrollIntoView({behavior:'instant',block:'center',inline:'center'})}var a=d.createElement('INPUT');a.setAttribute('type','file');a.setAttribute('multiple','');a.setAttribute('style','position:fixed;z-index:2147483647;left:0;top:0;');a.onchange=function(b){a.parentElement.removeChild(a);b.stopPropagation();var c={constructor:DataTransfer,effectAllowed:'all',dropEffect:'none',types:['Files'],files:a.files,setData:function(){},getData:function(){},clearData:function(){},setDragImage:function(){}};window.DataTransferItemList&&(c.items=Object.setPrototypeOf(Array.prototype.map.call(a.files,function(a){return{constructor:DataTransferItem,kind:'file',type:a.type,getAsFile:function(){return a},getAsString:function(b){var c=new FileReader;c.onload=function(a){b(a.target.result)};c.readAsText(a)}}}),{constructor:DataTransferItemList,add:function(){},clear:function(){},remove:function(){}}));['dragenter','dragover','drop'].forEach(function(a){var b=d.createEvent('DragEvent');b.initMouseEvent(a,!0,!0,d.defaultView,0,0,0,g,h,!1,!1,!1,!1,0,null);Object.setPrototypeOf(b,null);b.dataTransfer=c;Object.setPrototypeOf(b,DragEvent.prototype);f.dispatchEvent(b)})};d.documentElement.appendChild(a);a.getBoundingClientRect();return a;'"+

        "def drop_files(element, files, offsetX=0, offsetY=0):"+
        "driver = element.parent"+
        "isLocal = not driver._is_remote or '127.0.0.1' in driver.command_executor._url"+
        "paths = []"+


        "for file in (files if isinstance(files, list) else [files]) :"+
        "if not os.path.isfile(file) :"+
        "raise FileNotFoundError(file)"+
                "paths.append(file if isLocal else element._upload(file))"+

        "value = '\n'.join(paths)"+
        "elm_input = driver.execute_script(JS_DROP_FILES, element, offsetX, offsetY)"+
        "elm_input._execute('sendKeysToElement', {'value': [value], 'text': value})"+

        "WebElement.drop_files = drop_files";


//        String JS_DROP_FILE =
//                "var target = arguments[0]," +
//                        "    offsetX = arguments[1]," +
//                        "    offsetY = arguments[2]," +
//                        "    document = target.ownerDocument || document," +
//                        "    window = document.defaultView || window;" +
//                        "" +
//                        "var input = document.createElement('INPUT');" +
//                        "input.type = 'file';" +
//                        "input.style.display = 'none';" +
//                        "input.onchange = function () {" +
//                        "  var rect = target.getBoundingClientRect()," +
//                        "      x = rect.left + (offsetX || (rect.width >> 1))," +
//                        "      y = rect.top + (offsetY || (rect.height >> 1))," +
//                        "      dataTransfer = { files: this.files };" +
//                        "" +
//                        "  ['dragenter', 'dragover', 'drop'].forEach(function (name) {" +
//                        "    var evt = document.createEvent('MouseEvent');" +
//                        "    evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);" +
//                        "    evt.dataTransfer = dataTransfer;" +
//                        "    target.dispatchEvent(evt);" +
//                        "  });" +
//                        "" +
//                        "  setTimeout(function () { document.body.removeChild(input); }, 25);" +
//                        "};" +
//                        "document.body.appendChild(input);" +
//                        "return input;";

        WebElement input =  (WebElement)jse.executeScript(JS_DROP_FILES, target, offsetX, offsetY);
        input.sendKeys(filePath.getAbsoluteFile().toString());
        wait.until(ExpectedConditions.stalenessOf(input));
    }


    public static String convertDateFormat(String dateStr) throws ParseException {

        if (dateStr.contains("/")) {
            SimpleDateFormat dateParser = new SimpleDateFormat("M/dd/yy"); //----9/30/2017----
            Date date = dateParser.parse(dateStr);
//            System.out.println(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = (dateFormatter.format(date));
//            System.out.println("dateStr = " + dateStr);
//            System.out.println(dateFormatter.format(date));

        }else if (dateStr.contains("-")){
            SimpleDateFormat dateParser = new SimpleDateFormat("M-dd-yy"); //----9/30/2017----
            Date date = dateParser.parse(dateStr);
//            System.out.println(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = (dateFormatter.format(date));
//            System.out.println("dateStr = " + dateStr);
//            System.out.println(dateFormatter.format(date));

        }else if (dateStr.contains(" ")){
            SimpleDateFormat dateParser = new SimpleDateFormat("M dd yy"); //----9/30/2017----
            Date date = dateParser.parse(dateStr);
//            System.out.println(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = (dateFormatter.format(date));
//            System.out.println("dateStr = " + dateStr);
//            System.out.println(dateFormatter.format(date));
        }

        return dateStr;

    }

    /**
     *  checks that a switch element is NO
     * @param element
     *
     */

    public static void switchButtonNO(WebElement element){
        String currentSwitch = element.getText();
        //System.out.println("currentSwitch = " + currentSwitch);
        BrowserUtils.waitFor(1);
        if(currentSwitch.equals("Yes")){
            element.click();
            BrowserUtils.waitFor(1);
        }
        Assert.assertEquals("No",element.getText());

    }
    /**
     *  checks that a switch element is YES
     * @param element
     *
     */
    public static void switchButtonYES(WebElement element){
        String currentSwitch = element.getText();
        //System.out.println("currentSwitch = " + currentSwitch);
        BrowserUtils.waitFor(1);
        if(currentSwitch.equals("No")){
            element.click();
            BrowserUtils.waitFor(1);
        }
        Assert.assertEquals("Yes",element.getText());

    }
    /**
     *  waits to see the locator for 15 sec
     * @param locator as string
     *
     */
    public static void verifyTiming(String locator){
        List<WebElement> number;
        for (int i = 0; i <7; i++) {
            number=Driver.get().findElements(By.xpath(locator));
            int many=number.size();
            if(many>0){
                BrowserUtils.waitFor(1.7);
                break;
            }else{
                BrowserUtils.waitFor(3);
            }
        }

    }

    public static void takeScreenshot(String fileWithPath) {

        //Convert web driver object to TakeScreenshot
        TakesScreenshot scrShot =((TakesScreenshot)Driver.get());

        //Call getScreenshotAs method to create image file
        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination
        File DestFile=new File(fileWithPath);

        //Copy file at destination
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void takeScreenshot() {

        //Convert web driver object to TakeScreenshot
        TakesScreenshot scrShot =((TakesScreenshot)Driver.get());

        //Call getScreenshotAs method to create image file
        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination
        String projectPath = System.getProperty("user.dir");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss_S");
        String strDate= formatter.format(date);

        String filePath = "src/test/resources/Screenshots/sc_"+ strDate +".png";
        String fullPath = projectPath+"/"+filePath;

        File DestFile=new File(fullPath);

        //Copy file at destination
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String read837andDelete(String filePath) {
        File file = new File(filePath);
        String fileContent = "";
        try (FileReader fr = new FileReader(file))
        {
            char[] chars = new char[(int) file.length()];
            fr.read(chars);

            fileContent = new String(chars);
//            System.out.println(fileContent);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
        return fileContent;
    }

    public static String getDownloadPath() {

        String userName = System.getProperty("user.name");
//        System.out.println("userName = " + userName);
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String downloadFolder = "";

        if(systemType.contains("linux")&&userName.equals("jenkins")){
            downloadFolder = "/var/lib/jenkins/workspace/Regression/src/test/resources/Downloads";
        }else if(systemType.contains("windows")){
            downloadFolder = System.getProperty("user.home")+"/Downloads";
        }else if(systemType.contains("mac")){
            downloadFolder = System.getProperty("user.home") +"/Downloads";
        }else if(systemType.contains("linux")&&!userName.equals("jenkins")){
            downloadFolder = System.getProperty("user.home") +"/Downloads";
        }

        return downloadFolder;
    }

    public static String getDownloadPath(String fileName) {

        String userName = System.getProperty("user.name");
//        System.out.println("userName = " + userName);
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String downloadFolder = "";

        if(systemType.contains("linux")&&userName.equals("jenkins")){
            downloadFolder = "/var/lib/jenkins/workspace/Regression/src/test/resources/Downloads/"+fileName;
        }else if(systemType.contains("windows")){
            downloadFolder = System.getProperty("user.home")+"\\Downloads\\"+fileName;
        }else if(systemType.contains("mac")){
            downloadFolder = System.getProperty("user.home") +"/Downloads/"+fileName;
        }else if(systemType.contains("linux")&&!userName.equals("jenkins")){
            downloadFolder = System.getProperty("user.home") +"/Downloads/"+fileName;
        }

        return downloadFolder;
    }

    public static String getPath(String path) {

        String userName = System.getProperty("user.name");
//        System.out.println("userName = " + userName);
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String selectedPath = "";

        if(systemType.contains("linux")&&userName.equals("jenkins")){
            selectedPath = "/var/lib/jenkins/workspace/Regression/src/test/resources/Downloads/"+path;
        }else if(systemType.contains("windows")){
            selectedPath = System.getProperty("user.home")+"\\"+path;
        }else if(systemType.contains("mac")){
            selectedPath = System.getProperty("user.home") +"/"+path;
        }else if(systemType.contains("linux")&&!userName.equals("jenkins")){
            selectedPath = System.getProperty("user.home") +"/"+path;
        }
//        System.out.println("selectedPath = " + selectedPath);
        return selectedPath;

    }

    public static void dotSpinner() {

        int spinSize = 0;
        do {
            BrowserUtils.waitFor(0.5);
            List<WebElement> dotSpining = Driver.get().findElements(By.xpath("//span[@class='ant-spin-dot ant-spin-dot-spin']/i"));
            spinSize = dotSpining.size();
//            System.out.println("dotSpining.size() = " + dotSpining.size());
        }while (spinSize > 0);
//        System.out.println("Page is uploaded");
    }

    public static void waitLoader() {
        List<WebElement> loaderSpining = Driver.get().findElements(By.xpath("//div[@class='loader_loader__CQVg8']"));
        do {
            BrowserUtils.waitFor(1);
        } while (loaderSpining.size() > 0);
        BrowserUtils.waitFor(1);
    }


}








