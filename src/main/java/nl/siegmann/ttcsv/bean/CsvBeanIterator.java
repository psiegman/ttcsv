package nl.siegmann.ttcsv.bean;

import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvBeanIterator<T> implements Iterator<Stream<T>> {

    private final Iterator<List<String>> csvIterator;
    private final Function<List<String>, Stream<T>> beanFactory;

    public CsvBeanIterator(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) throws Exception {
        this.csvIterator = csvIterator;
        this.beanFactory = createBeanFactory(csvBeanConfig, csvIterator);
    }

    private Function<List<String>, Stream<T>> createBeanFactory(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) throws Exception {
        if (!csvIterator.hasNext()) {
            throw new IllegalArgumentException("Unable to create BeanFactory for empty values iterator");
        }
        List<String> propertyNames = csvIterator.next();
        return csvBeanConfig.getBeanFactory(propertyNames);
    }

    @Override
    public boolean hasNext() {
        return csvIterator.hasNext();
    }

    @SneakyThrows
    @Override
    public Stream<T> next() {
        return beanFactory.apply(csvIterator.next());
    }
}
