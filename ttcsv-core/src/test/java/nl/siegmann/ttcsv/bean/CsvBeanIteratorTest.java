package nl.siegmann.ttcsv.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvBeanIteratorTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Fruit {
        private long id;
        private String name;
    }

    private static class FruitSupplier implements Supplier<Fruit> {

        List<Fruit> suppliedFruit = new ArrayList<>();

        @Override
        public Fruit get() {
            Fruit fruit = new Fruit();
            suppliedFruit.add(fruit);
            return fruit;
        }
    }

    @Test
    public void shouldReadFruit() {
        // given
        String csvData = "id|name\n"
                + "1|Apple\n"
                + "2|Pear\n"
                + "3|Orange\n";

        FruitSupplier fruitSupplier = new FruitSupplier();

        // when
        List<Fruit> fruits = new CsvBeanReader<>(new CsvBeanConfig<>(fruitSupplier).withFieldSeparator('|'))
                .apply(new StringReader(csvData))
                .collect(Collectors.toList());

        // then
        assertThat(fruits.size()).isEqualTo(3);
        assertThat(fruitSupplier.suppliedFruit.size()).isEqualTo(3);
    }
}