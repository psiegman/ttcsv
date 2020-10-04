package nl.siegmann.ttcsv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.BeanFactory;
import nl.siegmann.ttcsv.bean.BeanFactoryBuilder;
import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import nl.siegmann.ttcsv.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A set of unit tests meant as an example of usage.
 */
public class ExampleTest {

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
                    Arrays.asList(
                            Arrays.asList("id", "name"),
                            Arrays.asList("1", "apple"),
                            Arrays.asList("2", "orange")
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
                    Arrays.asList(
                            Arrays.asList("id", "name", "description"),
                            Arrays.asList("1", "apple", "a green one, try it!\nA second line"),
                            Arrays.asList("2", "orange", "an orange orange")
                    ));
        }
    }

    @DisplayName("Using ttcsv to read csv to java beans")
    @Nested
    static class CsvBeanReaderTests {

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Fruit {
            private long id;
            private String name;
            private Float price;
        }

        private BeanFactoryBuilder beanFactoryBuilder;

        @BeforeEach
        public void setUp() {
            this.beanFactoryBuilder = new BeanFactoryBuilder();
        }

        @DisplayName("Read a csv file with Fruits using the BeanFactory")
        @Test
        public void shouldReadTwoFruits() throws Exception {
            // given
            Stream<List<String>> csvStream = createCsvStream(
                    "id|name|price",
                    "1|apple|1.25",
                    "2|orange|1.37"
            );
            Function<List<String>, Stream<Fruit>> beanFactory = beanFactoryBuilder.createBeanFactory(Fruit.class);

            // when
            List<Fruit> Fruits = csvStream
                    .flatMap(beanFactory)
                    .collect(Collectors.toList());

            // then
            assertThat(Fruits).isEqualTo(Arrays.asList(
                    new Fruit(1, "apple", 1.25f),
                    new Fruit(2, "orange", 1.37f)
            ));
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
        public void shouldReadTwoFruitsWithCustomConverter() throws Exception {
            // given
            Stream<List<String>> csvStream = createCsvStream(
                    "id|name|price",
                    "1|apple|1,25",
                    "2|pear|1,37"
            );
            BeanFactory<Fruit> beanFactory = beanFactoryBuilder.createBeanFactory(Fruit.class);
            beanFactory.getConverterRegistry().registerConverterForType(new CustomFloatConverter(), Float.TYPE, Float.class);

            // when
            List<Fruit> fruits = csvStream
                    .flatMap(beanFactory)
                    .collect(Collectors.toList());

            // then
            assertThat(fruits).isEqualTo(Arrays.asList(
                    new Fruit(1, "apple", 1.25f),
                    new Fruit(2, "pear", 1.37f)
            ));
        }

        private Stream<List<String>> createCsvStream(String... csvData) {
            return new CsvReader(new CsvConfig().withFieldSeparator('|'))
                    .apply(StringIteratorReader.of("\n", csvData));
        }

    }
}