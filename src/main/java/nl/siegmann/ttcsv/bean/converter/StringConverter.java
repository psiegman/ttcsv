package nl.siegmann.ttcsv.bean.converter;

public class StringConverter implements Converter<String> {

    @Override
    public String apply(String s) {
        return s;
    }
}
