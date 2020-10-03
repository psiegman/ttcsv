package nl.siegmann.ttcsv.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Creates a Reader from an Iterator of Strings inserting the given separator.
 *
 * Notes:
 * - The given separator will not be read after the last string in the iterator.
 * - Contains utility methods to create this reader from a Stream or an array of Strings.
 */
public class StringIteratorReader extends Reader {

    private static final int END_OF_STREAM_CHAR = -1;

    private final Iterator<String> segments;
    private final String separator;
    private String currentSegment;
    private int segmentPos = 0;

    public StringIteratorReader(String separator, Iterator<String> segments) {
        this.segments = segments == null ? Collections.emptyIterator() : segments;
        this.separator = separator == null ? "" : separator;
    }

    public static Reader of(String separator, Stream<String> data) {
        return new StringIteratorReader(separator, data.iterator());
    }

    public static Reader of(String separator, String... data) {
        return new StringIteratorReader(separator, Arrays.asList(data).iterator());
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (!checkAdvance()) {
            return END_OF_STREAM_CHAR;
        }

        int nrRead = 0;

        if (segmentPos < currentSegment.length()) {
            for (nrRead = 0; nrRead < Math.min(len, currentSegment.length() - segmentPos); nrRead++) {
                cbuf[nrRead + off] = currentSegment.charAt(nrRead + segmentPos);
            }
        }

        if (nrRead < len && segments.hasNext() && (nrRead + segmentPos < currentSegment.length() + separator.length())) {
            int nrOfSeparatorCharsToRead = Math.min((len - nrRead), (separator.length() + currentSegment.length()) - (segmentPos + nrRead));
            for (int i = 0; i < nrOfSeparatorCharsToRead; i++) {
                cbuf[i + nrRead + off] = separator.charAt(i);
            }
            nrRead += nrOfSeparatorCharsToRead;
        }

        if (endOfSegmentReached(nrRead, segmentPos)) {
            currentSegment = null;
            segmentPos = 0;
        } else {
            segmentPos += nrRead;
        }

        return nrRead;
    }

    /**
     * Whether we reached the end of the last segment or the end of a non-last segment + separator.
     */
    private boolean endOfSegmentReached(int nrRead, int segmentPos) {
        if (nrRead + segmentPos < currentSegment.length()) {
            return false;
        }
        if (!segments.hasNext()) {
            return true;
        }

        return nrRead + segmentPos >= (currentSegment.length() + separator.length());
    }

    private boolean checkAdvance() {
        if (currentSegment == null && segments.hasNext()) {
            currentSegment = segments.next();
            segmentPos = 0;
        }
        return currentSegment != null;
    }

    @Override
    public void close() throws IOException {

    }
}
