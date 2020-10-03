package nl.siegmann.ttcsv.util;

import nl.siegmann.ttcsv.test.IOTestUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StringIteratorReaderTest {

    @Test
    public void test_empty_empty_empty() throws IOException {
        // given
        Iterator<String> data = Stream.of("", "").iterator();
        String separator = "";
        Reader reader = new StringIteratorReader(separator, data);

        // when
        String actualResult = IOUtil.toString(reader);

        // then
        assertThat(actualResult).isEqualTo("");
    }

    @Test
    public void test_simple() throws IOException {
        // given
        Iterator<String> data = Stream.of("a", "b").iterator();
        String separator = "X";
        Reader reader = new StringIteratorReader(separator, data);

        // when
        String actualResult = IOUtil.toString(reader);

        // then
        assertThat(actualResult).isEqualTo("aXb");
    }

    @Test
    public void test_simple_one_by_one() throws IOException {
        // given
        Iterator<String> data = Stream.of("a", "b").iterator();
        String separator = "X";
        Reader reader = new StringIteratorReader(separator, data);

        // when
        int char1 = reader.read();
        int char2 = reader.read();
        int char3 = reader.read();
        int char4 = reader.read();
        int char5 = reader.read();

        // then
        assertThat(char1).isEqualTo('a');
        assertThat(char2).isEqualTo('X');
        assertThat(char3).isEqualTo('b');
        assertThat(char4).isEqualTo(-1);
        assertThat(char5).isEqualTo(-1);
    }

    @Test
    public void test_simple_read_by_2() throws IOException {
        // given
        Iterator<String> data = Stream.of("a", "b").iterator();
        String separator = "X";
        Reader reader = new StringIteratorReader(separator, data);
        int readSize = 2;

        // when
        String actualResult = IOTestUtil.toString(reader, readSize);

        // then
        assertThat(actualResult).isEqualTo("aXb");
    }

    @Test
    public void test_simple_read_by_1() throws IOException {
        // given
        Iterator<String> data = Stream.of("a", "b").iterator();
        String separator = "X";
        Reader reader = new StringIteratorReader(separator, data);
        int readSize = 1;

        // when
        String actualResult = IOTestUtil.toString(reader, readSize);

        // then
        assertThat(actualResult).isEqualTo("aXb");
    }

    @Test
    public void test_simple_zero_separator() throws IOException {
        // given
        Iterator<String> data = Stream.of("a", "b").iterator();
        String separator = "";

        // when
        Reader reader = new StringIteratorReader(separator, data);

        // then
        assertThat(IOUtil.toString(reader)).isEqualTo("ab");
    }

    @Test
    public void test_two_empty_non_empty_separator() throws IOException {
        // given
        Iterator<String> data = Stream.of("", "").iterator();
        String separator = "X";

        // when
        Reader reader = new StringIteratorReader(separator, data);

        // then
        assertThat(IOUtil.toString(reader)).isEqualTo("X");
    }

    @Test
    public void test_three_empty_non_empty_separator() throws IOException {
        // given
        Iterator<String> data = Stream.of("", "", "").iterator();
        String separator = "X";

        // when
        Reader reader = new StringIteratorReader(separator, data);

        // then
        assertThat(IOUtil.toString(reader)).isEqualTo("XX");
    }

    @Test
    public void test_two_simple_single_char_separator() throws IOException {
        // given
        Iterator<String> data = Stream.of("ab", "cd").iterator();
        String separator = "X";
        Reader reader = new StringIteratorReader(separator, data);

        // when
        String actualResult = IOTestUtil.toString(reader, 1);

        // then
        assertThat(actualResult).isEqualTo("abXcd");
    }

//    @Test
//    public void test_small_csv() throws IOException {
//        // given
//        Reader input = new StringIteratorReader("\n", new String[]{
//                "id|name|lastUpdate",
//                "1|alice|2020-10-26T10:15:30.00Z"
//        });
//
//        // when
//        String actualResult = IOUtil.toString(input);
//
//        assertThat(actualResult).isEqualTo("id|name|lastUpdate" + "\n" + "1|alice|2020-10-26T10:15:30.00Z");
//    }
//
}