package nl.siegmann.ttcsv.jackson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.bean.BeanSerializer;
import nl.siegmann.ttcsv.bean.CsvBeanWriter;
import nl.siegmann.ttcsv.csv.CsvConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class JacksonTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Fruit {
        private int id;
        private String name;
    }

    public static class FruitBeanSerializer implements BeanSerializer<Fruit> {

        @Override
        public List<String> getColumnHeaders() {
            return List.of("id", "name");
        }

        @Override
        public List<String> apply(Fruit fruit) {
            return List.of(
                    String.valueOf(fruit.getId()),
                    fruit.getName());
        }
    }


    @Test
    public void doIt() throws IOException {

        // given
        CsvBeanWriter<Fruit> csvBeanWriter = new CsvBeanWriter<>(new FruitBeanSerializer());
        Stream<Fruit> testData = Stream.of(new Fruit(1, "Apple"), new Fruit(2, "Pear"));

        // when
        String actualResult = csvBeanWriter.writeCsvToString(testData);

        // then
        assertThat(actualResult).isEqualTo(
                "id,name\n\r"
                        + "1,Apple\n\r"
                        + "2,Pear\n\r");
    }

    @Test
    public void doItWithCustomCsvConfig() throws IOException {

        // given
        CsvBeanWriter<Fruit> csvBeanWriter = new CsvBeanWriter<>(new FruitBeanSerializer()).withCsvConfig(new CsvConfig().withFieldSeparator('|'));
        Stream<Fruit> testData = Stream.of(new Fruit(1, "Apple"), new Fruit(2, "Pear"));

        // when
        String actualResult = csvBeanWriter.writeCsvToString(testData);

        // then
        assertThat(actualResult).isEqualTo(
                "id|name\n\r"
                        + "1|Apple\n\r"
                        + "2|Pear\n\r");
    }
}
