package nl.siegmann.ttcsv.bean;

import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.util.List;
import java.util.function.Function;

public class BeanFactoryBuilder {

    private final ConverterRegistry converterRegistry;

    public BeanFactoryBuilder() {
        this(new ConverterRegistry());
    }

    public BeanFactoryBuilder(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <T> Function<List<String>, T> createBeanFactory(Class<T> targetBeanClass) throws Exception {
        return createBeanFactory(targetBeanClass, null);
    }

    public <T> Function<List<String>, T> createBeanFactory(Class<T> targetBeanClass, List<String> propertyNames) throws Exception {
        return new BeanFactory<>(targetBeanClass, propertyNames, converterRegistry);
    }
}
