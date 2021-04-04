package nl.siegmann.ttcsv.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvBeanReaderTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fruit {
        private long id;
        private String name;
        private BigDecimal price;
        private Instant lastUpdate;
    }

    @Test
    public void test_single_fruit() throws IOException {
        // given
        Reader input = StringIteratorReader.of("\n",
                "id|name|price|lastUpdate",
                "1|apple|1.95|2020-10-26T10:15:30.00Z"
        );
        CsvBeanReader<Fruit> csvBeanReader = new CsvBeanReader<>(new CsvBeanConfig<>(Fruit::new).withFieldSeparator('|'));

        // when
        List<Fruit> fruits = csvBeanReader.apply(input).collect(Collectors.toList());

        // then
        assertThat(fruits).isEqualTo(Collections.singletonList(
                new Fruit(1, "apple", new BigDecimal("1.95"), Instant.parse("2020-10-26T10:15:30.00Z"))));
    }

    @Disabled
    public void test_column_mapper() throws IOException {
        // given
        Reader input = StringIteratorReader.of("\n",
                "id|the_name|price_in_euros|lastUpdate",
                "1|apple|1.95|2020-10-26T10:15:30.00Z"
        );
        FieldMappers<Fruit> fieldMappers = new FieldMappers<>();
        fieldMappers
                .add("the_name", (fruit, name) -> fruit.setName((String) name), String.class)
                .add("price_in_euros", (fruit, price) -> fruit.setPrice((BigDecimal) price), BigDecimal.class);

        CsvBeanReader<Fruit> csvBeanReader = new CsvBeanReader<>(new CsvBeanConfig<>(Fruit::new).withFieldSeparator('|'));

        // when
        List<Fruit> fruits = csvBeanReader.apply(input).collect(Collectors.toList());

        // then
        assertThat(fruits).isEqualTo(Collections.singletonList(
                new Fruit(1, "apple", new BigDecimal("1.95"), Instant.parse("2020-10-26T10:15:30.00Z"))));
    }
}
