package nl.siegmann.ttcsv.util;

import java.io.IOException;
import java.io.Reader;

public class IOUtil {

    public static String toString(Reader reader) throws IOException {
        char[] buffer = new char[8 * 1024];
        StringBuilder result = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(buffer, 0, buffer.length)) >= 0) {
            result.append(buffer, 0, numCharsRead);
        }
        reader.close();
        return result.toString();
    }
}
