package nl.siegmann.ttcsv.bean;

import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.util.List;
import java.util.function.Supplier;

public class BeanFactoryBuilder {

    private final ConverterRegistry converterRegistry;

    public BeanFactoryBuilder() {
        this(new ConverterRegistry());
    }

    public BeanFactoryBuilder(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <T> BeanFactory<T> createBeanFactory(Supplier<T> beanSupplier) throws Exception {
        return createBeanFactory(beanSupplier, null);
    }

    public <T> BeanFactory<T> createBeanFactory(Supplier<T> beanSupplier, List<String> propertyNames) throws Exception {
        return new BeanFactory<T>(beanSupplier, propertyNames, new ConverterRegistry(converterRegistry));
    }
}
