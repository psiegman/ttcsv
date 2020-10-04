# Twenty-twenty CSV

A modern java csv reader/writer.

## Introduction
A java 8 lambda/stream-friendly CSV reader.
It takes a single line to convert a Reader with csv data to a stream of lists of Strings.

### Input

Reading this input:

    id,name
    1,apple
    2,pear

Takes this java code:

```java
Stream<List<String>> csvDataStream = new CsvReader().apply(reader);
```

Pipe-separated input:

    id|name
    1|apple
    2|pear

Using a custom config:

```java
CsvConfig csvConfig = new CsvConfig().withFieldSeparator('|');
Stream<List<String>> csvDataStream = new CsvReader(csvConfig).apply(reader);
```

## Features
- supports quote escaping
- supports newlines in quoted values

## Assumptions
- A row fits into memory
- A CSV file may be larger than the available memory

## Example usage

[Sample Code](src/test/java/nl/siegmann/ttcsv/ExampleTest.java)