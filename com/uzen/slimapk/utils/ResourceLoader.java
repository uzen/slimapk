package com.uzen.slimapk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * methods for load resources.
 *
 * @author dongliu
 */
public class ResourceLoader {

    /**
     * load system attr ids for parse binary xml.
     */
    public static Map<Integer, String> loadSystemAttrIds() {
        try (BufferedReader reader = toReader("/values.ini")) {
            Map<Integer, String> map = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.trim().split("=");
                if (items.length != 2) {
                    continue;
                }
                String name = items[0].trim();
                Integer id = Integer.valueOf(items[1].trim());
                map.put(id, name);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader toReader(String path) {
        return new BufferedReader(new InputStreamReader(
                ResourceLoader.class.getResourceAsStream(path)));
    }
}
