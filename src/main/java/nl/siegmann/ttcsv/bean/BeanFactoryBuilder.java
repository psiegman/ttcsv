package nl.siegmann.ttcsv.bean;

import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.util.List;

public class BeanFactoryBuilder {

    private final ConverterRegistry converterRegistry;

    public BeanFactoryBuilder() {
        this(new ConverterRegistry());
    }

    public BeanFactoryBuilder(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <T> BeanFactory<T> createBeanFactory(Class<T> targetBeanClass) throws Exception {
        return createBeanFactory(targetBeanClass, null);
    }

    public <T> BeanFactory<T> createBeanFactory(Class<T> targetBeanClass, List<String> propertyNames) throws Exception {
        return new BeanFactory<>(targetBeanClass, propertyNames, new ConverterRegistry(converterRegistry));
    }
}
