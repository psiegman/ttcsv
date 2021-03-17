package nl.siegmann.ttcsv.util;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void shouldSplit() {
        assertThat(StringUtils.split("a.b", ".")).isEqualTo(List.of("a", "b"));
    }

    @Test
    public void shouldSplitOnCharacter() {
        assertThat(StringUtils.split("a.b", '.')).isEqualTo(List.of("a", "b"));
    }

}