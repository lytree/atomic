package top.yang.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import org.checkerframework.checker.units.qual.C;
import top.yang.collections.ArrayUtils;
import top.yang.io.function.IOConsumer;
import top.yang.io.output.NullOutputStream;
import top.yang.io.output.StringBuilderWriter;
import top.yang.string.StringUtils;
import top.yang.string.CharsetsUtils;

/**
 * @author PrideYang
 */
public class IOUtils {

    /**
     * The default buffer size to use for the skip() methods.
     */
    private static final int SKIP_BUFFER_SIZE = 2048;

    /**
     * The current set global allocation limit override, -1 means limits are applied per record type.
     */
    private static int BYTE_ARRAY_MAX_OVERRIDE = -1;


    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";
    /**
     * CR char.
     *
     * @since 2.9.0
     */
    public static final int CR = '\r';

    /**
     * The default buffer size ({@value}) to use in copy methods.
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * The system directory separator character.
     */
    public static final char DIR_SEPARATOR = File.separatorChar;

    /**
     * The Unix directory separator character.
     */
    public static final char DIR_SEPARATOR_UNIX = '/';

    /**
     * The Windows directory separator character.
     */
    public static final char DIR_SEPARATOR_WINDOWS = '\\';

    /**
     * A singleton empty byte array.
     *
     * @since 2.9.0
     */
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /**
     * Represents the end-of-file (or stream).
     *
     * @since 2.5 (made public)
     */
    public static final int EOF = -1;


    /**
     * Internal byte array buffer.
     */
    private static final ThreadLocal<byte[]> SKIP_BYTE_BUFFER = ThreadLocal
            .withInitial(IOUtils::byteArray);

    /**
     * Internal byte array buffer.
     */
    private static final ThreadLocal<char[]> SKIP_CHAR_BUFFER = ThreadLocal
            .withInitial(IOUtils::charArray);

