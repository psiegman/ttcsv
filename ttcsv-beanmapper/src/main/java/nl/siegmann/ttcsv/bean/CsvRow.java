package nl.siegmann.ttcsv.bean;

import java.util.List;

public class CsvRow {

    private final List<CsvRowMapper.Column> columns;
    private final long rowIndex;
    private List<String> values;

    public CsvRow(List<CsvRowMapper.Column> columns, List<String> values, long rowIndex) {
        this.columns = columns;
        this.values = values;
        this.rowIndex = rowIndex;
    }

    public List<CsvRowMapper.Column> getColumns() {
        return columns;
    }

    public List<String> getValues() {
        return values;
    }
}
