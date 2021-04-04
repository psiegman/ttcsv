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
import static org.assertj.core.groups.Tuple.tuple;

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


        // when
        FruitSupplier fruitSupplier = new FruitSupplier();
        List<Fruit> fruits = new CsvBeanReader<>(new CsvBeanConfig<>(fruitSupplier).withFieldSeparator('|'))
                .apply(new StringReader(csvData))
                .collect(Collectors.toList());

        // then
        assertThat(fruits.size()).isEqualTo(fruitSupplier.suppliedFruit.size()).isEqualTo(3);

        assertThat(fruits.get(0)).isSameAs(fruitSupplier.suppliedFruit.get(0));
        assertThat(fruits.get(1)).isSameAs(fruitSupplier.suppliedFruit.get(1));
        assertThat(fruits.get(2)).isSameAs(fruitSupplier.suppliedFruit.get(2));

        assertThat(fruits)
                .extracting("id", "name")
                .containsExactly(
                    tuple(1l, "Apple"),
                    tuple(2L, "Pear"),
                    tuple(3L, "Orange"));
    }
}