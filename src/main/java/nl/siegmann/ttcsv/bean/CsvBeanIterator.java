package nl.siegmann.ttcsv.bean;

import lombok.Data;
import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.beanmapper.BeanMapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CsvBeanIterator<T> implements Iterator<T> {

    public enum ProcessingState {
        /**
         * Continue processing the data as usual.
         */
        CONTINUE,

        /**
         * Skip the current row
         */
        SKIP,

        /**
         * Stop processing after this row
         */
        LAST,

        /**
         * Stop processing
         */
        STOP
    }

    @Data
    public static class Row<T> {
        private int columnIndex;
        private List<String> values;
        private long rowNr;
        private T targetBean;

        public String getCurrentValue() {
            return values.get(columnIndex);
        }

        public void incRowNumber() {
            rowNr++;
        }
    }

    private final CsvBeanConfig<T> csvBeanConfig;
    private Iterator<List<String>> csvIterator;
    private final BeanMapper<T> beanMapper;
    private final Row<T> row = new Row<>();

    public CsvBeanIterator(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) {
        this.csvBeanConfig = csvBeanConfig;
        this.csvIterator = csvIterator;
        this.beanMapper = createBeanMapper(csvBeanConfig, csvIterator);
    }

    private BeanMapper<T> createBeanMapper(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) {
        List<String> columnNames = csvIterator.next();
        return new BeanMapper<T>(columnNames, csvBeanConfig.getTargetClass(), csvBeanConfig.getConverterRegistry());
    }

    @Override
    public boolean hasNext() {
        return csvIterator.hasNext();
    }

    @SneakyThrows
    @Override
    public T next() {
        List<String> values = csvIterator.next();

        row.incRowNumber();
        row.setValues(values);
        row.setTargetBean(csvBeanConfig.getTargetClassSupplier().get());

        ProcessingState processingState = ProcessingState.CONTINUE;
        if (csvBeanConfig.getRowPreProcessor() != null) {
            processingState = csvBeanConfig.getRowPreProcessor().apply(this.row);
        }

        if (processingState == ProcessingState.SKIP) {
            return csvBeanConfig.getSkipRowBean();
        } else if (processingState == ProcessingState.STOP) {
            csvIterator = Collections.emptyIterator();
            return csvBeanConfig.getSkipRowBean();
        }

        for (int i = 0; i < values.size(); i++) {
            row.setColumnIndex(i);
            beanMapper.applyValue(row);
        }

        if (processingState == ProcessingState.LAST) {
            csvIterator = Collections.emptyIterator();
        }

        return this.row.getTargetBean();
    }
}
