package nl.siegmann.ttcsv.bean.converter;

public class ShortConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Short.parseShort(s);
    }
}
