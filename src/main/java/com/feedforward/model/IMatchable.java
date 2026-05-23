package com.feedforward.model;

import java.util.List;

public interface IMatchable {
    void runMatchingAlgorithm();
    List<?> getRankedRecipients();
}