package com.bjournal.bookjournal.model.enumerations;

public enum Genre {
    SCI_FI("Sci-Fi"), THRILLER("Thriller"), COMEDY("Comedy"), ROMANCE("Romance"),
    HORROR("Horror"), FANTASY("Fantasy"), MYSTERY("Mystery"), HISTORICAL_FICTION("Historical Fiction"),
    MEMOIR("Memoir"), SELF_HELP("Self-help"), BIOGRAPHY("Biography"), TRUE_CRIME("True-crime"),
    YOUNG_ADULT("Young-adult"), DYSTOPIAN("Dystopian"), ADVENTURE("Adventure"),
    ACTION("Action"), CONTEMPORARY("Contemporary"), GRAPHIC_NOVEL("Graphic-novel"),
    HISTORY("History"), TRAVEL("Travel"), RELIGION("Religion"), HUMANITIES("Humanities"),
    PARENTING("Parenting"), SCIENCE("Science");

    private final String displayValue;

    private Genre(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
