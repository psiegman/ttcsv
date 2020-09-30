package nl.siegmann.ttcsv.test;

import java.io.IOException;
import java.io.StringReader;

/**
 * A reader that first reads from the given data, after which it throws the given IOException.
 * <p>
 * Meant for testing IOExceptions in unit tests.
 */
public class IOExceptionReader extends StringReader {

    private final IOException ioException;

    public IOExceptionReader(String data, IOException ioException) {
        super(data);
        this.ioException = ioException;
    }

    @Override
    public int read() throws IOException {
        int character = super.read();
        if (character < 0) {
            throw ioException;
        }
        return character;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int nrCharsRead = super.read(cbuf, off, len);
        if (nrCharsRead < 0) {
            throw ioException;
        }
        return nrCharsRead;
    }
}
