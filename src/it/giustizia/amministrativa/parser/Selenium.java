package it.giustizia.amministrativa.parser;

import it.giustizia.amministrativa.parser.addons.FirefoxDriverAddon;
import it.giustizia.amministrativa.parser.constants.Constants;
import it.giustizia.amministrativa.parser.utils.TestParams;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.giustizia.amministrativa.parser.utils.LoggerUtil.i;

/**
 * Created by avsupport on 2/10/15.
 */
public class Selenium {

    private static FirefoxDriverAddon driver;
    private static WebDriver.Timeouts timeouts;
    private static TestParams testParams;

    public static void main(String[] args) {
        testParams = new TestParams(args);
        driver = new FirefoxDriverAddon();
        timeouts = driver.manage().timeouts();
        Constants.CURRENT_TYPE = testParams.type;

        if(testParams.type.equalsIgnoreCase(Constants.Type.TAR)) {
            if(testParams.provinces.size() == 0) {
                testParams.provinces.addAll(testParams.provincesHashMap.getArrayListOfAllKeys());
            }
            for(String provinceName : testParams.provinces) {
                i("PROVINCE: " + provinceName);
                start(provinceName);
            }
        } else {
            start(null);
        }

        driver.quit();
    }

    private static void start(String provinceName) {

        WebElement webElement = null;

        driver.get(provinceName == null ? Constants.Url.CS_MAIN_URL : Constants.Url.TAR_MAIN_URL);

        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        if(!driver.clickOnElementBy(By.xpath(provinceName != null ?  testParams.provincesHashMap.get(provinceName) : Constants.Xpath.CONSIGLIO_DI_STATTO), testParams.defaultTimeout)) return;

        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        if(provinceName == null) {
            if(!driver.clickOnElementBy(By.xpath(Constants.Xpath.ATTIVITA_GIURISDIZIONALE), testParams.defaultTimeout)) return;
            timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);
        }

        checkFolders(getFolderName(provinceName == null ? "cds" : provinceName));

        webElement = driver.findDynamicElement(By.xpath(Constants.Xpath.PROVVEDIMENTI), 1, testParams.defaultTimeout);

        if(webElement == null) return;
        webElement.click();

        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        if(!driver.inputText(By.xpath(Constants.Xpath.EDIT_TEXT_FROM), testParams.dateFrom, testParams.defaultTimeout)) return;
        if(!driver.inputText(By.xpath(Constants.Xpath.EDIT_TEXT_TO), testParams.dateTo, testParams.defaultTimeout)) return;

        if(!driver.clickOnElementBy(By.xpath(Constants.Xpath.BUTTON_CERCA), testParams.defaultTimeout)) return;
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        driver.sleep(10000);

        processedAllItems();
    }

    private static String getFolderName(String defaultName) {
        Pattern p = Pattern.compile("(attivita=(\\w{0,}))");
        Matcher m = p.matcher(driver.getCurrentUrl());
        String name = null;

        if(m.find()) {
            if (m.groupCount() == 2) {
                name = m.group(2);
            }
        }

        return name == null ? defaultName : name.replaceAll("_", "/");
    }

    private static void checkFolders(String name) {
        testParams.folder = new File("Reports-" + System.currentTimeMillis() + "/" + name);
        if(!testParams.folder.exists()) testParams.folder.mkdirs();
        testParams.metadata = new File(testParams.folder.getAbsolutePath() + "/" + testParams.metadataFileName);
        if(!testParams.metadata.exists()) try {
            testParams.metadata.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> clickedItems = new ArrayList<String>();

    private static void processedAllItems() {
        boolean isContinue = true;
        int currentLineIndex = 1;
        FileWriter fileWriter;

        while (isContinue) {
            driver.sleep(1000);
            WebElement table;
            table = driver.findDynamicElement(By.xpath(Constants.Xpath.T_BODY), 10);
            if(table == null || !table.isDisplayed()) return;

            List<WebElement> webElements = table.findElements(By.className(Constants.ClassName.ROW));
            i("Size:" + webElements.size());
            isContinue = false;
            for (WebElement child : webElements) {
                if (!child.isDisplayed()) {
                    i("table row is not disabled");
                    Coordinates coordinates = ((Locatable) webElements.get(webElements.size() - 1)).getCoordinates();
                    coordinates.inViewPort();
                    driver.sleep(2000);
                }

                List<WebElement> cells = child.findElements(By.className(Constants.ClassName.CELL));
                String line = "";
                String fileName = "";
                for (int i = 0; i < cells.size(); i++) {
                    WebElement currentCell = cells.get(i);
//                    i("i="+i+"; text:" + currentCell.getText());
                    if(Constants.CURRENT_TYPE.equals(Constants.Type.TAR) ||
                            Constants.CURRENT_TYPE.equals(Constants.Type.CDS)) {
                        if (i == 0 || i == 1 || i == 5) {
                            try {
                                fileName = fileName + (fileName.isEmpty() ? "" : "-") + currentCell.getText();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                    line = line + currentCell.getText() + "      ";
                }
                i("File name:" + fileName);
                if(line.equals("")) {
                    i("line is empty");
                    i("######### break ##############");
                    break;
                } else {
                    i("line: " + line);
                }
                if(!line.trim().equals("") && !clickedItems.contains(line)) {
                    i(currentLineIndex + ". " + line);
                    try {
                        fileWriter = new FileWriter(testParams.metadata, true);
                        fileWriter.append(line + " " + fileName + ".html\n");
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentLineIndex ++;
                    isContinue = true;
                    clickedItems.add(line);
                    if (child.isDisplayed()) {
                        child.click();
                        if(!openDetails(fileName + ".html")) {
                            try {
                                fileWriter = new FileWriter(testParams.folder.getAbsolutePath() + "/ERROR.html", true);
                                fileWriter.append(driver.getPageSource());
                                fileWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            processingDialog();
                        }
                        break;
                    } else {
                        isContinue = false;
                    }
                }
            }
        }
    }

    private static boolean processingDialog() {
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);
        try {
            WebElement webElement = driver.findElement(By.id("d1::msgDlg::cancel"));
            if (webElement != null && webElement.isDisplayed()) {
                i("Click on OK");
                webElement.click();
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        i("Cannot find dialog");
        return false;
    }

    private static boolean openDetails(String fileName) {
        if(!driver.clickOnElementBy(By.xpath(Constants.Xpath.BUTTON_VISUALIZZA), testParams.defaultTimeout)) return true;

        if(!driver.waitForNumberOfWindowsToEqual(2, driver, testParams.defaultTimeout)) {
            return false;
        }
        String winHandleBefore = driver.getWindowHandle();

        Set<String> windowsHelpers = driver.getWindowHandles();

        driver.switchTo().window(windowsHelpers.toArray()[1].toString());
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(testParams.folder.getAbsolutePath() + "/" + fileName, true);
            fileWriter.append(driver.getPageSource());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver.close();

        driver.switchTo().window(winHandleBefore);
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);
        return true;
    }
}

