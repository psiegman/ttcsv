package nl.siegmann.ttcsv.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvBeanReaderTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {
        private long id;
        private String name;
        private Instant lastUpdate;
    }

    @Test
    public void test_single_person() throws IOException {
        // given
        Reader input = StringIteratorReader.of("\n",
                "id|name|lastUpdate",
                "1|alice|2020-10-26T10:15:30.00Z"
        );
        CsvBeanReader<Person> csvBeanReader = new CsvBeanReader<>(new CsvBeanConfig(Person::new).withFieldSeparator('|'));

        // when
        List<Person> persons = csvBeanReader.apply(input).collect(Collectors.toList());

        // then
        assertThat(persons).isEqualTo(Collections.singletonList(
                new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z"))));
    }
}
