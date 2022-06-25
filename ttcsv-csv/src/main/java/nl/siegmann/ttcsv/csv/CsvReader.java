package nl.siegmann.ttcsv.csv;

import nl.siegmann.ttcsv.util.StreamUtil;

import java.io.Reader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Converts a Reader containing data in csv format to stream of list of strings.
 * <p>
 *     Configuring the csv format. For this @see nl.siegmann.ttcsv.csv.CsvConfig.
 * </p>
 * <p>
 * Thread-safety
 * This reader can be used many times to read different csv files with the same config.
 * However, if you want to read a csv file with a difference config (different separator for instance) you have to create
 * a new CSVReader instance.
 */
public class CsvReader implements Function<Reader, Stream<List<String>>> {

    private final CsvConfig csvConfig;

    /**
     * Creates a CsvReader with all the default settings of CsvConfig.
     *
     * @see nl.siegmann.ttcsv.csv.CsvConfig
     */
    public CsvReader() {
        this(new CsvConfig());
    }

    public CsvReader(CsvConfig csvConfig) {
        this.csvConfig = csvConfig;
    }

    @Override
    public Stream<List<String>> apply(Reader reader) {
        CsvIterator csvIterator = new CsvIterator(csvConfig, reader);
        return StreamUtil.toStream(csvIterator);
    }
}
