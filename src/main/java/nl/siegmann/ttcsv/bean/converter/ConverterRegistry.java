package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private final ConverterRegistry parentConverterRegistry;

    private final Map<Class, Converter> convertersByType;

    public ConverterRegistry() {
        this(null);
    }

    public ConverterRegistry(ConverterRegistry parentConverterRegistry) {
        this.parentConverterRegistry = parentConverterRegistry;
        if (parentConverterRegistry == null) {
            convertersByType = createStandardConverters();
        } else {
            convertersByType = new HashMap<>();
        }
    }

    private static Map<Class, Converter> createStandardConverters() {
        Map<Class, Converter> converterMap = new HashMap<>();
        registerConverterForTypes(converterMap, new BooleanConverter(), Boolean.TYPE, Boolean.class);
        registerConverterForTypes(converterMap, new DoubleConverter(), Double.TYPE, Double.class);
        registerConverterForTypes(converterMap, new FloatConverter(), Float.TYPE, Float.class);
        registerConverterForTypes(converterMap, new InstantConverter(), Instant.class);
        registerConverterForTypes(converterMap, new IntegerConverter(), Integer.TYPE, Integer.class);
        registerConverterForTypes(converterMap, new LongConverter(), Long.TYPE, Long.class);
        registerConverterForTypes(converterMap, new ShortConverter(), Short.TYPE, Short.class);
        registerConverterForTypes(converterMap, new StringConverter(), String.class);
        return converterMap;
    }

    public Converter findConverterForType(Class targetClass) {
        Converter converter = convertersByType.get(targetClass);
        if (converter == null && parentConverterRegistry != null) {
            converter = parentConverterRegistry.findConverterForType(targetClass);
        }
        return converter;
    }

    @SafeVarargs
    public final <T> void registerConverterForTypes(Converter converter, Class... classes) {
        registerConverterForTypes(this.convertersByType, converter, classes);
    }

    @SafeVarargs
    public static <T> void registerConverterForTypes(Map<Class, Converter> typeConverters, Converter converter, Class... classes) {
        if (classes == null) {
            return;
        }
        Arrays.stream(classes).forEach(clazz -> typeConverters.put(clazz, converter));
    }
}
