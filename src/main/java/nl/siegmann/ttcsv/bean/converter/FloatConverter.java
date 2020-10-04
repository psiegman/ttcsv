package nl.siegmann.ttcsv.bean.converter;

public class FloatConverter extends AbstractConverter<Float> {

    @Override
    public Float applyNotBlank(String s) {
        return Float.parseFloat(s);
    }
}
