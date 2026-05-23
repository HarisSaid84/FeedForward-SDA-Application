package com.feedforward.model;

public interface IReportable {
    Report generateReport(String type, String range);
    void exportReport(String format);
}