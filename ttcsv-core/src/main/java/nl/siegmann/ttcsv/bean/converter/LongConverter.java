package nl.siegmann.ttcsv.bean.converter;

public class LongConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Long.parseLong(s);
    }
}
