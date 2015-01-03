package com.storemanager.models;

import java.util.List;
import java.util.Map;

public class ReportData {
    private String[] header;
    private List<String[]> data;
    private Map<String, String> footer;

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }

    public Map<String, String> getFooter() {
        return footer;
    }

    public void setFooter(Map<String, String> footer) {
        this.footer = footer;
    }
}
