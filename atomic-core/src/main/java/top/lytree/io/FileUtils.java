package top.lytree.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import top.lytree.collections.ArrayUtils;
import top.lytree.lang.CharUtils;
import top.lytree.lang.CharsetUtils;
import top.lytree.exception.FileExistsException;
import top.lytree.io.file.AccumulatorPathVisitor;
import top.lytree.io.file.Counters;
import top.lytree.io.file.PathFilter;
import top.lytree.io.file.PathUtils;
import top.lytree.io.file.StandardDeleteOption;
import top.lytree.io.filefilter.FileEqualsFileFilter;
import top.lytree.io.filefilter.FileFileFilter;
import top.lytree.io.filefilter.FileFilterUtils;
import top.lytree.io.filefilter.IOFileFilter;
import top.lytree.io.filefilter.NameFileFilter;
import top.lytree.io.filefilter.SuffixFileFilter;
import top.lytree.io.filefilter.TrueFileFilter;
import top.lytree.io.function.IOConsumer;
import top.lytree.lang.StringUtils;
import top.lytree.thread.ThreadUtil;
import top.lytree.utils.Assert;
import top.lytree.utils.SystemUtils;

public class FileUtils {

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    /**
     * An empty array of type {@code File}.
     */
    public static final File[] EMPTY_FILE_ARRAY = {};

    /**
     * Instances should NOT be constructed in standard programming.
     */
    private FileUtils() { //NOSONAR

    }

    /**
     * Copies the given array and adds StandardCopyOption.COPY_ATTRIBUTES.
     *
     * @param copyOptions sorted copy options.
     * @return a new array.
     */
    private static CopyOption[] addCopyAttributes(final CopyOption... copyOptions) {
        // Make a copy first since we don't want to sort the call site's version.
        final CopyOption[] actual = Arrays.copyOf(copyOptions, copyOptions.length + 1);
        Arrays.sort(actual, 0, copyOptions.length);
        if (Arrays.binarySearch(copyOptions, 0, copyOptions.length, StandardCopyOption.COPY_ATTRIBUTES) >= 0) {
            return copyOptions;
        }
        actual[actual.length - 1] = StandardCopyOption.COPY_ATTRIBUTES;
        return actual;
    }

    /**
     * 返回文件大小的可读版本，其中输入表示特定的字节数。
     * <p>
     * 如果大小超过1GB，则返回的大小为整个GB的数量，即大小被舍入到最近的GB边界。
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @throws NullPointerException if the given {@code BigInteger} is {@code null}.
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(final BigInteger size) {
        Objects.requireNonNull(size, "size");
        final String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_EB_BI) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_PB_BI) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_TB_BI) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_GB_BI) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_MB_BI) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_KB_BI) + " KB";
        } else {
            displaySize = size + " bytes";
        }
        return displaySize;
    }

    /**
     * 返回文件大小的可读版本，其中输入表示特定的字节数。
     * <p>
     * 如果大小超过1GB，则返回的大小为整个GB的数量，即大小被舍入到最近的GB边界。
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(final long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    /**
     * 返回文件大小的可读版本，其中输入表示特定的字节数。
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(final Number size) {
        return byteCountToDisplaySize(size.longValue());
    }

    /**
     * 使用指定的校验和对象计算文件的校验和。如果需要，可以通过重用相同的校验和对象，使用一个{@code Checksum}实例检查多个文件。例如:
     *
     * <pre>
     * long checksum = FileUtils.checksum(file, new CRC32()).getValue();
     * </pre>
     *
     * @param file     the file to checksum, must not be {@code null}
     * @param checksum the checksum object to be used, must not be {@code null}
     * @return the checksum specified, updated with the content of the file
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws NullPointerException     if the given {@code Checksum} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a file.
     * @throws IOException              if an IO error occurs reading the file.
     */
    public static Checksum checksum(final File file, final Checksum checksum) throws IOException {
        requireExistsChecked(file, "file");
        requireFile(file, "file");
        Objects.requireNonNull(checksum, "checksum");
        try (InputStream inputStream = new CheckedInputStream(Files.newInputStream(file.toPath()), checksum)) {
            IOUtils.consume(inputStream);
        }
        return checksum;
    }

    /**
     * 使用CRC32校验和例程计算文件的校验和。返回校验和的值。
     *
     * @param file the file to checksum, must not be {@code null}
     * @return the checksum value
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a file.
     * @throws IOException              if an IO error occurs reading the file.
     */
    public static long checksumCRC32(final File file) throws IOException {
        return checksum(file, new CRC32()).getValue();
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(final File parentFile, final File file) throws IllegalArgumentException {
        if (null != parentFile && null != file) {
            if (!isSub(parentFile, file)) {
                throw new IllegalArgumentException(StringUtils.format(
                        "New file [{}] is outside of the parent dir: [{}]", file, parentFile));
            }
        }
        return file;
    }

    /**
     * 清除目录而不删除目录。
     *
     * @param directory directory to clean
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if directory does not exist or is not a directory.
     * @throws IOException              if an I/O error occurs.
     * @see #forceDelete(File)
     */
    public static void cleanDirectory(final File directory) throws IOException {
        IOConsumer.forEach(listFiles(directory, null), FileUtils::forceDelete);
    }

    /**
     * 清除目录而不删除目录。
     *
     * @param directory directory to clean, must not be {@code null}
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if directory does not exist or is not a directory.
     * @throws IOException              if an I/O error occurs.
     * @see #forceDeleteOnExit(File)
     */
    private static void cleanDirectoryOnExit(final File directory) throws IOException {
        IOConsumer.forEach(listFiles(directory, null), FileUtils::forceDeleteOnExit);
    }

    /**
     * Tests whether the contents of two files are equal.
     * <p>
     * This method checks to see if the two files are different lengths or if they point to the same file, before resorting to byte-by-byte comparison of the contents.
     * </p>
     * <p>
     * Code origin: Avalon
     * </p>
     *
     * @param file1 the first file
     * @param file2 the second file
     * @return true if the content of the files are equal or they both don't exist, false otherwise
     * @throws IllegalArgumentException when an input is not a file.
     * @throws IOException              If an I/O error occurs.
     * @see PathUtils#fileContentEquals(Path, Path, LinkOption[], java.nio.file.OpenOption...)
     */
    public static boolean contentEquals(final File file1, final File file2) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }

        if (!file1Exists) {
            // two not existing files are equal
            return true;
        }

        requireFile(file1, "file1");
        requireFile(file2, "file2");

