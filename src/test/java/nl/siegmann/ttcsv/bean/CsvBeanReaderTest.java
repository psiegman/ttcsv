package nl.siegmann.ttcsv.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import nl.siegmann.ttcsv.util.StringIteratorReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private BeanFactoryBuilder beanFactoryBuilder;

    @BeforeEach
    public void setUp() {
        this.beanFactoryBuilder = new BeanFactoryBuilder();
    }

    @Test
    public void test_single_person() throws IOException {
        // given
        Reader input = StringIteratorReader.of("\n",
                "id|name|lastUpdate",
                "1|alice|2020-10-26T10:15:30.00Z"
        );
        CsvBeanReader csvBeanReader = new CsvBeanReader(new CsvBeanConfig(Person::new).withFieldSeparator('|'));

        // when
        List<Person> persons = (List<Person>) csvBeanReader.apply(input).collect(Collectors.toList());

        // then
        assertThat(persons).isEqualTo(Collections.singletonList(
                new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z"))));
    }

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

    Stream<List<String>> createCsvStream(String... csvData) {
        return new CsvReader(new CsvConfig().withFieldSeparator('|'))
                .apply(StringIteratorReader.of("\n", csvData));
    }
}
