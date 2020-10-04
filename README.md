# Twenty-twenty CSV

A modern java csv reader/writer.

## Introduction
A java 8 lambda/stream-friendly CSV reader.
It takes a single line to convert a Reader with csv data to a stream of lists of Strings.

## Samples


### Simple CSV
Reading this input:

    id,name
    1,apple
    2,pear

Takes this java code:

```java
Stream<List<String>> csvDataStream = new CsvReader().apply(reader);
```

### Custom CSV
Pipe-separated input:

    id|name
    1|apple
    2|pear

Using a custom config:

```java
CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|');
Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(reader);
```

### Simple bean mapping

TTCSV contains a simple bean mapper that can create java beans from the csv data.

```java
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Fruit {
            private long id;
            private String name;
            private Float price;
        }

        private BeanFactoryBuilder beanFactoryBuilder;

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
```
## Features
- supports quote escaping
- supports newlines in quoted values

## Assumptions
- A row fits into memory
- A CSV file may be larger than the available memory

## Example usage

[Sample Code](src/test/java/nl/siegmann/ttcsv/ExampleTest.java)
