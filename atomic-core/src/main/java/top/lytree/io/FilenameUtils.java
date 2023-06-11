package top.lytree.io;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General file name and file path manipulation utilities.
 * <p>
 * When dealing with file names you can hit problems when moving from a Windows based development machine to a Unix based production machine. This class aims to help avoid those
 * problems.
 * </p>
 * <p>
 * <b>NOTE</b>: You may be able to avoid using this class entirely simply by
 * using JDK {@link File File} objects and the two argument constructor {@link File#File(File, String) File(File,String)}.
 * </p>
 * <p>
 * Most methods on this class are designed to work the same on both Unix and Windows. Those that don't include 'System', 'Unix' or 'Windows' in their name.
 * </p>
 * <p>
 * Most methods recognize both separators (forward and back), and both sets of prefixes. See the Javadoc of each method for details.
 * </p>
 * <p>
 * This class defines six components within a file name (example C:\dev\project\file.txt):
 * </p>
 * <ul>
 * <li>the prefix - C:\</li>
 * <li>the path - dev\project\</li>
 * <li>the full path - C:\dev\project\</li>
 * <li>the name - file.txt</li>
 * <li>the base name - file</li>
 * <li>the extension - txt</li>
 * </ul>
 * <p>
 * Note that this class works best if directory file names end with a separator.
 * If you omit the last separator, it is impossible to determine if the file name
 * corresponds to a file or a directory. As a result, we have chosen to say
 * it corresponds to a file.
 * </p>
 * <p>
 * This class only supports Unix and Windows style names.
 * Prefixes are matched as follows:
 * </p>
 * <pre>
 * Windows:
 * a\b\c.txt           --&gt; ""          --&gt; relative
 * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
 * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
 * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
 * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
 *
 * Unix:
 * a/b/c.txt           --&gt; ""          --&gt; relative
 * /a/b/c.txt          --&gt; "/"         --&gt; absolute
 * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
 * ~                   --&gt; "~/"        --&gt; current user (slash added)
 * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
 * ~user               --&gt; "~user/"    --&gt; named user (slash added)
 * </pre>
 * <p>
 * Both prefix styles are matched always, irrespective of the machine that you are
 * currently running on.
 * </p>
 * <p>
 * Origin of code: Excalibur, Alexandria, Tomcat, Commons-Utils.
 * </p>
 */
public class FilenameUtils {

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String EMPTY_STRING = "";

    private static final int NOT_FOUND = -1;

    /**
     * The extension separator character.
     */
    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * The extension separator String.
     */
    public static final String EXTENSION_SEPARATOR_STR = Character.toString(EXTENSION_SEPARATOR);

    /**
     * The Unix separator character.
     */
    private static final char UNIX_NAME_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_NAME_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_NAME_SEPARATOR = File.separatorChar;

    /**
     * The separator character that is the opposite of the system separator.
     */
    private static final char OTHER_SEPARATOR = flipSeparator(SYSTEM_NAME_SEPARATOR);

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private static final int IPV4_MAX_OCTET_VALUE = 255;

    private static final int IPV6_MAX_HEX_GROUPS = 8;

    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;

    private static final int MAX_UNSIGNED_SHORT = 0xffff;

    private static final int BASE_16 = 16;

    private static final Pattern REG_NAME_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*$");

    /**
     * 使用普通的命令行样式规则将fileName连接到基路径。
     * <p>
     * The effect is equivalent to resultant directory after changing directory to the first argument, followed by changing directory to the second argument.
     * </p>
     * <p>
     * The first argument is the base path, the second is the path to concatenate. The returned path is always normalized via {@link #normalize(String)}, thus {@code ..} is
     * handled.
     * </p>
     * <p>
     * If {@code pathToAdd} is absolute (has an absolute prefix), then it will be normalized and returned. Otherwise, the paths will be joined, normalized and returned.
     * </p>
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     * </p>
     * <pre>
     * /foo/      + bar        --&gt;  /foo/bar
     * /foo       + bar        --&gt;  /foo/bar
     * /foo       + /bar       --&gt;  /bar
     * /foo       + C:/bar     --&gt;  C:/bar
     * /foo       + C:bar      --&gt;  C:bar [1]
     * /foo/a/    + ../bar     --&gt;  /foo/bar
     * /foo/      + ../../bar  --&gt;  null
     * /foo/      + /bar       --&gt;  /bar
     * /foo/..    + /bar       --&gt;  /bar
     * /foo       + bar/c.txt  --&gt;  /foo/bar/c.txt
     * /foo/c.txt + bar        --&gt;  /foo/c.txt/bar [2]
     * </pre>
     * <p>
     * [1] Note that the Windows relative drive prefix is unreliable when used with this method.
     * </p>
     * <p>
     * [2] Note that the first parameter must be a path. If it ends with a name, then the name will be built into the concatenated path. If this might be a problem, use {@link
     * #getFullPath(String)} on the base path argument.
     * </p>
     *
     * @param basePath          the base path to attach to, always treated as a path
     * @param fullFileNameToAdd the fileName (or path) to attach to the base
     * @return the concatenated path, or null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String concat(final String basePath, final String fullFileNameToAdd) {
        final int prefix = getPrefixLength(fullFileNameToAdd);
        if (prefix < 0) {
            return null;
        }
        if (prefix > 0) {
            return normalize(fullFileNameToAdd);
        }
        if (basePath == null) {
            return null;
        }
        final int len = basePath.length();
        if (len == 0) {
            return normalize(fullFileNameToAdd);
        }
        final char ch = basePath.charAt(len - 1);
        if (isSeparator(ch)) {
            return normalize(basePath + fullFileNameToAdd);
        }
        return normalize(basePath + '/' + fullFileNameToAdd);
    }

    /**
     * 确定{@code 父目录}是否包含{@code 子目录}元素(文件或目录)。
     * <p>
     * 文件名应该是规范化的。
     * </p>
     * <p>
     * Edge cases:
     * <ul>
     * <li>A {@code directory} must not be null: if null, throw IllegalArgumentException</li>
     * <li>A directory does not contain itself: return false</li>
     * <li>A null child file is not contained in any parent: return false</li>
     * </ul>
     *
     * @param canonicalParent the file to consider as the parent.
     * @param canonicalChild  the file to consider as the child.
     * @return true is the candidate leaf is under by the specified composite. False otherwise.
     * @see FileUtils#directoryContains(File, File)
     */
    public static boolean directoryContains(final String canonicalParent, final String canonicalChild) {
        if (isEmpty(canonicalParent) || isEmpty(canonicalChild)) {
            return false;
        }

        if (IOCase.SYSTEM.checkEquals(canonicalParent, canonicalChild)) {
            return false;
        }

        final char separator = toSeparator(canonicalParent.charAt(0) == UNIX_NAME_SEPARATOR);
        final String parentWithEndSeparator = canonicalParent.charAt(canonicalParent.length() - 1) == separator ? canonicalParent : canonicalParent + separator;

        return IOCase.SYSTEM.checkStartsWith(canonicalChild, parentWithEndSeparator);
    }

    /**
     * 获取
     *
     * @param fileName         the fileName
     * @param includeSeparator 是否包含结束分隔符
     * @return the path
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    private static String doGetFullPath(final String fileName, final boolean includeSeparator) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        if (prefix >= fileName.length()) {
            if (includeSeparator) {
                return getPrefix(fileName);  // add end slash if necessary
            }
            return fileName;
        }
        final int index = indexOfLastSeparator(fileName);
        if (index < 0) {
            return fileName.substring(0, prefix);
        }
        int end = index + (includeSeparator ? 1 : 0);
        if (end == 0) {
            end++;
        }
        return fileName.substring(0, end);
    }

    /**
     * Does the work of getting the path.
     *
     * @param fileName     the fileName
     * @param separatorAdd 0表示省略结束分隔符，1表示返回它
     * @return the path
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    private static String doGetPath(final String fileName, final int separatorAdd) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        final int index = indexOfLastSeparator(fileName);
        final int endIndex = index + separatorAdd;
        if (prefix >= fileName.length() || index < 0 || prefix >= endIndex) {
            return EMPTY_STRING;
        }
        return requireNonNullChars(fileName.substring(prefix, endIndex));
    }

    /**
     * I内部方法来执行规范化。
     *
     * @param fileName      the fileName
     * @param separator     The separator character to use
     * @param keepSeparator true to keep the final separator
     * @return the normalized fileName
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    private static String doNormalize(final String fileName, final char separator, final boolean keepSeparator) {
        if (fileName == null) {
            return null;
        }

        requireNonNullChars(fileName);

        int size = fileName.length();
        if (size == 0) {
            return fileName;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }

        final char[] array = new char[size + 2];  // +1 for possible extra slash, +2 for arraycopy
        fileName.getChars(0, fileName.length(), array, 0);

        // fix separators throughout
        final char otherSeparator = flipSeparator(separator);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == otherSeparator) {
                array[i] = separator;
            }
        }

        // add extra separator on the end to simplify code below
        boolean lastIsDirectory = true;
        if (array[size - 1] != separator) {
            array[size++] = separator;
            lastIsDirectory = false;
        }

        // adjoining slashes
        // If we get here, prefix can only be 0 or greater, size 1 or greater
        // If prefix is 0, set loop start to 1 to prevent index errors
        for (int i = prefix != 0 ? prefix : 1; i < size; i++) {
            if (array[i] == separator && array[i - 1] == separator) {
                System.arraycopy(array, i, array, i - 1, size - i);
                size--;
                i--;
            }
        }

        // dot slash
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == separator && array[i - 1] == '.' &&
                    (i == prefix + 1 || array[i - 2] == separator)) {
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, i + 1, array, i - 1, size - i);
                size -= 2;
                i--;
            }
        }

        // double dot slash
        outer:
        for (int i = prefix + 2; i < size; i++) {
            if (array[i] == separator && array[i - 1] == '.' && array[i - 2] == '.' &&
                    (i == prefix + 2 || array[i - 3] == separator)) {
                if (i == prefix + 2) {
                    return null;
                }
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                int j;
                for (j = i - 4; j >= prefix; j--) {
                    if (array[j] == separator) {
                        // remove b/../ from a/b/../c
                        System.arraycopy(array, i + 1, array, j + 1, size - i);
                        size -= i - j;
                        i = j + 1;
                        continue outer;
                    }
                }
                // remove a/../ from a/../c
                System.arraycopy(array, i + 1, array, prefix, size - i);
                size -= i + 1 - prefix;
                i = prefix + 1;
            }
        }

        if (size <= 0) {  // should never be less than 0
            return EMPTY_STRING;
        }
        if (size <= prefix) {  // should never be less than prefix
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size);  // keep trailing separator
        }
        return new String(array, 0, size - 1);  // lose trailing separator
    }

    /**
     * Checks whether two fileNames are equal exactly.
     * <p>
     * No processing is performed on the fileNames other than comparison, thus this is merely a null-safe case-sensitive equals.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SENSITIVE
     */
    public static boolean equals(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SENSITIVE);
    }

    /**
     * Checks whether two fileNames are equal, optionally normalizing and providing control over the case-sensitivity.
     *
     * @param fileName1  the first fileName to query, may be null
     * @param fileName2  the second fileName to query, may be null
     * @param normalized whether to normalize the fileNames
     * @param ioCase     what case sensitivity rule to use, null means case-sensitive
     * @return true if the fileNames are equal, null equals null
     */
    public static boolean equals(String fileName1, String fileName2, final boolean normalized, final IOCase ioCase) {

        if (fileName1 == null || fileName2 == null) {
            return fileName1 == null && fileName2 == null;
        }
        if (normalized) {
            fileName1 = normalize(fileName1);
            if (fileName1 == null) {
                return false;
            }
            fileName2 = normalize(fileName2);
            if (fileName2 == null) {
                return false;
            }
        }
        return IOCase.value(ioCase, IOCase.SENSITIVE).checkEquals(fileName1, fileName2);
    }

    /**
     * Checks whether two fileNames are equal after both have been normalized.
     * <p>
     * Both fileNames are first passed to {@link #normalize(String)}. The check is then performed in a case-sensitive manner.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SENSITIVE
     */
    public static boolean equalsNormalized(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SENSITIVE);
    }

    /**
     * Checks whether two fileNames are equal after both have been normalized and using the case rules of the system.
     * <p>
     * Both fileNames are first passed to {@link #normalize(String)}. The check is then performed case-sensitive on Unix and case-insensitive on Windows.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SYSTEM
     */
    public static boolean equalsNormalizedOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SYSTEM);
    }

    /**
     * Checks whether two fileNames are equal using the case rules of the system.
     * <p>
     * No processing is performed on the fileNames other than comparison. The check is case-sensitive on Unix and case-insensitive on Windows.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SYSTEM
     */
    public static boolean equalsOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SYSTEM);
    }

    /**
     * Flips the Windows name separator to Linux and vice-versa.
     *
     * @param ch The Windows or Linux name separator.
     * @return The Windows or Linux name separator.
     */
    static char flipSeparator(final char ch) {
        if (ch == UNIX_NAME_SEPARATOR) {
            return WINDOWS_NAME_SEPARATOR;
        }
        if (ch == WINDOWS_NAME_SEPARATOR) {
            return UNIX_NAME_SEPARATOR;
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    /**
     * Special handling for NTFS ADS: Don't accept colon in the fileName.
     *
     * @param fileName a file name
     * @return ADS offsets.
     */
    private static int getAdsCriticalOffset(final String fileName) {
        // Step 1: Remove leading path segments.
        final int offset1 = fileName.lastIndexOf(SYSTEM_NAME_SEPARATOR);
        final int offset2 = fileName.lastIndexOf(OTHER_SEPARATOR);
        if (offset1 == -1) {
            if (offset2 == -1) {
                return 0;
            }
            return offset2 + 1;
        }
        if (offset2 == -1) {
            return offset1 + 1;
        }
        return Math.max(offset1, offset2) + 1;
    }

    /**
     * Gets the base name, minus the full path and extension, from a full fileName.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash and before the last dot is returned.
     * </p>
     * <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String getBaseName(final String fileName) {
        return removeExtension(getName(fileName));
    }

    /**
     * Gets the extension of a fileName.
     * <p>
     * This method returns the textual part of the fileName after the last dot. There must be no directory separator after the dot.
     * </p>
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the exception of a possible {@link IllegalArgumentException} on Windows (see
     * below).
     * </p>
     * <p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt".
     * In this case, the name wouldn't be the name of a file, but the identifier of an alternate data stream (bar.txt) on the file foo.exe. The method used to return ".txt" here,
     * which would be misleading. Commons IO 2.7, and later versions, are throwing an {@link IllegalArgumentException} for names like this.
     * </p>
     *
     * @param fileName the fileName to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null} if the fileName is {@code null}.
     * @throws IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact,
     *                                  the identifier of an Alternate Data Stream, for example "foo.exe:bar.txt".
     */
    public static String getExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return null;
        }
        final int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return EMPTY_STRING;
        }
        return fileName.substring(index + 1);
    }

    public static String getExtension(final File file) throws IllegalArgumentException {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return getExtension(file.getName());
    }

    /**
     * 从完整文件名获取完整路径，该文件名是前缀+路径。
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before and including the last forward or backslash.
     * </p>
     * <pre>
     * C:\a\b\c.txt --&gt; C:\a\b\
     * ~/a/b/c.txt  --&gt; ~/a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~/
     * ~/           --&gt; ~/
     * ~user        --&gt; ~user/
     * ~user/       --&gt; ~user/
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName 要查询的文件名，null返回null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getFullPath(final String fileName) {
        return doGetFullPath(fileName, true);
    }

    /**
     * 从完整的fileName获取完整路径，它是前缀+路径，也不包括最后的目录分隔符。
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before the last forward or backslash.
     * </p>
     * <pre>
     * C:\a\b\c.txt --&gt; C:\a\b
     * ~/a/b/c.txt  --&gt; ~/a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~
     * ~/           --&gt; ~
     * ~user        --&gt; ~user
     * ~user/       --&gt; ~user
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getFullPathNoEndSeparator(final String fileName) {
        return doGetFullPath(fileName, false);
    }

    /**
     * Gets the name minus the path from a full fileName.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash is returned.
     * </p>
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String getName(final String fileName) {
        if (fileName == null) {
            return null;
        }
        return requireNonNullChars(fileName).substring(indexOfLastSeparator(fileName) + 1);
    }

    /**
     * 从完整的fileName获取路径，其中不包含前缀。
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before and including the last forward or backslash.
     * </p>
     * <pre>
     * C:\a\b\c.txt --&gt; a\b\
     * ~/a/b/c.txt  --&gt; a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     * <p>
     * This method drops the prefix from the result. See {@link #getFullPath(String)} for the method that retains the prefix.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getPath(final String fileName) {
        return doGetPath(fileName, 1);
    }

    /**
     * 从完整的fileName获取路径，其中不包括前缀，也不包括最后的目录分隔符。
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before the last forward or backslash.
     * </p>
     * <pre>
     * C:\a\b\c.txt --&gt; a\b
     * ~/a/b/c.txt  --&gt; a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * </pre>
     * <p>
     * 不管代码运行在什么机器上，输出都是相同的。
     * </p>
     * <p>
     * 此方法从结果中删除前缀. See {@link #getFullPathNoEndSeparator(String)} for the method that retains the prefix.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getPathNoEndSeparator(final String fileName) {
        return doGetPath(fileName, 0);
    }

    /**
     * 从完整的文件名获取前缀， 例如 {@code C:/} or {@code ~/}.
     * <p>
     * This method will handle a file in either Unix or Windows format. The prefix includes the first slash in the full fileName where applicable.
     * </p>
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * </pre>
     * <p>
     * 不管代码运行在什么机器上，输出都是相同的。ie。Unix和Windows前缀都是匹配的。
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the prefix of the file, null if invalid
     * @throws IllegalArgumentException if the result contains the null character ({@code U+0000})
     */
    public static String getPrefix(final String fileName) {
        if (fileName == null) {
            return null;
        }
        final int len = getPrefixLength(fileName);
        if (len < 0) {
            return null;
        }
        if (len > fileName.length()) {
            requireNonNullChars(fileName);
            return fileName + UNIX_NAME_SEPARATOR;
        }
        return requireNonNullChars(fileName.substring(0, len));
    }

    /**
     * 返回fileName前缀的长度, such as {@code C:/} or {@code ~/}.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The prefix length includes the first slash in the full fileName if applicable. Thus, it is possible that the length returned is greater than the length of the input string.
     * </p>
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; 0           --&gt; relative
     * \a\b\c.txt          --&gt; 1           --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; 2           --&gt; drive relative
     * C:\a\b\c.txt        --&gt; 3           --&gt; absolute
     * \\server\a\b\c.txt  --&gt; 9           --&gt; UNC
     * \\\a\b\c.txt        --&gt; -1          --&gt; error
     *
     * Unix:
     * a/b/c.txt           --&gt; 0           --&gt; relative
     * /a/b/c.txt          --&gt; 1           --&gt; absolute
     * ~/a/b/c.txt         --&gt; 2           --&gt; current user
     * ~                   --&gt; 2           --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; 6           --&gt; named user
     * ~user               --&gt; 6           --&gt; named user (slash added)
     * //server/a/b/c.txt  --&gt; 9
     * ///a/b/c.txt        --&gt; -1          --&gt; error
     * C:                  --&gt; 0           --&gt; valid filename as only null character and / are reserved characters
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on. ie. both Unix and Windows prefixes are matched regardless.
     * </p>
     * <p>
     * Note that a leading // (or \\) is used to indicate a UNC name on Windows. These must be followed by a server name, so double-slashes are not collapsed to a single slash at
     * the start of the fileName.
     * </p>
     *
     * @param fileName the fileName to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    public static int getPrefixLength(final String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        final int len = fileName.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = fileName.charAt(0);
        if (ch0 == ':') {
            return NOT_FOUND;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;  // return a length greater than the input
            }
            return isSeparator(ch0) ? 1 : 0;
        }
        if (ch0 == '~') {
            int posUnix = fileName.indexOf(UNIX_NAME_SEPARATOR, 1);
            int posWin = fileName.indexOf(WINDOWS_NAME_SEPARATOR, 1);
            if (posUnix == NOT_FOUND && posWin == NOT_FOUND) {
                return len + 1;  // return a length greater than the input
            }
            posUnix = posUnix == NOT_FOUND ? posWin : posUnix;
            posWin = posWin == NOT_FOUND ? posUnix : posWin;
            return Math.min(posUnix, posWin) + 1;
        }
        final char ch1 = fileName.charAt(1);
        if (ch1 == ':') {
            ch0 = Character.toUpperCase(ch0);
            if (ch0 >= 'A' && ch0 <= 'Z') {
                if (len == 2 && !FileSystem.getCurrent().supportsDriveLetter()) {
                    return 0;
                }
                if (len == 2 || !isSeparator(fileName.charAt(2))) {
                    return 2;
                }
                return 3;
            }
            if (ch0 == UNIX_NAME_SEPARATOR) {
                return 1;
            }
            return NOT_FOUND;

        }
        if (!isSeparator(ch0) || !isSeparator(ch1)) {
            return isSeparator(ch0) ? 1 : 0;
        }
        int posUnix = fileName.indexOf(UNIX_NAME_SEPARATOR, 2);
        int posWin = fileName.indexOf(WINDOWS_NAME_SEPARATOR, 2);
        if (posUnix == NOT_FOUND && posWin == NOT_FOUND || posUnix == 2 || posWin == 2) {
            return NOT_FOUND;
        }
        posUnix = posUnix == NOT_FOUND ? posWin : posUnix;
        posWin = posWin == NOT_FOUND ? posUnix : posWin;
        final int pos = Math.min(posUnix, posWin) + 1;
        final String hostnamePart = fileName.substring(2, pos - 1);
        return isValidHostName(hostnamePart) ? pos : NOT_FOUND;
    }

    /**
     * 返回最后一个扩展分隔符字符的索引，该字符是一个点。
     * <p>
     * This method also checks that there is no directory separator after the last dot. To do this it uses {@link #indexOfLastSeparator(String)} which will handle a file in either
     * Unix or Windows format.
     * </p>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the exception of a possible {@link IllegalArgumentException} on Windows (see
     * below).
     * </p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt".
     * In this case, the name wouldn't be the name of a file, but the identifier of an alternate data stream (bar.txt) on the file foo.exe. The method used to return ".txt" here,
     * which would be misleading. Commons IO 2.7, and later versions, are throwing an {@link IllegalArgumentException} for names like this.
     *
     * @param fileName the fileName to find the last extension separator in, null returns -1
     * @return the index of the last extension separator character, or -1 if there is no such character
     * @throws IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact,
     *                                  the identifier of an Alternate Data Stream, for example "foo.exe:bar.txt".
     */
    public static int indexOfExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return NOT_FOUND;
        }
        if (isSystemWindows()) {
            // Special handling for NTFS ADS: Don't accept colon in the fileName.
            final int offset = fileName.indexOf(':', getAdsCriticalOffset(fileName));
            if (offset != -1) {
                throw new IllegalArgumentException("NTFS ADS separator (':') in file name is forbidden.");
            }
        }
        final int extensionPos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(fileName);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    /**
     * 返回最后一个目录分隔符字符的索引。
     * <p>
     * This method will handle a file in either Unix or Windows format. The position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param fileName the fileName to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there is no such character
     */
    public static int indexOfLastSeparator(final String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = fileName.lastIndexOf(UNIX_NAME_SEPARATOR);
        final int lastWindowsPos = fileName.lastIndexOf(WINDOWS_NAME_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    private static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    /**
     * 检查fileName的扩展名是否为指定的扩展名之一。
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The extension check is
     * case-sensitive on all platforms.
     *
     * @param fileName   the fileName to query, null returns false
     * @param extensions the extensions to check for, null checks for no extension
     * @return true if the fileName is one of the extensions
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final Collection<String> extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (extensions == null || extensions.isEmpty()) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        final String fileExt = getExtension(fileName);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查fileName的扩展名是否为指定的扩展名。
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The extension check is
     * case-sensitive on all platforms.
     *
     * @param fileName  the fileName to query, null returns false
     * @param extension the extension to check for, null or empty checks for no extension
     * @return true if the fileName has the specified extension
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final String extension) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (isEmpty(extension)) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        return getExtension(fileName).equals(extension);
    }

    /**
     * 检查fileName的扩展名是否为指定的扩展名之一。
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The extension check is
     * case-sensitive on all platforms.
     *
     * @param fileName   the fileName to query, null returns false
     * @param extensions the extensions to check for, null checks for no extension
     * @return true if the fileName is one of the extensions
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final String... extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (extensions == null || extensions.length == 0) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        final String fileExt = getExtension(fileName);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查给定的字符串是否代表有效的IPv4地址。
     *
     * @param name the name to validate
     * @return true if the given name is a valid IPv4 address
     */
    // mostly copied from org.apache.commons.validator.routines.InetAddressValidator#isValidInet4Address
    private static boolean isIPv4Address(final String name) {
        final Matcher m = IPV4_PATTERN.matcher(name);
        if (!m.matches() || m.groupCount() != 4) {
            return false;
        }

        // verify that address subgroups are legal
        for (int i = 1; i <= 4; i++) {
            final String ipSegment = m.group(i);
            final int iIpSegment = Integer.parseInt(ipSegment);
            if (iIpSegment > IPV4_MAX_OCTET_VALUE) {
                return false;
            }

            if (ipSegment.length() > 1 && ipSegment.startsWith("0")) {
                return false;
            }

        }

        return true;
    }


    /**
     * 检查给定的字符串是否代表有效的IPv6地址。
     *
     * @param inet6Address the name to validate
     * @return true if the given name is a valid IPv6 address
     */
    private static boolean isIPv6Address(final String inet6Address) {
        final boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
            return false;
        }
        if (inet6Address.startsWith(":") && !inet6Address.startsWith("::")
                || inet6Address.endsWith(":") && !inet6Address.endsWith("::")) {
            return false;
        }
        String[] octets = inet6Address.split(":");
        if (containsCompressedZeroes) {
            final List<String> octetList = new ArrayList<>(Arrays.asList(octets));
            if (inet6Address.endsWith("::")) {
                // String.split() drops ending empty segments
                octetList.add("");
            } else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                octetList.remove(0);
            }
            octets = octetList.toArray(EMPTY_STRING_ARRAY);
        }
        if (octets.length > IPV6_MAX_HEX_GROUPS) {
            return false;
        }
        int validOctets = 0;
        int emptyOctets = 0; // consecutive empty chunks
        for (int index = 0; index < octets.length; index++) {
            final String octet = octets[index];
            if (octet.isEmpty()) {
                emptyOctets++;
                if (emptyOctets > 1) {
                    return false;
                }
            } else {
                emptyOctets = 0;
                // Is last chunk an IPv4 address?
                if (index == octets.length - 1 && octet.contains(".")) {
                    if (!isIPv4Address(octet)) {
                        return false;
                    }
                    validOctets += 2;
                    continue;
                }
                if (octet.length() > IPV6_MAX_HEX_DIGITS_PER_GROUP) {
                    return false;
                }
                final int octetInt;
                try {
                    octetInt = Integer.parseInt(octet, BASE_16);
                } catch (final NumberFormatException e) {
                    return false;
                }
                if (octetInt < 0 || octetInt > MAX_UNSIGNED_SHORT) {
                    return false;
                }
            }
            validOctets++;
        }
        return validOctets <= IPV6_MAX_HEX_GROUPS && (validOctets >= IPV6_MAX_HEX_GROUPS || containsCompressedZeroes);
    }

    /**
     * 根据RFC 3986检查给定字符串是否为有效的主机名——不接受IP地址。
     *
     * @param name the hostname to validate
     * @return true if the given name is a valid host name
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     */
    private static boolean isRFC3986HostName(final String name) {
        final String[] parts = name.split("\\.", -1);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                // trailing dot is legal, otherwise we've hit a .. sequence
                return i == parts.length - 1;
            }
            if (!REG_NAME_PART_PATTERN.matcher(parts[i]).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符是否为分隔符。
     *
     * @param ch the character to check
     * @return true if it is a separator character
     */
    private static boolean isSeparator(final char ch) {
        return ch == UNIX_NAME_SEPARATOR || ch == WINDOWS_NAME_SEPARATOR;
    }

    /**
     * 确定是否正在使用Windows文件系统。
     *
     * @return true if the system is Windows
     */
    static boolean isSystemWindows() {
        return SYSTEM_NAME_SEPARATOR == WINDOWS_NAME_SEPARATOR;
    }

    /**
     * 根据RFC 3986检查给定字符串是否是有效的主机名。
     *
     * <p>Accepted are IP addresses (v4 and v6) as well as what the
     * RFC calls a "reg-name". Percent encoded names don't seem to be valid names in UNC paths.</p>
     *
     * @param name the hostname to validate
     * @return true if the given name is a valid host name
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     */
    private static boolean isValidHostName(final String name) {
        return isIPv6Address(name) || isRFC3986HostName(name);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤。
     * <p>
     * 此方法将路径规范化为标准格式。输入可以包含Unix或Windows格式的分隔符。输出将包含系统格式的分隔符。
     * <p>
     * A trailing slash will be retained. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed. A double dot will
     * cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     * <pre>
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * </pre>
     * (Note the file separator returned will be correct for Windows/Unix)
     *
     * @param fileName the fileName to normalize, null returns null
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalize(final String fileName) {
        return doNormalize(fileName, SYSTEM_NAME_SEPARATOR, true);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤。
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators in the format
     * specified.
     * <p>
     * A trailing slash will be retained. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed. A double dot will
     * cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     * <pre>
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * </pre>
     * The output will be the same on both Unix and Windows including the separator character.
     *
     * @param fileName      the fileName to normalize, null returns null
     * @param unixSeparator {@code true} if a unix separator should be used or {@code false} if a windows separator should be used.
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalize(final String fileName, final boolean unixSeparator) {
        return doNormalize(fileName, toSeparator(unixSeparator), true);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤，并删除任何最终目录分隔符。
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators in the format of
     * the system.
     * <p>
     * A trailing slash will be removed. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed. A double dot will
     * cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     * <pre>
     * /foo//               --&gt;   /foo
     * /foo/./              --&gt;   /foo
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar
     * ~/../bar             --&gt;   null
     * </pre>
     * (Note the file separator returned will be correct for Windows/Unix)
     *
     * @param fileName the fileName to normalize, null returns null
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalizeNoEndSeparator(final String fileName) {
        return doNormalize(fileName, SYSTEM_NAME_SEPARATOR, false);
    }

    /**
     * 规范化路径，删除双点和单点路径步骤，并删除任何最终目录分隔符。
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators in the format
     * specified.
     * <p>
     * A trailing slash will be removed. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed. A double dot will
     * cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows including the separator character.
     * <pre>
     * /foo//               --&gt;   /foo
     * /foo/./              --&gt;   /foo
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar
     * ~/../bar             --&gt;   null
     * </pre>
     *
     * @param fileName      the fileName to normalize, null returns null
     * @param unixSeparator {@code true} if a unix separator should be used or {@code false} if a windows separator should be used.
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalizeNoEndSeparator(final String fileName, final boolean unixSeparator) {
        return doNormalize(fileName, toSeparator(unixSeparator), false);
    }

    /**
     * 从fileName中删除扩展名。
     * <p>
     * This method returns the textual part of the fileName before the last dot. There must be no directory separator after the dot.
     * <pre>
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * a.b\c      --&gt; a.b\c
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param fileName the fileName to query, null returns null
     * @return the fileName minus the extension
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String removeExtension(final String fileName) {
        if (fileName == null) {
            return null;
        }
        requireNonNullChars(fileName);

        final int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 检查输入是否为空字符 ({@code U+0000}), a sign of unsanitized data being passed to to file level functions.
     * <p>
     * This may be used for poison byte attacks.
     *
     * @param path the path to check
     * @return The input
     * @throws IllegalArgumentException if path contains the null character ({@code U+0000})
     */
    private static String requireNonNullChars(final String path) {
        if (path.indexOf(0) >= 0) {
            throw new IllegalArgumentException(
                    "Null character present in file/path name. There are no known legitimate use cases for such data, but several injection attacks may use it");
        }
        return path;
    }

    /**
     * 将所有分隔符转换为系统分隔符。
     *
     * @param path the path to be changed, null ignored.
     * @return the updated path.
     */
    public static String separatorsToSystem(final String path) {
        return FileSystem.getCurrent().normalizeSeparators(path);
    }

    /**
     * 将所有分隔符转换为正斜杠的Unix分隔符。
     *
     * @param path the path to be changed, null ignored.
     * @return the new path.
     */
    public static String separatorsToUnix(final String path) {
        return FileSystem.LINUX.normalizeSeparators(path);
    }

    /**
     * 将所有分隔符转换为反斜杠的Windows分隔符。
     *
     * @param path the path to be changed, null ignored.
     * @return the updated path.
     */
    public static String separatorsToWindows(final String path) {
        return FileSystem.WINDOWS.normalizeSeparators(path);
    }

    /**
     * 将字符串拆分为许多标记. 文本被'?'和'*'。当多个'*'连续出现时，它们被分解为一个'*'。
     *
     * @param text the text to split
     * @return the array of tokens, never null
     */
    static String[] splitOnTokens(final String text) {
        // used by wildcardMatch
        // package level so a unit test may run on this

        if (text.indexOf('?') == NOT_FOUND && text.indexOf('*') == NOT_FOUND) {
            return new String[]{text};
        }

        final char[] array = text.toCharArray();
        final ArrayList<String> list = new ArrayList<>();
        final StringBuilder buffer = new StringBuilder();
        char prevChar = 0;
        for (final char ch : array) {
            if (ch == '?' || ch == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (ch == '?') {
                    list.add("?");
                } else if (prevChar != '*') {// ch == '*' here; check if previous char was '*'
                    list.add("*");
                }
            } else {
                buffer.append(ch);
            }
            prevChar = ch;
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * 如果给定true则返回'/'，否则返回'\\'。
     *
     * @param unixSeparator which separator to return.
     * @return '/' if given true, '\\' otherwise.
     */
    private static char toSeparator(final boolean unixSeparator) {
        return unixSeparator ? UNIX_NAME_SEPARATOR : WINDOWS_NAME_SEPARATOR;
    }

    /**
     * 检查fileName，看它是否匹配指定的通配符匹配器，始终测试区分大小写。< p > 通配符匹配器使用字符的'?'和'*'表示一个或多个(零个或多个)通配符。这与DOSUnix命令行中经常发现的情况相同。Unix总是区分大小写的。
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param fileName        the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the fileName matches the wildcard string
     * @see IOCase#SENSITIVE
     */
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SENSITIVE);
    }

    /**
     * 检查文件名，看它是否与允许对大小写进行控制的指定通配符匹配器相匹配。
     * <p>
     * 通配符匹配器使用字符的'?'和'*'表示一个或多个(零个或多个)通配符. N.B. 序列"*?"目前在匹配字符串中不能正常工作。
     *
     * @param fileName        the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @param ioCase          what case sensitivity rule to use, null means case-sensitive
     * @return true if the fileName matches the wildcard string
     */
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher, IOCase ioCase) {
        if (fileName == null && wildcardMatcher == null) {
            return true;
        }
        if (fileName == null || wildcardMatcher == null) {
            return false;
        }
        ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
        final String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        final Deque<int[]> backtrack = new ArrayDeque<>(wcs.length);

        // loop around a backtrack stack, to handle complex * matching
        do {
            if (!backtrack.isEmpty()) {
                final int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }

            // loop whilst tokens and text left to process
            while (wcsIdx < wcs.length) {

                if (wcs[wcsIdx].equals("?")) {
                    // ? so move to next text char
                    textIdx++;
                    if (textIdx > fileName.length()) {
                        break;
                    }
                    anyChars = false;

                } else if (wcs[wcsIdx].equals("*")) {
                    // set any chars status
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = fileName.length();
                    }

                } else {
                    // matching text token
                    if (anyChars) {
                        // any chars then try to locate text token
                        textIdx = ioCase.checkIndexOf(fileName, textIdx, wcs[wcsIdx]);
                        if (textIdx == NOT_FOUND) {
                            // token not found
                            break;
                        }
                        final int repeat = ioCase.checkIndexOf(fileName, textIdx + 1, wcs[wcsIdx]);
                        if (repeat >= 0) {
                            backtrack.push(new int[]{wcsIdx, repeat});
                        }
                    } else if (!ioCase.checkRegionMatches(fileName, textIdx, wcs[wcsIdx])) {
                        // matching from current position
                        // couldn't match token
                        break;
                    }

                    // matched text token, move text index to end of matched token
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }

                wcsIdx++;
            }

            // full match
            if (wcsIdx == wcs.length && textIdx == fileName.length()) {
                return true;
            }

        } while (!backtrack.isEmpty());

        return false;
    }

    /**
     * 使用系统的大小写规则检查fileName是否与指定的通配符匹配器匹配。
     * <p>
     * 通配符匹配器使用字符的'?'和'*'表示一个或多个(零个或多个)通配符。这与DOSUnix命令行中经常发现的情况相同。在Unix上区分大小写，在Windows上不区分大小写。
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param fileName        the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the fileName matches the wildcard string
     * @see IOCase#SYSTEM
     */
    public static boolean wildcardMatchOnSystem(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SYSTEM);
    }

    /**
     * Instances should NOT be constructed in standard programming.
     */
    public FilenameUtils() {
    }
}
