package nl.siegmann.ttcsv.bean;

import lombok.Getter;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class CsvBeanConfig<T> extends CsvConfig {

    private Class<T> targetClass;
    private final Supplier<T> targetClassSupplier;
    private final T skipRowBean = null;
    private ConverterRegistry converterRegistry;
    private final Function<CsvBeanIterator.Row<T>, CsvBeanIterator.ProcessingState> rowPreProcessor = null;

    public CsvBeanConfig(Supplier<T> targetClassSupplier) {
        this.targetClassSupplier = targetClassSupplier;
    }

    public Class<T> getTargetClass() {
        if (this.targetClass == null) {
            this.targetClass = (Class<T>) targetClassSupplier.get().getClass();
        }
        return targetClass;
    }

    public ConverterRegistry getConverterRegistry() {
        if (this.converterRegistry == null) {
            this.converterRegistry = new ConverterRegistry();
        }
        return converterRegistry;
    }

    public CsvBeanConfig<T> withConverterRegistry(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
        return this;
    }

    public CsvBeanConfig<T> withFieldSeparator(char fieldSeparator) {
        super.withFieldSeparator(fieldSeparator);
        return this;
    }

    public CsvBeanConfig<T> withRowSeparatorChars(String rowSeparatorChars) {
        super.withRowSeparatorChars(rowSeparatorChars);
        return this;
    }

    public CsvBeanConfig<T> withQuoteValues(boolean quoteValues) {
        super.withQuoteValues(quoteValues);
        return this;
    }

    public CsvBeanConfig<T> withTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
        return this;
    }
}
