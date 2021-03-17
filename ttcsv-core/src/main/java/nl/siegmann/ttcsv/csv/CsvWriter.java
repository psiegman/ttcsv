package nl.siegmann.ttcsv.csv;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class CsvWriter {
    private final CsvConfig csvConfig;

    public CsvWriter() {
        this(new CsvConfig());
    }

    public CsvWriter(CsvConfig csvConfig) {
        this.csvConfig = csvConfig;
    }

    public String writeCsvToString(Stream<List<String>> data) throws IOException {
        return writeCsv(data, new StringWriter()).toString();
    }

    public Writer writeCsv(Stream<List<String>> data, Writer out) throws IOException {
        for (Iterator<List<String>> rowIter = data.iterator(); rowIter.hasNext(); ) {
            for (Iterator<String> valueIter = rowIter.next().iterator(); valueIter.hasNext(); ) {
                String value = valueIter.next();
                value = escapeQuotes(value);
                if (csvConfig.isQuoteValues()) {
                    out.write('"');
                    out.write(value);
                    out.write('"');
                } else {
                    out.write(value);
                }
                if (valueIter.hasNext()) {
                    out.write(csvConfig.getFieldSeparator());
                } else {
                    out.write(csvConfig.getRowSeparatorChars());
                }
            }
        }
        return out;
    }

    private String escapeQuotes(String value) {
        StringBuilder result = null;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '"') {
                if (result == null) {
                    result = new StringBuilder(value.substring(0, i));
                }
                result.append("\"\"");
            } else if (result != null) {
                result.append(value.charAt(i));
            }
        }
        if (result == null) {
            return value;
        } else {
            return result.toString();
        }
    }
}
