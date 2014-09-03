package com.marius.komgikk.json;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GsonTest {

    @Test
    public void testFromJson() {
        String json = "{\"command\": \"SAVE\"}";

        Map map = new Gson().fromJson(json, Map.class);
        for (Object o : map.keySet()) {
            System.out.println(String.format("%s = %s", o, map.get(o)));
        }
    }

    @Test
    public void testToJson() {
        Map<String, String> strings = new HashMap<>();
        strings.put("Sønn", "Jonas");
        strings.put("Far", "Marius");

        String s = new Gson().toJson(strings);

        System.out.println(s);
    }
}
