package nl.siegmann.ttcsv.bean.converter;

public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    public Integer applyNotBlank(String s) {
        return Integer.parseInt(s);
    }
}
