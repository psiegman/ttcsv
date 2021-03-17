package nl.siegmann.ttcsv.csv;

import nl.siegmann.ttcsv.csv.CsvConfig;
import nl.siegmann.ttcsv.csv.CsvReader;
import nl.siegmann.ttcsv.csv.CsvWriter;
import nl.siegmann.ttcsv.test.TextValueSupplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class PerformanceTest {

    @Disabled
    @Test
    public void test_load_big_file() throws FileNotFoundException {
        // given
        Reader testData = new BufferedReader(new FileReader("/home/paul/test.csv"));

        // when
        long startTime = System.currentTimeMillis();
        long nrAs = new CsvReader(new CsvConfig().withFieldSeparator('|'))
                .apply(testData)
                .filter(row -> {
                    String value = row.get(23);
                    return value.charAt(value.length() - 1) == 'a';
                })
                .count();
        long endTime = System.currentTimeMillis();
        // Reader: 24820 ms
        // BufferedReader: 10257 ms
        // BufferedReader / initial row array size: 9372 ms

        System.out.println("Total time: " + (endTime - startTime) + " ms");

        // then
        assertThat(nrAs).isEqualTo(38462L);
    }

    public void test1() throws IOException {

        // given
        TextValueSupplier textValueSupplier = new TextValueSupplier();

        long nrRows = 1_000_000;
        int nrTextColumns = 100;
        Writer out = new FileWriter(new File("/home/paul/test.csv"));
        Stream<List<String>> data = LongStream
                .range(1, nrRows)
                .mapToObj(rowNr -> {
                    String textValue = textValueSupplier.get();
                    String[] row = new String[nrTextColumns + 2];
                    Arrays.fill(row, textValue);
                    row[0] = String.valueOf(rowNr);
                    row[row.length-1] = "hi";
                    return List.of(row);
                });

        CsvWriter csvWriter = new CsvWriter(new CsvConfig().withFieldSeparator('|').withQuoteValues(false).withRowSeparatorChars("\n"));

        // when
        csvWriter.writeCsv(
                data
                , out
        );

        out.close();
//        // then
//        assertThat(out.toString()).isEqualTo(
//                "\"1\"|\"a\"|\"\"\"hi\"\"\"\n"
//                        + "\"2\"|\"b\"|\"\"\"hi\"\"\"\n");
    }
}
