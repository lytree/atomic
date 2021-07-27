package top.yang.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import top.yang.io.Output.NullOutputStream;
import top.yang.string.CharsetsUtils;

/**
 * @author PrideYang
 */
public class IOUtils {

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
   * LF char.
   *
   * @since 2.9.0
   */
  public static final int LF = '\n';
  /**
   * The Unix line separator string.
   *
   * @see StandardLineSeparator#LF
   */
  public static final String LINE_SEPARATOR_UNIX = StandardLineSeparator.LF.getString();

  /**
   * The Windows line separator string.
   *
   * @see StandardLineSeparator#CRLF
   */
  public static final String LINE_SEPARATOR_WINDOWS = StandardLineSeparator.CRLF.getString();

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
   * Large streams (over 2GB) will return a chars copied value of {@code -1} after the copy has completed since the correct number of chars cannot be returned as an int. For large
   * streams use the {@code copyLarge(Reader, Writer)} method.
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
   * Note that the implementation uses {@link #skip(Reader, long)}. This means that the method may be considerably less efficient than using the actual skip implementation, this is
   * done to guarantee that the correct number of characters are skipped.
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
   * Skips bytes from an input byte stream. This implementation guarantees that it will read as many bytes as possible before giving up; this may not always be the case for skip()
   * implementations in subclasses of {@link InputStream}.
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
   * @param path 读取文件路径
   * @return 文件的字节流
   */
  public static byte[] file2byte(String path) throws IOException {
    FileInputStream in = new FileInputStream(new File(path));
    byte[] data = new byte[in.available()];
    in.read(data);
    in.close();
    return data;
  }

  /**
   * @param path 文件路径
   * @param data 文件数据
   * @throws IOException
   */
  public static void byte2file(String path, byte[] data) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(new File(path));
    outputStream.write(data);
    outputStream.close();
  }

  //按行读取文件成list

  /**
   * @param path    读取文件路径
   * @param encoder 文件编码
   * @return 数据list
   */

  public static ArrayList<String> file2list(String path, String encoder) throws IOException {
    ArrayList<String> alline = new ArrayList<String>();
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoder));
    String str = new String();
    while ((str = in.readLine()) != null) {
      alline.add(str);
    }
    in.close();
    return alline;
  }

  //输出list到文件
  public static void list2file(String path, ArrayList<String> data, String encoder)
      throws IOException {
    BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(path), encoder));
    for (String str : data) {
      out.write(str);
      out.newLine();
    }
    out.flush();
    out.close();
  }


  /**
   * //从标准输入中读入
   *
   * @return 返回输入内容
   * @throws IOException
   */
  public static String system2str() throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    return stdin.readLine();
  }


  /**
   * 读取文件成字符串
   *
   * @param path    文件路径
   * @param encoder 编码
   * @return 读取文件
   * @throws IOException
   */
  public static String file2str(String path, String encoder) throws IOException {
    StringBuilder sb = new StringBuilder();

    BufferedReader in = new BufferedReader(
        new InputStreamReader(new FileInputStream(path), encoder));
    String str = new String();
    while ((str = in.readLine()) != null) {
      sb.append(str);
    }
    in.close();
    return sb.toString();
  }

  /**
   * @param path    文件路径
   * @param data    输出数据
   * @param encoder 输出编码
   * @throws IOException
   */
  public static void str2file(String path, String data, String encoder) throws IOException {
    BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(path), encoder));
    out.write(data);
    out.flush();
    out.close();

  }
}
