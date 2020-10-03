# Twenty-twenty CSV

A modern java csv reader/writer

## Assumptions
- A row fits into memory
- A CSV file may be larger than the available memory

## Architecture

```plantuml

[CSVBeanReader\n\nReader to Stream<T>] as csv_bean_reader

[CSVMapReader\n\nReader to Stream<Map<String,String>>] as csv_map_reader

[CSVReader\n\nReader to Stream<List<String>>] as csv_reader

csv_bean_reader -down-> csv_map_reader

csv_map_reader -down-> csv_reader
```

## Example usage

```java
package nl.siegmann.ttcsv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.*;
import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import nl.siegmann.ttcsv.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A set of unit tests to be used in examples and documentation.
 */
public class ExampleTest {

    @DisplayName("Various tests to demonstrate how to read a CSV file using ttcsv")
    @Nested
    class CsvReaderTests {

        @DisplayName("Read a simple csv file")
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

        @DisplayName("Read a simple csv file with '|' as separators")
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

        @DisplayName("Read a quoted CSV file")
        @Test
        public void shouldReadQuotedCsv() {
            // given
            Reader input = new StringReader(
                    "\"id\"|\"name\"\n"
                            + "\"1\"|\"alice\"\n"
                            + "\"2\"|\"bob\"");
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
    }

    @DisplayName("Using ttcsv to read csv to java beans")
    @Nested
    static class CsvBeanReaderTests {

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Person {
            private long id;
            private String name;
            private Instant lastUpdate;
        }

        private BeanFactoryBuilder beanFactoryBuilder;

        @BeforeEach
        public void setUp() {
            this.beanFactoryBuilder = new BeanFactoryBuilder();
        }

        @DisplayName("Read a csv file with persons using the CsvBeanReader")
        @Test
        public void test_single_person() throws IOException {
            // given
            Reader input = StringIteratorReader.of("\n",
                    "id|name|lastUpdate",
                    "1|alice|2020-10-26T10:15:30.00Z"
            );
            CsvBeanConfig<Person> csvBeanConfig = new CsvBeanConfig<>(Person::new).withFieldSeparator('|');
            CsvBeanReader csvBeanReader = new CsvBeanReader(csvBeanConfig);

            // when
            List<Person> persons = (List<Person>) csvBeanReader.apply(input).peek(p -> System.out.println(p.getClass())).collect(Collectors.toList());

            // then
            assertThat(persons).isEqualTo(Collections.singletonList(
                    new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z"))));
        }

        @DisplayName("Read a csv file with persons using the BeanFactory")
        @Test
        public void test_two_persons_stream() throws Exception {
            // given
            Stream<List<String>> csvStream = createCsvStream(
                    "id|name|lastUpdate",
                    "1|alice|2020-10-26T10:15:30.00Z",
                    "2|bob|2020-10-26T11:15:30.00Z"
            );
            Function<List<String>, Person> beanFactory = beanFactoryBuilder.createBeanFactory(Person.class);

            // when
            List<Person> persons = csvStream
                    .map(beanFactory)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // then
            assertThat(persons).isEqualTo(Arrays.asList(
                    new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z")),
                    new Person(2, "bob", Instant.parse("2020-10-26T11:15:30.00Z"))
            ));
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Fruit {
            private String name;
            private float price;
        }

        /**
         * Reads a floating point number with a ',' as a decimal separator.
         */
        private static class CustomFloatConverter implements Converter<Float> {
            private DecimalFormat decimalFormat;

            public CustomFloatConverter() {

                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator(',');
                decimalFormatSymbols.setGroupingSeparator(' ');

                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

                this.decimalFormat = decimalFormat;
            }

            @SneakyThrows
            @Override
            public Float apply(String s) {
                if (StringUtils.isBlank(s)) {
                    return 0.0f;
                }
                return decimalFormat.parse(s).floatValue();
            }
        }


        @DisplayName("Read a csv file to java beans using a custom type converter")
        @Test
        public void test_custom_converter() throws Exception {
            // given
            Stream<List<String>> csvStream = createCsvStream(
                    "name|price",
                    "apple|1,25",
                    "pear|1,37"
            );
            BeanFactory<Fruit> beanFactory = beanFactoryBuilder.createBeanFactory(Fruit.class);
            beanFactory.getConverterRegistry().registerConverter(Float.TYPE, new CustomFloatConverter());

            // when
            List<Fruit> fruits = csvStream
                    .map(beanFactory)
                    .skip(1)
                    .collect(Collectors.toList());

            // then
            assertThat(fruits).isEqualTo(Arrays.asList(
                    new Fruit("apple", 1.25f),
                    new Fruit("pear", 1.37f)
            ));
        }

        private Stream<List<String>> createCsvStream(String... csvData) {
            return new CsvReader(new CsvConfig().withFieldSeparator('|'))
                    .apply(StringIteratorReader.of("\n", csvData));
        }

    }
}
```