package com.promptflow.dto;

import java.util.List;

public class QualityCheckResponse {
    private int score;
    private String level;
    private List<CheckItem> items;
    private List<String> suggestions;
    private String summary;

    public static class CheckItem {
        private String name;
        private boolean passed;
        private String message;
        private int weight;

        public CheckItem(String name, boolean passed, String message, int weight) {
            this.name = name;
            this.passed = passed;
            this.message = message;
            this.weight = weight;
        }

        public String getName() { return name; }
        public boolean isPassed() { return passed; }
        public String getMessage() { return message; }
        public int getWeight() { return weight; }
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public List<CheckItem> getItems() { return items; }
    public void setItems(List<CheckItem> items) { this.items = items; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
