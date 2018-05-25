package fr.aquillet.kiwi.toolkit.number;

import java.text.DecimalFormat;

public class DoubleFormatter {

    private static final DecimalFormat TWO_DIGIT_FORMATTER = new DecimalFormat("#.##");

    private DoubleFormatter() {
        // utility class
    }

    public static String format2Digits(double value) {
        return TWO_DIGIT_FORMATTER.format(value);
    }


}
