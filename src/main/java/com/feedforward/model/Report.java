package com.feedforward.model;

import java.time.LocalDateTime;

public class Report {

    private int reportID;
    private String reportType;
    private String dateRange;
    private LocalDateTime generatedDate;
    private String format;
    private String content;

    public Report() {}

    public Report(String reportType, String dateRange, String format, String content) {
        this.reportType = reportType;
        this.dateRange = dateRange;
        this.format = format;
        this.content = content;
        this.generatedDate = LocalDateTime.now();
    }

    // Information Expert — Report knows how to generate and export itself
    public void aggregateData() {
        System.out.println("Aggregating data for report: " + reportType);
    }

    public void generateReportFile() {
        System.out.println("Generating report file for: " + reportType);
    }

    public void displayReport() {
        System.out.println(content);
    }

    public void exportToFormat(String format) {
        System.out.println("Exporting report as: " + format);
    }

    public int getReportID() { return reportID; }
    public String getReportType() { return reportType; }
    public String getDateRange() { return dateRange; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public String getFormat() { return format; }
    public String getContent() { return content; }

    public void setReportID(int reportID) { this.reportID = reportID; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    public void setFormat(String format) { this.format = format; }
    public void setContent(String content) { this.content = content; }
}