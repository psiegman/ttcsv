package nl.siegmann.ttcsv.bean;

import lombok.Data;

public class JsonCsvSerializerTest {

    @Data
    public static class Fruit {
        private long id;
        // @JsonProperty
        private String name;
        private String description;
    }
}
