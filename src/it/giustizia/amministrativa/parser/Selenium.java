package it.giustizia.amministrativa.parser;

import it.giustizia.amministrativa.parser.addons.FirefoxDriverAddon;
import it.giustizia.amministrativa.parser.constants.Constants;
import it.giustizia.amministrativa.parser.utils.TestParams;
import org.openqa.selenium.*;
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
//        driver = new FirefoxDriverAddon(BrowserVersion.CHROME);
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

        checkFolders(provinceName = getFolderName(provinceName == null ? "cds" : provinceName));

        i("province id = " + provinceName);

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
        testParams.folder = testParams.isCreateNewResultsFolder ?
                new File("Reports-" + System.currentTimeMillis() + "/" + name) :
                new File("Reports" + "/" + name);
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

        String prevLine = "";
        int compareIndex = 0;

        WebElement selectedRow = null;
        WebElement scroller = null;

        String fileName = "";
        while (isContinue) {
            selectedRow = driver.findDynamicElement(By.className(Constants.ClassName.SELECTED_ROW), testParams.defaultTimeout);

            if(selectedRow == null) {
                driver.doDumpOfPage("Error_" + fileName + ".html");
                break;
            }

            List<WebElement> cells = selectedRow.findElements(By.className(Constants.ClassName.CELL));
            String line = fileName = "";
            for (int i = 0; i < cells.size(); i++) {
                WebElement currentCell = cells.get(i);
                if (i == 0 || i == 1 || i == 5) {
                    try {
                        fileName = fileName + (fileName.isEmpty() ? "" : "-") + currentCell.getText();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                line = line + currentCell.getText() + "\t";
            }

            if(line.equals(prevLine)) {
                compareIndex ++;
            } else {
                compareIndex = 0;
            }

            prevLine = line;

            if(compareIndex > 3) isContinue = false;

            if(!(new File(testParams.folder.getAbsolutePath() + "/" + fileName + ".html").exists())) {
                if(!line.trim().equals("") && !clickedItems.contains(line)) {
                    i("File name:" + fileName);
                    i(currentLineIndex + ". " + line);
                    driver.addDataToFile(testParams.metadata, line + " " + fileName + ".html\n");
                    currentLineIndex ++;
                    clickedItems.add(line);
                    if (!openDetails(fileName + ".html")) {
                        driver.doDumpOfPage(testParams.folder.getAbsolutePath() + "/ERROR-" + fileName + ".html");
                        processingDialog();
                    }
                }
            }
            selectedRow = driver.findDynamicElement(By.className(Constants.ClassName.SELECTED_ROW), testParams.defaultTimeout);
            if(selectedRow != null) selectedRow.click();

            scroller = driver.findDynamicElement(By.xpath(Constants.Xpath.SCROLLER), 10);

            if(scroller != null) scroller.sendKeys(Keys.DOWN);
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

        if(!driver.waitForNumberOfWindowsToEqual(2, driver, testParams.defaultTimeout * 2)) {
            return false;
        }
        String winHandleBefore = driver.getWindowHandle();

        Set<String> windowsHelpers = driver.getWindowHandles();

        driver.switchTo().window(windowsHelpers.toArray()[1].toString());
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);

        driver.doDumpOfPage(testParams.folder.getAbsolutePath() + "/" + fileName);

        driver.close();

        driver.switchTo().window(winHandleBefore);
        timeouts.pageLoadTimeout(testParams.defaultTimeout, TimeUnit.MILLISECONDS);
        return true;
    }
}

