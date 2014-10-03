package com.marius.komgikk.domain.json;

import java.util.ArrayList;
import java.util.List;

public class JsonTimeSummaryActivity {

    public String activityName;
    public String sap;
    public String normalizedFrom;
    public String normalizedTo;
    public String hours;
    public List<JsonTimeSummaryFromTo> actualTimes = new ArrayList<>();
}
