package nl.siegmann.ttcsv.bean.converter;

public class StringConverter implements Converter {

    @Override
    public Object apply(String s) {
        return s;
    }
}
