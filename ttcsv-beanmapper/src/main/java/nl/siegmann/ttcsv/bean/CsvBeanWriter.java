package nl.siegmann.ttcsv.bean;

import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

public class CsvBeanWriter<T> {

    private final BeanSerializer beanSerializer;
    private CsvWriter csvWriter = new CsvWriter();

    public CsvBeanWriter(BeanSerializer beanSerializer) {
        this.beanSerializer = beanSerializer;
    }

    public CsvBeanWriter withCsvConfig(CsvConfig csvConfig) {
        csvWriter = new CsvWriter(csvConfig);
        return this;
    }

    public String writeCsvToString(Stream<T> beans) throws IOException {
        return writeCsv(beans, new StringWriter()).toString();
    }

    public Writer writeCsv(Stream<T> beans, Writer out) throws IOException {
        csvWriter.writeCsv(
                Stream.concat(
                        Stream.of(beanSerializer.getColumnHeaders()),
                        beans.map(beanSerializer)),
                out);
        return out;
    }
}
