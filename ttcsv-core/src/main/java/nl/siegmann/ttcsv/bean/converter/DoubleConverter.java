package nl.siegmann.ttcsv.bean.converter;

public class DoubleConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Double.parseDouble(s);
    }
}
