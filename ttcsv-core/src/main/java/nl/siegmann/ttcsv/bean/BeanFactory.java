package nl.siegmann.ttcsv.bean;

import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactory<T> implements Function<List<String>, Stream<T>> {

    private final Supplier<T> beanSupplier;
    private final ConverterRegistry converterRegistry;
    private List<ValueMapper<T>> valueMappers;
    private List<String> columnNames;

    private static class ValueMapper<T> {
        private final Converter valueConverter;
        private final PropertyDescriptor propertyDescriptor;

        public ValueMapper(Class<T> targetClass, String columnName, ConverterRegistry converterRegistry) throws IntrospectionException {
            this.propertyDescriptor = new PropertyDescriptor(columnName, targetClass);
            this.valueConverter = converterRegistry.findConverterForType(propertyDescriptor.getPropertyType());
        }

        public void apply(String valueAsString, T targetBean) throws InvocationTargetException, IllegalAccessException {
            Object value = valueConverter.apply(valueAsString);
            propertyDescriptor.getWriteMethod().invoke(targetBean, value);
        }
    }

    private static class BeanInstanceSupplier<T> implements Supplier<T> {

        private final Constructor<T> beanConstructor;

        public BeanInstanceSupplier(Class<T> clazz) throws NoSuchMethodException {
            beanConstructor = clazz.getDeclaredConstructor();
        }

        @SneakyThrows
        @Override
        public T get() {
            return beanConstructor.newInstance();
        }
    }

    public BeanFactory(Supplier<T> beanSupplier, ConverterRegistry converterRegistry) throws Exception {
        this(beanSupplier, null, converterRegistry);
    }

    public BeanFactory(Supplier<T> beanSupplier, List<String> propertyNames, ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
        this.beanSupplier = beanSupplier;
        this.columnNames = propertyNames;
    }

    private static <T> List<ValueMapper<T>> createValueMappers(Class<T> targetClass, List<String> propertyNames, ConverterRegistry converterRegistry) {
        if (propertyNames == null) {
            return null;
        }
        return propertyNames.stream().map(columnHeader -> createValueMapper(columnHeader, targetClass, converterRegistry)).collect(Collectors.toList());
    }

    @SneakyThrows
    private static <T> ValueMapper<T> createValueMapper(String columnName, Class<T> targetClass, ConverterRegistry converterRegistry) {
        return new ValueMapper<>(targetClass, columnName, converterRegistry);
    }


    @SneakyThrows
    @Override
    public Stream<T> apply(List<String> values) {

        if (columnNames == null) {
            // check header row
            columnNames = values;
            return Stream.empty();
        }

        T bean = beanSupplier.get();
        if (valueMappers == null) {
            // first bean row
            this.valueMappers = createValueMappers((Class<T>) bean.getClass(), columnNames, converterRegistry);
        }

        for (int i = 0; i < values.size(); i++) {
            mapValue(values, i, bean);
        }
        return Stream.of(bean);
    }

    private void mapValue(List<String> values, int valueIndex, T targetBean) throws InvocationTargetException, IllegalAccessException {
        valueMappers.get(valueIndex).apply(values.get(valueIndex), targetBean);
    }

    /**
     * This BeanFactories ConverterRegistry.
     * Generally if the BeanFactory is created by the BeanFactoryBuilder its ConverterRegistry will have the BeanFactoryBuilder's
     * ConverterRegistry as its parent registry, allowing the BeanFactory to add/overwrite specific Converters without
     * changing the shared ConverterRegistry.
     */
    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }
}
