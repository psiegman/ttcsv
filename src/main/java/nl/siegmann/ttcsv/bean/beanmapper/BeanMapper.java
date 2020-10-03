package nl.siegmann.ttcsv.bean.beanmapper;

import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.CsvBeanIterator;
import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class BeanMapper<T> {

    private final List<ValueMapper<T>> valueMappers;

    public BeanMapper(List<String> columnHeaders, Class<T> targetClass, ConverterRegistry converterRegistry) {
        this.valueMappers = columnHeaders.stream().map(columnHeader -> createValueMapper(columnHeader, targetClass, converterRegistry)).collect(Collectors.toList());
    }

    public void applyValue(CsvBeanIterator.Row<T> row) throws Exception {
        valueMappers.get(row.getColumnIndex()).apply(row.getCurrentValue(), row.getTargetBean());
    }

    @SneakyThrows
    private ValueMapper<T> createValueMapper(String columnName, Class<T> targetClass, ConverterRegistry converterRegistry) {
        return new ValueMapper<>(targetClass, columnName, converterRegistry);
    }

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

}
