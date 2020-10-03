package nl.siegmann.ttcsv.csv;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CsvIterator implements Iterator<List<String>> {

    private static final List<String> NO_NEXT_ROW = new ArrayList<>(0);
    private static final int QUOTE_CHAR = '"';
    private int currentChar;


    private enum ReadState {
        INITIAL,
        END_OF_READER,
        IN_ROW_SEPARATOR
    }

    private final CsvConfig csvConfig;
    private final Reader reader;

    private ReadState readState = ReadState.INITIAL;
    private List<String> nextRow = null;
    private int previousRowLength = 0;

    public CsvIterator(CsvConfig csvConfig, Reader reader) {
        this.csvConfig = csvConfig;
        this.reader = reader;
    }

    @SneakyThrows
    @Override
    public boolean hasNext() {
        checkAdvanceRow();
        return readState != ReadState.END_OF_READER;
    }

    @SneakyThrows
    @Override
    public List<String> next() {
        checkAdvanceRow();
        if (readState == ReadState.END_OF_READER) {
            throw new NoSuchElementException("End of Reader reached, no next rows available");
        }
        List<String> row = nextRow;
        nextRow = null;
        return row;
    }

    private void checkAdvanceRow() throws IOException {
        if (readState != ReadState.END_OF_READER && nextRow == null) {
            this.nextRow = readNextRow(reader);
        }
    }

    private List<String> readNextRow(Reader reader) throws IOException {
        if (readState == ReadState.IN_ROW_SEPARATOR) {
            do {
                currentChar = reader.read();
            } while (csvConfig.isRowSeparatorChar(currentChar));
        } else {
            currentChar = reader.read();
        }

        if (currentChar < 0) {
            readState = ReadState.END_OF_READER;
            return NO_NEXT_ROW;
        }

        List<String> row = new ArrayList<>(previousRowLength);
        while (currentChar >= 0) {
            String value;
            if (QUOTE_CHAR == currentChar) {
                value = readQuotedValue();
            } else {
                value = readUnquotedValue();
            }
            row.add(value);
            if (currentChar == csvConfig.getFieldSeparator()) {
                currentChar = reader.read();
            } else if (csvConfig.isRowSeparatorChar(currentChar)) {
                readState = ReadState.IN_ROW_SEPARATOR;
                break;
            }
        }
        previousRowLength = row.size();
        return row;
    }

    String readUnquotedValue() throws IOException {
        StringBuilder value = new StringBuilder();
        while (currentChar >= 0
                && currentChar != csvConfig.getFieldSeparator()
                && !csvConfig.isRowSeparatorChar(currentChar)) {
            value.append((char) currentChar);
            currentChar = reader.read();
        }
        return value.toString();

    }

    String readQuotedValue() throws IOException {
        StringBuilder value = new StringBuilder();
        int previousChar = -1;
        currentChar = reader.read();
        while (currentChar >= 0) {
            if (previousChar == QUOTE_CHAR) {
                previousChar = -1;
                if (currentChar == QUOTE_CHAR) {
                    value.append((char) QUOTE_CHAR);
                } else {
                    break;
                }
            } else {
                if (previousChar >= 0) {
                    value.append((char) previousChar);
                }
                previousChar = currentChar;
            }
            currentChar = reader.read();
        }

        while (currentChar >= 0) {
            if (currentChar == csvConfig.getFieldSeparator()
                    || csvConfig.isRowSeparatorChar(currentChar)) {
                break;
            }
            currentChar = reader.read();
        }

        return value.toString();
    }

    boolean continueReadingQuotedValue(int previousChar, int currentChar) {
        if (currentChar < 0) {
            return false;
        }
        return currentChar != QUOTE_CHAR || previousChar < 0 || previousChar == QUOTE_CHAR;
    }
}
