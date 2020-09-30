package nl.siegmann.ttcsv.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.bean.CsvBeanConfig;
import nl.siegmann.ttcsv.bean.CsvBeanReader;
import nl.siegmann.ttcsv.util.StringArrayReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @Test
    public void test_single_person() throws IOException {
        // given
        Reader input = new StringArrayReader(new String[]{
                "id|name|lastUpdate",
                "1|alice|2020-10-26T10:15:30.00Z"
        }, "\n");
//        Reader input = new StringReader("id|name|lastUpdate\n1|alice|2020-10-26T10:15:30.00Z");
        CsvBeanReader csvBeanReader = new CsvBeanReader(new CsvBeanConfig(Person::new).withFieldSeparator('|'));

        // when
        List<Person> persons = (List<Person>) csvBeanReader.apply(input).collect(Collectors.toList());

        // then
        assertThat(persons).isEqualTo(Collections.singletonList(
                new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z"))));
    }

    @Test
    public void test_two_persons() throws IOException {
        // given
        CsvBeanReader csvBeanReader = new CsvBeanReader(new CsvBeanConfig(Person::new).withFieldSeparator('|'));
        Reader input = new StringReader("id|name|lastUpdate\n1|alice|2020-10-26T10:15:30.00Z\n2|bob|2020-10-26T11:15:30.00Z");

        // when
        Stream<Person> csvData = csvBeanReader.apply(input);

        // then
        List<Person> personList = csvData.collect(Collectors.toList());
        assertThat(personList).isEqualTo(Arrays.asList(
                new Person(1, "alice", Instant.parse("2020-10-26T10:15:30.00Z")),
                new Person(2, "bob", Instant.parse("2020-10-26T11:15:30.00Z"))
        ));
    }
}
