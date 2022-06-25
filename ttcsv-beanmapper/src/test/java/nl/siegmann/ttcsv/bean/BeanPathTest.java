package nl.siegmann.ttcsv.bean;

import lombok.Data;
import lombok.SneakyThrows;
import nl.siegmann.ttcsv.util.StringUtils;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanPathTest {

    @Data
    public static class Address {
        private String street;
        private String city;
    }

    @Data
    public static class Person {
        private String name;
        private Address address;
    }

    @Data
    public static class Book {
        private String title;
        private Person author;

    }

    @Test
    public void shouldDoFoo() throws IntrospectionException {
        // given
        String propertyPath = "author.address.street";
        String value = "main street";

        // when
        ValueMapper<Book> valueMapper = new ValueMapper<>(Book.class, propertyPath);
        Book book = valueMapper.apply(new Book(), value);

        // then
        assertThat(book.getAuthor().getAddress().getStreet()).isEqualTo(value);
    }


    private static class PropertyBeanFactory {

        private final PropertyDescriptor propertyDescriptor;

        public PropertyBeanFactory(Class<?> beanClass, String propertyName) throws IntrospectionException {
            this.propertyDescriptor = new PropertyDescriptor(propertyName, beanClass);
        }

        public Class<?> getPropertyClass() {
            return propertyDescriptor.getPropertyType();
        }

        public Object foo(Object baseBean) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
            Object nextBean = propertyDescriptor.getReadMethod().invoke(baseBean);
            if (nextBean == null) {
                nextBean = propertyDescriptor.getPropertyType().getConstructor().newInstance();
                propertyDescriptor.getWriteMethod().invoke(baseBean, nextBean);
            }
            return nextBean;
        }

        public void setValue(Object targetBean, Object value) throws InvocationTargetException, IllegalAccessException {
            propertyDescriptor.getWriteMethod().invoke(targetBean, value);
        }
    }

    private static class ValueMapper<T> implements BiFunction<T, String, T> {

        private final List<PropertyBeanFactory> beanPath;
        private final PropertyDescriptor propertyDescriptor;

        public ValueMapper(Class<T> targetClass, String propertyExpression) throws IntrospectionException {
            List<String> propertyPath = StringUtils.split(propertyExpression, ".");
            this.beanPath = createBeanPathFactory(propertyPath.subList(0, propertyPath.size() - 1), targetClass);
            propertyDescriptor = new PropertyDescriptor(propertyPath.get(propertyPath.size() - 1), beanPath.get(beanPath.size()-1).getPropertyClass());
        }

        private static <T> List<PropertyBeanFactory> createBeanPathFactory(List<String> propertyPath, Class<T> rootClass) throws IntrospectionException {
            List<PropertyBeanFactory> propertyBeanFactories = new ArrayList<>(propertyPath.size());
            Class currentClass = rootClass;
            for (String propertyName : propertyPath) {
                PropertyBeanFactory propertyBeanFactory = new PropertyBeanFactory(currentClass, propertyName);
                propertyBeanFactories.add(propertyBeanFactory);
                currentClass = propertyBeanFactory.getPropertyClass();
            }
            return propertyBeanFactories;
        }

        @SneakyThrows
        @Override
        public T apply(T targetBean, String propertyValue) {
            Object currentBean = targetBean;
            for (PropertyBeanFactory propertyBeanFactory: beanPath) {
                currentBean = propertyBeanFactory.foo(currentBean);
            }
            Object value = convertToObject(propertyValue);
            propertyDescriptor.getWriteMethod().invoke(currentBean, value);
            return targetBean;
        }

        private Object convertToObject(String value) {
            return value;
        }
    }
}
