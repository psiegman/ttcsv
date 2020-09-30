package nl.siegmann.ttcsv.bean.converter;

public class LongConverter implements Converter<Long> {

    @Override
    public Long apply(String s) {
        return Long.parseLong(s);
    }
}
