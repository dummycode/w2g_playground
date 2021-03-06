package edu.gatech.w2gplayground.Enums;

public enum Phrase {
    TEST("test"),
    SCAN("scan"),
    MANUAL_ENTRY("manual entry"),
    QUANTITY_OVERRIDE("quantity override"),
    SELECT("select"),
    HELP("help"),
    REFRESH("refresh");

    String phrase;

    Phrase(String phrase) {
        this.phrase = phrase;
    }

    public String getPhrase() {
        return this.phrase;
    }
}
