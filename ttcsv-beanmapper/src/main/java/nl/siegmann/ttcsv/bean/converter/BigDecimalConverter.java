package nl.siegmann.ttcsv.bean.converter;

import java.math.BigDecimal;

public class BigDecimalConverter extends AbstractConverter {

    @Override
    public Object applyNotBlank(String s) {
        return new BigDecimal(s);
    }
}
