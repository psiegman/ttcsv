package nl.siegmann.ttcsv.bean.converter;

public class FloatConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Float.parseFloat(s);
    }
}
