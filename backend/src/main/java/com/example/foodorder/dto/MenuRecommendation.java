package com.example.foodorder.dto;

import com.example.foodorder.model.MenuItem;

/**
 * A single recommendation produced by the rule engine.
 * Bundles the matched item, a human-readable reason, and a score.
 */
public class MenuRecommendation {

    private final MenuItem item;
    private final String   reason;
    private final int      matchScore; // 0-100

    public MenuRecommendation(MenuItem item, String reason, int matchScore) {
        this.item       = item;
        this.reason     = reason;
        this.matchScore = matchScore;
    }

    public MenuItem getItem()       { return item; }
    public String   getReason()     { return reason; }
    public int      getMatchScore() { return matchScore; }
}
