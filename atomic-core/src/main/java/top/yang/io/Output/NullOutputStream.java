package top.yang.io.Output;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author PrideYang
 */
public class NullOutputStream extends OutputStream {
    /**
     * The singleton instance.
     *
     * @since 2.12.0
     */
    public static final NullOutputStream INSTANCE = new NullOutputStream();

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b The bytes to write
     * @param off The start offset
     * @param len The number of bytes to write
     */
    @Override
    public void write(final byte[] b, final int off, final int len) {
        // To /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b The byte to write
     */
    @Override
    public void write(final int b) {
        // To /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b The bytes to write
     * @throws IOException never
     */
    @Override
    public void write(final byte[] b) throws IOException {
        // To /dev/null
    }
}
