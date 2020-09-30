# Twenty-twenty CSV

A modern java csv reader/writer

## Assumptions
- A row fits into memory
- A CSV file may be larger than the available memory

## Architecture

```plantuml

[CSVBeanReader\n\nReader to Stream<T>] as csv_bean_reader

[CSVMapReader\n\nReader to Stream<Map<String,String>>] as csv_map_reader

[CSVReader\n\nReader to Stream<List<String>>] as csv_reader

csv_bean_reader -down-> csv_map_reader

csv_map_reader -down-> csv_reader
```