package top.lytree;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lytree.collections.CollectionUtils;
import top.lytree.exception.ExceptionUtils;
import top.lytree.io.IOUtils;
import top.lytree.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class SFTPClient extends AbstractFtp {
    private static final Logger logger = LoggerFactory.getLogger(SFTPClient.class);
    private net.schmizz.sshj.sftp.SFTPClient sftpClient;
    private final SSHClient ssh;

    private final FtpConfig ftpConfig;

    private final String privateKeyPath;

    public SFTPClient(String username, String host, int port, String privateKeyPath) {
        this(new FtpConfig(host, port, username, null, StandardCharsets.UTF_8), privateKeyPath);
    }

    public SFTPClient(String username, String password, String host, int port) {
        this(new FtpConfig(host, port, username, password, StandardCharsets.UTF_8), null);
    }

    public SFTPClient(FtpConfig ftpConfig, String privateKeyPath) {
        super(ftpConfig);
        this.ftpConfig = ftpConfig;
        this.ssh = new SSHClient();
        this.privateKeyPath = privateKeyPath;
    }


    public boolean login() {
        try {
            // Disable strict host key checking
            ssh.loadKnownHosts();
            ssh.connect(ftpConfig.getHost(), ftpConfig.getPort());
            ssh.setRemoteCharset(ftpConfig.getCharset());
            if (null != ftpConfig.getSystemKey()) {
                ssh.authPublickey(ftpConfig.getUser(), ssh.loadKeys(privateKeyPath));
            } else {
                ssh.authPassword(ftpConfig.getUser(), ftpConfig.getPassword());
            }

            sftpClient = ssh.newSFTPClient();
        } catch (Exception e) {
            logger.error("sftp 连接失败 错误信息{}  {}", ftpConfig.toString(), ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * 关闭连接 server
     */
    public void logout() throws IOException {
        if (sftpClient != null) {
            sftpClient.close();
        }
        if (ssh.isConnected()) {
            ssh.disconnect();
        }
    }

    @Override
    public AbstractFtp reconnectIfTimeout() {
        if (StringUtils.isBlank(this.ftpConfig.getHost())) {
            throw new FtpException("Host is blank!");
        }
        try {
            this.cd(StringUtils.SLASH);
        } catch (final FtpException e) {
            close();
            login();
        }
        return this;
    }

    @Override
    public boolean cd(final String directory) {
        final String exec = String.format("cd %s", directory);
        command(exec);
        final String pwd = pwd();
        return pwd.equals(directory);
    }

    @Override
    public String pwd() {
        return command("pwd");
    }

    @Override
    public boolean mkdir(final String dir) {
        try {
            sftpClient.mkdir(dir);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
        return containsFile(dir);
    }

    @Override
    public List<String> ls(final String path) {
        final List<RemoteResourceInfo> infoList;
        try {
            infoList = sftpClient.ls(path);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
        if (CollectionUtils.isNotEmpty(infoList)) {
            return infoList.stream().map(RemoteResourceInfo::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean delFile(final String path) {
        try {
            sftpClient.rm(path);
            return !containsFile(path);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public boolean delDir(final String dirPath) {
        try {
            sftpClient.rmdir(dirPath);
            return !containsFile(dirPath);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public boolean uploadFile(final String destPath, final File file) {
        try {
            sftpClient.put(new FileSystemFile(file), destPath);
            return containsFile(destPath);
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public void download(final String path, final File outFile) {
        try {
            sftpClient.get(path, new FileSystemFile(outFile));
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public void recursiveDownloadFolder(final String sourcePath, final File destDir) {
        final List<String> files = ls(sourcePath);
        if (files != null && !files.isEmpty()) {
            files.forEach(path -> download(sourcePath + "/" + path, destDir));
        }
    }

    @Override
    public void close() {
        try {
            sftpClient.close();
            ssh.disconnect();
        } catch (final IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 是否包含该文件
     *
     * @param fileDir 文件绝对路径
     * @return true:包含 false:不包含
     * @author youyongkun
     * @since 5.7.18
     */
    public boolean containsFile(final String fileDir) {
        try {
            sftpClient.lstat(fileDir);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }


    /**
     * 执行Linux 命令
     *
     * @param exec 命令
     * @return 返回响应结果.
     * @author youyongkun
     * @since 5.7.19
     */
    public String command(final String exec) {
        Session session = null;
        try {
            session = ssh.startSession();
            final Session.Command command = session.exec(exec);
            final InputStream inputStream = command.getInputStream();
            return IOUtils.toString(inputStream, DEFAULT_CHARSET);
        } catch (final Exception e) {
            throw new FtpException(e);
        } finally {
            IOUtils.closeQuietly(session);
        }
    }

}
