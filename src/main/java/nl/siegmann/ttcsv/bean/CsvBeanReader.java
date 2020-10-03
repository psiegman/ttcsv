package nl.siegmann.ttcsv.bean;

import lombok.SneakyThrows;
import nl.siegmann.ttcsv.csv.CsvIterator;
import nl.siegmann.ttcsv.util.StreamUtil;

import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvBeanReader<T> implements Function<Reader, Stream<T>> {

    private final CsvBeanConfig<T> csvBeanConfig;

    public CsvBeanReader(CsvBeanConfig<T> csvBeanConfig) {
        this.csvBeanConfig = csvBeanConfig;
    }

    @SneakyThrows
    @Override
    public Stream<T> apply(Reader reader) {
        CsvBeanIterator<T> csvBeanIterator = new CsvBeanIterator<>(csvBeanConfig, new CsvIterator(csvBeanConfig, reader));
        return StreamUtil.toStream(csvBeanIterator);
    }
}
