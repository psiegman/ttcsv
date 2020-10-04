package nl.siegmann.ttcsv.bean.converter;

import nl.siegmann.ttcsv.util.StringUtils;

public abstract class AbstractConverter implements Converter {

    private final Object defaultValue;

    public AbstractConverter() {
        this(null);
    }

    public AbstractConverter(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Object apply(String s) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        return applyNotBlank(s);
    }

    public abstract Object applyNotBlank(String s);
}
