package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;

public class InstantConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return Instant.parse(s);
    }
}
