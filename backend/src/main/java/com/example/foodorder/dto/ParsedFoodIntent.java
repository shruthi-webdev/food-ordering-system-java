package com.example.foodorder.dto;

import java.util.List;

/**
 * Structured intent extracted by the LLM from a natural-language food request.
 * The LLM ONLY produces this JSON — it never recommends dishes.
 */
public class ParsedFoodIntent {
    private Boolean veg;                       // null = no preference
    private Double maxBudget;                  // null = no limit
    private String spicePreference;            // MILD, MEDIUM, SPICY, EXTRA_SPICY, or null
    private Integer maxPrepTime;               // minutes, null = no limit
    private List<String> excludeIngredients;   // e.g. ["onion", "nuts"]
    private String mealType;                   // BREAKFAST, LUNCH, DINNER, SNACK, or null
    private int quantity = 1;                  // how many items to recommend
    private boolean ambiguous = false;         // true if request is unclear
    private boolean contradictory = false;     // true if request has conflicts
    private String clarificationNote;          // explanation of conflict/ambiguity

    // Getters and Setters
    public Boolean getVeg() { return veg; }
    public void setVeg(Boolean veg) { this.veg = veg; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public String getSpicePreference() { return spicePreference; }
    public void setSpicePreference(String spicePreference) { this.spicePreference = spicePreference; }

    public Integer getMaxPrepTime() { return maxPrepTime; }
    public void setMaxPrepTime(Integer maxPrepTime) { this.maxPrepTime = maxPrepTime; }

    public List<String> getExcludeIngredients() { return excludeIngredients; }
    public void setExcludeIngredients(List<String> excludeIngredients) { this.excludeIngredients = excludeIngredients; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAmbiguous() { return ambiguous; }
    public void setAmbiguous(boolean ambiguous) { this.ambiguous = ambiguous; }

    public boolean isContradictory() { return contradictory; }
    public void setContradictory(boolean contradictory) { this.contradictory = contradictory; }

    public String getClarificationNote() { return clarificationNote; }
    public void setClarificationNote(String clarificationNote) { this.clarificationNote = clarificationNote; }
}
