package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;
import java.util.Arrays;
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
        registerConverterForType(converterMap, new LongConverter(), Long.TYPE, Long.class);
        registerConverterForType(converterMap, new IntegerConverter(), Integer.TYPE, Integer.class);
        registerConverterForType(converterMap, new FloatConverter(), Float.TYPE, Float.class);
        registerConverterForType(converterMap, new DoubleConverter(), Double.TYPE, Double.class);
        registerConverterForType(converterMap, new DoubleConverter(), Double.TYPE, Double.class);
        registerConverterForType(converterMap, new StringConverter(), String.class);
        registerConverterForType(converterMap, new InstantConverter(), Instant.class);
        return converterMap;
    }

    public <T> Converter findConverterForType(Class<T> targetClass) {
        Converter converter = typeConverters.get(targetClass);
        if (converter == null && parentConverterRegistry != null) {
            converter = parentConverterRegistry.findConverterForType(targetClass);
        }
        return converter;
    }

    public <T> void registerConverterForType(Converter<T> converter, Class<T>... classes) {
        registerConverterForType(this.typeConverters, converter, classes);
    }

    public static <T> void registerConverterForType(Map<Class, Converter> typeConverters, Converter<T> converter, Class<T>... classes) {
        if (classes == null) {
            return;
        }
        Arrays.stream(classes).forEach(clazz -> typeConverters.put(clazz, converter));
    }
}
