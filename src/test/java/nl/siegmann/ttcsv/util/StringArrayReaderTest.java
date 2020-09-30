package nl.siegmann.ttcsv.util;

import nl.siegmann.ttcsv.util.IOUtil;
import nl.siegmann.ttcsv.util.StringArrayReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

public class StringArrayReaderTest {

    @Test
    public void test_simple() throws IOException {
        // given
        String[] data = new String[]{"a", "b"};
        String separator = "X";

        // when
        Reader reader = new StringArrayReader(data, separator);

        // then
        assertThat(IOUtil.toString(reader)).isEqualTo("aXb");
    }

    @Test
    public void test_simple_zero_separator() throws IOException {
        // given
        String[] data = new String[]{"a", "b"};
        String separator = "";

        // when
        Reader reader = new StringArrayReader(data, separator);

        // then
        assertThat(IOUtil.toString(reader)).isEqualTo("ab");
    }

    @Test
    public void test_small_csv() throws IOException {
        // given
        Reader input = new StringArrayReader(new String[]{
                "id|name|lastUpdate",
                "1|alice|2020-10-26T10:15:30.00Z"
        }, "\n");

        // when
        String actualResult = IOUtil.toString(input);

        assertThat(actualResult).isEqualTo("id|name|lastUpdate" + "\n" + "1|alice|2020-10-26T10:15:30.00Z");
    }
}
