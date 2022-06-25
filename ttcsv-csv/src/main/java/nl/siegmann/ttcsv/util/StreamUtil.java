package nl.siegmann.ttcsv.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static <T> Stream<T> toStream(Iterator<T> iterator) {
        return toStream(iterator, Spliterator.NONNULL, false);
    }

    /**
     *
     * @param iterator Source to read from
     * @param characteristics @see java.util.Spliterator
     * @param parallel
     * @param <T>
     * @return
     */
    public static <T> Stream<T> toStream(Iterator<T> iterator, int characteristics, boolean parallel) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
                iterator,
                characteristics);
        return StreamSupport.stream(spliterator, parallel);
    }
}
