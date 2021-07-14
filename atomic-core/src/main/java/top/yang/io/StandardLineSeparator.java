package top.yang.io;

import java.nio.charset.Charset;
import java.util.Objects;

public enum StandardLineSeparator {

    /**
     * Carriage return. This is the line ending used on Macos 9 and earlier.
     */
    CR("\r"),

    /**
     * Carriage return followed by line feed. This is the line ending used on Windows.
     */
    CRLF("\r\n"),

    /**
     * Line feed. This is the line ending used on Linux and Macos X and later.
     */
    LF("\n");

    private final String lineSeparator;

    /**
     * Constructs a new instance for a non-null line separator.
     *
     * @param lineSeparator a non-null line separator.
     */
    StandardLineSeparator(final String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator, "lineSeparator");
    }

    /**
     * Gets the bytes for this instance encoded using the given Charset.
     *
     * @param charset the encoding Charset.
     * @return the bytes for this instance encoded using the given Charset.
     */
    public byte[] getBytes(final Charset charset) {
        return lineSeparator.getBytes(charset);
    }

    /**
     * Gets the String value of this instance.
     *
     * @return the String value of this instance.
     */
    public String getString() {
        return lineSeparator;
    }
}