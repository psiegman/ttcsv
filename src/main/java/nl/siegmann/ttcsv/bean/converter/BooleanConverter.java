package nl.siegmann.ttcsv.bean.converter;

public class BooleanConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Boolean.parseBoolean(s);
    }
}
