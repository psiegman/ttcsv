package nl.siegmann.ttcsv.bean;

import lombok.Data;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

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

    private final Iterator<List<String>> csvIterator;
    private final Function<List<String>,T> beanFactory;

    public CsvBeanIterator(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) throws Exception {
        this.csvIterator = csvIterator;
        this.beanFactory = createBeanFactory(csvBeanConfig, csvIterator);
    }

    private Function<List<String>,T> createBeanFactory(CsvBeanConfig<T> csvBeanConfig, Iterator<List<String>> csvIterator) throws Exception {
        if (!csvIterator.hasNext()) {
            throw new IllegalArgumentException("Unable to create BeanFactory for empty values iterator");
        }
        List<String> propertyNames = csvIterator.next();
        return csvBeanConfig.getBeanFactoryBuilder().createBeanFactory(csvBeanConfig.getTargetClass(), propertyNames);
    }

    @Override
    public boolean hasNext() {
        return csvIterator.hasNext();
    }

    @SneakyThrows
    @Override
    public T next() {
//        List<String> values = csvIterator.next();
        return beanFactory.apply(csvIterator.next());
//        row.incRowNumber();
//        row.setValues(values);
//        row.setTargetBean(csvBeanConfig.getTargetClassSupplier().get());
//
//        ProcessingState processingState = ProcessingState.CONTINUE;
//        if (csvBeanConfig.getRowPreProcessor() != null) {
//            processingState = csvBeanConfig.getRowPreProcessor().apply(this.row);
//        }
//
//        if (processingState == ProcessingState.SKIP) {
//            return csvBeanConfig.getSkipRowBean();
//        } else if (processingState == ProcessingState.STOP) {
//            csvIterator = Collections.emptyIterator();
//            return csvBeanConfig.getSkipRowBean();
//        }
//
//        for (int i = 0; i < values.size(); i++) {
//            row.setColumnIndex(i);
//            beanMapper.applyValue(row);
//        }
//
//        if (processingState == ProcessingState.LAST) {
//            csvIterator = Collections.emptyIterator();
//        }
//
//        return this.row.getTargetBean();
    }
}
