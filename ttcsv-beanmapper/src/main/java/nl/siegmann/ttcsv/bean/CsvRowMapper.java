package nl.siegmann.ttcsv.bean;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CsvRowMapper implements Function<List<String>, Stream<CsvRow>> {

    public static class Column {
        private int index;
        private String name;

        public Column(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    private List<Column> columns;
    private long rowIndex = 0;

    @Override
    public Stream<CsvRow> apply(List<String> values) {
        rowIndex++;
        if (columns == null) {
            this.columns = IntStream
                    .range(0, values.size())
                    .mapToObj(i -> new Column(i, values.get(i)))
                    .collect(Collectors.toList());
            return Stream.empty();

        }
        return Stream.of(new CsvRow(columns, values, rowIndex));
    }
}
