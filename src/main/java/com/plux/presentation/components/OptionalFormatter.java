package com.plux.presentation.components;

import javax.swing.*;
import java.text.ParseException;

public class OptionalFormatter extends JFormattedTextField.AbstractFormatter {
    private final JFormattedTextField.AbstractFormatter formatter;

    public OptionalFormatter(JFormattedTextField.AbstractFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (text == null || text.isEmpty()) return null;

        return formatter.stringToValue(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) return null;

        return formatter.valueToString(value);
    }

}
