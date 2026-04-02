package com.example.foodorder.dto;

import java.util.List;

/**
 * Structured user intent extracted from a natural-language food request.
 * This is what the keyword-based "LLM simulator" produces.
 *
 * All fields are mutable so the parser can build it incrementally.
 */
public class ParsedFoodIntent {

    private Boolean      veg;                    // null = no preference
    private Double       maxBudget;              // null = no limit
    private String       spicePreference;        // MILD | MEDIUM | SPICY | EXTRA_SPICY | null
    private Integer      maxPrepTime;            // minutes, null = no limit
    private List<String> excludeIngredients;     // e.g. ["onion", "nuts"]
    private String       mealType;               // BREAKFAST | LUNCH | DINNER | SNACK | null
    private int          quantity      = 1;
    private boolean      ambiguous     = false;
    private boolean      contradictory = false;
    private String       clarificationNote;

    // ── Getters ──────────────────────────────────────────
    public Boolean      getVeg()                { return veg; }
    public Double       getMaxBudget()          { return maxBudget; }
    public String       getSpicePreference()    { return spicePreference; }
    public Integer      getMaxPrepTime()        { return maxPrepTime; }
    public List<String> getExcludeIngredients() { return excludeIngredients; }
    public String       getMealType()           { return mealType; }
    public int          getQuantity()           { return quantity; }
    public boolean      isAmbiguous()           { return ambiguous; }
    public boolean      isContradictory()       { return contradictory; }
    public String       getClarificationNote()  { return clarificationNote; }

    // ── Setters ──────────────────────────────────────────
    public void setVeg(Boolean veg)                           { this.veg = veg; }
    public void setMaxBudget(Double maxBudget)                { this.maxBudget = maxBudget; }
    public void setSpicePreference(String spicePreference)    { this.spicePreference = spicePreference; }
    public void setMaxPrepTime(Integer maxPrepTime)           { this.maxPrepTime = maxPrepTime; }
    public void setExcludeIngredients(List<String> list)      { this.excludeIngredients = list; }
    public void setMealType(String mealType)                  { this.mealType = mealType; }
    public void setQuantity(int quantity)                     { this.quantity = quantity; }
    public void setAmbiguous(boolean ambiguous)               { this.ambiguous = ambiguous; }
    public void setContradictory(boolean contradictory)       { this.contradictory = contradictory; }
    public void setClarificationNote(String clarificationNote){ this.clarificationNote = clarificationNote; }
}
