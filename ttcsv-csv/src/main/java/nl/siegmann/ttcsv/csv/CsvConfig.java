package nl.siegmann.ttcsv.csv;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;

/**
 * Contains the configuration of the csv file, with things like field separator, row separator, etc.
 *
 * Defaults:
 * - field separator: ','
 * - row separator: '\n\r'
 */
@Getter
public class CsvConfig {

    public static final char DEFAULT_FIELD_SEPARATOR = ',';
    public static final String DEFAULT_ROW_SEPARATOR_CHARS = "\n\r";
    public static final boolean DEFAULT_QUOTE_VALUES = false;

    private char fieldSeparator = DEFAULT_FIELD_SEPARATOR;
    private String rowSeparatorChars = DEFAULT_ROW_SEPARATOR_CHARS;
    private boolean quoteValues = DEFAULT_QUOTE_VALUES;

    public boolean isRowSeparatorChar(int c) {
        return rowSeparatorChars.indexOf(c) >= 0;
    }

    public CsvConfig withFieldSeparator(char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
        return this;
    }

    public CsvConfig withRowSeparatorChars(String rowSeparatorChars) {
        this.rowSeparatorChars = rowSeparatorChars;
        return this;
    }

    public CsvConfig withQuoteValues(boolean quoteValues) {
        this.quoteValues = quoteValues;
        return this;
    }
}
