package nl.siegmann.ttcsv.bean.converter;

import nl.siegmann.ttcsv.util.StringUtils;

public abstract class AbstractConverter<T> implements Converter<T> {

    private final T defaultValue;

    public AbstractConverter() {
        this(null);
    }

    public AbstractConverter(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T apply(String s) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        return applyNotBlank(s);
    }

    public abstract T applyNotBlank(String s);
}
