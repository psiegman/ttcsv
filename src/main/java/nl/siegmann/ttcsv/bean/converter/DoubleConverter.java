package nl.siegmann.ttcsv.bean.converter;

public class DoubleConverter extends AbstractConverter<Double> {

    @Override
    public Double applyNotBlank(String s) {
        return Double.parseDouble(s);
    }
}
