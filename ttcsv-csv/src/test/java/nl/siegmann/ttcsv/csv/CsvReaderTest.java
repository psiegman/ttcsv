package nl.siegmann.ttcsv.csv;

import nl.siegmann.ttcsv.test.IOExceptionReader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvReaderTest {

    @Nested
    class UnquotedTests {

        @Test
        public void test_single_value() throws IOException {
            // given
            String input = "apple";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(Collections.singletonList("apple")));
        }


        @Test
        public void test_two_rows() throws IOException {
            // given
            String input = "apple\npear";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(
                    List.of(
                            Collections.singletonList("apple"),
                            Collections.singletonList("pear")
                    ));
        }

        @Test
        public void test_two_rows_two_columns() throws IOException {
            // given
            String input = "apple,pear\r\norange,tangerine";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(
                    List.of(
                            List.of("apple", "pear"),
                            List.of("orange", "tangerine")
                    ));
        }

        @Test
        public void test_ioexception() throws IOException {
            // given
            String input = "apple";
            Reader reader = new IOExceptionReader(input, new IOException("test IOException"));

            // when
            Stream<List<String>> csvData = new CsvReader().apply(reader);

            // then
            IOException exception = assertThrows(IOException.class, () -> csvData.collect(Collectors.toList()));
            assertThat(exception.getMessage()).isEqualTo("test IOException");
        }

        @Test
        public void test_two_values() throws IOException {
            // given
            String input = "apple,pear";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(List.of("apple", "pear")));
        }

        @Test
        public void test_value_empty_value() throws IOException {
            // given
            String input = "apple,,pear";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(List.of("apple", "", "pear")));
        }
    }

    @Nested
    class QuotedTests {
        @Test
        public void test_single_value_quoted() throws IOException {
            // given
            String input = "\"apple\"";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(Collections.singletonList("apple")));
        }

        @Test
        public void test_single_value_quoted_nested_quote() throws IOException {
            // given
            String input = "\"ap\"\"ple\"";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(Collections.singletonList("ap\"ple")));
        }

        @Test
        public void test_quoted_separator() throws IOException {
            // given
            String input = "\"apple,pear\"";

            // when
            List<List<String>> csvData = new CsvReader().apply(new StringReader(input)).collect(Collectors.toList());

            // then
            assertThat(csvData).isEqualTo(Collections.singletonList(Collections.singletonList("apple,pear")));
        }

        @Test
        public void shouldReadQuotedCsv() {
            // given
            Reader input = new StringReader(
                    "\"id\"|\"name\"\n"
                            + "\"1\"|\"alice\"\n"
                            + "\"2\"|\"bob\"");
            CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|').withQuoteValues(true);
            Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(input);

            // when
            List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

            // then
            assertThat(actualCsvData).isEqualTo(
                    List.of(
                            List.of("id", "name"),
                            List.of("1", "alice"),
                            List.of("2", "bob")
                    ));
        }

    }
}
