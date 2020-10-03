package nl.siegmann.ttcsv;

import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A set of unit tests to be used in examples and documentation.
 */
public class ExampleTest {

    @Test
    public void shouldReadSimpleCsv() {
        // given
        Reader input = new StringReader("id,name\n1,alice\n2,bob");
        Function<Reader, Stream<List<String>>> csvReader = new CsvReader();
        Stream<List<String>> csvDataStream = csvReader.apply(input);

        // when
        List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

        // then
        assertThat(actualCsvData).isEqualTo(
                Arrays.asList(
                        Arrays.asList("id", "name"),
                        Arrays.asList("1", "alice"),
                        Arrays.asList("2", "bob")
                ));
    }

    @Test
    public void shouldReadPipeSeparatedCsv() {
        // given
        Reader input = new StringReader("id|name\n1|alice\n2|bob");
        CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|');
        Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(input);

        // when
        List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

        // then
        assertThat(actualCsvData).isEqualTo(
                Arrays.asList(
                        Arrays.asList("id", "name"),
                        Arrays.asList("1", "alice"),
                        Arrays.asList("2", "bob")
                ));
    }

    @Test
    public void shouldReadQuotedCsv() {
        // given
        Reader input = new StringReader("\"id\"|\"name\"\n\"1\"|\"alice\"\n\"2\"|\"bob\"");
        CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|').withQuoteValues(true);
        Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(input);

        // when
        List<List<String>> actualCsvData = csvDataStream.collect(Collectors.toList());

        // then
        assertThat(actualCsvData).isEqualTo(
                Arrays.asList(
                        Arrays.asList("id", "name"),
                        Arrays.asList("1", "alice"),
                        Arrays.asList("2", "bob")
                ));
    }
}