    /**
     * Returns the given InputStream if it is already a {@link BufferedInputStream}, otherwise creates a BufferedInputStream from the given InputStream.
     *
     * @param inputStream the InputStream to wrap or return (not null)
     * @return the given InputStream or a new {@link BufferedInputStream} for the given InputStream
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    @SuppressWarnings("resource") // parameter null check
    public static BufferedInputStream buffer(final InputStream inputStream) {
        // reject null early on rather than waiting for IO operation to fail
        // not checked by BufferedInputStream
        Objects.requireNonNull(inputStream, "inputStream");
        return inputStream instanceof BufferedInputStream ?
                (BufferedInputStream) inputStream : new BufferedInputStream(inputStream);
    }

    /**
     * Returns the given InputStream if it is already a {@link BufferedInputStream}, otherwise creates a BufferedInputStream from the given InputStream.
     *
     * @param inputStream the InputStream to wrap or return (not null)
     * @param size        the buffer size, if a new BufferedInputStream is created.
     * @return the given InputStream or a new {@link BufferedInputStream} for the given InputStream
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    @SuppressWarnings("resource") // parameter null check
    public static BufferedInputStream buffer(final InputStream inputStream, final int size) {
        // reject null early on rather than waiting for IO operation to fail
        // not checked by BufferedInputStream
        Objects.requireNonNull(inputStream, "inputStream");
        return inputStream instanceof BufferedInputStream ?
                (BufferedInputStream) inputStream : new BufferedInputStream(inputStream, size);
    }

    /**
     * Returns the given OutputStream if it is already a {@link BufferedOutputStream}, otherwise creates a BufferedOutputStream from the given OutputStream.
     *
     * @param outputStream the OutputStream to wrap or return (not null)
     * @return the given OutputStream or a new {@link BufferedOutputStream} for the given OutputStream
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    @SuppressWarnings("resource") // parameter null check
    public static BufferedOutputStream buffer(final OutputStream outputStream) {
        // reject null early on rather than waiting for IO operation to fail
        // not checked by BufferedInputStream
        Objects.requireNonNull(outputStream, "outputStream");
        return outputStream instanceof BufferedOutputStream ?
                (BufferedOutputStream) outputStream : new BufferedOutputStream(outputStream);
    }

    /**
     * Returns the given OutputStream if it is already a {@link BufferedOutputStream}, otherwise creates a BufferedOutputStream from the given OutputStream.
     *
     * @param outputStream the OutputStream to wrap or return (not null)
     * @param size         the buffer size, if a new BufferedOutputStream is created.
     * @return the given OutputStream or a new {@link BufferedOutputStream} for the given OutputStream
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    @SuppressWarnings("resource") // parameter null check
    public static BufferedOutputStream buffer(final OutputStream outputStream, final int size) {
        // reject null early on rather than waiting for IO operation to fail
        // not checked by BufferedInputStream
        Objects.requireNonNull(outputStream, "outputStream");
        return outputStream instanceof BufferedOutputStream ?
                (BufferedOutputStream) outputStream : new BufferedOutputStream(outputStream, size);
    }

    /**
     * Returns the given reader if it is already a {@link BufferedReader}, otherwise creates a BufferedReader from the given reader.
     *
     * @param reader the reader to wrap or return (not null)
     * @return the given reader or a new {@link BufferedReader} for the given reader
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    public static BufferedReader buffer(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * Returns the given reader if it is already a {@link BufferedReader}, otherwise creates a BufferedReader from the given reader.
     *
     * @param reader the reader to wrap or return (not null)
     * @param size   the buffer size, if a new BufferedReader is created.
     * @return the given reader or a new {@link BufferedReader} for the given reader
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    public static BufferedReader buffer(final Reader reader, final int size) {
        return reader instanceof BufferedReader ? (BufferedReader) reader
                : new BufferedReader(reader, size);
    }

    /**
     * Returns the given Writer if it is already a {@link BufferedWriter}, otherwise creates a BufferedWriter from the given Writer.
     *
     * @param writer the Writer to wrap or return (not null)
     * @return the given Writer or a new {@link BufferedWriter} for the given Writer
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    public static BufferedWriter buffer(final Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    /**
     * Returns the given Writer if it is already a {@link BufferedWriter}, otherwise creates a BufferedWriter from the given Writer.
     *
     * @param writer the Writer to wrap or return (not null)
     * @param size   the buffer size, if a new BufferedWriter is created.
     * @return the given Writer or a new {@link BufferedWriter} for the given Writer
     * @throws NullPointerException if the input parameter is null
     * @since 2.5
     */
    public static BufferedWriter buffer(final Writer writer, final int size) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer
                : new BufferedWriter(writer, size);
    }

    /**
     * Returns a new byte array of size {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @return a new byte array of size {@link #DEFAULT_BUFFER_SIZE}.
     * @since 2.9.0
     */
    public static byte[] byteArray() {
        return byteArray(DEFAULT_BUFFER_SIZE);
    }

    /**
     * Returns a new byte array of the given size.
     * <p>
     * TODO Consider guarding or warning against large allocations...
     *
     * @param size array size.
     * @return a new byte array of the given size.
     * @since 2.9.0
     */
    public static byte[] byteArray(final int size) {
        return new byte[size];
    }

    /**
     * Returns a new char array of size {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @return a new char array of size {@link #DEFAULT_BUFFER_SIZE}.
     * @since 2.9.0
     */
    private static char[] charArray() {
        return charArray(DEFAULT_BUFFER_SIZE);
    }

    /**
     * Returns a new char array of the given size.
     * <p>
     * TODO Consider guarding or warning against large allocations...
     *
     * @param size array size.
     * @return a new char array of the given size.
     * @since 2.9.0
     */
    private static char[] charArray(final int size) {
        return new char[size];
    }

    /**
     * Consumes bytes from a {@code InputStream} and ignores them.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * </p>
     *
     * @param input the {@code InputStream} to read.
     * @return the number of bytes copied. or {@code 0} if {@code input is null}.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 2.8.0
     */
    public static long consume(final InputStream input)
            throws IOException {
        return copyLarge(input, NullOutputStream.INSTANCE, getByteArray());
    }


    /**
     * Copies chars from a {@code Reader} to bytes on an {@code OutputStream} using the specified character encoding, and calling flush.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * </p>
     * <p>
     * Due to the implementation of OutputStreamWriter, this method performs a flush.
     * </p>
     * <p>
     * This method uses {@link OutputStreamWriter}.
     * </p>
     *
     * @param reader        the {@code Reader} to read from
     * @param output        the {@code OutputStream} to write to
     * @param outputCharset the charset to use for the OutputStream, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void copy(final Reader reader, final OutputStream output,
            final Charset outputCharset)
            throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(output,
                CharsetsUtils.toCharset(outputCharset));
        copy(reader, writer);
        // XXX Unless anyone is planning on rewriting OutputStreamWriter,
        // we have to flush here.
        writer.flush();
    }

    /**
     * Copies chars from a {@code Reader} to bytes on an {@code OutputStream} using the specified character encoding, and calling flush.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * Due to the implementation of OutputStreamWriter, this method performs a flush.
     * <p>
     * This method uses {@link OutputStreamWriter}.
     *
     * @param reader            the {@code Reader} to read from
     * @param output            the {@code OutputStream} to write to
     * @param outputCharsetName the name of the requested charset for the OutputStream, null means platform default
     * @throws NullPointerException                         if the input or output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void copy(final Reader reader, final OutputStream output,
            final String outputCharsetName)
            throws IOException {
        copy(reader, output, CharsetsUtils.toCharset(outputCharsetName));
    }

    /**
     * Copies bytes from an {@code InputStream} to an {@code OutputStream}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of {@code -1} after the copy has completed since the correct number of bytes cannot be returned as an int. For
     * large streams use the {@code copyLarge(InputStream, OutputStream)} method.
     * </p>
     *
     * @param inputStream  the {@code InputStream} to read.
     * @param outputStream the {@code OutputStream} to write.
     * @return the number of bytes copied, or -1 if greater than {@link Integer#MAX_VALUE}.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 1.1
     */
    public static int copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final long count = copyLarge(inputStream, outputStream);
        if (count > Integer.MAX_VALUE) {
            return EOF;
        }
        return (int) count;
    }

    /**
     * Copies bytes from an {@code InputStream} to an {@code OutputStream} using an internal buffer of the given size.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     *
     * @param inputStream  the {@code InputStream} to read.
     * @param outputStream the {@code OutputStream} to write to
     * @param bufferSize   the bufferSize used to copy from the input to the output
     * @return the number of bytes copied.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 2.5
     */
    public static long copy(final InputStream inputStream, final OutputStream outputStream,
            final int bufferSize)
            throws IOException {
        return copyLarge(inputStream, outputStream, IOUtils.byteArray(bufferSize));
    }

    /**
     * Copies chars from a {@code Reader} to a {@code Writer}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * <p>
     * Large streams (over 2GB) will return a chars copied value of {@code -1} after the copy has completed since the correct number of chars cannot be returned as an int. For
     * large streams use the {@code copyLarge(Reader, Writer)} method.
     *
     * @param reader the {@code Reader} to read.
     * @param writer the {@code Writer} to write.
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static int copy(final Reader reader, final Writer writer) throws IOException {
        final long count = copyLarge(reader, writer);
        if (count > Integer.MAX_VALUE) {
            return EOF;
        }
        return (int) count;
    }

    /**
     * Copies bytes from a {@code URL} to an {@code OutputStream}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * </p>
     *
     * @param url  the {@code URL} to read.
     * @param file the {@code OutputStream} to write.
     * @return the number of bytes copied.
     * @throws NullPointerException if the URL is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 2.9.0
     */
    public static long copy(final URL url, final File file) throws IOException {
        try (OutputStream outputStream = Files
                .newOutputStream(Objects.requireNonNull(file, "file").toPath())) {
            return copy(url, outputStream);
        }
    }

    /**
     * Copies bytes from a {@code URL} to an {@code OutputStream}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * </p>
     *
     * @param url          the {@code URL} to read.
     * @param outputStream the {@code OutputStream} to write.
     * @return the number of bytes copied.
     * @throws NullPointerException if the URL is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 2.9.0
     */
    public static long copy(final URL url, final OutputStream outputStream) throws IOException {
        try (InputStream inputStream = Objects.requireNonNull(url, "url").openStream()) {
            return copyLarge(inputStream, outputStream);
        }
    }


    /**
     * Copies bytes from an {@code InputStream} to chars on a {@code Writer} using the specified character encoding.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * This method uses {@link InputStreamReader}.
     * </p>
     *
     * @param input        the {@code InputStream} to read from
     * @param writer       the {@code Writer} to write to
     * @param inputCharset the charset to use for the input stream, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void copy(final InputStream input, final Writer writer, final Charset inputCharset)
            throws IOException {
        final InputStreamReader reader = new InputStreamReader(input, CharsetsUtils.toCharset(inputCharset));
        copy(reader, writer);
    }

    /**
     * Copies bytes from an {@code InputStream} to chars on a {@code Writer} using the specified character encoding.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     * <p>
     * This method uses {@link InputStreamReader}.
     * </p>
     *
     * @param input            the {@code InputStream} to read from
     * @param writer           the {@code Writer} to write to
     * @param inputCharsetName the name of the requested charset for the InputStream, null means platform default
     * @throws NullPointerException                         if the input or output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void copy(final InputStream input, final Writer writer, final String inputCharsetName)
            throws IOException {
        copy(input, writer, CharsetsUtils.toCharset(inputCharsetName));
    }


    /**
     * Copies chars from a {@code Reader} to a {@code Appendable}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * </p>
     * <p>
     * Large streams (over 2GB) will return a chars copied value of {@code -1} after the copy has completed since the correct number of chars cannot be returned as an int. For
     * large streams use the {@code copyLarge(Reader, Writer)} method.
     * </p>
     *
     * @param reader the {@code Reader} to read from
     * @param output the {@code Appendable} to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.7
     */
    public static long copy(final Reader reader, final Appendable output) throws IOException {
        return copy(reader, output, CharBuffer.allocate(DEFAULT_BUFFER_SIZE));
    }

    /**
     * Copies chars from a {@code Reader} to an {@code Appendable}.
     * <p>
     * This method uses the provided buffer, so there is no need to use a {@code BufferedReader}.
     * </p>
     *
     * @param reader the {@code Reader} to read from
     * @param output the {@code Appendable} to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.7
     */
    public static long copy(final Reader reader, final Appendable output, final CharBuffer buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = reader.read(buffer))) {
            buffer.flip();
            output.append(buffer, 0, n);
            count += n;
        }
        return count;
    }


    /**
     * Copies bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * </p>
     *
     * @param inputStream  the {@code InputStream} to read.
     * @param outputStream the {@code OutputStream} to write.
     * @return the number of bytes copied.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 1.3
     */
    public static long copyLarge(final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        return copy(inputStream, outputStream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}.
     * <p>
     * This method uses the provided buffer, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     *
     * @param inputStream  the {@code InputStream} to read.
     * @param outputStream the {@code OutputStream} to write.
     * @param buffer       the buffer to use for the copy
     * @return the number of bytes copied.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @since 2.2
     */
    @SuppressWarnings("resource") // streams are closed by the caller.
    public static long copyLarge(final InputStream inputStream, final OutputStream outputStream,
            final byte[] buffer)
            throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        Objects.requireNonNull(outputStream, "outputStream");
        long count = 0;
        int n;
        while (EOF != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copies some or all bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}, optionally skipping input bytes.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}. This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input       the {@code InputStream} to read from
     * @param output      the {@code OutputStream} to write to
     * @param inputOffset : number of bytes to skip from input before copying -ve values are ignored
     * @param length      : number of bytes to copy. -ve means all
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final InputStream input, final OutputStream output,
            final long inputOffset,
            final long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, getByteArray());
    }

    /**
     * Copies some or all bytes from a large (over 2GB) {@code InputStream} to an {@code OutputStream}, optionally skipping input bytes.
     * <p>
     * This method uses the provided buffer, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}. This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input       the {@code InputStream} to read from
     * @param output      the {@code OutputStream} to write to
     * @param inputOffset : number of bytes to skip from input before copying -ve values are ignored
     * @param length      : number of bytes to copy. -ve means all
     * @param buffer      the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final InputStream input, final OutputStream output,
            final long inputOffset, final long length, final byte[] buffer) throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;
        if (length > 0 && length < bufferLength) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }
        return totalRead;
    }

    /**
     * Copies chars from a large (over 2GB) {@code Reader} to a {@code Writer}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param reader the {@code Reader} to source.
     * @param writer the {@code Writer} to target.
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(final Reader reader, final Writer writer) throws IOException {
        return copyLarge(reader, writer, getCharArray());
    }

    /**
     * Copies chars from a large (over 2GB) {@code Reader} to a {@code Writer}.
     * <p>
     * This method uses the provided buffer, so there is no need to use a {@code BufferedReader}.
     * <p>
     *
     * @param reader the {@code Reader} to source.
     * @param writer the {@code Writer} to target.
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader reader, final Writer writer, final char[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copies some or all chars from a large (over 2GB) {@code InputStream} to an {@code OutputStream}, optionally skipping input chars.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedReader}.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param reader      the {@code Reader} to read from
     * @param writer      the {@code Writer} to write to
     * @param inputOffset : number of chars to skip from input before copying -ve values are ignored
     * @param length      : number of chars to copy. -ve means all
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader reader, final Writer writer, final long inputOffset,
            final long length)
            throws IOException {
        return copyLarge(reader, writer, inputOffset, length, getCharArray());
    }

    /**
     * Copies some or all chars from a large (over 2GB) {@code InputStream} to an {@code OutputStream}, optionally skipping input chars.
     * <p>
     * This method uses the provided buffer, so there is no need to use a {@code BufferedReader}.
     * <p>
     *
     * @param reader      the {@code Reader} to read from
     * @param writer      the {@code Writer} to write to
     * @param inputOffset : number of chars to skip from input before copying -ve values are ignored
     * @param length      : number of chars to copy. -ve means all
     * @param buffer      the buffer to be used for the copy
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader reader, final Writer writer, final long inputOffset,
            final long length,
            final char[] buffer)
            throws IOException {
        if (inputOffset > 0) {
            skipFully(reader, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        int bytesToRead = buffer.length;
        if (length > 0 && length < buffer.length) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = reader.read(buffer, 0, bytesToRead))) {
            writer.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, buffer.length);
            }
        }
        return totalRead;
    }

    /**
     * Gets the thread local byte array.
     *
     * @return the thread local byte array.
     */
    static byte[] getByteArray() {
        return SKIP_BYTE_BUFFER.get();
    }

    /**
     * Gets the thread local char array.
     *
     * @return the thread local char array.
     */
    static char[] getCharArray() {
        return SKIP_CHAR_BUFFER.get();
    }

    /**
     * Returns the length of the given array in a null-safe manner.
     *
     * @param array an array or null
     * @return the array length -- or 0 if the given array is null.
     * @since 2.7
     */
    public static int length(final byte[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * Returns the length of the given array in a null-safe manner.
     *
     * @param array an array or null
     * @return the array length -- or 0 if the given array is null.
     * @since 2.7
     */
    public static int length(final char[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * Returns the length of the given CharSequence in a null-safe manner.
     *
     * @param csq a CharSequence or null
     * @return the CharSequence length -- or 0 if the given CharSequence is null.
     * @since 2.7
     */
    public static int length(final CharSequence csq) {
        return csq == null ? 0 : csq.length();
    }

    /**
     * Returns the length of the given array in a null-safe manner.
     *
     * @param array an array or null
     * @return the array length -- or 0 if the given array is null.
     * @since 2.7
     */
    public static int length(final Object[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * Closes the given {@link Closeable} as a null-safe operation.
     *
     * @param closeable The resource to close, may be null.
     * @throws IOException if an I/O error occurs.
     * @since 2.7
     */
    public static void close(final Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    /**
     * Closes the given {@link Closeable}s as null-safe operations.
     *
     * @param closeables The resource(s) to close, may be null.
     * @throws IOException if an I/O error occurs.
     * @since 2.8.0
     */
    public static void close(final Closeable... closeables) throws IOException {
        IOConsumer.forEach(closeables, IOUtils::close);
    }

    /**
     * Closes the given {@link Closeable} as a null-safe operation.
     *
     * @param closeable The resource to close, may be null.
     * @param consumer  Consume the IOException thrown by {@link Closeable#close()}.
     * @throws IOException if an I/O error occurs.
     * @since 2.7
     */
    public static void close(final Closeable closeable, final IOConsumer<IOException> consumer) throws IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
            }
        }
    }

    /**
     * Closes a URLConnection.
     *
     * @param conn the connection to close.
     * @since 2.4
     */
    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    /**
     * Closes a {@code Closeable} unconditionally.
     *
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * <p>
     * Example code:
     * </p>
     * <pre>
     * Closeable closeable = null;
     * try {
     *     closeable = new FileReader(&quot;foo.txt&quot;);
     *     // process closeable
     *     closeable.close();
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(closeable);
     * }
     * </pre>
     * <p>
     * Closing all streams:
     * </p>
     * <pre>
     * try {
     *     return IOUtils.copy(inputStream, outputStream);
     * } finally {
     *     IOUtils.closeQuietly(inputStream);
     *     IOUtils.closeQuietly(outputStream);
     * }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param closeable the objects to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     * @since 2.0
     */
    public static void closeQuietly(final Closeable closeable) {
        closeQuietly(closeable, null);
    }

    /**
     * Closes a {@code Closeable} unconditionally.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored.
     * <p>
     * This is typically used in finally blocks to ensure that the closeable is closed even if an Exception was thrown before the normal close statement was reached.
     * <br>
     * <b>It should not be used to replace the close statement(s)
     * which should be present for the non-exceptional case.</b>
     * <br>
     * It is only intended to simplify tidying up where normal processing has already failed and reporting close failure as well is not necessary or useful.
     * <p>
     * Example code:
     * </p>
     * <pre>
     * Closeable closeable = null;
     * try {
     *     closeable = new FileReader(&quot;foo.txt&quot;);
     *     // processing using the closeable; may throw an Exception
     *     closeable.close(); // Normal close - exceptions not ignored
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     <b>IOUtils.closeQuietly(closeable); // In case normal close was skipped due to Exception</b>
     * }
     * </pre>
     * <p>
     * Closing all streams:
     * <br>
     * <pre>
     * try {
     *     return IOUtils.copy(inputStream, outputStream);
     * } finally {
     *     IOUtils.closeQuietly(inputStream, outputStream);
     * }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param closeables the objects to close, may be null or already closed
     * @see #closeQuietly(Closeable)
     * @see Throwable#addSuppressed(java.lang.Throwable)
     * @since 2.5
     */
    public static void closeQuietly(final Closeable... closeables) {
        if (closeables != null) {
            Arrays.stream(closeables).forEach(IOUtils::closeQuietly);
        }
    }

    /**
     * Closes the given {@link Closeable} as a null-safe operation while consuming IOException by the given {@code consumer}.
     *
     * @param closeable The resource to close, may be null.
     * @param consumer  Consumes the IOException thrown by {@link Closeable#close()}.
     * @since 2.7
     */
    public static void closeQuietly(final Closeable closeable, final Consumer<IOException> consumer) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
            }
        }
    }

    /**
     * Closes an {@code InputStream} unconditionally.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   byte[] data = new byte[1024];
     *   InputStream in = null;
     *   try {
     *       in = new FileInputStream("foo.txt");
     *       in.read(data);
     *       in.close(); //close errors are handled
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(in);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param input the InputStream to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    public static void closeQuietly(final InputStream input) {
        closeQuietly((Closeable) input);
    }

    /**
     * Closes an {@code OutputStream} unconditionally.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     * byte[] data = "Hello, World".getBytes();
     *
     * OutputStream out = null;
     * try {
     *     out = new FileOutputStream("foo.txt");
     *     out.write(data);
     *     out.close(); //close errors are handled
     * } catch (IOException e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(out);
     * }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param output the OutputStream to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    public static void closeQuietly(final OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * Closes an {@code Reader} unconditionally.
     * <p>
     * Equivalent to {@link Reader#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   char[] data = new char[1024];
     *   Reader in = null;
     *   try {
     *       in = new FileReader("foo.txt");
     *       in.read(data);
     *       in.close(); //close errors are handled
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(in);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param reader the Reader to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    public static void closeQuietly(final Reader reader) {
        closeQuietly((Closeable) reader);
    }

    /**
     * Closes a {@code Selector} unconditionally.
     * <p>
     * Equivalent to {@link Selector#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   Selector selector = null;
     *   try {
     *       selector = Selector.open();
     *       // process socket
     *
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(selector);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param selector the Selector to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     * @since 2.2
     */
    public static void closeQuietly(final Selector selector) {
        closeQuietly((Closeable) selector);
    }

    /**
     * Closes a {@code ServerSocket} unconditionally.
     * <p>
     * Equivalent to {@link ServerSocket#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   ServerSocket socket = null;
     *   try {
     *       socket = new ServerSocket();
     *       // process socket
     *       socket.close();
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(socket);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param serverSocket the ServerSocket to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     * @since 2.2
     */
    public static void closeQuietly(final ServerSocket serverSocket) {
        closeQuietly((Closeable) serverSocket);
    }

    /**
     * Closes a {@code Socket} unconditionally.
     * <p>
     * Equivalent to {@link Socket#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   Socket socket = null;
     *   try {
     *       socket = new Socket("http://www.foo.com/", 80);
     *       // process socket
     *       socket.close();
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(socket);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param socket the Socket to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     * @since 2.0
     */
    public static void closeQuietly(final Socket socket) {
        closeQuietly((Closeable) socket);
    }

    /**
     * Closes an {@code Writer} unconditionally.
     * <p>
     * Equivalent to {@link Writer#close()}, except any exceptions will be ignored. This is typically used in finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <pre>
     *   Writer out = null;
     *   try {
     *       out = new StringWriter();
     *       out.write("Hello World");
     *       out.close(); //close errors are handled
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(out);
     *   }
     * </pre>
     * <p>
     * Also consider using a try-with-resources statement where appropriate.
     * </p>
     *
     * @param writer the Writer to close, may be null or already closed
     * @see Throwable#addSuppressed(java.lang.Throwable)
     */
    public static void closeQuietly(final Writer writer) {
        closeQuietly((Closeable) writer);
    }

    /**
     * Returns an Iterator for the lines in an {@code InputStream}, using the character encoding specified (or default encoding if null).
     * <p>
     * {@code LineIterator} holds a reference to the open {@code InputStream} specified here. When you have finished with the iterator you should close the stream to free internal
     * resources. This can be done by using a try-with-resources block, closing the stream directly, or by calling {@link LineIterator#close()}.
     * </p>
     * <p>
     * The recommended usage pattern is:
     * </p>
     * <pre>
     * try {
     *   LineIterator it = IOUtils.lineIterator(stream, charset);
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(stream);
     * }
     * </pre>
     *
     * @param input   the {@code InputStream} to read from, not null
     * @param charset the charset to use, null means platform default
     * @return an Iterator of the lines in the reader, never null
     * @throws IllegalArgumentException if the input is null
     * @since 2.3
     */
    public static LineIterator lineIterator(final InputStream input, final Charset charset) {
        return new LineIterator(new InputStreamReader(input, CharsetsUtils.toCharset(charset)));
    }

    /**
     * Returns an Iterator for the lines in an {@code InputStream}, using the character encoding specified (or default encoding if null).
     * <p>
     * {@code LineIterator} holds a reference to the open {@code InputStream} specified here. When you have finished with the iterator you should close the stream to free internal
     * resources. This can be done by using a try-with-resources block, closing the stream directly, or by calling {@link LineIterator#close()}.
     * </p>
     * <p>
     * The recommended usage pattern is:
     * </p>
     * <pre>
     * try {
     *   LineIterator it = IOUtils.lineIterator(stream, "UTF-8");
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(stream);
     * }
     * </pre>
     *
     * @param input       the {@code InputStream} to read from, not null
     * @param charsetName the encoding to use, null means platform default
     * @return an Iterator of the lines in the reader, never null
     * @throws IllegalArgumentException                     if the input is null
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.2
     */
    public static LineIterator lineIterator(final InputStream input, final String charsetName) {
        return lineIterator(input, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Returns an Iterator for the lines in a {@code Reader}.
     * <p>
     * {@code LineIterator} holds a reference to the open {@code Reader} specified here. When you have finished with the iterator you should close the reader to free internal
     * resources. This can be done by using a try-with-resources block, closing the reader directly, or by calling {@link LineIterator#close()}.
     * </p>
     * <p>
     * The recommended usage pattern is:
     * </p>
     * <pre>
     * try {
     *   LineIterator it = IOUtils.lineIterator(reader);
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(reader);
     * }
     * </pre>
     *
     * @param reader the {@code Reader} to read from, not null
     * @return an Iterator of the lines in the reader, never null
     * @throws IllegalArgumentException if the reader is null
     * @since 1.2
     */
    public static LineIterator lineIterator(final Reader reader) {
        return new LineIterator(reader);
    }

    /**
     * Writes bytes from a {@code byte[]} to an {@code OutputStream}.
     *
     * @param data   the byte array to write, do not modify during output, null ignored
     * @param output the {@code OutputStream} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static void write(final byte[] data, final OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }


    /**
     * Writes bytes from a {@code byte[]} to chars on a {@code Writer} using the specified character encoding.
     * <p>
     * This method uses {@link String#String(byte[], String)}.
     * </p>
     *
     * @param data    the byte array to write, do not modify during output, null ignored
     * @param writer  the {@code Writer} to write to
     * @param charset the charset to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void write(final byte[] data, final Writer writer, final Charset charset) throws IOException {
        if (data != null) {
            writer.write(new String(data, CharsetsUtils.toCharset(charset)));
        }
    }

    /**
     * Writes bytes from a {@code byte[]} to chars on a {@code Writer} using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     * <p>
     * This method uses {@link String#String(byte[], String)}.
     * </p>
     *
     * @param data        the byte array to write, do not modify during output, null ignored
     * @param writer      the {@code Writer} to write to
     * @param charsetName the name of the requested charset, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void write(final byte[] data, final Writer writer, final String charsetName) throws IOException {
        write(data, writer, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Writes chars from a {@code char[]} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * This method uses {@link String#String(char[])} and {@link String#getBytes(String)}.
     * </p>
     *
     * @param data    the char array to write, do not modify during output, null ignored
     * @param output  the {@code OutputStream} to write to
     * @param charset the charset to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void write(final char[] data, final OutputStream output, final Charset charset) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(CharsetsUtils.toCharset(charset)));
        }
    }

    /**
     * Writes chars from a {@code char[]} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     * <p>
     * This method uses {@link String#String(char[])} and {@link String#getBytes(String)}.
     * </p>
     *
     * @param data        the char array to write, do not modify during output, null ignored
     * @param output      the {@code OutputStream} to write to
     * @param charsetName the name of the requested charset, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void write(final char[] data, final OutputStream output, final String charsetName)
            throws IOException {
        write(data, output, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Writes chars from a {@code char[]} to a {@code Writer}
     *
     * @param data   the char array to write, do not modify during output, null ignored
     * @param writer the {@code Writer} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static void write(final char[] data, final Writer writer) throws IOException {
        if (data != null) {
            writer.write(data);
        }
    }


    /**
     * Writes chars from a {@code CharSequence} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * This method uses {@link String#getBytes(String)}.
     * </p>
     *
     * @param data    the {@code CharSequence} to write, null ignored
     * @param output  the {@code OutputStream} to write to
     * @param charset the charset to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void write(final CharSequence data, final OutputStream output, final Charset charset)
            throws IOException {
        if (data != null) {
            write(data.toString(), output, charset);
        }
    }

    /**
     * Writes chars from a {@code CharSequence} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     * <p>
     * This method uses {@link String#getBytes(String)}.
     * </p>
     *
     * @param data        the {@code CharSequence} to write, null ignored
     * @param output      the {@code OutputStream} to write to
     * @param charsetName the name of the requested charset, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 2.0
     */
    public static void write(final CharSequence data, final OutputStream output, final String charsetName)
            throws IOException {
        write(data, output, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Writes chars from a {@code CharSequence} to a {@code Writer}.
     *
     * @param data   the {@code CharSequence} to write, null ignored
     * @param writer the {@code Writer} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.0
     */
    public static void write(final CharSequence data, final Writer writer) throws IOException {
        if (data != null) {
            write(data.toString(), writer);
        }
    }


    /**
     * Writes chars from a {@code String} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * This method uses {@link String#getBytes(String)}.
     * </p>
     *
     * @param data    the {@code String} to write, null ignored
     * @param output  the {@code OutputStream} to write to
     * @param charset the charset to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void write(final String data, final OutputStream output, final Charset charset) throws IOException {
        if (data != null) {
            output.write(data.getBytes(CharsetsUtils.toCharset(charset)));
        }
    }

    /**
     * Writes chars from a {@code String} to bytes on an {@code OutputStream} using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     * <p>
     * This method uses {@link String#getBytes(String)}.
     * </p>
     *
     * @param data        the {@code String} to write, null ignored
     * @param output      the {@code OutputStream} to write to
     * @param charsetName the name of the requested charset, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void write(final String data, final OutputStream output, final String charsetName)
            throws IOException {
        write(data, output, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Writes chars from a {@code String} to a {@code Writer}.
     *
     * @param data   the {@code String} to write, null ignored
     * @param writer the {@code Writer} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static void write(final String data, final Writer writer) throws IOException {
        if (data != null) {
            writer.write(data);
        }
    }


    /**
     * Writes bytes from a {@code byte[]} to an {@code OutputStream} using chunked writes. This is intended for writing very large byte arrays which might otherwise cause excessive
     * memory usage if the native code has to allocate a copy.
     *
     * @param data   the byte array to write, do not modify during output, null ignored
     * @param output the {@code OutputStream} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.5
     */
    public static void writeChunked(final byte[] data, final OutputStream output)
            throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                final int chunk = Math.min(bytes, DEFAULT_BUFFER_SIZE);
                output.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    /**
     * Writes chars from a {@code char[]} to a {@code Writer} using chunked writes. This is intended for writing very large byte arrays which might otherwise cause excessive memory
     * usage if the native code has to allocate a copy.
     *
     * @param data   the char array to write, do not modify during output, null ignored
     * @param writer the {@code Writer} to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.5
     */
    public static void writeChunked(final char[] data, final Writer writer) throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                final int chunk = Math.min(bytes, DEFAULT_BUFFER_SIZE);
                writer.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    /**
     * Writes the {@code toString()} value of each item in a collection to an {@code OutputStream} line by line, using the specified character encoding and the specified line
     * ending.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param output     the {@code OutputStream} to write to, not null, not closed
     * @param charset    the charset to use, null means platform default
     * @throws NullPointerException if the output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static void writeLines(final Collection<?> lines, String lineEnding, final OutputStream output,
            final Charset charset) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = System.lineSeparator();
        }
        final Charset cs = CharsetsUtils.toCharset(charset);
        for (final Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }

    /**
     * Writes the {@code toString()} value of each item in a collection to an {@code OutputStream} line by line, using the specified character encoding and the specified line
     * ending.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * </p>
     *
     * @param lines       the lines to write, null entries produce blank lines
     * @param lineEnding  the line separator to use, null is system default
     * @param output      the {@code OutputStream} to write to, not null, not closed
     * @param charsetName the name of the requested charset, null means platform default
     * @throws NullPointerException                         if the output is null
     * @throws IOException                                  if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported.
     * @since 1.1
     */
    public static void writeLines(final Collection<?> lines, final String lineEnding,
            final OutputStream output, final String charsetName) throws IOException {
        writeLines(lines, lineEnding, output, CharsetsUtils.toCharset(charsetName));
    }

    /**
     * Writes the {@code toString()} value of each item in a collection to a {@code Writer} line by line, using the specified line ending.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param writer     the {@code Writer} to write to, not null, not closed
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    public static void writeLines(final Collection<?> lines, String lineEnding,
            final Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = System.lineSeparator();
        }
        for (final Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }


    /**
     * Skips the requested number of bytes or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link InputStream#skip(long)} may not skip as many bytes as requested (most likely because of reaching EOF).
     * <p>
     * Note that the implementation uses {@link #skip(InputStream, long)}. This means that the method may be considerably less efficient than using the actual skip implementation,
     * this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param input  stream to skip
     * @param toSkip the number of bytes to skip
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of bytes skipped was incorrect
     * @see InputStream#skip(long)
     * @since 2.0
     */
    public static void skipFully(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skips the requested number of bytes or fail if there are not enough left.
     *
     * @param input  ReadableByteChannel to skip
     * @param toSkip the number of bytes to skip
     * @throws IOException              if there is a problem reading the ReadableByteChannel
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of bytes skipped was incorrect
     * @since 2.5
     */
    public static void skipFully(final ReadableByteChannel input, final long toSkip)
            throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        final long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skips the requested number of characters or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link Reader#skip(long)} may not skip as many characters as requested (most likely because of reaching EOF).
     * <p>
     * Note that the implementation uses {@link #skip(Reader, long)}. This means that the method may be considerably less efficient than using the actual skip implementation, this
     * is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param reader stream to skip
     * @param toSkip the number of characters to skip
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException             if the number of characters skipped was incorrect
     * @see Reader#skip(long)
     * @since 2.0
     */
    public static void skipFully(final Reader reader, final long toSkip) throws IOException {
        final long skipped = skip(reader, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skips bytes from an input byte stream. This implementation guarantees that it will read as many bytes as possible before giving up; this may not always be the case for
     * skip() implementations in subclasses of {@link InputStream}.
     * <p>
     * Note that the implementation uses {@link InputStream#read(byte[], int, int)} rather than delegating to {@link InputStream#skip(long)}. This means that the method may be
     * considerably less efficient than using the actual skip implementation, this is done to guarantee that the correct number of bytes are skipped.
     * </p>
     *
     * @param input  byte stream to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see InputStream#skip(long)
     * @see <a href="https://issues.apache.org/jira/browse/IO-203">IO-203 - Add skipFully() method for
     * InputStreams</a>
     * @since 2.0
     */
    public static long skip(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize access to SKIP_BYTE_BUFFER: - we don't care if the buffer is created multiple
         * times (the data is ignored) - we always use the same size buffer, so if it it is recreated it will still be
         * OK (if the buffer size were variable, we would need to synch. to ensure some other thread did not create a
         * smaller one)
         */
        long remain = toSkip;
        while (remain > 0) {
            // See https://issues.apache.org/jira/browse/IO-203 for why we use read() rather than delegating to skip()
            final byte[] byteArray = getByteArray();
            final long n = input.read(byteArray, 0, (int) Math.min(remain, byteArray.length));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skips bytes from a ReadableByteChannel. This implementation guarantees that it will read as many bytes as possible before giving up.
     *
     * @param input  ReadableByteChannel to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws IOException              if there is a problem reading the ReadableByteChannel
     * @throws IllegalArgumentException if toSkip is negative
     * @since 2.5
     */
    public static long skip(final ReadableByteChannel input, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        final ByteBuffer skipByteBuffer = ByteBuffer
                .allocate((int) Math.min(toSkip, DEFAULT_BUFFER_SIZE));
        long remain = toSkip;
        while (remain > 0) {
            skipByteBuffer.position(0);
            skipByteBuffer.limit((int) Math.min(remain, DEFAULT_BUFFER_SIZE));
            final int n = input.read(skipByteBuffer);
            if (n == EOF) {
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skips characters from an input character stream. This implementation guarantees that it will read as many characters as possible before giving up; this may not always be the
     * case for skip() implementations in subclasses of {@link Reader}.
     * <p>
     * Note that the implementation uses {@link Reader#read(char[], int, int)} rather than delegating to {@link Reader#skip(long)}. This means that the method may be considerably
     * less efficient than using the actual skip implementation, this is done to guarantee that the correct number of characters are skipped.
     * </p>
     *
     * @param reader character stream to skip
     * @param toSkip number of characters to skip.
     * @return number of characters actually skipped.
     * @throws IOException              if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see Reader#skip(long)
     * @see <a href="https://issues.apache.org/jira/browse/IO-203">IO-203 - Add skipFully() method for
     * InputStreams</a>
     * @since 2.0
     */
    public static long skip(final Reader reader, final long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        long remain = toSkip;
        while (remain > 0) {
            // See https://issues.apache.org/jira/browse/IO-203 for why we use read() rather than delegating to skip()
            final char[] charArray = getCharArray();
            final long n = reader.read(charArray, 0, (int) Math.min(remain, charArray.length));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileUtils.getFileType(resource))) {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br> Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return DIR_SEPARATOR_UNIX == c || DIR_SEPARATOR_WINDOWS == c;
    }


    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }


    /**
     * Gets the contents of an {@code InputStream} as a String using the specified character encoding.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     *
     * @param input   the {@code InputStream} to read from
     * @param charset the charset to use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since 2.3
     */
    public static String toString(final InputStream input, final Charset charset) throws IOException {
        try (final StringBuilderWriter sw = new StringBuilderWriter()) {
            copy(input, sw, charset);
            return sw.toString();
        }
    }
}
