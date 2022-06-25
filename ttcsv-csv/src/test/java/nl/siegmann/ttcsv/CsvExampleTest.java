package nl.siegmann.ttcsv;

import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A set of unit tests meant as an example of usage.
 */
public class CsvExampleTest {

    @DisplayName("Various tests to demonstrate how to read a CSV file using ttcsv")
    @Nested
    class CsvReaderTests {

        @DisplayName("Read a simple csv file with '|' as separators")
        @Test
        public void shouldReadPipeSeparatedCsv() {
            // given
            Reader input = new StringReader("id|name\n1|apple\n2|orange");
            CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|');
            Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(input);

            // when
            List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

            // then
            assertThat(actualCsvData).isEqualTo(
                    List.of(
                            List.of("id", "name"),
                            List.of("1", "apple"),
                            List.of("2", "orange")
                    ));
        }

        @DisplayName("Read a more complex csv file with quoted values and embedded quotes, separators and newlines")
        @Test
        public void shouldReadEmbeddedQuoteCsv() {
            // given
            Reader input = new StringReader("id,name,description\n1,apple,\"a green one, try it!\nA second line\"\n2,orange,an orange orange");
            Stream<List<String>> csvDataStream = new CsvReader().apply(input);

            // when
            List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

            // then
            assertThat(actualCsvData).isEqualTo(
                    List.of(
                            List.of("id", "name", "description"),
                            List.of("1", "apple", "a green one, try it!\nA second line"),
                            List.of("2", "orange", "an orange orange")
                    ));
        }
    }
}