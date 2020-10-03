package nl.siegmann.ttcsv.bean.converter;

import java.util.function.Function;

public interface Converter<T> extends Function<String, T> {

}
