package net.hydrogen2oxygen.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MateriaMedica {

    private String remedyName;
    private String remedyAlternativeNames;
    private Map<String, List<String>> categories = new LinkedHashMap<>();

    public String getRemedyName() {
        return remedyName;
    }

    public void setRemedyName(String remedyName) {
        this.remedyName = remedyName;
    }

    public String getRemedyAlternativeNames() {
        return remedyAlternativeNames;
    }

    public void setRemedyAlternativeNames(String remedyAlternativeNames) {
        this.remedyAlternativeNames = remedyAlternativeNames;
    }

    public Map<String, List<String>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, List<String>> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "MateriaMedica{" +
                "remedyName='" + remedyName + '\'' +
                ", remedyAlternativeNames='" + remedyAlternativeNames + '\'' +
                ", categories=" + categories +
                '}';
    }
}
