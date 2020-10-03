package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private final ConverterRegistry parentConverterRegistry;

    private final Map<Class, Converter> typeConverters;

    public ConverterRegistry() {
        this(null);
    }

    public ConverterRegistry(ConverterRegistry parentConverterRegistry) {
        this.parentConverterRegistry = parentConverterRegistry;
        if (parentConverterRegistry == null) {
            typeConverters = createStandardConverters();
        } else {
            typeConverters = new HashMap<>();
        }
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
        Converter converter = typeConverters.get(targetClass);
        if (converter == null && parentConverterRegistry != null) {
            converter = parentConverterRegistry.findConverterForType(targetClass);
        }
        return converter;
    }

    public <T> void registerConverter(Class<T> clazz, Converter<T> converter) {
        this.typeConverters.put(clazz, converter);
    }
}
