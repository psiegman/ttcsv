package nl.siegmann.ttcsv.bean;

import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.bean.converter.ConverterRegistry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FieldMappers<T> {

    private ConverterRegistry converterRegistry;
    private Map<String, List<BeanPropertySetter>> beanPropertySetters = new HashMap<>();


    private static class BeanPropertySetter<T> implements BiConsumer<T, String> {

        private final Converter converter;
        private final BiConsumer<T, Object> beanSetter;

        private BeanPropertySetter(Converter converter, BiConsumer<T, Object> beanSetter) {
            this.converter = converter;
            this.beanSetter = beanSetter;
        }

        @Override
        public void accept(T t, String s) {
            Object value = converter.apply(s);
            beanSetter.accept(t, value);
        }
    }

    public  FieldMappers<T> add(String sourceField, BiConsumer<T, ?> fieldSetter, Class<?> targetPropertyClass) {
        Converter converter = converterRegistry.findConverterForType(targetPropertyClass);
        return add(sourceField, fieldSetter, converter);
    }

    public  FieldMappers<T> add(String sourceField, BiConsumer<T, ?> fieldSetter, Converter converter) {
        beanPropertySetters
                .computeIfAbsent(sourceField, _key -> new ArrayList<BeanPropertySetter>())
                .add((BeanPropertySetter) fieldSetter);
        return this;
    }
}
