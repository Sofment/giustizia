package it.giustizia.amministrativa.parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by avsupport on 2/11/15.
 */
public class ProvincesHashMap extends HashMap<String, String> {
    public String put(String province, String xpath) {
        return super.put(province, xpath);
    }

    public String get(String key) {
        return super.get(key);
    }

    public String getAllKeys() {
        String keys = "";
        for (String key : super.keySet()) {
            keys = keys + key + "\n";
        }
        return keys;
    }

    public ArrayList<String> getArrayListOfAllKeys() {
        ArrayList<String> keys = new ArrayList<String>();
        for (String key : super.keySet()) {
            keys.add(key);
        }
        return keys;
    }
}
