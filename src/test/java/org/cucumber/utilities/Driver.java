package org.cucumber.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Driver {
    private Driver() {
    }
    // InheritableThreadLocal  --> this is like a container, bag, pool.
    // in this pool we can have separate objects for each thread
    // for each thread, in InheritableThreadLocal we can have separate object for that thread
    // driver class will provide separate webdriver object per thread
    private static InheritableThreadLocal<WebDriver> driverPool = new InheritableThreadLocal<>();
    public static WebDriver get() {
        //if this thread doesn't have driver - create it and add to pool
        if (driverPool.get() == null) {
//            if we pass the driver from terminal then use that one
//           if we do not pass the driver from terminal then use the one properties file
            ChromeOptions chromeOptions = new ChromeOptions();
            String browser = System.getProperty("browser") != null ? browser = System.getProperty("browser") : ConfigurationReader.get("browser");
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver());
                    break;
                case "chromeTesting":
                    String homeDir = System.getProperty("user.home");
//                    System.out.println("homeDir = " + homeDir);
                    ChromeOptions chromeOptionsTesting = new ChromeOptions();

                    String systemType = System.getProperty("os.name").toLowerCase();
                    if (systemType.contains("linux")){
                        chromeOptionsTesting.setBinary(homeDir+"/chrome/chrome"); // for linux******
                    } else if (systemType.contains("windows")) {
                        chromeOptionsTesting.setBinary(homeDir+ File.separator+"chrome"+File.separator+"chrome.exe"); //for windows******
                    } else if (systemType.contains("mac")) {
                        chromeOptionsTesting.setBinary(homeDir+ File.separator+"chrome"+File.separator+"chrome.app"); //for mac os******
                    }


                    chromeOptionsTesting.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--force-device-scale-factor=0.70", "--disable-infobars");

//                    System.setProperty("webdriver.chrome.driver", homeDir+"/.cache/selenium/chromedriver/linux64/131.0.6778.87/chromedriver");
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(chromeOptionsTesting));
                    break;
                case "chromeOld":
                    homeDir = System.getProperty("user.home");
                    ChromeOptions chromiumOptionsOld = new ChromeOptions();

                    chromiumOptionsOld.setBinary(homeDir + File.separator + "oldChrome" + File.separator + "chrome-win" + File.separator + "chrome.exe"); // for Windows
                    System.setProperty("webdriver.chrome.driver", homeDir + File.separator + "oldChrome" + File.separator + "chromedriver_win32" + File.separator + "chromedriver.exe");
                    chromiumOptionsOld.addArguments("--no-sandbox", "--disable-dev-shm-usage");
//                    WebDriverManager.chromiumdriver().setup();
                    driverPool.set(new ChromeDriver(chromiumOptionsOld));
                    break;
                case "chromium":
                    homeDir = System.getProperty("user.home");
                    ChromeOptions chromiumOptions = new ChromeOptions();

                    String systemType2 = System.getProperty("os.name").toLowerCase();
                    if (systemType2.contains("linux")){
                        chromiumOptions.setBinary(homeDir+"/chrome/chrome"); // for linux******
                    } else if (systemType2.contains("windows")) {
                        chromiumOptions.setBinary(homeDir+ File.separator+"chromium"+File.separator+"chrome.exe"); //for windows******
                    } else if (systemType2.contains("mac")) {
                        chromiumOptions.setBinary(homeDir+ File.separator+"chromium"+File.separator+"Chromium.app"); //for mac os******
                    }
//                    chromiumOptions.setBinary(homeDir+"/chromium/chromium");
                    chromiumOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");

//                    System.setProperty("webdriver.chrome.driver", homeDir+"/.cache/selenium/chromedriver/linux64/chromedriver-linux64/chromedriver");
                    WebDriverManager.chromiumdriver().setup();
//                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(chromiumOptions));
                    break;
                case "chrome2":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(new ChromeOptions().addArguments("ignore-certificate-errors","force-device-scale-factor=0.50", "high-dpi-support=0.50", "no-sandbox")));
              //      driverPool.set(new ChromeDriver(new ChromeOptions().addArguments("ignore-certificate-errors","force-device-scale-factor=0.50", "high-dpi-support=0.50")));
              //      driver = new ChromeDriver();
                    break;
                case "chrome3":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(new ChromeOptions().addArguments("force-device-scale-factor=0.75", "high-dpi-support=0.75")));
                    //driver = new ChromeDriver();
                    break;
                case "chrome-headless":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(new ChromeOptions().setHeadless(true)));
                    break;
                case "chrome-headless2":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(new ChromeOptions().addArguments("window-size=1920,1080","disable-gpu","start-maximized","no-sandbox","force-device-scale-factor=0.50", "high-dpi-support=0.50").setHeadless(true)));
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driverPool.set(new FirefoxDriver());
                    break;
                case "firefox-headless":
                    WebDriverManager.firefoxdriver().setup();
                    driverPool.set(new FirefoxDriver(new FirefoxOptions().setHeadless(true)));
                    break;
                case "ie":
                    if (!System.getProperty("os.name").toLowerCase().contains("windows"))
                        throw new WebDriverException("Your OS doesn't support Internet Explorer");
                    WebDriverManager.iedriver().setup();
                    driverPool.set(new InternetExplorerDriver());
                    break;
                case "edge":
                    if (!System.getProperty("os.name").toLowerCase().contains("windows"))
                        throw new WebDriverException("Your OS doesn't support Edge");
                    WebDriverManager.edgedriver().setup();
                    driverPool.set(new EdgeDriver());
                    break;
                case "safari":
                    if (!System.getProperty("os.name").toLowerCase().contains("mac"))
                        throw new WebDriverException("Your OS doesn't support Safari");
                    WebDriverManager.getInstance(SafariDriver.class).setup();
                    driverPool.set(new SafariDriver());
                    break;
                case "chromenohead":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.setHeadless(true);
                    options.addArguments(
                            "window-size=1920,1080",
                            "disable-gpu",
                            "start-maximized",
                            "no-sandbox"
//                            "force-device-scale-factor=0.50",
//                            "high-dpi-support=0.50"
                    );
                    driverPool.set(new ChromeDriver(options));
                    break;

                case "docker_chrome":
                    ChromeOptions chromeOptionsDocker = new ChromeOptions();
                    chromeOptionsDocker.setCapability("platform", Platform.ANY);
                    chromeOptionsDocker.addArguments(
                            "window-size=1920,1080",
                            "disable-gpu",
                            "start-maximized",
                            "no-sandbox",
                            "no-proxy-server",
                            "dns-prefetch-disable",
                            "disable-extensions",
//                            "disable-dev-shm-usage",
                            "force-device-scale-factor=0.50",
                            "high-dpi-support=0.50"
                    );

                    chromeOptionsDocker.setPageLoadStrategy(PageLoadStrategy.EAGER);
                    try {
                        driverPool.set(new RemoteWebDriver(new URL("http://localhost:4445"),chromeOptionsDocker)); //for seleniumGrid docker
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;

                case "remote_chrome":
                    chromeOptions.setCapability("platform", Platform.ANY);
                    chromeOptions.addArguments(
                            "window-size=1920,1080",
                            "disable-gpu",
                            "start-maximized",
                            "no-sandbox",
//                            "disable-dev-shm-usage",
                            "force-device-scale-factor=0.50",
                            "high-dpi-support=0.50"
                    );
                    try {
                        driverPool.set(new RemoteWebDriver(new URL("http://xx.xxx.xx.111:4445"),chromeOptions)); //for seleniumGrid docker
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;

                case "remote_chrome_dev":
                    chromeOptions.setCapability("platform", Platform.ANY);
                    chromeOptions.addArguments(
                            "window-size=1920,1080",
                            "disable-gpu",
                            "start-maximized",
                            "no-sandbox",
//                            "disable-dev-shm-usage",
                            "force-device-scale-factor=0.50",
                            "high-dpi-support=0.50"
                            );
                    try {
                        driverPool.set(new RemoteWebDriver(new URL("http://xx.xx.xxx.111:4445"),chromeOptions)); //for seleniumGrid docker
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

            }
        }
        return driverPool.get();
    }
    public static void closeDriver() {
        driverPool.get().quit();
        driverPool.remove();
    }
}