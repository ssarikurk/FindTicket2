package org.cucumber.pages;

import org.cucumber.utilities.BrowserUtils;
import org.cucumber.utilities.ConfigurationReader;
import org.cucumber.utilities.Driver;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class LoginPage extends BasePage{

    public LoginPage(){PageFactory.initElements(Driver.get(), this);}

    @FindBy(id="user-name")
    public  WebElement usernameSaucelab;
    @FindBy(id="password")
    public  WebElement passwordSaucelab;
    @FindBy(id="login-button")
    public  WebElement loginBtn;

    @FindBy(xpath = "//input[@type='email']")
    public WebElement username;

    @FindBy(css = ".ant-collapse-item")
    public List<WebElement> practiceSelectList;

    public void login(String userNameStr, String passwordStr) {

        usernameSaucelab.clear();
        usernameSaucelab.sendKeys(userNameStr);
        BrowserUtils.waitFor(0.5);
        passwordSaucelab.clear();
        passwordSaucelab.sendKeys(passwordStr);
        BrowserUtils.waitFor(0.5);
        loginBtn.click();

    }


}
