package nl.siegmann.ttcsv.bean.converter;

public class LongConverter extends AbstractConverter<Long> {

    @Override
    public Long applyNotBlank(String s) {
        return Long.parseLong(s);
    }
}
