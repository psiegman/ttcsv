package nl.siegmann.ttcsv.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvContext {
    public static class Column {
        private int index;
        private String name;

        public Column(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    private List<Column> columns = new ArrayList<>();
    private int currentColumnIndex;
    private long currentRowIndex;
    private List<String> currentRow;

    public CsvContext(List<String> columnNames) {
        this.columns = IntStream
                .range(0, columnNames.size())
                .mapToObj(i -> new Column(i, columnNames.get(i)))
                .collect(Collectors.toList());
    }

    /**
     * Sets the current row to the given values, also sets the currentColumnIndex to 0 and advances the row counter
     *
     * @param values
     */
    public void advanceRow(List<String> values) {
        this.currentRow = values;
        currentColumnIndex = 0;
        currentRowIndex++;
    }

    public int advanceColumn() {
        return ++currentColumnIndex;
    }

    public Column getCurrentColumn() {
        return columns.get(currentColumnIndex);
    }

    public String getCurrentValue() {
        return currentRow.get(currentColumnIndex);
    }
}