        if (file1.length() != file2.length()) {
            // lengths differ, cannot be equal
            return false;
        }

        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            // same file
            return true;
        }

        try (InputStream input1 = Files.newInputStream(file1.toPath()); InputStream input2 = Files.newInputStream(file2.toPath())) {
            return IOUtils.contentEquals(input1, input2);
        }
    }

    /**
     * Compares the contents of two files to determine if they are equal or not.
     * <p>
     * This method checks to see if the two files point to the same file, before resorting to line-by-line comparison of the contents.
     * </p>
     *
     * @param file1       the first file
     * @param file2       the second file
     * @param charsetName the name of the requested charset. May be null, in which case the platform default is used
     * @return true if the content of the files are equal or neither exists, false otherwise
     * @throws IllegalArgumentException    when an input is not a file.
     * @throws IOException                 in case of an I/O error.
     * @throws UnsupportedCharsetException If the named charset is unavailable (unchecked exception).
     * @see IOUtils#contentEqualsIgnoreEOL(Reader, Reader)
     */
    public static boolean contentEqualsIgnoreEOL(final File file1, final File file2, final String charsetName)
            throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }

        if (!file1Exists) {
            // two not existing files are equal
            return true;
        }

        requireFile(file1, "file1");
        requireFile(file2, "file2");

        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            // same file
            return true;
        }

        final Charset charset = CharsetUtils.toCharset(charsetName);
        try (Reader input1 = new InputStreamReader(Files.newInputStream(file1.toPath()), charset);
             Reader input2 = new InputStreamReader(Files.newInputStream(file2.toPath()), charset)) {
            return IOUtils.contentEqualsIgnoreEOL(input1, input2);
        }
    }

    /**
     * Converts a Collection containing java.io.File instances into array representation. This is to account for the difference between File.listFiles() and FileUtils.listFiles().
     *
     * @param files a Collection containing java.io.File instances
     * @return an array of java.io.File
     */
    public static File[] convertFileCollectionToFileArray(final Collection<File> files) {
        return files.toArray(EMPTY_FILE_ARRAY);
    }

    /**
     * Copies a whole directory to a new location preserving the file dates.
     * <p>
     * This method copies the specified directory and all its child directories and files to the specified destination. The destination is the new location and name of the
     * directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the files' last modified date/times using
     * {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the methods throws IOException.
     * </p>
     *
     * @param srcDir  an existing directory to copy, must not be {@code null}.
     * @param destDir the new directory, must not be {@code null}.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    /**
     * Copies a whole directory to a new location.
     * <p>
     * This method copies the contents of the specified source directory to within the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     *
     * @param srcDir           an existing directory to copy, must not be {@code null}.
     * @param destDir          the new directory, must not be {@code null}.
     * @param preserveFileDate true if the file date of the copy should be the same as the original.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectory(final File srcDir, final File destDir, final boolean preserveFileDate)
            throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    /**
     * Copies a filtered directory to a new location preserving the file dates.
     * <p>
     * This method copies the contents of the specified source directory to within the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the files' last modified date/times using
     * {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the methods throws IOException.
     * </p>
     * <b>Example: Copy directories only</b>
     *
     * <pre>
     * // only copy the directory structure
     * FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY);
     * </pre>
     *
     * <b>Example: Copy directories and txt files</b>
     *
     * <pre>
     * // Create a filter for ".txt" files
     * IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     * // Create a filter for either directories or ".txt" files
     * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     * // Copy using the filter
     * FileUtils.copyDirectory(srcDir, destDir, filter);
     * </pre>
     *
     * @param srcDir  an existing directory to copy, must not be {@code null}.
     * @param destDir the new directory, must not be {@code null}.
     * @param filter  the filter to apply, null means copy all directories and files should be the same as the original.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter)
            throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }

    /**
     * Copies a filtered directory to a new location.
     * <p>
     * This method copies the contents of the specified source directory to within the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     * <b>Example: Copy directories only</b>
     *
     * <pre>
     * // only copy the directory structure
     * FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
     * </pre>
     *
     * <b>Example: Copy directories and txt files</b>
     *
     * <pre>
     * // Create a filter for ".txt" files
     * IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     * // Create a filter for either directories or ".txt" files
     * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     * // Copy using the filter
     * FileUtils.copyDirectory(srcDir, destDir, filter, false);
     * </pre>
     *
     * @param srcDir           an existing directory to copy, must not be {@code null}.
     * @param destDir          the new directory, must not be {@code null}.
     * @param filter           the filter to apply, null means copy all directories and files.
     * @param preserveFileDate true if the file date of the copy should be the same as the original.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter, final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, filter, preserveFileDate, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Copies a filtered directory to a new location.
     * <p>
     * This method copies the contents of the specified source directory to within the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     * <b>Example: Copy directories only</b>
     *
     * <pre>
     * // only copy the directory structure
     * FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
     * </pre>
     *
     * <b>Example: Copy directories and txt files</b>
     *
     * <pre>
     * // Create a filter for ".txt" files
     * IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     * IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     * // Create a filter for either directories or ".txt" files
     * FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     * // Copy using the filter
     * FileUtils.copyDirectory(srcDir, destDir, filter, false);
     * </pre>
     *
     * @param srcDir           an existing directory to copy, must not be {@code null}
     * @param destDir          the new directory, must not be {@code null}
     * @param fileFilter       the filter to apply, null means copy all directories and files
     * @param preserveFileDate true if the file date of the copy should be the same as the original
     * @param copyOptions      options specifying how the copy should be done, for example {@link StandardCopyOption}.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter fileFilter, final boolean preserveFileDate,
                                     final CopyOption... copyOptions) throws IOException {
        requireFileCopy(srcDir, destDir);
        requireDirectory(srcDir, "srcDir");
        requireCanonicalPathsNotEquals(srcDir, destDir);

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        final String srcDirCanonicalPath = srcDir.getCanonicalPath();
        final String destDirCanonicalPath = destDir.getCanonicalPath();
        if (destDirCanonicalPath.startsWith(srcDirCanonicalPath)) {
            final File[] srcFiles = listFiles(srcDir, fileFilter);
            if (srcFiles.length > 0) {
                exclusionList = new ArrayList<>(srcFiles.length);
                for (final File srcFile : srcFiles) {
                    final File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, fileFilter, exclusionList, preserveFileDate, preserveFileDate ? addCopyAttributes(copyOptions) : copyOptions);
    }

    /**
     * Copies a directory to within another directory preserving the file dates.
     * <p>
     * This method copies the source directory and all its contents to a directory of the same name in the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the files' last modified date/times using
     * {@link File#setLastModified(long)}, however it is not guaranteed that those operations will succeed. If the modification operation fails, the methods throws IOException.
     * </p>
     *
     * @param sourceDir      an existing directory to copy, must not be {@code null}.
     * @param destinationDir the directory to place the copy in, must not be {@code null}.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void copyDirectoryToDirectory(final File sourceDir, final File destinationDir) throws IOException {
        requireDirectoryIfExists(sourceDir, "sourceDir");
        requireDirectoryIfExists(destinationDir, "destinationDir");
        copyDirectory(sourceDir, new File(destinationDir, sourceDir.getName()), true);
    }

    /**
     * Copies a file to a new location preserving the file date.
     * <p>
     * This method copies the contents of the specified source file to the specified destination file. The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the file's last modified date/times using
     * {@link StandardCopyOption#COPY_ATTRIBUTES}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the methods throws
     * IOException.
     * </p>
     *
     * @param srcFile  an existing file to copy, must not be {@code null}.
     * @param destFile the new file, must not be {@code null}.
     * @throws NullPointerException if any of the given {@code File}s are {@code null}.
     * @throws IOException          if source or destination is invalid.
     * @throws IOException          if an error occurs or setting the last-modified time didn't succeeded.
     * @throws IOException          if the output file length is not the same as the input file length after the copy completes.
     * @see #copyFileToDirectory(File, File)
     * @see #copyFile(File, File, boolean)
     */
    public static void copyFile(final File srcFile, final File destFile) throws IOException {
        copyFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Copies an existing file to a new file location.
     * <p>
     * This method copies the contents of the specified source file to the specified destination file. The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the file's last
     * modified date/times using {@link StandardCopyOption#COPY_ATTRIBUTES}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     *
     * @param srcFile          an existing file to copy, must not be {@code null}.
     * @param destFile         the new file, must not be {@code null}.
     * @param preserveFileDate true if the file date of the copy should be the same as the original.
     * @throws NullPointerException if any of the given {@code File}s are {@code null}.
     * @throws IOException          if source or destination is invalid.
     * @throws IOException          if an error occurs or setting the last-modified time didn't succeeded.
     * @throws IOException          if the output file length is not the same as the input file length after the copy completes
     * @see #copyFile(File, File, boolean, CopyOption...)
     */
    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        // @formatter:off
        copyFile(srcFile, destFile, preserveFileDate
                ? new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING}
                : new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
        // @formatter:on
    }

    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file to the specified destination file. The directory holding the destination file is created if it does not exist.
     * If the destination file exists, you can overwrite it with {@link StandardCopyOption#REPLACE_EXISTING}.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the file's last
     * modified date/times using {@link StandardCopyOption#COPY_ATTRIBUTES}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     *
     * @param srcFile          an existing file to copy, must not be {@code null}.
     * @param destFile         the new file, must not be {@code null}.
     * @param preserveFileDate true if the file date of the copy should be the same as the original.
     * @param copyOptions      options specifying how the copy should be done, for example {@link StandardCopyOption}..
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IllegalArgumentException if source is not a file.
     * @throws IOException              if the output file length is not the same as the input file length after the copy completes.
     * @throws IOException              if an I/O error occurs, or setting the last-modified time didn't succeeded.
     * @see #copyFileToDirectory(File, File, boolean)
     */
    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate, final CopyOption... copyOptions) throws IOException {
        copyFile(srcFile, destFile, preserveFileDate ? addCopyAttributes(copyOptions) : copyOptions);
    }

    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file to the specified destination file. The directory holding the destination file is created if it does not exist.
     * If the destination file exists, you can overwrite it if you use {@link StandardCopyOption#REPLACE_EXISTING}.
     * </p>
     *
     * @param srcFile     an existing file to copy, must not be {@code null}.
     * @param destFile    the new file, must not be {@code null}.
     * @param copyOptions options specifying how the copy should be done, for example {@link StandardCopyOption}..
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IllegalArgumentException if source is not a file.
     * @throws IOException              if the output file length is not the same as the input file length after the copy completes.
     * @throws IOException              if an I/O error occurs.
     * @see StandardCopyOption
     */
    public static void copyFile(final File srcFile, final File destFile, final CopyOption... copyOptions) throws IOException {
        requireFileCopy(srcFile, destFile);
        requireFile(srcFile, "srcFile");
        requireCanonicalPathsNotEquals(srcFile, destFile);
        createParentDirectories(destFile);
        requireFileIfExists(destFile, "destFile");
        if (destFile.exists()) {
            requireCanWrite(destFile, "destFile");
        }

        // On Windows, the last modified time is copied by default.
        Files.copy(srcFile.toPath(), destFile.toPath(), copyOptions);

        // TODO IO-386: Do we still need this check?
        requireEqualSizes(srcFile, destFile, srcFile.length(), destFile.length());
    }

    /**
     * Copies bytes from a {@code File} to an {@code OutputStream}.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}.
     * </p>
     *
     * @param input  the {@code File} to read.
     * @param output the {@code OutputStream} to write.
     * @return the number of bytes copied
     * @throws NullPointerException if the File is {@code null}.
     * @throws NullPointerException if the OutputStream is {@code null}.
     * @throws IOException          if an I/O error occurs.
     */
    public static long copyFile(final File input, final OutputStream output) throws IOException {
        try (InputStream fis = Files.newInputStream(input.toPath())) {
            return IOUtils.copyLarge(fis, output);
        }
    }

    /**
     * Copies a file to a directory preserving the file date.
     * <p>
     * This method copies the contents of the specified source file to a file of the same name in the specified destination directory. The destination directory is created if it
     * does not exist. If the destination file exists, then this method will overwrite it.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the file's last modified date/times using
     * {@link StandardCopyOption#COPY_ATTRIBUTES}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the methods throws
     * IOException.
     * </p>
     *
     * @param srcFile an existing file to copy, must not be {@code null}.
     * @param destDir the directory to place the copy in, must not be {@code null}.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if source or destination is invalid.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     * @see #copyFile(File, File, boolean)
     */
    public static void copyFileToDirectory(final File srcFile, final File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }

    /**
     * Copies a file to a directory optionally preserving the file date.
     * <p>
     * This method copies the contents of the specified source file to a file of the same name in the specified destination directory. The destination directory is created if it
     * does not exist. If the destination file exists, then this method will overwrite it.
     * </p>
     * <p>
     * <strong>Note:</strong> Setting {@code preserveFileDate} to {@code true} tries to preserve the file's last
     * modified date/times using {@link StandardCopyOption#COPY_ATTRIBUTES}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the
     * methods throws IOException.
     * </p>
     *
     * @param sourceFile       an existing file to copy, must not be {@code null}.
     * @param destinationDir   the directory to place the copy in, must not be {@code null}.
     * @param preserveFileDate true if the file date of the copy should be the same as the original.
     * @throws NullPointerException if any of the given {@code File}s are {@code null}.
     * @throws IOException          if an error occurs or setting the last-modified time didn't succeeded.
     * @throws IOException          if the output file length is not the same as the input file length after the copy completes.
     * @see #copyFile(File, File, CopyOption...)
     */
    public static void copyFileToDirectory(final File sourceFile, final File destinationDir, final boolean preserveFileDate) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        requireDirectoryIfExists(destinationDir, "destinationDir");
        copyFile(sourceFile, new File(destinationDir, sourceFile.getName()), preserveFileDate);
    }

    /**
     * Copies bytes from an {@link InputStream} {@code source} to a file {@code destination}. The directories up to {@code destination} will be created if they don't already exist.
     * {@code destination} will be overwritten if it already exists.
     * <p>
     * <em>The {@code source} stream is closed.</em>
     * </p>
     * <p>
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     * </p>
     *
     * @param source      the {@code InputStream} to copy bytes from, must not be {@code null}, will be closed
     * @param destination the non-directory {@code File} to write bytes to (possibly overwriting), must not be {@code null}
     * @throws IOException if {@code destination} is a directory
     * @throws IOException if {@code destination} cannot be written
     * @throws IOException if {@code destination} needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     */
    public static void copyInputStreamToFile(final InputStream source, final File destination) throws IOException {
        try (InputStream inputStream = source) {
            copyToFile(inputStream, destination);
        }
    }

    /**
     * Copies a file or directory to within another directory preserving the file dates.
     * <p>
     * This method copies the source file or directory, along all its contents, to a directory of the same name in the specified destination directory.
     * </p>
     * <p>
     * The destination directory is created if it does not exist. If the destination directory did exist, then this method merges the source with the destination, with the source
     * taking precedence.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the files' last modified date/times using
     * {@link StandardCopyOption#COPY_ATTRIBUTES} or {@link File#setLastModified(long)} depending on the input, however it is not guaranteed that those operations will succeed. If
     * the modification operation fails, the methods throws IOException.
     * </p>
     *
     * @param sourceFile     an existing file or directory to copy, must not be {@code null}.
     * @param destinationDir the directory to place the copy in, must not be {@code null}.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     * @see #copyDirectoryToDirectory(File, File)
     * @see #copyFileToDirectory(File, File)
     */
    public static void copyToDirectory(final File sourceFile, final File destinationDir) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        if (sourceFile.isFile()) {
            copyFileToDirectory(sourceFile, destinationDir);
        } else if (sourceFile.isDirectory()) {
            copyDirectoryToDirectory(sourceFile, destinationDir);
        } else {
            throw new FileNotFoundException("The source " + sourceFile + " does not exist");
        }
    }

    /**
     * Copies a files to a directory preserving each file's date.
     * <p>
     * This method copies the contents of the specified source files to a file of the same name in the specified destination directory. The destination directory is created if it
     * does not exist. If the destination file exists, then this method will overwrite it.
     * </p>
     * <p>
     * <strong>Note:</strong> This method tries to preserve the file's last
     * modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that the operation will succeed. If the modification operation fails, the methods
     * throws IOException.
     * </p>
     *
     * @param sourceIterable a existing files to copy, must not be {@code null}.
     * @param destinationDir the directory to place the copy in, must not be {@code null}.
     * @throws NullPointerException if any of the given {@code File}s are {@code null}.
     * @throws IOException          if source or destination is invalid.
     * @throws IOException          if an error occurs or setting the last-modified time didn't succeeded.
     * @see #copyFileToDirectory(File, File)
     */
    public static void copyToDirectory(final Iterable<File> sourceIterable, final File destinationDir) throws IOException {
        Objects.requireNonNull(sourceIterable, "sourceIterable");
        for (final File src : sourceIterable) {
            copyFileToDirectory(src, destinationDir);
        }
    }

    /**
     * Copies bytes from an {@link InputStream} source to a {@link File} destination. The directories up to {@code destination} will be created if they don't already exist. {@code
     * destination} will be overwritten if it already exists. The {@code source} stream is left open, e.g. for use with {@link java.util.zip.ZipInputStream ZipInputStream}. See
     * {@link #copyInputStreamToFile(InputStream, File)} for a method that closes the input stream.
     *
     * @param inputStream the {@code InputStream} to copy bytes from, must not be {@code null}
     * @param file        the non-directory {@code File} to write bytes to (possibly overwriting), must not be {@code null}
     * @throws NullPointerException     if the InputStream is {@code null}.
     * @throws NullPointerException     if the File is {@code null}.
     * @throws IllegalArgumentException if the file object is a directory.
     * @throws IllegalArgumentException if the file is not writable.
     * @throws IOException              if the directories could not be created.
     * @throws IOException              if an IO error occurs during copying.
     */
    public static void copyToFile(final InputStream inputStream, final File file) throws IOException {
        try (OutputStream out = newOutputStream(file, false)) {
            IOUtils.copy(inputStream, out);
        }
    }


    /**
     * Creates all parent directories for a File object.
     *
     * @param file the File that may need parents, may be null.
     * @return The parent directory, or {@code null} if the given file does not name a parent
     * @throws IOException if the directory was not created along with all its parent directories.
     * @throws IOException if the given file object is not null and not a directory.
     */
    public static File createParentDirectories(final File file) throws IOException {
        return mkdirs(getParentFile(file));
    }

    /**
     * Gets the current directory.
     *
     * @return the current directory.
     */
    public static File current() {
        return PathUtils.current().toFile();
    }

    /**
     * Decodes the specified URL as per RFC 3986, i.e. transforms percent-encoded octets to characters by decoding with the UTF-8 character set. This function is primarily intended
     * for usage with {@link URL} which unfortunately does not enforce proper URLs. As such, this method will leniently accept invalid characters or malformed
     * percent-encoded octets and simply pass them literally through to the result string. Except for rare edge cases, this will make unencoded URLs pass through unaltered.
     *
     * @param url The URL to decode, may be {@code null}.
     * @return The decoded URL or {@code null} if the input was {@code null}.
     */
    static String decodeUrl(final String url) {
        String decoded = url;
        if (url != null && url.indexOf('%') >= 0) {
            final int n = url.length();
            final StringBuilder builder = new StringBuilder();
            final ByteBuffer byteBuffer = ByteBuffer.allocate(n);
            for (int i = 0; i < n; ) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            final byte octet = (byte) Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            byteBuffer.put(octet);
                            i += 3;
                        } while (i < n && url.charAt(i) == '%');
                        continue;
                    } catch (final RuntimeException ignored) {
                        // malformed percent-encoded octet, fall through and
                        // append characters literally
                    } finally {
                        if (byteBuffer.position() > 0) {
                            byteBuffer.flip();
                            builder.append(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                            byteBuffer.clear();
                        }
                    }
                }
                builder.append(url.charAt(i++));
            }
            decoded = builder.toString();
        }
        return decoded;
    }

    /**
     * Deletes the given File but throws an IOException if it cannot, unlike {@link File#delete()} which returns a boolean.
     *
     * @param file The file to delete.
     * @return the given file.
     * @throws NullPointerException if the parameter is {@code null}
     * @throws IOException          if the file cannot be deleted.
     * @see File#delete()
     */
    public static File delete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Files.delete(file.toPath());
        return file;
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException              in case deletion is unsuccessful
     * @throws NullPointerException     if the parameter is {@code null}
     * @throws IllegalArgumentException if {@code directory} is not a directory
     */
    public static void deleteDirectory(final File directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        delete(directory);
    }

    /**
     * Schedules a directory recursively for deletion on JVM exit.
     *
     * @param directory directory to delete, must not be {@code null}
     * @throws NullPointerException if the directory is {@code null}
     * @throws IOException          in case deletion is unsuccessful
     */
    private static void deleteDirectoryOnExit(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        directory.deleteOnExit();
        if (!isSymlink(directory)) {
            cleanDirectoryOnExit(directory);
        }
    }

    /**
     * Deletes a file, never throwing an exception. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * </p>
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
     * </ul>
     *
     * @param file file or directory to delete, can be {@code null}
     * @return {@code true} if the file or directory was deleted, otherwise {@code false}
     */
    public static boolean deleteQuietly(final File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (final Exception ignored) {
            // ignore
        }

        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }

    /**
     * Determines whether the {@code parent} directory contains the {@code child} element (a file or directory).
     * <p>
     * Files are normalized before comparison.
     * </p>
     * <p>
     * Edge cases:
     * <ul>
     * <li>A {@code directory} must not be null: if null, throw IllegalArgumentException</li>
     * <li>A {@code directory} must be a directory: if not a directory, throw IllegalArgumentException</li>
     * <li>A directory does not contain itself: return false</li>
     * <li>A null child file is not contained in any parent: return false</li>
     * </ul>
     *
     * @param directory the file to consider as the parent.
     * @param child     the file to consider as the child.
     * @return true is the candidate leaf is under by the specified composite. False otherwise.
     * @throws IOException              if an IO error occurs while checking the files.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     * @see FilenameUtils#directoryContains(String, String)
     */
    public static boolean directoryContains(final File directory, final File child) throws IOException {
        requireDirectoryExists(directory, "directory");

        if (child == null || !directory.exists() || !child.exists()) {
            return false;
        }

        // Canonicalize paths (normalizes relative paths)
        return FilenameUtils.directoryContains(directory.getCanonicalPath(), child.getCanonicalPath());
    }

    /**
     * Internal copy directory method.
     *
     * @param srcDir          the validated source directory, must not be {@code null}.
     * @param destDir         the validated destination directory, must not be {@code null}.
     * @param fileFilter      the filter to apply, null means copy all directories and files.
     * @param exclusionList   List of files and directories to exclude from the copy, may be null.
     * @param preserveDirDate preserve the directories last modified dates.
     * @param copyOptions     options specifying how the copy should be done, see {@link StandardCopyOption}.
     * @throws IOException if the directory was not created along with all its parent directories.
     * @throws IOException if the given file object is not a directory.
     */
    private static void doCopyDirectory(final File srcDir, final File destDir, final FileFilter fileFilter, final List<String> exclusionList,
                                        final boolean preserveDirDate, final CopyOption... copyOptions) throws IOException {
        // recurse dirs, copy files.
        final File[] srcFiles = listFiles(srcDir, fileFilter);
        requireDirectoryIfExists(destDir, "destDir");
        mkdirs(destDir);
        requireCanWrite(destDir, "destDir");
        for (final File srcFile : srcFiles) {
            final File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, fileFilter, exclusionList, preserveDirDate, copyOptions);
                } else {
                    copyFile(srcFile, dstFile, copyOptions);
                }
            }
        }
        // Do this last, as the above has probably affected directory metadata
        if (preserveDirDate) {
            setLastModified(srcDir, destDir);
        }
    }

    /**
     * 创建File对象
     *
     * @param path
     * @return File
     */
    public static File file(final String path) {
        if (null == path) {
            return null;
        }
        return new File(path);
    }

    /**
     * 创建File对象<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父目录
     * @param path   文件路径
     * @return File
     */
    public static File file(final String parent, final String path) {
        return file(new File(parent), path);
    }

    /**
     * 创建File对象<br>
     * 根据的路径构建文件，在Win下直接构建，在Linux下拆分路径单独构建
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(final File parent, final String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return checkSlip(parent, buildFile(parent, path));
    }

    /**
     * 通过多层目录参数创建文件<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param directory 父目录
     * @param names     元素名（多层目录名），由外到内依次传入
     * @return the file 文件
     * @since 4.0.6
     */
    public static File file(final File directory, final String... names) {
        Assert.notNull(directory, "directory must not be null");
        if (ArrayUtils.isEmpty(names)) {
            return directory;
        }

        File file = directory;
        for (final String name : names) {
            if (null != name) {
                file = file(file, name);
            }
        }
        return file;
    }

    /**
     * 通过多层目录创建文件
     * <p>
     * 元素名（多层目录名）
     *
     * @param names 多层文件的文件名，由外到内依次传入
     * @return the file 文件
     * @since 4.0.6
     */
    public static File file(final String... names) {
        if (ArrayUtils.isEmpty(names)) {
            return null;
        }

        File file = null;
        for (final String name : names) {
            if (file == null) {
                file = file(name);
            } else {
                file = file(file, name);
            }
        }
        return file;
    }

    /**
     * 删除文件或目录。对于一个目录，删除该目录及其所有子目录。
     * <p>
     * The difference between File.delete() and this method are:
     * </p>
     * <ul>
     * <li>The directory does not have to be empty.</li>
     * <li>You get an exception when a file or directory cannot be deleted.</li>
     * </ul>
     *
     * @param file file or directory to delete, must not be {@code null}.
     * @throws NullPointerException  if the file is {@code null}.
     * @throws FileNotFoundException if the file was not found.
     * @throws IOException           in case deletion is unsuccessful.
     */
    public static void forceDelete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        final Counters.PathCounters deleteCounters;
        try {
            deleteCounters = PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY,
                    StandardDeleteOption.OVERRIDE_READ_ONLY);
        } catch (final IOException e) {
            throw new IOException("Cannot delete file: " + file, e);
        }

        if (deleteCounters.getFileCounter().get() < 1 && deleteCounters.getDirectoryCounter().get() < 1) {
            // didn't find a file to delete.
            throw new FileNotFoundException("File does not exist: " + file);
        }
    }

    /**
     * jvm运行结束后要删除的文件。如果文件是目录，删除它和所有子目录.
     *
     * @param file file or directory to delete, must not be {@code null}.
     * @throws NullPointerException if the file is {@code null}.
     * @throws IOException          in case deletion is unsuccessful.
     */
    public static void forceDeleteOnExit(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        } else {
            file.deleteOnExit();
        }
    }

    /**
     * 创建一个目录，其中包括任何必要但不存在的父目录。如果指定名称的文件已经存在，但它不是目录，则抛出IOException。如果无法创建目录(或者文件已经存在但不是目录)，则抛出IOException。
     *
     * @param directory directory to create, may be {@code null}.
     * @throws IOException       if the directory was not created along with all its parent directories.
     * @throws IOException       if the given file object is not a directory.
     * @throws SecurityException See {@link File#mkdirs()}.
     */
    public static void forceMkdir(final File directory) throws IOException {
        mkdirs(directory);
    }

    /**
     * 为给定的文件创建任何必要但不存在的父目录。如果父目录无法创建，则抛出IOException。
     *
     * @param file file with parent to create, must not be {@code null}.
     * @throws NullPointerException if the file is {@code null}.
     * @throws IOException          if the parent directory cannot be created.
     */
    public static void forceMkdirParent(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        forceMkdir(getParentFile(file));
    }

    /**
     * 从name元素集构造一个文件。
     *
     * @param directory the parent directory.
     * @param names     the name elements.
     * @return the new file.
     */
    public static File getFile(final File directory, final String... names) {
        Objects.requireNonNull(directory, "directory");
        Objects.requireNonNull(names, "names");
        File file = directory;
        for (final String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    /**
     * 从name元素集构造一个文件。
     *
     * @param names the name elements.
     * @return the file.
     */
    public static File getFile(final String... names) {
        Objects.requireNonNull(names, "names");
        File file = null;
        for (final String name : names) {
            if (file == null) {
                file = new File(name);
            } else {
                file = new File(file, name);
            }
        }
        return file;
    }

    /**
     * 获取给定文件的父级。给定的文件可以是bull，文件的父文件也可以是null。
     *
     * @param file The file to query.
     * @return The parent file or {@code null}.
     */
    private static File getParentFile(final File file) {
        return file == null ? null : file.getParentFile();
    }

    /**
     * 返回一个表示系统临时目录的{@link File}。
     *
     * @return the system temporary directory.
     */
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    /**
     * 返回系统临时目录的路径。
     *
     * @return the path to the system temporary directory.
     */
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 返回一个表示用户主目录的{@link File}。
     *
     * @return the user's home directory.
     */
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }

    /**
     * 返回用户主目录的路径。
     *
     * @return the path to the user's home directory.
     */
    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录，非空
     * @param sub    子目录，非空
     * @return 子目录是否为父目录的子目录
     * @since 4.5.4
     */
    public static boolean isSub(final File parent, final File sub) {
        Assert.notNull(parent);
        Assert.notNull(sub);
        return PathUtils.isSub(parent.toPath(), sub.toPath());
    }

    /**
     * 测试指定的{@code File}是否为目录。 Implemented as a null-safe delegate to {@code Files.isDirectory(Path path, LinkOption... options)}.
     *
     * @param file    the path to the file.
     * @param options options indicating how symbolic links are handled
     * @return {@code true} if the file is a directory; {@code false} if the path is null, the file does not exist, is not a directory, or it cannot be determined if the file is a
     * directory or not.
     * @throws SecurityException In the case of the default provider, and a security manager is installed, the {@link SecurityManager#checkRead(String) checkRead} method is invoked
     *                           to check read access to the directory.
     */
    public static boolean isDirectory(final File file, final LinkOption... options) {
        return file != null && Files.isDirectory(file.toPath(), options);
    }

    /**
     * 测试目录是否为空。
     *
     * @param directory the directory to query.
     * @return whether the directory is empty.
     * @throws IOException           if an I/O error occurs.
     * @throws NotDirectoryException if the file could not otherwise be opened because it is not a directory
     *                               <i>(optional specific exception)</i>.
     */
    public static boolean isEmptyDirectory(final File directory) throws IOException {
        return PathUtils.isEmptyDirectory(directory.toPath());
    }

    /**
     * 测试当前指定的{@code Flie}是否比指定的{@code ChronoLocalDate}更新。
     *
     * <p>Note: The input date is assumed to be in the system default time-zone with the time
     * part set to the current time. To use a non-default time-zone use the method {@link #isFileNewer(File, ChronoLocalDateTime, ZoneId) isFileNewer(file,
     * chronoLocalDate.atTime(LocalTime.now(zoneId)), zoneId)} where {@code zoneId} is a valid {@link ZoneId}.
     *
     * @param file            the {@code File} of which the modification date must be compared.
     * @param chronoLocalDate the date reference.
     * @return true if the {@code File} exists and has been modified after the given {@code ChronoLocalDate} at the current time.
     * @throws NullPointerException if the file or local date is {@code null}.
     */
    public static boolean isFileNewer(final File file, final ChronoLocalDate chronoLocalDate) {
        return isFileNewer(file, chronoLocalDate, LocalTime.now());
    }

    /**
     * 测试指定的{@code File}在指定的时间是否比指定的{@code ChronoLocalDate}更新。
     *
     * <p>Note: The input date and time are assumed to be in the system default time-zone. To use a
     * non-default time-zone use the method {@link #isFileNewer(File, ChronoLocalDateTime, ZoneId) isFileNewer(file, chronoLocalDate.atTime(localTime), zoneId)} where {@code
     * zoneId} is a valid {@link ZoneId}.
     *
     * @param file            the {@code File} of which the modification date must be compared.
     * @param chronoLocalDate the date reference.
     * @param localTime       the time reference.
     * @return true if the {@code File} exists and has been modified after the given {@code ChronoLocalDate} at the given time.
     * @throws NullPointerException if the file, local date or zone ID is {@code null}.
     */
    public static boolean isFileNewer(final File file, final ChronoLocalDate chronoLocalDate, final LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return isFileNewer(file, chronoLocalDate.atTime(localTime));
    }

    /**
     * 测试指定的{@code File}在系统默认时区是否比指定的{@code ChronoLocalDateTime}更新。
     *
     * <p>Note: The input date and time is assumed to be in the system default time-zone. To use a
     * non-default time-zone use the method {@link #isFileNewer(File, ChronoLocalDateTime, ZoneId) isFileNewer(file, chronoLocalDateTime, zoneId)} where {@code zoneId} is a valid
     * {@link ZoneId}.
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoLocalDateTime the date reference.
     * @return true if the {@code File} exists and has been modified after the given {@code ChronoLocalDateTime} at the system-default time zone.
     * @throws NullPointerException if the file or local date time is {@code null}.
     */
    public static boolean isFileNewer(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime) {
        return isFileNewer(file, chronoLocalDateTime, ZoneId.systemDefault());
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code ZoneId}处指定的{@code ChronoLocalDateTime}更新。
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoLocalDateTime the date reference.
     * @param zoneId              the time zone.
     * @return true if the {@code File} exists and has been modified after the given {@code ChronoLocalDateTime} at the given {@code ZoneId}.
     * @throws NullPointerException if the file, local date time or zone ID is {@code null}.
     */
    public static boolean isFileNewer(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime, final ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return isFileNewer(file, chronoLocalDateTime.atZone(zoneId));
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code ChronoZonedDateTime}更新。
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoZonedDateTime the date reference.
     * @return true if the {@code File} exists and has been modified after the given {@code ChronoZonedDateTime}.
     * @throws NullPointerException if the file or zoned date time is {@code null}.
     */
    public static boolean isFileNewer(final File file, final ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return UncheckedIO.get(() -> PathUtils.isNewer(file.toPath(), chronoZonedDateTime));
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code日期}更新。
     *
     * @param file the {@code File} of which the modification date must be compared.
     * @param date the date reference.
     * @return true if the {@code File} exists and has been modified after the given {@code Date}.
     * @throws NullPointerException if the file or date is {@code null}.
     */
    public static boolean isFileNewer(final File file, final Date date) {
        Objects.requireNonNull(date, "date");
        return isFileNewer(file, date.getTime());
    }

    /**
     * 测试指定的{@code File}是否比引用{@code File}更新。
     *
     * @param file      the {@code File} of which the modification date must be compared.
     * @param reference the {@code File} of which the modification date is used.
     * @return true if the {@code File} exists and has been modified more recently than the reference {@code File}.
     * @throws NullPointerException     if the file or reference file is {@code null}.
     * @throws IllegalArgumentException if the reference file doesn't exist.
     */
    public static boolean isFileNewer(final File file, final File reference) {
        requireExists(reference, "reference");
        return UncheckedIO.get(() -> PathUtils.isNewer(file.toPath(), reference.toPath()));
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code FileTime}更新。
     *
     * @param file     the {@code File} of which the modification date must be compared.
     * @param fileTime the file time reference.
     * @return true if the {@code File} exists and has been modified after the given {@code FileTime}.
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if the file or local date is {@code null}.
     */
    public static boolean isFileNewer(final File file, final FileTime fileTime) throws IOException {
        Objects.requireNonNull(file, "file");
        return PathUtils.isNewer(file.toPath(), fileTime);
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code instant}更新。
     *
     * @param file    the {@code File} of which the modification date must be compared.
     * @param instant the date reference.
     * @return true if the {@code File} exists and has been modified after the given {@code Instant}.
     * @throws NullPointerException if the file or instant is {@code null}.
     */
    public static boolean isFileNewer(final File file, final Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return UncheckedIO.get(() -> PathUtils.isNewer(file.toPath(), instant));
    }

    /**
     * 测试指定的{@code File}是否比指定的时间引用更新。
     *
     * @param file       the {@code File} of which the modification date must be compared.
     * @param timeMillis the time reference measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     * @return true if the {@code File} exists and has been modified after the given time reference.
     * @throws NullPointerException if the file is {@code null}.
     */
    public static boolean isFileNewer(final File file, final long timeMillis) {
        Objects.requireNonNull(file, "file");
        return UncheckedIO.get(() -> PathUtils.isNewer(file.toPath(), timeMillis));
    }

    /**
     * 测试指定的{@code File}在当前时间是否比指定的{@code ChronoLocalDate}旧。
     *
     * <p>Note: The input date is assumed to be in the system default time-zone with the time
     * part set to the current time. To use a non-default time-zone use the method {@link #isFileOlder(File, ChronoLocalDateTime, ZoneId) isFileOlder(file,
     * chronoLocalDate.atTime(LocalTime.now(zoneId)), zoneId)} where {@code zoneId} is a valid {@link ZoneId}.
     *
     * @param file            the {@code File} of which the modification date must be compared.
     * @param chronoLocalDate the date reference.
     * @return true if the {@code File} exists and has been modified before the given {@code ChronoLocalDate} at the current time.
     * @throws NullPointerException if the file or local date is {@code null}.
     * @see ZoneId#systemDefault()
     * @see LocalTime#now()
     */
    public static boolean isFileOlder(final File file, final ChronoLocalDate chronoLocalDate) {
        return isFileOlder(file, chronoLocalDate, LocalTime.now());
    }

    /**
     * 测试指定的{@code File}是否比指定的{@code LocalTime}指定的{@code ChronoLocalDate}旧。
     *
     * <p>Note: The input date and time are assumed to be in the system default time-zone. To use a
     * non-default time-zone use the method {@link #isFileOlder(File, ChronoLocalDateTime, ZoneId) isFileOlder(file, chronoLocalDate.atTime(localTime), zoneId)} where {@code
     * zoneId} is a valid {@link ZoneId}.
     *
     * @param file            the {@code File} of which the modification date must be compared.
     * @param chronoLocalDate the date reference.
     * @param localTime       the time reference.
     * @return true if the {@code File} exists and has been modified before the given {@code ChronoLocalDate} at the specified time.
     * @throws NullPointerException if the file, local date or local time is {@code null}.
     * @see ZoneId#systemDefault()
     */
    public static boolean isFileOlder(final File file, final ChronoLocalDate chronoLocalDate, final LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return isFileOlder(file, chronoLocalDate.atTime(localTime));
    }

    /**
     * 测试指定的{@code File}是否比系统默认时区指定的{@code ChronoLocalDateTime}旧。
     *
     * <p>Note: The input date and time is assumed to be in the system default time-zone. To use a
     * non-default time-zone use the method {@link #isFileOlder(File, ChronoLocalDateTime, ZoneId) isFileOlder(file, chronoLocalDateTime, zoneId)} where {@code zoneId} is a valid
     * {@link ZoneId}.
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoLocalDateTime the date reference.
     * @return true if the {@code File} exists and has been modified before the given {@code ChronoLocalDateTime} at the system-default time zone.
     * @throws NullPointerException if the file or local date time is {@code null}.
     * @see ZoneId#systemDefault()
     */
    public static boolean isFileOlder(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime) {
        return isFileOlder(file, chronoLocalDateTime, ZoneId.systemDefault());
    }

    /**
     * Tests if the specified {@code File} is older than the specified {@code ChronoLocalDateTime} at the specified {@code ZoneId}.
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoLocalDateTime the date reference.
     * @param zoneId              the time zone.
     * @return true if the {@code File} exists and has been modified before the given {@code ChronoLocalDateTime} at the given {@code ZoneId}.
     * @throws NullPointerException if the file, local date time or zone ID is {@code null}.
     */
    public static boolean isFileOlder(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime, final ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return isFileOlder(file, chronoLocalDateTime.atZone(zoneId));
    }

    /**
     * Tests if the specified {@code File} is older than the specified {@code ChronoZonedDateTime}.
     *
     * @param file                the {@code File} of which the modification date must be compared.
     * @param chronoZonedDateTime the date reference.
     * @return true if the {@code File} exists and has been modified before the given {@code ChronoZonedDateTime}.
     * @throws NullPointerException if the file or zoned date time is {@code null}.
     */
    public static boolean isFileOlder(final File file, final ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return isFileOlder(file, chronoZonedDateTime.toInstant());
    }

    /**
     * Tests if the specified {@code File} is older than the specified {@code Date}.
     *
     * @param file the {@code File} of which the modification date must be compared.
     * @param date the date reference.
     * @return true if the {@code File} exists and has been modified before the given {@code Date}.
     * @throws NullPointerException if the file or date is {@code null}.
     */
    public static boolean isFileOlder(final File file, final Date date) {
        Objects.requireNonNull(date, "date");
        return isFileOlder(file, date.getTime());
    }

    /**
     * Tests if the specified {@code File} is older than the reference {@code File}.
     *
     * @param file      the {@code File} of which the modification date must be compared.
     * @param reference the {@code File} of which the modification date is used.
     * @return true if the {@code File} exists and has been modified before the reference {@code File}.
     * @throws NullPointerException     if the file or reference file is {@code null}.
     * @throws IllegalArgumentException if the reference file doesn't exist.
     */
    public static boolean isFileOlder(final File file, final File reference) {
        requireExists(reference, "reference");
        return UncheckedIO.get(() -> PathUtils.isOlder(file.toPath(), reference.toPath()));
    }

    /**
     * Tests if the specified {@code File} is older than the specified {@code FileTime}.
     *
     * @param file     the {@code File} of which the modification date must be compared.
     * @param fileTime the file time reference.
     * @return true if the {@code File} exists and has been modified before the given {@code FileTime}.
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if the file or local date is {@code null}.
     */
    public static boolean isFileOlder(final File file, final FileTime fileTime) throws IOException {
        Objects.requireNonNull(file, "file");
        return PathUtils.isOlder(file.toPath(), fileTime);
    }

    /**
     * Tests if the specified {@code File} is older than the specified {@code Instant}.
     *
     * @param file    the {@code File} of which the modification date must be compared.
     * @param instant the date reference.
     * @return true if the {@code File} exists and has been modified before the given {@code Instant}.
     * @throws NullPointerException if the file or instant is {@code null}.
     */
    public static boolean isFileOlder(final File file, final Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return UncheckedIO.get(() -> PathUtils.isOlder(file.toPath(), instant));
    }

    /**
     * Tests if the specified {@code File} is older than the specified time reference.
     *
     * @param file       the {@code File} of which the modification date must be compared.
     * @param timeMillis the time reference measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     * @return true if the {@code File} exists and has been modified before the given time reference.
     * @throws NullPointerException if the file is {@code null}.
     */
    public static boolean isFileOlder(final File file, final long timeMillis) {
        Objects.requireNonNull(file, "file");
        return UncheckedIO.get(() -> PathUtils.isOlder(file.toPath(), timeMillis));
    }

    /**
     * 测试指定的{@code File}是否是常规文件。实现为{@code Files的空安全委托。isRegularFile(路径路径,LinkOption……选项)}。
     *
     * @param file    the path to the file.
     * @param options options indicating how symbolic links are handled
     * @return {@code true} if the file is a regular file; {@code false} if the path is null, the file does not exist, is not a regular file, or it cannot be determined if the file
     * is a regular file or not.
     * @throws SecurityException In the case of the default provider, and a security manager is installed, the {@link SecurityManager#checkRead(String) checkRead} method is invoked
     *                           to check read access to the directory.
     */
    public static boolean isRegularFile(final File file, final LinkOption... options) {
        return file != null && Files.isRegularFile(file.toPath(), options);
    }

    /**
     * 测试指定的文件是否是符号链接而不是实际文件。
     * <p>
     * This method delegates to {@link Files#isSymbolicLink(Path path)}
     * </p>
     *
     * @param file the file to test.
     * @return true if the file is a symbolic link, see {@link Files#isSymbolicLink(Path path)}.
     * @see Files#isSymbolicLink(Path)
     */
    public static boolean isSymlink(final File file) {
        return file != null && Files.isSymbolicLink(file.toPath());
    }

    /**
     * 迭代给定目录(以及可选的子目录)中的文件。
     * <p>
     * The resulting iterator MUST be consumed in its entirety in order to close its underlying stream.
     * </p>
     * <p>
     * All files found are filtered by an IOFileFilter.
     * </p>
     *
     * @param directory  the directory to search in
     * @param fileFilter filter to apply when finding files.
     * @param dirFilter  optional filter to apply when finding subdirectories. If this parameter is {@code null}, subdirectories will not be included in the search. Use
     *                   TrueFileFilter.INSTANCE to match all directories.
     * @return an iterator of java.io.File for the matching files
     * @see FileFilterUtils
     * @see NameFileFilter
     */
    public static Iterator<File> iterateFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }

    /**
     * 迭代匹配扩展数组给定目录(以及可选的其子目录)中的文件。
     * <p>
     * The resulting iterator MUST be consumed in its entirety in order to close its underlying stream.
     * </p>
     *
     * @param directory  the directory to search in
     * @param extensions an array of extensions, ex. {"java","xml"}. If this parameter is {@code null}, all files are returned.
     * @param recursive  if true all subdirectories are searched as well
     * @return an iterator of java.io.File with the matching files
     */
    public static Iterator<File> iterateFiles(final File directory, final String[] extensions, final boolean recursive) {
        return UncheckedIO.apply(d -> StreamIterator.iterator(streamFiles(d, recursive, extensions)), directory);
    }

    /**
     * 迭代给定目录(以及可选的子目录)中的文件。
     * <p>
     * The resulting iterator MUST be consumed in its entirety in order to close its underlying stream.
     * </p>
     * <p>
     * All files found are filtered by an IOFileFilter.
     * </p>
     * <p>
     * The resulting iterator includes the subdirectories themselves.
     * </p>
     *
     * @param directory  the directory to search in
     * @param fileFilter filter to apply when finding files.
     * @param dirFilter  optional filter to apply when finding subdirectories. If this parameter is {@code null}, subdirectories will not be included in the search. Use
     *                   TrueFileFilter.INSTANCE to match all directories.
     * @return an iterator of java.io.File for the matching files
     * @see FileFilterUtils
     * @see NameFileFilter
     */
    public static Iterator<File> iterateFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }

    /**
     * 返回最后修改时间(以毫秒为单位) {@link Files#getLastModifiedTime(Path, LinkOption...)}.
     * <p>
     * For the best precision, use {@link #lastModifiedFileTime(File)}.
     * </p>
     * <p>
     * Use this method to avoid issues with {@link File#lastModified()} like
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8177809">JDK-8177809</a> where {@link File#lastModified()} is
     * losing milliseconds (always ends in 000). This bug exists in OpenJDK 8 and 9, and is fixed in 10.
     * </p>
     *
     * @param file The File to query.
     * @return See {@link FileTime#toMillis()}.
     * @throws IOException if an I/O error occurs.
     */
    public static long lastModified(final File file) throws IOException {
        // https://bugs.openjdk.java.net/browse/JDK-8177809
        // File.lastModified() is losing milliseconds (always ends in 000)
        // This bug is in OpenJDK 8 and 9, and fixed in 10.
        return lastModifiedFileTime(file).toMillis();
    }

    /**
     * 返回最后一次修改 {@link FileTime} via {@link Files#getLastModifiedTime(Path, LinkOption...)}.
     * <p>
     * Use this method to avoid issues with {@link File#lastModified()} like
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8177809">JDK-8177809</a> where {@link File#lastModified()} is
     * losing milliseconds (always ends in 000). This bug exists in OpenJDK 8 and 9, and is fixed in 10.
     * </p>
     *
     * @param file The File to query.
     * @return See {@link Files#getLastModifiedTime(Path, LinkOption...)}.
     * @throws IOException if an I/O error occurs.
     */
    public static FileTime lastModifiedFileTime(final File file) throws IOException {
        // https://bugs.openjdk.java.net/browse/JDK-8177809
        // File.lastModified() is losing milliseconds (always ends in 000)
        // This bug is in OpenJDK 8 and 9, and fixed in 10.
        return Files.getLastModifiedTime(Objects.requireNonNull(file.toPath(), "file"));
    }

    /**
     * 返回最后修改时间(以毫秒为单位) {@link Files#getLastModifiedTime(Path, LinkOption...)}.
     * <p>
     * For the best precision, use {@link #lastModifiedFileTime(File)}.
     * </p>
     * <p>
     * Use this method to avoid issues with {@link File#lastModified()} like
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8177809">JDK-8177809</a> where {@link File#lastModified()} is
     * losing milliseconds (always ends in 000). This bug exists in OpenJDK 8 and 9, and is fixed in 10.
     * </p>
     *
     * @param file The File to query.
     * @return See {@link FileTime#toMillis()}.
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static long lastModifiedUnchecked(final File file) {
        // https://bugs.openjdk.java.net/browse/JDK-8177809
        // File.lastModified() is losing milliseconds (always ends in 000)
        // This bug is in OpenJDK 8 and 9, and fixed in 10.
        return UncheckedIO.apply(FileUtils::lastModified, file);
    }

    /**
     * 使用虚拟机的默认编码，为{@code File}中的行返回一个迭代器。
     *
     * @param file the file to open for input, must not be {@code null}
     * @return an Iterator of the lines in the file, never {@code null}
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           if an I/O error occurs.
     * @see #lineIterator(File, String)
     */
    public static LineIterator lineIterator(final File file) throws IOException {
        return lineIterator(file, null);
    }

    /**
     * 对象中的行返回迭代器{@code File}.
     * <p>
     * This method opens an {@code InputStream} for the file. When you have finished with the iterator you should close the stream to free internal resources. This can be done by
     * using a try-with-resources block or calling the {@link LineIterator#close()} method.
     * </p>
     * <p>
     * The recommended usage pattern is:
     * </p>
     * <pre>
     * LineIterator it = FileUtils.lineIterator(file, "UTF-8");
     * try {
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   LineIterator.closeQuietly(iterator);
     * }
     * </pre>
     * <p>
     * If an exception occurs during the creation of the iterator, the underlying stream is closed.
     * </p>
     *
     * @param file        the file to open for input, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return a LineIterator for lines in the file, never {@code null}; MUST be closed by the caller.
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           if an I/O error occurs.
     */
    @SuppressWarnings("resource") // Caller closes the result LineIterator.
    public static LineIterator lineIterator(final File file, final String charsetName) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath());
            return IOUtils.lineIterator(inputStream, charsetName);
        } catch (final IOException | RuntimeException ex) {
            IOUtils.closeQuietly(inputStream, ex::addSuppressed);
            throw ex;
        }
    }

    private static AccumulatorPathVisitor listAccumulate(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter,
                                                         final FileVisitOption... options) throws IOException {
        final boolean isDirFilterSet = dirFilter != null;
        final FileEqualsFileFilter rootDirFilter = new FileEqualsFileFilter(directory);
        final PathFilter dirPathFilter = isDirFilterSet ? rootDirFilter.or(dirFilter) : rootDirFilter;
        final AccumulatorPathVisitor visitor = new AccumulatorPathVisitor(Counters.noopPathCounters(), fileFilter, dirPathFilter);
        final Set<FileVisitOption> optionSet = new HashSet<>();
        Collections.addAll(optionSet, options);
        Files.walkFileTree(directory.toPath(), optionSet, toMaxDepth(isDirFilterSet), visitor);
        return visitor;
    }

    /**
     * 列出目录中的文件，断言所提供的目录存在并且是一个目录。
     *
     * @param directory  The directory to list
     * @param fileFilter Optional file filter, may be null.
     * @return The files in the directory, never {@code null}.
     * @throws NullPointerException     if directory is {@code null}.
     * @throws IllegalArgumentException if directory does not exist or is not a directory.
     * @throws IOException              if an I/O error occurs.
     */
    private static File[] listFiles(final File directory, final FileFilter fileFilter) throws IOException {
        requireDirectoryExists(directory, "directory");
        final File[] files = fileFilter == null ? directory.listFiles() : directory.listFiles(fileFilter);
        if (files == null) {
            // null if the directory does not denote a directory, or if an I/O error occurs.
            throw new IOException("Unknown I/O error listing contents of directory: " + directory);
        }
        return files;
    }

    /**
     * 在给定目录(以及可选的子目录)中查找文件。所有找到的文件都通过ifilefilter进行过滤。
     * <p>
     * If your search should recurse into subdirectories you can pass in an IOFileFilter for directories. You don't need to bind a DirectoryFileFilter (via logical AND) to this
     * filter. This method does that for you.
     * </p>
     * <p>
     * An example: If you want to search through all directories called "temp" you pass in {@code FileFilterUtils.NameFileFilter("temp")}
     * </p>
     * <p>
     * Another common usage of this method is find files in a directory tree but ignoring the directories generated CVS. You can simply pass in {@code
     * FileFilterUtils.makeCVSAware(null)}.
     * </p>
     *
     * @param directory  the directory to search in
     * @param fileFilter filter to apply when finding files. Must not be {@code null}, use {@link TrueFileFilter#INSTANCE} to match all files in selected directories.
     * @param dirFilter  optional filter to apply when finding subdirectories. If this parameter is {@code null}, subdirectories will not be included in the search. Use {@link
     *                   TrueFileFilter#INSTANCE} to match all directories.
     * @return a collection of java.io.File with the matching files
     * @see FileFilterUtils
     * @see NameFileFilter
     */
    public static Collection<File> listFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        final AccumulatorPathVisitor visitor = UncheckedIO
                .apply(d -> listAccumulate(d, FileFileFilter.INSTANCE.and(fileFilter), dirFilter, FileVisitOption.FOLLOW_LINKS), directory);
        return visitor.getFileList().stream().map(Path::toFile).collect(Collectors.toList());
    }

    /**
     * 查找与扩展名数组匹配的给定目录(以及可选的其子目录)中的文件。
     *
     * @param directory  the directory to search in
     * @param extensions an array of extensions, ex. {"java","xml"}. If this parameter is {@code null}, all files are returned.
     * @param recursive  if true all subdirectories are searched as well
     * @return a collection of java.io.File with the matching files
     */
    public static Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
        return UncheckedIO.apply(d -> toList(streamFiles(d, recursive, extensions)), directory);
    }

    /**
     * 在给定目录(以及可选的子目录)中查找文件。所有找到的文件都通过ifilefilter进行过滤。
     * <p>
     * The resulting collection includes the starting directory and any subdirectories that match the directory filter.
     * </p>
     *
     * @param directory  the directory to search in
     * @param fileFilter filter to apply when finding files.
     * @param dirFilter  optional filter to apply when finding subdirectories. If this parameter is {@code null}, subdirectories will not be included in the search. Use
     *                   TrueFileFilter.INSTANCE to match all directories.
     * @return a collection of java.io.File with the matching files
     * @see FileUtils#listFiles
     * @see FileFilterUtils
     * @see NameFileFilter
     */
    public static Collection<File> listFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        final AccumulatorPathVisitor visitor = UncheckedIO.apply(d -> listAccumulate(d, fileFilter, dirFilter, FileVisitOption.FOLLOW_LINKS),
                directory);
        final List<Path> list = visitor.getFileList();
        list.addAll(visitor.getDirList());
        return list.stream().map(Path::toFile).collect(Collectors.toList());
    }

    /**
     * 调用{@link File#mkdirs()}并在失败时抛出异常。
     *
     * @param directory the receiver for {@code mkdirs()}, may be null.
     * @return the given file, may be null.
     * @throws IOException       if the directory was not created along with all its parent directories.
     * @throws IOException       if the given file object is not a directory.
     * @throws SecurityException See {@link File#mkdirs()}.
     * @see File#mkdirs()
     */
    private static File mkdirs(final File directory) throws IOException {
        if (directory != null && !directory.mkdirs() && !directory.isDirectory()) {
            throw new IOException("Cannot create directory '" + directory + "'.");
        }
        return directory;
    }

    /**
     * 移动一个目录。
     * <p>
     * 当目标目录在另一个文件系统上时，执行“复制和删除”操作。
     * </p>
     *
     * @param srcDir  the directory to be moved.
     * @param destDir the destination directory.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void moveDirectory(final File srcDir, final File destDir) throws IOException {
        validateMoveParameters(srcDir, destDir);
        requireDirectory(srcDir, "srcDir");
        requireAbsent(destDir, "destDir");
        if (!srcDir.renameTo(destDir)) {
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }
            copyDirectory(srcDir, destDir);
            deleteDirectory(srcDir);
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir +
                        "' after copy to '" + destDir + "'");
            }
        }
    }

    /**
     * 将一个目录移动到另一个目录。
     *
     * @param source        the file to be moved.
     * @param destDir       the destination file.
     * @param createDestDir If {@code true} create the destination directory, otherwise if {@code false} throw an IOException.
     * @throws NullPointerException     if any of the given {@code File}s are {@code null}.
     * @throws IllegalArgumentException if the source or destination is invalid.
     * @throws FileNotFoundException    if the source does not exist.
     * @throws IOException              if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void moveDirectoryToDirectory(final File source, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(source, destDir);
        if (!destDir.isDirectory()) {
            if (destDir.exists()) {
                throw new IOException("Destination '" + destDir + "' is not a directory");
            }
            if (!createDestDir) {
                throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist [createDestDir=" + false + "]");
            }
            mkdirs(destDir);
        }
        moveDirectory(source, new File(destDir, source.getName()));
    }

    /**
     * 移动保存属性的文件。
     * <p>
     * Shorthand for {@code moveFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES)}.
     * </p>
     * <p>
     * When the destination file is on another file system, do a "copy and delete".
     * </p>
     *
     * @param srcFile  the file to be moved.
     * @param destFile the destination file.
     * @throws NullPointerException  if any of the given {@code File}s are {@code null}.
     * @throws FileExistsException   if the destination file exists.
     * @throws FileNotFoundException if the source file does not exist.
     * @throws IOException           if source or destination is invalid.
     * @throws IOException           if an error occurs.
     */
    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        moveFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES);
    }

    /**
     * 移动一个文件。
     * <p>
     * 当目标文件在另一个文件系统上时，执行“复制和删除”操作。
     * </p>
     *
     * @param srcFile     the file to be moved.
     * @param destFile    the destination file.
     * @param copyOptions Copy options.
     * @throws NullPointerException  if any of the given {@code File}s are {@code null}.
     * @throws FileExistsException   if the destination file exists.
     * @throws FileNotFoundException if the source file does not exist.
     * @throws IOException           if source or destination is invalid.
     * @throws IOException           if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void moveFile(final File srcFile, final File destFile, final CopyOption... copyOptions) throws IOException {
        validateMoveParameters(srcFile, destFile);
        requireFile(srcFile, "srcFile");
        requireAbsent(destFile, "destFile");
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile, copyOptions);
            if (!srcFile.delete()) {
                FileUtils.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }

    /**
     * 将文件移动到目录。
     *
     * @param srcFile       the file to be moved.
     * @param destDir       the destination file.
     * @param createDestDir If {@code true} create the destination directory, otherwise if {@code false} throw an IOException.
     * @throws NullPointerException  if any of the given {@code File}s are {@code null}.
     * @throws FileExistsException   if the destination file exists.
     * @throws FileNotFoundException if the source file does not exist.
     * @throws IOException           if source or destination is invalid.
     * @throws IOException           if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void moveFileToDirectory(final File srcFile, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(srcFile, destDir);
        if (!destDir.exists() && createDestDir) {
            mkdirs(destDir);
        }
        requireExistsChecked(destDir, "destDir");
        requireDirectory(destDir, "destDir");
        moveFile(srcFile, new File(destDir, srcFile.getName()));
    }

    /**
     * 将文件或目录移动到目标目录。
     * <p>
     * 当目标在另一个文件系统上时，执行“复制和删除”操作。
     * </p>
     *
     * @param src           the file or directory to be moved.
     * @param destDir       the destination directory.
     * @param createDestDir If {@code true} create the destination directory, otherwise if {@code false} throw an IOException.
     * @throws NullPointerException  if any of the given {@code File}s are {@code null}.
     * @throws FileExistsException   if the directory or file exists in the destination directory.
     * @throws FileNotFoundException if the source file does not exist.
     * @throws IOException           if source or destination is invalid.
     * @throws IOException           if an error occurs or setting the last-modified time didn't succeeded.
     */
    public static void moveToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(src, destDir);
        if (src.isDirectory()) {
            moveDirectoryToDirectory(src, destDir, createDestDir);
        } else {
            moveFileToDirectory(src, destDir, createDestDir);
        }
    }

    /**
     * 通过打开或创建文件创建新的OutputStream，返回可用于向文件写入字节的输出流。
     *
     * @param append Whether or not to append.
     * @param file   the File.
     * @return a new OutputStream.
     * @throws IOException if an I/O error occurs.
     * @see PathUtils#newOutputStream(Path, boolean)
     */
    public static OutputStream newOutputStream(final File file, final boolean append) throws IOException {
        return PathUtils.newOutputStream(Objects.requireNonNull(file, "file").toPath(), append);
    }

    /**
     * 为指定的文件打开{@link FileInputStream}，提供比简单调用{@code new FileInputStream(file)}更好的错误消息。
     * <p>
     * 在方法的最后，流要么被成功打开，要么抛出异常。
     * </p>
     * <p>
     * An exception is thrown if the file does not exist. An exception is thrown if the file object exists but is a directory. An exception is thrown if the file exists but cannot
     * be read.
     * </p>
     *
     * @param file the file to open for input, must not be {@code null}
     * @return a new {@link FileInputStream} for the specified file
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           See FileNotFoundException above, FileNotFoundException is a subclass of IOException.
     */
    public static FileInputStream openInputStream(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return new FileInputStream(file);
    }

    /**
     * 为指定的文件打开{@link FileOutputStream}，如果父目录不存在，则检查并创建父目录。
     * <p>
     * 在方法的最后，流要么被成功打开，要么抛出异常。
     * </p>
     * <p>
     * The parent directory will be created if it does not exist. The file will be created if it does not exist. An exception is thrown if the file object exists but is a
     * directory. An exception is thrown if the file exists but cannot be written to. An exception is thrown if the parent directory cannot be created.
     * </p>
     *
     * @param file the file to open for output, must not be {@code null}
     * @return a new {@link FileOutputStream} for the specified file
     * @throws NullPointerException     if the file object is {@code null}.
     * @throws IllegalArgumentException if the file object is a directory
     * @throws IllegalArgumentException if the file is not writable.
     * @throws IOException              if the directories could not be created.
     */
    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * 为指定的文件打开{@link FileOutputStream}，如果父目录不存在，则检查并创建父目录。
     * <p>
     * 在方法的最后，流要么被成功打开，要么抛出异常。
     * </p>
     * <p>
     * The parent directory will be created if it does not exist. The file will be created if it does not exist. An exception is thrown if the file object exists but is a
     * directory. An exception is thrown if the file exists but cannot be written to. An exception is thrown if the parent directory cannot be created.
     * </p>
     *
     * @param file   the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws NullPointerException     if the file object is {@code null}.
     * @throws IllegalArgumentException if the file object is a directory
     * @throws IllegalArgumentException if the file is not writable.
     * @throws IOException              if the directories could not be created.
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.exists()) {
            requireFile(file, "file");
            requireCanWrite(file, "file");
        } else {
            createParentDirectories(file);
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 将文件的内容读入字节数组。文件总是关闭的。
     *
     * @param file the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           if an I/O error occurs.
     */
    public static byte[] readFileToByteArray(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return Files.readAllBytes(file.toPath());
    }


    /**
     * 将文件的内容读入String。文件总是关闭的。
     *
     * @param file        the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           if an I/O error occurs.
     */
    public static String readFileToString(final File file, final Charset charsetName) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return IOUtils.toString(inputStream, CharsetUtils.toCharset(charsetName));
        }
    }

    /**
     * 将文件的内容读入String。文件总是关闭的。
     *
     * @param file        the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws NullPointerException        if file is {@code null}.
     * @throws FileNotFoundException       if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for
     *                                     reading.
     * @throws IOException                 if an I/O error occurs.
     * @throws UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the named charset is unavailable.
     */
    public static String readFileToString(final File file, final String charsetName) throws IOException {
        return readFileToString(file, CharsetUtils.toCharset(charsetName));
    }


    /**
     * 将文件的内容逐行读取到字符串列表中。文件总是关闭的。
     *
     * @param file    the file to read, must not be {@code null}
     * @param charset the charset to use, {@code null} means platform default
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws NullPointerException  if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws IOException           if an I/O error occurs.
     */
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        return Files.readAllLines(file.toPath(), charset);
    }


    /**
     * 将文件的内容逐行读取到字符串列表中。文件总是关闭的。
     *
     * @param file        the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws NullPointerException        if file is {@code null}.
     * @throws FileNotFoundException       if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for
     *                                     reading.
     * @throws IOException                 if an I/O error occurs.
     * @throws UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the named charset is unavailable.
     */
    public static List<String> readLines(final File file, final String charsetName) throws IOException {
        return readLines(file, CharsetUtils.toCharset(charsetName));
    }

    private static void requireAbsent(final File file, final String name) throws FileExistsException {
        if (file.exists()) {
            throw new FileExistsException(String.format("File element in parameter '%s' already exists: '%s'", name, file));
        }
    }

    /**
     * 如果给定文件的规范表示相等，抛出IllegalArgumentException异常。
     *
     * @param file1 The first file to compare.
     * @param file2 The second file to compare.
     * @throws IOException              if an I/O error occurs.
     * @throws IllegalArgumentException if the given files' canonical representations are equal.
     */
    private static void requireCanonicalPathsNotEquals(final File file1, final File file2) throws IOException {
        final String canonicalPath = file1.getCanonicalPath();
        if (canonicalPath.equals(file2.getCanonicalPath())) {
            throw new IllegalArgumentException(String
                    .format("File canonical paths are equal: '%s' (file1='%s', file2='%s')", canonicalPath, file1, file2));
        }
    }

    /**
     * 如果文件不可写，抛出{@link IllegalArgumentException}。这提供了比拒绝普通访问更精确的异常消息。
     *
     * @param file The file to test.
     * @param name The parameter name to use in the exception message.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the file is not writable.
     */
    private static void requireCanWrite(final File file, final String name) {
        Objects.requireNonNull(file, "file");
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File parameter '" + name + " is not writable: '" + file + "'");
        }
    }

    /**
     * 要求给定的{@code File}是一个目录。
     *
     * @param directory The {@code File} to check.
     * @param name      The parameter name to use in the exception message in case of null input or if the file is not a directory.
     * @return the given directory.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     */
    private static File requireDirectory(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a directory: '" + directory + "'");
        }
        return directory;
    }

    /**
     * 要求给定的{@code File}存在并且是一个目录。
     *
     * @param directory The {@code File} to check.
     * @param name      The parameter name to use in the exception message in case of null input.
     * @return the given directory.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     */
    private static File requireDirectoryExists(final File directory, final String name) {
        requireExists(directory, name);
        requireDirectory(directory, name);
        return directory;
    }

    /**
     * 要求给定的{@code File}是一个目录(如果它存在的话)。
     *
     * @param directory The {@code File} to check.
     * @param name      The parameter name to use in the exception message in case of null input.
     * @return the given directory.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} exists but is not a directory.
     */
    private static File requireDirectoryIfExists(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (directory.exists()) {
            requireDirectory(directory, name);
        }
        return directory;
    }

    /**
     * 要求两个文件长度相等。
     *
     * @param srcFile  Source file.
     * @param destFile Destination file.
     * @param srcLen   Source file length.
     * @param dstLen   Destination file length
     * @throws IOException Thrown when the given sizes are not equal.
     */
    private static void requireEqualSizes(final File srcFile, final File destFile, final long srcLen, final long dstLen) throws IOException {
        if (srcLen != dstLen) {
            throw new IOException(
                    "Failed to copy full contents from '" + srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
    }

    /**
     * 要求给定的{@code File}存在，如果不存在则抛出{@link IllegalArgumentException}。
     *
     * @param file          The {@code File} to check.
     * @param fileParamName The parameter name to use in the exception message in case of {@code null} input.
     * @return the given file.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist.
     */
    private static File requireExists(final File file, final String fileParamName) {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }

    /**
     * 要求给定的{@code File}存在，如果不存在则抛出{@link FileNotFoundException}。
     *
     * @param file          The {@code File} to check.
     * @param fileParamName The parameter name to use in the exception message in case of {@code null} input.
     * @return the given file.
     * @throws NullPointerException  if the given {@code File} is {@code null}.
     * @throws FileNotFoundException if the given {@code File} does not exist.
     */
    private static File requireExistsChecked(final File file, final String fileParamName) throws FileNotFoundException {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new FileNotFoundException(
                    "File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }

    /**
     * 要求给定的{@code File}是一个文件。
     *
     * @param file The {@code File} to check.
     * @param name The parameter name to use in the exception message.
     * @return the given file.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a file.
     */
    private static File requireFile(final File file, final String name) {
        Objects.requireNonNull(file, name);
        if (!file.isFile()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a file: " + file);
        }
        return file;
    }

    /**
     * 需要文件复制操作的参数属性。
     *
     * @param source      the source file
     * @param destination the destination
     * @throws NullPointerException  if any of the given {@code File}s are {@code null}.
     * @throws FileNotFoundException if the source does not exist.
     */
    private static void requireFileCopy(final File source, final File destination) throws FileNotFoundException {
        requireExistsChecked(source, "source");
        Objects.requireNonNull(destination, "destination");
    }

    /**
     * 要求给定的{@code File}是一个文件(如果它存在)。
     *
     * @param file The {@code File} to check.
     * @param name The parameter name to use in the exception message in case of null input.
     * @return the given directory.
     * @throws NullPointerException     if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does exists but is not a directory.
     */
    private static File requireFileIfExists(final File file, final String name) {
        Objects.requireNonNull(file, name);
        return file.exists() ? requireFile(file, name) : file;
    }

    /**
     * 将给定的{@code targetFile}的最后修改日期设置为{@code sourceFile}的值。
     *
     * @param sourceFile The source file to query.
     * @param targetFile The target file or directory to set.
     * @throws NullPointerException if sourceFile is {@code null}.
     * @throws NullPointerException if targetFile is {@code null}.
     * @throws IOException          if setting the last-modified time failed.
     */
    private static void setLastModified(final File sourceFile, final File targetFile) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        Objects.requireNonNull(targetFile, "targetFile");
        if (targetFile.isFile()) {
            PathUtils.setLastModifiedTime(targetFile.toPath(), sourceFile.toPath());
        } else {
            setLastModified(targetFile, lastModified(sourceFile));
        }
    }

    /**
     * 将给定的{@code targetFile}的最后修改日期设置为给定值。
     *
     * @param file       The source file to query.
     * @param timeMillis The new last-modified time, measured in milliseconds since the epoch 01-01-1970 GMT.
     * @throws NullPointerException if file is {@code null}.
     * @throws IOException          if setting the last-modified time failed.
     */
    private static void setLastModified(final File file, final long timeMillis) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!file.setLastModified(timeMillis)) {
            throw new IOException(String.format("Failed setLastModified(%s) on '%s'", timeMillis, file));
        }
    }

    /**
     * 返回指定文件或目录的大小。如果提供的{@link File}是一个常规文件，则返回文件的长度。如果参数是一个目录，则递归计算目录的大小。如果目录或子目录受到安全限制，则其大小将不包括在内。
     * <p>
     * Note that overflow is not detected, and the return value may be negative if overflow occurs. See {@link #sizeOfAsBigInteger(File)} for an alternative method that does not
     * overflow.
     * </p>
     *
     * @param file the regular file or directory to return the size of (must not be {@code null}).
     * @return the length of the file, or recursive size of the directory, provided (in bytes).
     * @throws NullPointerException     if the file is {@code null}.
     * @throws IllegalArgumentException if the file does not exist.
     * @throws UncheckedIOException     if an IO error occurs.
     */
    public static long sizeOf(final File file) {
        requireExists(file, "file");
        return UncheckedIO.get(() -> PathUtils.sizeOf(file.toPath()));
    }

    /**
     * 返回指定文件或目录的大小。如果提供的{@link File}是一个常规文件，则返回文件的长度。如果参数是一个目录，则递归计算目录的大小。如果目录或子目录受到安全限制，则其大小将不包括在内。D
     *
     * @param file the regular file or directory to return the size of (must not be {@code null}).
     * @return the length of the file, or recursive size of the directory, provided (in bytes).
     * @throws NullPointerException     if the file is {@code null}.
     * @throws IllegalArgumentException if the file does not exist.
     * @throws UncheckedIOException     if an IO error occurs.
     */
    public static BigInteger sizeOfAsBigInteger(final File file) {
        requireExists(file, "file");
        return UncheckedIO.get(() -> PathUtils.sizeOfAsBigInteger(file.toPath()));
    }

    /**
     * 递归地计算目录的大小(所有文件的长度之和)。
     * <p>
     * 注意，没有检测到溢出，如果发生溢出，返回值可能为负数. See {@link #sizeOfDirectoryAsBigInteger(File)} for an alternative method that does not overflow.
     * </p>
     *
     * @param directory directory to inspect, must not be {@code null}.
     * @return size of directory in bytes, 0 if directory is security restricted, a negative number when the real total is greater than {@link Long#MAX_VALUE}.
     * @throws NullPointerException if the directory is {@code null}.
     * @throws UncheckedIOException if an IO error occurs.
     */
    public static long sizeOfDirectory(final File directory) {
        requireDirectoryExists(directory, "directory");
        return UncheckedIO.get(() -> PathUtils.sizeOfDirectory(directory.toPath()));
    }

    /**
     * 递归地计算目录的大小(所有文件的长度之和)。
     *
     * @param directory directory to inspect, must not be {@code null}.
     * @return size of directory in bytes, 0 if directory is security restricted.
     * @throws NullPointerException if the directory is {@code null}.
     * @throws UncheckedIOException if an IO error occurs.
     */
    public static BigInteger sizeOfDirectoryAsBigInteger(final File directory) {
        requireDirectoryExists(directory, "directory");
        return UncheckedIO.get(() -> PathUtils.sizeOfDirectoryAsBigInteger(directory.toPath()));
    }

    /**
     * 在与扩展名数组匹配的给定目录(以及可选的其子目录)中的文件上的流。
     *
     * @param directory  the directory to search in
     * @param recursive  if true all subdirectories are searched as well
     * @param extensions an array of extensions, ex. {"java","xml"}. If this parameter is {@code null}, all files are returned.
     * @return an iterator of java.io.File with the matching files
     * @throws IOException if an I/O error is thrown when accessing the starting file.
     */
    public static Stream<File> streamFiles(final File directory, final boolean recursive, final String... extensions) throws IOException {
        // @formatter:off
        final IOFileFilter filter = extensions == null
                ? FileFileFilter.INSTANCE
                : FileFileFilter.INSTANCE.and(new SuffixFileFilter(toSuffixes(extensions)));
        // @formatter:on
        return PathUtils.walk(directory.toPath(), filter, toMaxDepth(recursive), false, FileVisitOption.FOLLOW_LINKS).map(Path::toFile);
    }

    /**
     * 从{@code URL}转换为{@code File}。
     * <p>
     * From version 1.1 this method will decode the URL. Syntax such as {@code file:///my%20docs/file.txt} will be correctly decoded to {@code /my docs/file.txt}. Starting with
     * version 1.5, this method uses UTF-8 to decode percent-encoded octets to characters. Additionally, malformed percent-encoded octets are handled leniently by passing them
     * through literally.
     * </p>
     *
     * @param url the file URL to convert, {@code null} returns {@code null}
     * @return the equivalent {@code File} object, or {@code null} if the URL's protocol is not {@code file}
     */
    public static File toFile(final URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        final String filename = url.getFile().replace('/', File.separatorChar);
        return new File(decodeUrl(filename));
    }

    /**
     * 将{@code URL}数组中的每一个转换为{@code File}。
     * <p>
     * Returns an array of the same size as the input. If the input is {@code null}, an empty array is returned. If the input contains {@code null}, the output array contains
     * {@code null} at the same index.
     * </p>
     * <p>
     * This method will decode the URL. Syntax such as {@code file:///my%20docs/file.txt} will be correctly decoded to {@code /my docs/file.txt}.
     * </p>
     *
     * @param urls the file URLs to convert, {@code null} returns empty array
     * @return a non-{@code null} array of Files matching the input, with a {@code null} item if there was a {@code null} at that index in the input array
     * @throws IllegalArgumentException if any file is not a URL file
     * @throws IllegalArgumentException if any file is incorrectly encoded
     */
    public static File[] toFiles(final URL... urls) {
        if (IOUtils.length(urls) == 0) {
            return EMPTY_FILE_ARRAY;
        }
        final File[] files = new File[urls.length];
        for (int i = 0; i < urls.length; i++) {
            final URL url = urls[i];
            if (url != null) {
                if (!"file".equalsIgnoreCase(url.getProtocol())) {
                    throw new IllegalArgumentException("Can only convert file URL to a File: " + url);
                }
                files[i] = toFile(url);
            }
        }
        return files;
    }

    private static List<File> toList(final Stream<File> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * 将是否递归转换为递归最大深度。
     *
     * @param recursive whether or not to recurse
     * @return the recursion depth
     */
    private static int toMaxDepth(final boolean recursive) {
        return recursive ? Integer.MAX_VALUE : 1;
    }

    /**
     * Converts an array of file extensions to suffixes.
     *
     * @param extensions an array of extensions. Format: {"java", "xml"}
     * @return an array of suffixes. Format: {".java", ".xml"}
     * @throws NullPointerException if the parameter is null
     */
    private static String[] toSuffixes(final String... extensions) {
        Objects.requireNonNull(extensions, "extensions");
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }

    /**
     * 实现类似Unix "touch"实用程序的行为。创建一个大小为0的新文件，或者，如果文件存在，则更新文件的修改时间。
     * <p>
     * NOTE: As from v1.3, this method throws an IOException if the last modified date of the file cannot be set. Also, as from v1.3 this method creates parent directories if they
     * do not exist.
     * </p>
     *
     * @param file the File to touch.
     * @throws NullPointerException if the parameter is {@code null}.
     * @throws IOException          if setting the last-modified time failed or an I/O problem occurs.
     */
    public static void touch(final File file) throws IOException {
        PathUtils.touch(Objects.requireNonNull(file, "file").toPath());
    }

    /**
     * 将{@code File}的每个数组转换为{@code URL}。
     * <p>
     * 返回与输入大小相同的数组。
     * </p>
     *
     * @param files the files to convert, must not be {@code null}
     * @return an array of URLs matching the input
     * @throws IOException          if a file cannot be converted
     * @throws NullPointerException if the parameter is null
     */
    public static URL[] toURLs(final File... files) throws IOException {
        Objects.requireNonNull(files, "files");
        final URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    /**
     * 验证给定的参数。
     * <ul>
     * <li>Throws {@link NullPointerException} if {@code source} is null</li>
     * <li>Throws {@link NullPointerException} if {@code destination} is null</li>
     * <li>Throws {@link FileNotFoundException} if {@code source} does not exist</li>
     * </ul>
     *
     * @param source      the file or directory to be moved.
     * @param destination the destination file or directory.
     * @throws FileNotFoundException if the source file does not exist.
     */
    private static void validateMoveParameters(final File source, final File destination) throws FileNotFoundException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        if (!source.exists()) {
            throw new FileNotFoundException("Source '" + source + "' does not exist");
        }
    }

    /**
     * 等待文件系统传播创建的文件，并设置超时。
     * <p>
     * This method repeatedly tests {@link Files#exists(Path, LinkOption...)} until it returns true up to the maximum time specified in seconds.
     * </p>
     *
     * @param file    the file to check, must not be {@code null}
     * @param seconds the maximum time in seconds to wait
     * @return true if file exists
     * @throws NullPointerException if the file is {@code null}
     */
    public static boolean waitFor(final File file, final int seconds) {
        Objects.requireNonNull(file, "file");
        return PathUtils.waitFor(file.toPath(), Duration.ofSeconds(seconds), PathUtils.EMPTY_LINK_OPTION_ARRAY);
    }

    // Private method, must be invoked will a directory parameter

    /**
     * 写入一个CharSequence到一个文件，如果该文件不存在则创建该文件。
     *
     * @param file    the file to write
     * @param data    the content to write to the file
     * @param charset the name of the requested charset, {@code null} means platform default
     * @throws IOException in case of an I/O error
     */
    public static void write(final File file, final CharSequence data, final Charset charset) throws IOException {
        write(file, data, charset, false);
    }

    /**
     * 写入一个CharSequence到一个文件，如果该文件不存在则创建该文件。
     *
     * @param file    the file to write
     * @param data    the content to write to the file
     * @param charset the charset to use, {@code null} means platform default
     * @param append  if {@code true}, then the data will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void write(final File file, final CharSequence data, final Charset charset, final boolean append) throws IOException {
        writeStringToFile(file, Objects.toString(data, null), charset, append);
    }

    /**
     * 写入一个CharSequence到一个文件，如果该文件不存在则创建该文件。
     *
     * @param file        the file to write
     * @param data        the content to write to the file
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void write(final File file, final CharSequence data, final String charsetName) throws IOException {
        write(file, data, charsetName, false);
    }

    // Must be called with a directory

    /**
     * 写入一个CharSequence到一个文件，如果该文件不存在则创建该文件。
     *
     * @param file        the file to write
     * @param data        the content to write to the file
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param append      if {@code true}, then the data will be added to the end of the file rather than overwriting
     * @throws IOException                 in case of an I/O error
     * @throws UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported by the
     *                                     VM
     */
    public static void write(final File file, final CharSequence data, final String charsetName, final boolean append) throws IOException {
        write(file, data, CharsetUtils.toCharset(charsetName), append);
    }

    /**
     * 将字节数组写入文件，如果文件不存在则创建该文件。
     * <p>
     * 注意:从v1.3开始，如果文件的父目录不存在，就会创建它们。
     * </p>
     *
     * @param file the file to write to
     * @param data the content to write to the file
     * @throws IOException in case of an I/O error
     */
    public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    /**
     * 将字节数组写入文件，如果文件不存在则创建该文件。
     *
     * @param file   the file to write to
     * @param data   the content to write to the file
     * @param append if {@code true}, then bytes will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append) throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }

    /**
     * 将从offset {@code off}开始的指定字节数组中的{@code len}字节写入文件，如果文件不存在则创建该文件。
     *
     * @param file the file to write to
     * @param data the content to write to the file
     * @param off  the start offset in the data
     * @param len  the number of bytes to write
     * @throws IOException in case of an I/O error
     */
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len) throws IOException {
        writeByteArrayToFile(file, data, off, len, false);
    }

    /**
     * 将从offset {@code off}开始的指定字节数组中的{@code len}字节写入文件，如果文件不存在则创建该文件。
     *
     * @param file   the file to write to
     * @param data   the content to write to the file
     * @param off    the start offset in the data
     * @param len    the number of bytes to write
     * @param append if {@code true}, then bytes will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len, final boolean append) throws IOException {
        try (OutputStream out = newOutputStream(file, append)) {
            out.write(data, off, len);
        }
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用默认的VM编码和默认行结束。
     *
     * @param file  the file to write to
     * @param lines the lines to write, {@code null} entries produce blank lines
     * @throws IOException in case of an I/O error
     */
    public static void writeLines(final File file, final Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }


    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用默认的VM编码和默认行结束。
     *
     * @param file   the file to write to
     * @param lines  the lines to write, {@code null} entries produce blank lines
     * @param append if {@code true}, then the lines will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void writeLines(final File file, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用默认的VM编码和指定的行结束。
     *
     * @param file       the file to write to
     * @param lines      the lines to write, {@code null} entries produce blank lines
     * @param lineEnding the line separator to use, {@code null} is system default
     * @throws IOException in case of an I/O error
     */
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用默认的VM编码和指定的行结束。
     *
     * @param file       the file to write to
     * @param lines      the lines to write, {@code null} entries produce blank lines
     * @param lineEnding the line separator to use, {@code null} is system default
     * @param append     if {@code true}, then the lines will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding, final boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用指定的字符编码和默认行结束。
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created if they do not exist.
     * </p>
     *
     * @param file        the file to write to
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param lines       the lines to write, {@code null} entries produce blank lines
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines) throws IOException {
        writeLines(file, charsetName, lines, null, false);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}，可选追加。将使用指定的字符编码和默认行结束。
     *
     * @param file        the file to write to
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param lines       the lines to write, {@code null} entries produce blank lines
     * @param append      if {@code true}, then the lines will be added to the end of the file rather than overwriting
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, charsetName, lines, null, append);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用指定的字符编码和行结束。
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created if they do not exist.
     * </p>
     *
     * @param file        the file to write to
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param lines       the lines to write, {@code null} entries produce blank lines
     * @param lineEnding  the line separator to use, {@code null} is system default
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, charsetName, lines, lineEnding, false);
    }

    /**
     * 将集合中每个项的{@code toString()}值逐行写入指定的{@code File}。将使用指定的字符编码和行结束。
     *
     * @param file        the file to write to
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param lines       the lines to write, {@code null} entries produce blank lines
     * @param lineEnding  the line separator to use, {@code null} is system default
     * @param append      if {@code true}, then the lines will be added to the end of the file rather than overwriting
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final String lineEnding, final boolean append)
            throws IOException {
        try (OutputStream out = new BufferedOutputStream(newOutputStream(file, append))) {
            IOUtils.writeLines(lines, lineEnding, out, charsetName);
        }
    }

    /**
     * 将字符串写入文件，如果文件不存在则创建该文件。
     * <p>
     * 注意:从v1.3开始，如果文件的父目录不存在，就会创建它们。
     * </p>
     *
     * @param file    the file to write
     * @param data    the content to write to the file
     * @param charset the charset to use, {@code null} means platform default
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeStringToFile(final File file, final String data, final Charset charset) throws IOException {
        writeStringToFile(file, data, charset, false);
    }

    /**
     * 将字符串写入文件，如果文件不存在则创建该文件。
     *
     * @param file    the file to write
     * @param data    the content to write to the file
     * @param charset the charset to use, {@code null} means platform default
     * @param append  if {@code true}, then the String will be added to the end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public static void writeStringToFile(final File file, final String data, final Charset charset, final boolean append) throws IOException {
        try (OutputStream out = newOutputStream(file, append)) {
            IOUtils.write(data, out, charset);
        }
    }

    /**
     * 将字符串写入文件，如果文件不存在则创建该文件。
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created if they do not exist.
     * </p>
     *
     * @param file        the file to write
     * @param data        the content to write to the file
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @throws IOException                          in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeStringToFile(final File file, final String data, final String charsetName) throws IOException {
        writeStringToFile(file, data, charsetName, false);
    }

    /**
     * 将字符串写入文件，如果文件不存在则创建该文件。
     *
     * @param file        the file to write
     * @param data        the content to write to the file
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @param append      if {@code true}, then the String will be added to the end of the file rather than overwriting
     * @throws IOException                 in case of an I/O error
     * @throws UnsupportedCharsetException thrown instead of {@link java.io .UnsupportedEncodingException} in version 2.2 if the encoding is not supported by the
     *                                     VM
     */
    public static void writeStringToFile(final File file, final String data, final String charsetName, final boolean append) throws IOException {
        writeStringToFile(file, data, CharsetUtils.toCharset(charsetName), append);
    }


    /**
     * 创建文件夹，如果存在直接返回此文件夹<br> 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = new File(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br> 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            mkdirsSafely(dir, 5, 1);
        }
        return dir;
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir         待创建的目录
     * @param tryCount    最大尝试次数
     * @param sleepMillis 线程等待的毫秒数
     * @return true表示创建成功，false表示创建失败
     * @author z8g
     */
    public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        for (int i = 1; i <= tryCount; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
            // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (dir.exists()) {
                return true;
            }
            ThreadUtil.sleep(sleepMillis);
        }
        return dir.exists();
    }

    /**
     * 根据压缩包中的路径构建目录结构，在Win下直接构建，在Linux下拆分路径单独构建
     *
     * @param outFile  最外部路径
     * @param fileName 文件名，可以包含路径
     * @return 文件或目录
     * @since 5.0.5
     */
    private static File buildFile(File outFile, String fileName) {
        // 替换Windows路径分隔符为Linux路径分隔符，便于统一处理
        fileName = fileName.replace(CharUtils.BACKSLASH, CharUtils.SLASH);
        if (!FilenameUtils.isSystemWindows()
                // 检查文件名中是否包含"/"，不考虑以"/"结尾的情况
                && fileName.lastIndexOf(CharUtils.SLASH, fileName.length() - 2) > 0) {
            // 在Linux下多层目录创建存在问题，/会被当成文件名的一部分，此处做处理
            // 使用/拆分路径（zip中无\），级联创建父目录
            final String[] pathParts = StringUtils.split(fileName, StringUtils.SLASH);
            final int lastPartIndex = pathParts.length - 1;//目录个数
            for (int i = 0; i < lastPartIndex; i++) {
                //由于路径拆分，slip不检查，在最后一步检查
                outFile = new File(outFile, pathParts[i]);
            }
            //noinspection ResultOfMethodCallIgnored
            outFile.mkdirs();
            // 最后一个部分如果非空，作为文件名
            fileName = pathParts[lastPartIndex];
        }
        return new File(outFile, fileName);
    }

}
