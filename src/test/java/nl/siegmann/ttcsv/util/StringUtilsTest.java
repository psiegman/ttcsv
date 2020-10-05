package nl.siegmann.ttcsv.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    void isBlank() {
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank(" ")).isTrue();
        assertThat(StringUtils.isBlank("\t")).isTrue();
        assertThat(StringUtils.isBlank(" \t \r \n ")).isTrue();
        assertThat(StringUtils.isBlank("x")).isFalse();
    }
}