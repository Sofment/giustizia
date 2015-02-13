package it.giustizia.amministrativa.parser.addons;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by avsupport on 2/11/15.
 */
public class FirefoxDriverAddon extends FirefoxDriver {

//    public FirefoxDriverAddon (boolean isEnableJs) {
//        super(isEnableJs);
//    }
//
//    public FirefoxDriverAddon (BrowserVersion browserVersion) {
//        super(browserVersion);
//    }


    public boolean isElementPresent(By by)
    {
        boolean present;
        try
        {
            findElement(by);
            present = true;
        } catch (NoSuchElementException e) {
            present = false;
        } catch (UnhandledAlertException e) {
            present = false;
        }
        return present;
    }

    public WebElement findDynamicElement(By by, long timeOutMs) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeOutMs) return null;
            if(isElementPresent(by)) {
                return findElement(by);
            }
        }
    }

    public WebElement findDynamicElement(By by, int instance, long timeOut) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeOut) return null;
            if(isElementPresent(by)) {
                List<WebElement> webElements = findElements(by);
                if(webElements.size() > instance) {
                    return webElements.get(instance);
                }
                return null;
            }
        }
    }

    public boolean waitForNumberOfWindowsToEqual(final int numberOfWindows, WebDriver driver, long timeoutMs) {
        try {
            new WebDriverWait(driver, (timeoutMs / 1000)) {
            }.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return (driver.getWindowHandles().size() == numberOfWindows);
                }
            });
        } catch (TimeoutException ex) {
            return false;
        }
        return true;
    }

    public boolean clickOnElementBy(By by, long timeoutMs) {
        try {
            WebElement webElement = findDynamicElement(by, timeoutMs);
            if (webElement != null && webElement.isDisplayed()) {
                webElement.click();
                return true;
            }
        } catch (TimeoutException timeoutException) {
            return false;
        }
        return false;
    }

    public boolean inputText(By by, String text, long timeoutMs) {
        WebElement webElement = findDynamicElement(by, timeoutMs);
        if(webElement != null) {
            webElement.sendKeys(text);
            return true;
        }
        return false;
    }

    public void sleep(long timeoutMs) {
        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
