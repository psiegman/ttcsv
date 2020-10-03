package nl.siegmann.ttcsv.test;

import java.io.IOException;
import java.io.Reader;

public class IOTestUtil {

    public static String toString(Reader reader, int readSegmentSize) throws IOException {
        char[] buffer = new char[8 * 1024];
        StringBuilder result = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(buffer, 0, Math.min(readSegmentSize, buffer.length))) >= 0) {
            result.append(buffer, 0, numCharsRead);
        }
        reader.close();
        return result.toString();
    }
}
