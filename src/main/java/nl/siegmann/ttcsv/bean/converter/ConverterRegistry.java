package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private final Map<Class, Converter> typeConverters;

    public ConverterRegistry() {
        typeConverters = createStandardConverters();
    }

    private static Map<Class, Converter> createStandardConverters() {
        Map<Class, Converter> converterMap = new HashMap<>();
        converterMap.put(Long.class, new AbstractConverter() {

            @Override
            public Object applyNotBlank(String s) {
                return Long.parseLong(s);
            }
        });
        converterMap.put(Long.TYPE, new LongConverter());
        converterMap.put(String.class, new StringConverter());
        converterMap.put(Instant.class, new InstantConverter());
        return converterMap;
    }

    public <T> Converter findConverterForType(Class<T> targetClass) {
        return typeConverters.get(targetClass);
    }
}
