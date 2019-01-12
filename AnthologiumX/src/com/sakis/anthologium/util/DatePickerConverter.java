package com.sakis.anthologium.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class DatePickerConverter extends StringConverter<LocalDate> {
    String pattern = "dd.MM.yyyy";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);


    public DatePickerConverter(String pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public String toString(LocalDate date) {
        if (date != null) {
            return dateFormatter.format(date);
        } else {
            return "";
        }
    }

    @Override
    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
            return LocalDate.parse(string, dateFormatter);
        } else {
            return null;
        }
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }
}
