package nl.siegmann.ttcsv.bean.converter;

import java.time.Instant;

public class InstantConverter extends AbstractConverter<Instant> {

    @Override
    public Instant applyNotBlank(String s) {
        return Instant.parse(s);
    }
}
