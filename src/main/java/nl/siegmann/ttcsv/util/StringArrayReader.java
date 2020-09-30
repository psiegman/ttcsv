package nl.siegmann.ttcsv.util;

import java.io.IOException;
import java.io.Reader;

public class StringArrayReader extends Reader {

    private int segmentPos = 0;
    private int dataPos = 0;

    private final String[] data;
    private final String separator;

    public StringArrayReader(String[] data, String separator) {
        this.data = data;
        this.separator = separator;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (dataPos >= data.length) {
            return -1;
        }
        int nrRead = 0;
        String segment = data[dataPos];
        if (segmentPos < segment.length()) {
            for (int i = 0; i < Math.min(len, segment.length() - segmentPos); i++) {
                cbuf[i + off] = segment.charAt(i + segmentPos);
                nrRead++;
            }
        }
        if (nrRead < len
                && segmentPos >= segment.length()
                && nrRead + segmentPos < segment.length() + separator.length()) {
            for (int i = segmentPos - segment.length(); i < Math.min(len, separator.length()); i++) {
                cbuf[nrRead + off] = separator.charAt(i);
                nrRead++;
            }
        }
        if (nrRead + segmentPos >= segment.length() + separator.length()
                || dataPos >= data.length - 1 && nrRead >= data[dataPos].length()) {
            dataPos++;
            segmentPos = 0;
        } else {
            segmentPos = segmentPos + nrRead;
        }
        return nrRead;
    }

    @Override
    public void close() throws IOException {

    }
}
