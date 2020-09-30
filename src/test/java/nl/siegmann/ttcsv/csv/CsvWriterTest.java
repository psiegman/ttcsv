package nl.siegmann.ttcsv.csv;


import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvWriter;
import nl.siegmann.ttcsv.test.TextValueSupplier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvWriterTest {

    @Test
    public void test1() throws IOException {

        // given
        TextValueSupplier textValueSupplier = new TextValueSupplier();

        Writer out = new StringWriter();
        Stream<List<String>> data = LongStream
                .range(1, 3)
                .mapToObj(rowNr -> Arrays.asList(
                        String.valueOf(rowNr),
                        textValueSupplier.get(),
                        "\"hi\""));

        CsvWriter csvWriter = new CsvWriter(new CsvConfig().withFieldSeparator('|').withQuoteValues(true).withRowSeparatorChars("\n"));

        // when
        csvWriter.writeCsv(
                data
                , out
        );

        // then
        assertThat(out.toString()).isEqualTo(
                "\"1\"|\"a\"|\"\"\"hi\"\"\"\n"
                        + "\"2\"|\"b\"|\"\"\"hi\"\"\"\n");
    }
}
