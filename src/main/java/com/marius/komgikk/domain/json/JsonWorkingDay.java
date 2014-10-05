package com.marius.komgikk.domain.json;

import java.util.ArrayList;
import java.util.List;

public class JsonWorkingDay {

    public List<JsonTimeEvent> events = new ArrayList<>();
    public JsonDate prevDate;
    public JsonDate selectedDate;
    public JsonDate nextDate;
}
