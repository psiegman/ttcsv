package nl.siegmann.ttcsv.test;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Generates a sequence of texts using the alphabet as digits.
 *
 * Sample output:
 * - a
 * - b
 * - c
 * ...
 * - z
 * - aa
 * - ab
 * etc
 */
public class TextValueSupplier implements Supplier<String> {

    private static final char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private char[] currentLetters;

    public TextValueSupplier() {
        this(1);
    }

    public TextValueSupplier(int nrStartLetters) {
        this.currentLetters = new char[nrStartLetters];
        Arrays.fill(currentLetters, (char) ((int) letters[0] - 1));
    }

    @Override
    public String get() {
        int firstNonMax = getFirstNonMax();
        if (firstNonMax >= 0) {
            currentLetters[firstNonMax] = (char) ((int) currentLetters[firstNonMax] + 1);
            if (firstNonMax < currentLetters.length - 1) {
                Arrays.fill(currentLetters, firstNonMax + 1, currentLetters.length, letters[0]);
            }
        } else {
            currentLetters = new char[currentLetters.length + 1];
            Arrays.fill(currentLetters, letters[0]);
        }
        return new String(currentLetters);
    }

    private int getFirstNonMax() {
        for (int i = currentLetters.length - 1; i >= 0; i--) {
            if (currentLetters[i] != letters[letters.length - 1]) {
                return i;
            }
        }
        return -1;
    }
}
