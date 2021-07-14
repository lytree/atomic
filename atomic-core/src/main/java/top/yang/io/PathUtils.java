package top.yang.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author PrideYang
 */
public class PathUtils {
//    /**
//     * Cleans a directory including sub-directories without deleting directories.
//     *
//     * @param directory directory to clean.
//     * @return The visitation path counters.
//     * @throws IOException if an I/O error is thrown by a visitor method.
//     */
//    public static PathCounters cleanDirectory(final Path directory) throws IOException {
//        return cleanDirectory(directory, EMPTY_DELETE_OPTION_ARRAY);
//    }
//
//    /**
//     * Cleans a directory including sub-directories without deleting directories.
//     *
//     * @param directory     directory to clean.
//     * @param deleteOptions How to handle deletion.
//     * @return The visitation path counters.
//     * @throws IOException if an I/O error is thrown by a visitor method.
//     * @since 2.8.0
//     */
//    public static PathCounters cleanDirectory(final Path directory, final DeleteOption... deleteOptions)
//            throws IOException {
//        return visitFileTree(new CleaningPathVisitor(Counters.longPathCounters(), deleteOptions), directory)
//                .getPathCounters();
//    }
//
//    /**
//     * Copies a directory to another directory.
//     *
//     * @param sourceDirectory The source directory.
//     * @param targetDirectory The target directory.
//     * @param copyOptions     Specifies how the copying should be done.
//     * @return The visitation path counters.
//     * @throws IOException if an I/O error is thrown by a visitor method.
//     */
//    public static PathCounters copyDirectory(final Path sourceDirectory, final Path targetDirectory,
//                                             final CopyOption... copyOptions) throws IOException {
//        final Path absoluteSource = sourceDirectory.toAbsolutePath();
//        return visitFileTree(
//                new CopyDirectoryVisitor(Counters.longPathCounters(), absoluteSource, targetDirectory, copyOptions),
//                absoluteSource).getPathCounters();
//    }

    /**
     * Copies a URL to a directory.
     *
     * @param sourceFile  The source URL.
     * @param targetFile  The target file.
     * @param copyOptions Specifies how the copying should be done.
     * @return The target file
     * @throws IOException if an I/O error occurs.
     * @see Files#copy(InputStream, Path, CopyOption...)
     */
    public static Path copyFile(final URL sourceFile, final Path targetFile, final CopyOption... copyOptions)
            throws IOException {
        try (final InputStream inputStream = sourceFile.openStream()) {
            Files.copy(inputStream, targetFile, copyOptions);
            return targetFile;
        }
    }

    /**
     * Copies a file to a directory.
     *
     * @param sourceFile      The source file.
     * @param targetDirectory The target directory.
     * @param copyOptions     Specifies how the copying should be done.
     * @return The target file
     * @throws IOException if an I/O error occurs.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static Path copyFileToDirectory(final Path sourceFile, final Path targetDirectory,
                                           final CopyOption... copyOptions) throws IOException {
        return Files.copy(sourceFile, targetDirectory.resolve(sourceFile.getFileName()), copyOptions);
    }

    /**
     * Copies a URL to a directory.
     *
     * @param sourceFile      The source URL.
     * @param targetDirectory The target directory.
     * @param copyOptions     Specifies how the copying should be done.
     * @return The target file
     * @throws IOException if an I/O error occurs.
     * @see Files#copy(InputStream, Path, CopyOption...)
     */
    public static Path copyFileToDirectory(final URL sourceFile, final Path targetDirectory,
                                           final CopyOption... copyOptions) throws IOException {
        try (final InputStream inputStream = sourceFile.openStream()) {
            Files.copy(inputStream, targetDirectory.resolve(sourceFile.getFile()), copyOptions);
            return targetDirectory;
        }
    }

}
