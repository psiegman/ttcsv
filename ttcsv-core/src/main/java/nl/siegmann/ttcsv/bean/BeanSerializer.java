package nl.siegmann.ttcsv.bean;

import java.util.List;
import java.util.function.Function;

public interface BeanSerializer<T> extends Function<T, List<String>> {

    List<String> getColumnHeaders();
}
