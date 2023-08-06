package top.lytree;

import top.lytree.collections.ArrayUtils;
import top.lytree.collections.ListUtils;
import top.lytree.ftp.*;
import top.lytree.io.FileUtils;
import top.lytree.io.FilenameUtils;
import top.lytree.lang.StringUtils;
import top.lytree.utils.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FtpClient extends AbstractFtp {
    /**
     * 默认端口
     */
    public static final int DEFAULT_PORT = 21;

    private FTPClient client;
    private FTPMode mode;
    /**
     * 执行完操作是否返回当前目录
     */
    private boolean backToPwd;

    /**
     * 构造，匿名登录
     *
     * @param host 域名或IP
     */
    public FtpClient(final String host) {
        this(host, DEFAULT_PORT);
    }

    /**
     * 构造，匿名登录
     *
     * @param host 域名或IP
     * @param port 端口
     */
    public FtpClient(final String host, final int port) {
        this(host, port, "anonymous", "");
    }

    /**
     * 构造
     *
     * @param host     域名或IP
     * @param port     端口
     * @param user     用户名
     * @param password 密码
     */
    public FtpClient(final String host, final int port, final String user, final String password) {
        this(host, port, user, password, StandardCharsets.UTF_8);
    }

    /**
     * 构造
     *
     * @param host     域名或IP
     * @param port     端口
     * @param user     用户名
     * @param password 密码
     * @param charset  编码
     */
    public FtpClient(final String host, final int port, final String user, final String password, final Charset charset) {
        this(host, port, user, password, charset, null, null);
    }

    /**
     * 构造
     *
     * @param host               域名或IP
     * @param port               端口
     * @param user               用户名
     * @param password           密码
     * @param charset            编码
     * @param serverLanguageCode 服务器语言 例如：zh
     * @param systemKey          服务器标识 例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT
     */
    public FtpClient(final String host, final int port, final String user, final String password, final Charset charset, final String serverLanguageCode, final String systemKey) {
        this(host, port, user, password, charset, serverLanguageCode, systemKey, null);
    }

    /**
     * 构造
     *
     * @param host               域名或IP
     * @param port               端口
     * @param user               用户名
     * @param password           密码
     * @param charset            编码
     * @param serverLanguageCode 服务器语言
     * @param systemKey          系统关键字
     * @param mode               模式
     */
    public FtpClient(final String host, final int port, final String user, final String password, final Charset charset, final String serverLanguageCode, final String systemKey, final FTPMode mode) {
        this(new FtpConfig(host, port, user, password, charset, serverLanguageCode, systemKey), mode);
    }

    /**
     * 构造
     *
     * @param config FTP配置
     * @param mode   模式
     */
    public FtpClient(final FtpConfig config, final FTPMode mode) {
        super(config);
        this.mode = mode;
        this.init();
    }

    /**
     * 构造
     *
     * @param client 自定义实例化好的{@link FTPClient}
     * @since 5.7.22
     */
    public FtpClient(final FTPClient client) {
        super(FtpConfig.of());
        this.client = client;
    }

    /**
     * 初始化连接
     *
     * @return this
     */
    public FtpClient init() {
        return this.init(this.ftpConfig, this.mode);
    }

    /**
     * 初始化连接
     *
     * @param host     域名或IP
     * @param port     端口
     * @param user     用户名
     * @param password 密码
     * @return this
     */
    public FtpClient init(final String host, final int port, final String user, final String password) {
        return this.init(host, port, user, password, null);
    }

    /**
     * 初始化连接
     *
     * @param host     域名或IP
     * @param port     端口
     * @param user     用户名
     * @param password 密码
     * @param mode     模式
     * @return this
     */
    public FtpClient init(final String host, final int port, final String user, final String password, final FTPMode mode) {
        return init(new FtpConfig(host, port, user, password, this.ftpConfig.getCharset(), null, null), mode);
    }

    /**
     * 初始化连接
     *
     * @param config FTP配置
     * @param mode   模式
     * @return this
     */
    public FtpClient init(final FtpConfig config, final FTPMode mode) {
        final FTPClient client = new FTPClient();
        // issue#I3O81Y@Gitee
        client.setRemoteVerificationEnabled(false);

        final Charset charset = config.getCharset();
        if (null != charset) {
            client.setControlEncoding(charset.toString());
        }
        client.setConnectTimeout((int) config.getConnectionTimeout());
        final String systemKey = config.getSystemKey();
        if (StringUtils.isNotBlank(systemKey)) {
            final FTPClientConfig conf = new FTPClientConfig(systemKey);
            final String serverLanguageCode = config.getServerLanguageCode();
            if (StringUtils.isNotBlank(serverLanguageCode)) {
                conf.setServerLanguageCode(config.getServerLanguageCode());
            }
            client.configure(conf);
        }

        // connect
        try {
            // 连接ftp服务器
            client.connect(config.getHost(), config.getPort());
            client.setSoTimeout((int) config.getSoTimeout());
            // 登录ftp服务器
            client.login(config.getUser(), config.getPassword());
        } catch (final IOException e) {
            throw new FtpException(e);
        }
        final int replyCode = client.getReplyCode(); // 是否成功登录服务器
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            try {
                client.disconnect();
            } catch (final IOException e) {
                // ignore
            }
            throw new FtpException("Login failed for user [{}], reply code is: [{}]", config.getUser(), replyCode);
        }
        this.client = client;
        if (mode != null) {
            //noinspection resource
            setMode(mode);
        }
        return this;
    }

    /**
     * 设置FTP连接模式，可选主动和被动模式
     *
     * @param mode 模式枚举
     * @return this
     * @since 4.1.19
     */
    public FtpClient setMode(final FTPMode mode) {
        this.mode = mode;
        switch (mode) {
            case Active:
                this.client.enterLocalActiveMode();
                break;
            case Passive:
                this.client.enterLocalPassiveMode();
                break;
        }
        return this;
    }

    /**
     * 设置执行完操作是否返回当前目录
     *
     * @param backToPwd 执行完操作是否返回当前目录
     * @return this
     * @since 4.6.0
     */
    public FtpClient setBackToPwd(final boolean backToPwd) {
        this.backToPwd = backToPwd;
        return this;
    }

    /**
     * 是否执行完操作返回当前目录
     *
     * @return 执行完操作是否返回当前目录
     * @since 5.7.17
     */
    public boolean isBackToPwd() {
        return this.backToPwd;
    }

    /**
     * 如果连接超时的话，重新进行连接 经测试，当连接超时时，client.isConnected()仍然返回ture，无法判断是否连接超时 因此，通过发送pwd命令的方式，检查连接是否超时
     *
     * @return this
     */
    @Override
    public FtpClient reconnectIfTimeout() {
        String pwd = null;
        try {
            pwd = pwd();
        } catch (final FtpException fex) {
            // ignore
        }

        if (pwd == null) {
            return this.init();
        }
        return this;
    }

    /**
     * 改变目录
     *
     * @param directory 目录
     * @return 是否成功
     */
    @Override
    synchronized public boolean cd(final String directory) {
        if (StringUtils.isBlank(directory)) {
            // 当前目录
            return true;
        }

        try {
            return client.changeWorkingDirectory(directory);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 远程当前目录
     *
     * @return 远程当前目录
     * @since 4.1.14
     */
    @Override
    public String pwd() {
        try {
            return client.printWorkingDirectory();
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public List<String> ls(final String path) {
        return Arrays.stream(lsFiles(path)).map(FTPFile::getName).collect(Collectors.toList());
    }

    /**
     * 遍历某个目录下所有文件和目录，不会递归遍历<br>
     * 此方法自动过滤"."和".."两种目录
     *
     * @param path      目录
     * @param predicate 过滤器，null表示不过滤，默认去掉"."和".."两种目录
     * @return 文件或目录列表
     * @since 5.3.5
     */
    public List<FTPFile> lsFiles(final String path, final Predicate<FTPFile> predicate) {
        final FTPFile[] ftpFiles = lsFiles(path);
        if (ArrayUtils.isEmpty(ftpFiles)) {
            return Collections.emptyList();
        }

        final List<FTPFile> result = new ArrayList<>(ftpFiles.length - 2 <= 0 ? ftpFiles.length : ftpFiles.length - 2);
        String fileName;
        for (final FTPFile ftpFile : ftpFiles) {
            fileName = ftpFile.getName();
            if (!StringUtils.equals(".", fileName) && !StringUtils.equals("..", fileName)) {
                if (null == predicate || predicate.test(ftpFile)) {
                    result.add(ftpFile);
                }
            }
        }
        return result;
    }

    /**
     * 遍历某个目录下所有文件和目录，不会递归遍历
     *
     * @param path 目录，如果目录不存在，抛出异常
     * @return 文件或目录列表
     * @throws FtpException 路径不存在
     */
    public FTPFile[] lsFiles(final String path) throws FtpException {
        String pwd = null;
        if (StringUtils.isNotBlank(path)) {
            pwd = pwd();
            if (!cd(path)) {
                throw new FtpException("Change dir to [{}] error, maybe path not exist!", path);
            }
        }

        FTPFile[] ftpFiles;
        try {
            ftpFiles = this.client.listFiles();
        } catch (final IOException e) {
            throw new FtpException(e);
        } finally {
            // 回到原目录
            cd(pwd);
        }

        return ftpFiles;
    }

    @Override
    public boolean mkdir(final String dir) {
        try {
            return this.client.makeDirectory(dir);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 获取服务端目录状态。
     *
     * @param path 路径
     * @return 状态int，服务端不同，返回不同
     * @ IO异常
     * @since 5.4.3
     */
    public int stat(final String path)  {
        try {
            return this.client.stat(path);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 判断ftp服务器目录内是否还有子元素（目录或文件）
     *
     * @param path 文件路径
     * @return 是否存在
     * @ IO异常
     */
    public boolean existFile(final String path)  {
        final FTPFile[] ftpFileArr;
        try {
            ftpFileArr = client.listFiles(path);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
        return ArrayUtils.isNotEmpty(ftpFileArr);
    }

    @Override
    public boolean delFile(final String path)  {
        final String pwd = pwd();
        final String fileName = FilenameUtils.getName(path);
        final String dir = StringUtils.removeEnd(path, fileName);
        if (!cd(dir)) {
            throw new FtpException("Change dir to [{}] error, maybe dir not exist!", path);
        }

        boolean isSuccess;
        try {
            isSuccess = client.deleteFile(fileName);
        } catch (final IOException e) {
            throw new FtpException(e);
        } finally {
            // 回到原目录
            cd(pwd);
        }
        return isSuccess;
    }

    @Override
    public boolean delDir(final String dirPath)  {
        final FTPFile[] dirs;
        try {
            dirs = client.listFiles(dirPath);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
        String name;
        String childPath;
        for (final FTPFile ftpFile : dirs) {
            name = ftpFile.getName();
            childPath = StringUtils.format("{}/{}", dirPath, name);
            if (ftpFile.isDirectory()) {
                // 上级和本级目录除外
                if (!".".equals(name) && !"..".equals(name)) {
                    delDir(childPath);
                }
            } else {
                delFile(childPath);
            }
        }

        // 删除空目录
        try {
            return this.client.removeDirectory(dirPath);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. remotePath为null或""上传到当前路径
     * 2. remotePath为相对路径则相对于当前路径的子路径
     * 3. remotePath为绝对路径则上传到此路径
     * </pre>
     *
     * @param remotePath 服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param file       文件
     * @return 是否上传成功
     */
    @Override
    public boolean uploadFile(final String remotePath, final File file) {
        Assert.notNull(file, "file to upload is null !");
        if (!FileUtils.isRegularFile(file)) {
            throw new FtpException("[{}] is not a file!", file);
        }
        return uploadFile(remotePath, file.getName(), file);
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. remotePath为null或""上传到当前路径
     * 2. remotePath为相对路径则相对于当前路径的子路径
     * 3. remotePath为绝对路径则上传到此路径
     * </pre>
     *
     * @param file       文件
     * @param remotePath 服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param fileName   自定义在服务端保存的文件名
     * @return 是否上传成功
     * @ IO异常
     */
    public boolean uploadFile(final String remotePath, final String fileName, final File file)  {
        try (final InputStream in = FileUtils.openInputStream(file)) {
            return uploadFile(remotePath, fileName, in);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. remotePath为null或""上传到当前路径
     * 2. remotePath为相对路径则相对于当前路径的子路径
     * 3. remotePath为绝对路径则上传到此路径
     * </pre>
     *
     * @param remotePath 服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param fileName   文件名
     * @param fileStream 文件流
     * @return 是否上传成功
     * @ IO异常
     */
    public boolean uploadFile(final String remotePath, final String fileName, final InputStream fileStream)  {
        try {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (final IOException e) {
            throw new FtpException(e);
        }

        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }

        if (StringUtils.isNotBlank(remotePath)) {
            mkDirs(remotePath);
            if (!cd(remotePath)) {
                throw new FtpException("Change dir to [{}] error, maybe dir not exist!", remotePath);
            }
        }

        try {
            return client.storeFile(fileName, fileStream);
        } catch (final IOException e) {
            throw new FtpException(e);
        } finally {
            if (this.backToPwd) {
                cd(pwd);
            }
        }
    }

    /**
     * 递归上传文件（支持目录）<br>
     * 上传时，如果uploadFile为目录，只复制目录下所有目录和文件到目标路径下，并不会复制目录本身<br>
     * 上传时，自动创建父级目录
     *
     * @param remotePath 目录路径
     * @param uploadFile 上传文件或目录
     */
    public void upload(final String remotePath, final File uploadFile) {
        if (!FileUtils.isDirectory(uploadFile)) {
            this.uploadFile(remotePath, uploadFile);
            return;
        }

        final File[] files = uploadFile.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            return;
        }

        final List<File> dirs = new ArrayList<>(files.length);
        //第一次只处理文件，防止目录在前面导致先处理子目录，而引发文件所在目录不正确
        for (final File f : files) {
            if (f.isDirectory()) {
                dirs.add(f);
            } else {
                this.uploadFile(remotePath, f);
            }
        }
        //第二次只处理目录
        for (final File f : dirs) {
            final String dir = FilenameUtils.normalize(remotePath + "/" + f.getName());
            upload(dir, f);
        }
    }

    /**
     * 下载文件
     *
     * @param path    文件路径，包含文件名
     * @param outFile 输出文件或目录，当为目录时，使用服务端的文件名
     */
    @Override
    public void download(final String path, final File outFile) {
        final String fileName = FilenameUtils.getName(path);
        final String dir = StringUtils.removeEnd(path, fileName);
        download(dir, fileName, outFile);
    }

    /**
     * 递归下载FTP服务器上文件到本地(文件目录和服务器同步)
     *
     * @param sourcePath ftp服务器目录
     * @param destDir    本地目录
     */
    @Override
    public void recursiveDownloadFolder(final String sourcePath, final File destDir) {
        String fileName;
        String srcFile;
        File destFile;
        for (final FTPFile ftpFile : lsFiles(sourcePath, null)) {
            fileName = ftpFile.getName();
            srcFile = StringUtils.format("{}/{}", sourcePath, fileName);
            destFile = FileUtils.file(destDir, fileName);

            if (!ftpFile.isDirectory()) {
                // 本地不存在文件或者ftp上文件有修改则下载
                if (!FileUtils.isRegularFile(destFile)
                        || (ftpFile.getTimestamp().getTimeInMillis() > destFile.lastModified())) {
                    download(srcFile, destFile);
                }
            } else {
                // 服务端依旧是目录，继续递归
                FileUtils.mkdir(destFile);
                recursiveDownloadFolder(srcFile, destFile);
            }
        }
    }

    /**
     * 下载文件
     *
     * @param path     文件所在路径（远程目录），不包含文件名
     * @param fileName 文件名
     * @param outFile  输出文件或目录，当为目录时使用服务端文件名
     * @ IO异常
     */
    public void download(final String path, final String fileName, File outFile)  {
        if (outFile.isDirectory()) {
            outFile = new File(outFile, fileName);
        }
        if (!outFile.exists()) {
            try {
                FileUtils.touch(outFile);
            } catch (IOException e) {
                throw new FtpException(e);
            }
        }
        try (final OutputStream out = FileUtils.openOutputStream(outFile)) {
            download(path, fileName, out);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 下载文件到输出流
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @param out      输出位置
     */
    public void download(final String path, final String fileName, final OutputStream out) {
        download(path, fileName, out, null);
    }

    /**
     * 下载文件到输出流
     *
     * @param path            服务端的文件路径
     * @param fileName        服务端的文件名
     * @param out             输出流，下载的文件写出到这个流中
     * @param fileNameCharset 文件名编码，通过此编码转换文件名编码为ISO8859-1
     * @ IO异常
     * @since 5.5.7
     */
    public void download(final String path, String fileName, final OutputStream out, final Charset fileNameCharset)  {
        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }

        if (!cd(path)) {
            throw new FtpException("Change dir to [{}] error, maybe dir not exist!", path);
        }

        if (null != fileNameCharset) {
            fileName = new String(fileName.getBytes(fileNameCharset), StandardCharsets.ISO_8859_1);
        }
        try {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            client.retrieveFile(fileName, out);
        } catch (final IOException e) {
            throw new FtpException(e);
        } finally {
            if (backToPwd) {
                cd(pwd);
            }
        }
    }

    /**
     * 获取FTPClient客户端对象
     *
     * @return {@link FTPClient}
     */
    public FTPClient getClient() {
        return this.client;
    }

    @Override
    public void close() throws IOException {
        if (null != this.client) {
            this.client.logout();
            if (this.client.isConnected()) {
                this.client.disconnect();
            }
            this.client = null;
        }
    }
}
