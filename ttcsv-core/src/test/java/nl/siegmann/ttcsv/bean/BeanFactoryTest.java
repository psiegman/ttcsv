package nl.siegmann.ttcsv.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import nl.siegmann.ttcsv.bean.converter.Converter;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import nl.siegmann.ttcsv.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanFactoryTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fruit {
        private long id;
        private String name;
        private Float price;
        private Instant lastUpdate;
    }

    private BeanFactoryBuilder beanFactoryBuilder;

    @BeforeEach
    public void setUp() {
        this.beanFactoryBuilder = new BeanFactoryBuilder();
    }


    @Test
    public void test_two_persons_stream() throws Exception {
        // given
        Stream<List<String>> csvStream = createCsvStream(
                "id|name|lastUpdate",
                "1|apple|2020-10-26T10:15:30.00Z",
                "2|pear|2020-10-26T11:15:30.00Z"
        );

        // when
        List<Fruit> fruits = csvStream
                .flatMap(beanFactoryBuilder.createBeanFactory(Fruit::new))
                .collect(Collectors.toList());

        // then
        assertThat(fruits).isEqualTo(List.of(
                new Fruit(1, "apple", null, Instant.parse("2020-10-26T10:15:30.00Z")),
                new Fruit(2, "pear", null, Instant.parse("2020-10-26T11:15:30.00Z"))
        ));
    }

    /**
     * Reads a floating point number with a ',' as a decimal separator.
     */
    private static class CustomFloatConverter implements Converter {
        private final DecimalFormat decimalFormat;

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

    @DisplayName("Read CSV data to beans using a custom converter that uses ',' as a decimal separator")
    @Test
    public void test_custom_converter() throws Exception {
        // given
        Stream<List<String>> csvStream = createCsvStream(
                "id|name|price",
                "1|apple|1,25",
                "2|pear|1,37"
        );
        BeanFactory<Fruit> beanFactory = beanFactoryBuilder.createBeanFactory(Fruit::new);
        beanFactory.getConverterRegistry().registerConverterForTypes(new CustomFloatConverter(), Float.TYPE, Float.class);

        // when
        List<Fruit> fruits = csvStream
                .flatMap(beanFactory)
                .collect(Collectors.toList());

        // then
        assertThat(fruits).isEqualTo(List.of(
                new Fruit(1, "apple", 1.25f, null),
                new Fruit(2, "pear", 1.37f, null)
        ));
    }

    Stream<List<String>> createCsvStream(String... csvData) {
        return new CsvReader(new CsvConfig().withFieldSeparator('|'))
                .apply(StringIteratorReader.of("\n", csvData));
    }
}
