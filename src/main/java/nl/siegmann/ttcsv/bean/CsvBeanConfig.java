package nl.siegmann.ttcsv.bean;

import lombok.Getter;
import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;
import nl.siegmann.ttcsv.csv.CsvConfig;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Getter
public class CsvBeanConfig<T> extends CsvConfig {

    private Class<T> targetClass;
    private final Supplier<T> targetClassSupplier;
    private final T skipRowBean = null;
    private ConverterRegistry converterRegistry;
    private BeanFactoryBuilder beanFactoryBuilder;
    private Function<List<String>, Stream<T>> beanFactory;

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

    public CsvBeanConfig<T> withBeanFactory(Function<List<String>, Stream<T>> beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }

    public CsvBeanConfig<T> withBeanFactoryBuilder(BeanFactoryBuilder beanFactoryBuilder) {
        this.beanFactoryBuilder = beanFactoryBuilder;
        return this;
    }

    public Function<List<String>, Stream<T>> getBeanFactory(List<String> propertyNames) throws Exception {
        if (beanFactory == null) {
            if (beanFactoryBuilder == null) {
                this.beanFactoryBuilder = new BeanFactoryBuilder();
            }
            this.beanFactory = beanFactoryBuilder.createBeanFactory(getTargetClass(), propertyNames);
        }
        return beanFactory;
    }
}
