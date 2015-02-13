package it.giustizia.amministrativa.parser.utils;

import it.giustizia.amministrativa.parser.ProvincesHashMap;

import java.io.File;
import java.util.ArrayList;

import static it.giustizia.amministrativa.parser.utils.LoggerUtil.i;

/**
 * Created by avsupport on 2/11/15.
 */
public class TestParams {

    public long defaultTimeout = 90000;
    public String metadataFileName = "metadata.txt";
    public File metadata = null;
    public File folder = null;
    public ArrayList<String> listOfArgs = new ArrayList<String>();
    public ArrayList<String> provinces = new ArrayList<String>();
    public String type = "tar";
    public String dateFrom = "12/01/2014";
    public String dateTo = "2/9/2015";
    public boolean isCreateNewResultsFolder = false;
    public ProvincesHashMap provincesHashMap = new ProvincesHashMap();

    public TestParams(String... args) {
        setUpParameters();
        validateArgs(args);
        if(!type.equalsIgnoreCase("tar") && !type.equalsIgnoreCase("cds")) {
                i("Please make sure you type valid valuer.\n" +
                        "value of \"type\" key should be \"tar\" or \"cds\"\n" +
                        "-e key tar\n" +
                        "or\n" +
                        "-e key cds\n" +
                        "also you can skip this param.\n" +
                        "For more details use java -jar parser.jar help \n");
                System.exit(0);
        }
    }
    
    private void setUpParameters() {

        listOfArgs.add("type");
        listOfArgs.add("timeout");
        listOfArgs.add("province");
        listOfArgs.add("isCreateNewResultsFolder");
        listOfArgs.add("dateFrom");
        listOfArgs.add("dateTo");

        provincesHashMap.put("L'Aquila", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[1]/td/div[2]/a[1]");
        provincesHashMap.put("Pescara", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[1]/td/div[2]/a[2]");
        provincesHashMap.put("Potenza", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[2]/td/div[2]/a");
        provincesHashMap.put("Catanzaro", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[3]/td/div[2]/a[1]");
        provincesHashMap.put("Reggio Calabria", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[3]/td/div[2]/a[2]");
        provincesHashMap.put("Napoli", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[4]/td/div[2]/a[1]");
        provincesHashMap.put("Salerno", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[4]/td/div[2]/a[2]");
        provincesHashMap.put("Bologna", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[5]/td/div[2]/a[1]");
        provincesHashMap.put("Parma", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[5]/td/div[2]/a[2]");
        provincesHashMap.put("Trieste", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[6]/td/div[2]/a");
        provincesHashMap.put("Roma", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[7]/td/div[2]/a[1]");
        provincesHashMap.put("Latina", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[7]/td/div[2]/a[2]");
        provincesHashMap.put("Genova", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[8]/td/div[2]/a");
        provincesHashMap.put("Milano", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[9]/td/div[2]/a[1]");
        provincesHashMap.put("Brescia", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[9]/td/div[2]/a[2]");
        provincesHashMap.put("Ancona", "//*[@id=\"content\"]/div[2]/div[3]/table/tbody/tr[10]/td/div[2]/a");

        provincesHashMap.put("Campobasso", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[1]/td/div[2]/a");
        provincesHashMap.put("Torino", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[2]/td/div[2]/a");
        provincesHashMap.put("Bari", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[3]/td/div[2]/a[1]");
        provincesHashMap.put("Lecce", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[3]/td/div[2]/a[2]");
        provincesHashMap.put("Cagliari", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[4]/td/div[2]/a");
        provincesHashMap.put("Palermo", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[5]/td/div[2]/a[1]");
        provincesHashMap.put("Catania", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[5]/td/div[2]/a[2]");
        provincesHashMap.put("Firenze", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[6]/td/div[2]/a");
        provincesHashMap.put("Trento", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[7]/td/div[2]/a[1]");
        provincesHashMap.put("Bolzano", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[7]/td/div[2]/a[2]");
        provincesHashMap.put("Perugia", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[8]/td/div[2]/a");
        provincesHashMap.put("Aosta", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[9]/td/div[2]/a");
        provincesHashMap.put("Venezia", "//*[@id=\"content\"]/div[2]/div[5]/table/tbody/tr[10]/td/div[2]/a");
    }

    private void validateArgs(String... args) {

        boolean isValidFormat = true;
        boolean validArgumentsCount = args.length % 3 == 0;
        String arg = "";
        if(validArgumentsCount) {
            for (int currentArgIndex = 0; currentArgIndex < args.length; currentArgIndex++) {
                if (!args[currentArgIndex].trim().equalsIgnoreCase("-e")) {
                    isValidFormat = false;
                    break;
                }
                currentArgIndex++;
                if(!listOfArgs.contains(arg = args[currentArgIndex].trim())) {
                    isValidFormat = false;
                    break;
                }
                currentArgIndex++;
                setValue(arg, args[currentArgIndex].trim());
            }
        }

        if(args.length > 0 && args[0].equalsIgnoreCase("help")) {
            i("\n" +
                    "Usage: \n" +
                    "(optional) -e dateFrom stringValue, Default value is \"12/01/2014\"\n" +
                    "(optional) -e dateTo stringValue, Default value is \"2/9/2015\"\n" +
                    "(optional) -e type stringValue, value of \"type\" key should be \"tar\" or \"cds\". Default value is \"tar\"\n" +
                    "(optional) -e timeout longValue, Default value is \"90000\" ms\n" +
                    "(optional) -e province stringValue, you can call that key many times\n" +
                    "(optional) -e isCreateNewResultsFolder booleanValue - If you need to crete new results folder for each test launching then put true. Default params is false" +
                    "Default value is true.\n\n" +
                    "Available provinces:\n" +
                    provincesHashMap.getAllKeys());
            System.exit(0);
        }

        if(!validArgumentsCount || !isValidFormat) {
            i("Please make sure you type valid arguments.\n" +
                    "For more details use java -jar parser.jar help \n");
            System.exit(0);
        }
    }

    private void setValue(String arg, String value) {
        if(arg.equalsIgnoreCase("dateFrom")) {
            dateFrom = value;
        } else if(arg.equalsIgnoreCase("dateTo")) {
            dateTo = value;
        } else if(arg.equalsIgnoreCase("type")) {
            type = value;
        } else if(arg.equalsIgnoreCase("province")) {
            provinces.add(value);
        } else if(arg.equalsIgnoreCase("timeout")) {
            try {
                defaultTimeout = Long.parseLong(value);
            } catch (Exception ex) {
                i(" arg \"timeout\" could be long. Default value ia 30000 ms.");
                System.exit(0);
            }
        } else if(arg.equalsIgnoreCase("isCreateNewResultsFolder")) {
            try {
                isCreateNewResultsFolder = Boolean.parseBoolean(value);
            } catch (Exception ex) {
                i(" arg \"isCreateNewResultsFolder\" could be boolean. \nIf you need to crete new results folder for each test launching then put true. \nDefault value is false");
                System.exit(0);
            }
        }
    }
}
