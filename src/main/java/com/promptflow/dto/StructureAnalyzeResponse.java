package com.promptflow.dto;

import java.util.List;

public class StructureAnalyzeResponse {
    private int score;
    private String level;
    private List<StructureElement> elements;
    private List<String> suggestions;
    private String summary;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<StructureElement> getElements() {
        return elements;
    }

    public void setElements(List<StructureElement> elements) {
        this.elements = elements;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class StructureElement {
        private String name;
        private String description;
        private boolean present;
        private String content;
        private int weight;

        public StructureElement(String name, String description, boolean present, String content, int weight) {
            this.name = name;
            this.description = description;
            this.present = present;
            this.content = content;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPresent() {
            return present;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
