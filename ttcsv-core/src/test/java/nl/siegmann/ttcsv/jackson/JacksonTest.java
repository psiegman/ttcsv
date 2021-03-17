package nl.siegmann.ttcsv.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvWriter;
import nl.siegmann.ttcsv.util.StreamUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JacksonTest {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({"id", "name"})
    @Data
    public static class Fruit {
        private long id;

        @JsonProperty("myName")
        private String name;

        private String description;

        @JsonProperty("longStory")
        public String getDescription() {
            return description;
        }
    }

    @Test
    public void test1() throws JsonProcessingException {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Fruit apple = new Fruit(1, "apple", "a green one");

        // when
        Map<String, Object> jsonData = objectMapper.convertValue(apple, Map.class);

        // then
        assertThat(jsonData).containsOnly(
                entry("id", 1l),
                entry("myName", "apple"),
                entry("longStory", "a green one")
        );
    }

    @Test
    public void shouldCompareDescriptorColumnValue() {
        // given
        List<String> orderedProperties = List.of("b", "c");
        PropertyDescriptorColumnValueFactoryComparator propertyDescriptorColumnValueFactoryComparator = new PropertyDescriptorColumnValueFactoryComparator(orderedProperties);
        PropertyDescriptorColumnValueFactory p1 = mock(PropertyDescriptorColumnValueFactory.class, Mockito.RETURNS_DEEP_STUBS);
        PropertyDescriptorColumnValueFactory p2 = mock(PropertyDescriptorColumnValueFactory.class, Mockito.RETURNS_DEEP_STUBS);

        // when
        Stream
                .of(
                        List.of("a", "b", "1"),
                        List.of("b", "a", "-1"),
                        List.of("b", "c", "-1"),
                        List.of("c", "d", "-1"),
                        List.of("d", "c", "1"),
                        List.of("e", "f", "-1"),
                        List.of("f", "e", "1")
                )
                .forEach(testValues -> {
                    String p1Name = testValues.get(0);
                    String p2Name = testValues.get(1);
                    String expectedResult = testValues.get(2);
                    when(p1.getPropertyDescriptor().getName()).thenReturn(p1Name);
                    when(p2.getPropertyDescriptor().getName()).thenReturn(p2Name);
                    assertThat(String.valueOf(propertyDescriptorColumnValueFactoryComparator.compare(p1, p2)))
                            .as("Comparing '" + p1Name + "' with '" + p2Name + "'")
                            .isEqualTo(expectedResult);
                });
    }

    @Test
    public void test2() throws IOException, IntrospectionException {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Fruit apple = new Fruit(1, "apple", "a green one");
        Fruit pear = new Fruit(2, "pear", "It's pear shaped");
        List<ColumnValueFactory> columnValueFactories = createColumnValueFactories(Fruit.class);

        // when
        CsvDataFactory csvDataFactory = new CsvDataFactory(columnValueFactories, List.of(apple, pear).iterator());
        List<List<String>> appleValues = (List<List<String>>) StreamUtil.toStream(csvDataFactory).collect(Collectors.toList());
        String csv = new CsvWriter(new CsvConfig().withFieldSeparator('|')).writeCsvToString(appleValues.stream());
        System.out.println("Hello, world");
    }

    private class CsvDataFactory<T> implements Iterator<List<String>> {
        private List<ColumnValueFactory> columnValueFactories;
        private Iterator<T> beans;
        private boolean headerIsWritten = false;

        public CsvDataFactory(List<ColumnValueFactory> columnValueFactories, Iterator<T> beans) {
            this.columnValueFactories = columnValueFactories;
            this.beans = beans;
        }

        @Override
        public boolean hasNext() {
            if (headerIsWritten == false) {
                return true;
            }
            return beans.hasNext();
        }

        @Override
        public List<String> next() {
            if (headerIsWritten == false) {
                headerIsWritten = true;
                return getHeaderValues();
            }
            if (!beans.hasNext()) {
                throw new NoSuchElementException("No more elements available");
            }
            return createBeanValues(beans.next());
        }

        private List<String> createBeanValues(T bean) {
            List<String> values = columnValueFactories.stream().flatMap(f -> {
                try {
                    return f.getValues(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            if (values == null) {
                values = List.of(new String[columnValueFactories.size()]);
            }
            return values;
        }

        private List<String> getHeaderValues() {
            return columnValueFactories.stream().flatMap(ColumnValueFactory::getColumnNames).collect(Collectors.toList());
        }
    }

    private interface ColumnValueFactory {
        Stream<String> getColumnNames();

        Stream<String> getValues(Object bean) throws Exception;
    }


    @Data
    private static class PropertyDescriptorColumnValueFactory implements ColumnValueFactory {

        private String columnName;
        private PropertyDescriptor propertyDescriptor;

        public PropertyDescriptorColumnValueFactory(String columnName, PropertyDescriptor propertyDescriptor) {
            this.columnName = columnName;
            this.propertyDescriptor = propertyDescriptor;
        }

        @Override
        public Stream<String> getColumnNames() {
            return Stream.of(columnName);
        }

        @Override
        public Stream<String> getValues(Object bean) throws Exception {
            return Stream.of(String.valueOf(propertyDescriptor.getReadMethod().invoke(bean)));
        }
    }

    private String getSerializatinPropertyName(Class beanClass, PropertyDescriptor propertyDescriptor) {
        String serializationPropertyName = propertyDescriptor.getName();

        JsonProperty jsonProperty = propertyDescriptor.getReadMethod().getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            serializationPropertyName = jsonProperty.value();
        } else {
            try {
                Field propertyField = beanClass.getDeclaredField(propertyDescriptor.getName());
                jsonProperty = propertyField.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    serializationPropertyName = jsonProperty.value();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return serializationPropertyName;
    }

    private List<ColumnValueFactory> createColumnValueFactories(Class beanClass) throws IntrospectionException {
        JsonPropertyOrder jsonPropertyOrder = (JsonPropertyOrder) beanClass.getAnnotation(JsonPropertyOrder.class);
        List<String> sortedProperties = Collections.emptyList();

        if (jsonPropertyOrder != null) {
            sortedProperties = List.of(jsonPropertyOrder.value());
        }
        Comparator<PropertyDescriptorColumnValueFactory> propertyDescriptorColumnValueFactoryComparator = new PropertyDescriptorColumnValueFactoryComparator(sortedProperties);

        List<ColumnValueFactory> columnValueFactories = Stream
                .of(Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
                .filter(pd -> !pd.getName().equals("class"))
                .map(pd -> new PropertyDescriptorColumnValueFactory(getSerializatinPropertyName(beanClass, pd), pd))
                .sorted(propertyDescriptorColumnValueFactoryComparator)
                .collect(Collectors.toList());

        return columnValueFactories;
    }

    private static class PropertyDescriptorColumnValueFactoryComparator implements Comparator<PropertyDescriptorColumnValueFactory> {

        private List<String> orderedFields;

        private PropertyDescriptorColumnValueFactoryComparator(List<String> orderedFields) {
            this.orderedFields = orderedFields;
        }

        @Override
        public int compare(PropertyDescriptorColumnValueFactory o1, PropertyDescriptorColumnValueFactory o2) {
            int result;
            String o1Name = o1.getPropertyDescriptor().getName();
            String o2Name = o2.getPropertyDescriptor().getName();
            int o1Pos = orderedFields.indexOf(o1Name);
            int o2Pos = orderedFields.indexOf(o2Name);
            if (o1Pos < 0) {
                if (o2Pos < 0) {
                    result = o1Name.compareTo(o2Name);
                } else {
                    result = 1;
                }
            } else if (o2Pos < 0) {
                result = -1;
            } else {
                result = o1Pos - o2Pos;
            }
            return result;
        }
    }
}
