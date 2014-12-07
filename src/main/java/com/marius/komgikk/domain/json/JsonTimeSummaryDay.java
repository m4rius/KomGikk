package com.marius.komgikk.domain.json;

import java.util.ArrayList;
import java.util.List;

public class JsonTimeSummaryDay {
    public String day;
    public String hours;
    public List<JsonTimeSummaryActivity> activities = new ArrayList<>();
}
