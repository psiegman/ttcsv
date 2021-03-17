package nl.siegmann.ttcsv.bean.converter;

public class IntegerConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Integer.parseInt(s);
    }
}
