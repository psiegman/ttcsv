package nl.siegmann.ttcsv.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static <T> Stream<T> toStream(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);
    }
}
