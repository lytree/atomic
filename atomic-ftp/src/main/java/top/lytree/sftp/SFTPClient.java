package top.lytree.sftp;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SFTPClient {
    private static final Logger logger = LoggerFactory.getLogger(SFTPClient.class);
    private net.schmizz.sshj.sftp.SFTPClient sftpClient;
    private final SSHClient ssh;

    private final String username;

    private final String password;

    private final String host;
    private final int port;

    private final String privateKeyPath;

    public SFTPClient(String username, String password, String host, int port) {
        this.ssh = new SSHClient();
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.privateKeyPath = null;
    }

    public SFTPClient(String username, String host, int port, String privateKeyPath) {
        this.ssh = new SSHClient();
        this.username = username;
        this.password = "";
        this.host = host;
        this.port = port;
        this.privateKeyPath = privateKeyPath;
    }


    public boolean login() {
        try {
            // Disable strict host key checking
            ssh.loadKnownHosts();
            ssh.connect(host, port);
            if (null != privateKeyPath) {
                ssh.authPublickey(username, ssh.loadKeys(privateKeyPath));
                // ssh.loadKeys(privateKeyPath);
            } else {
                ssh.authPassword(username, password);
            }

            sftpClient = ssh.newSFTPClient();
        } catch (Exception e) {
            logger.error("sftp 连接失败 错误信息：{} {} {} {}  {}", username, host, port, password, ExceptionUtils.getStackTrace(e));
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

    public List<String> getAllFileName(String remoteDir) {
        try {
            List<RemoteResourceInfo> remoteResourceInfos = sftpClient.ls(remoteDir);
            return remoteResourceInfos.stream().map(RemoteResourceInfo::getName).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("sftp 获取文件列表失败 错误信息{}", ExceptionUtils.getStackTrace(e));
        }
        return new ArrayList<>();
    }

    public List<String> getFileName(String remoteDir, RemoteResourceFilter remoteResourceFilter) {
        try {
            List<RemoteResourceInfo> remoteResourceInfos = sftpClient.ls(remoteDir, remoteResourceFilter);
            return remoteResourceInfos.stream().map(RemoteResourceInfo::getPath).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("sftp 获取文件列表失败 错误信息{}", ExceptionUtils.getStackTrace(e));
        }
        return new ArrayList<>();
    }

    public void download(String file, String locDir) throws IOException {
        sftpClient.get(file, new FileSystemFile(locDir));
    }

    public boolean upload(String file, String directory) {
        if (sftpClient == null) {
            return false;
        }
        try {
            if (directory != null && !"".equals(directory)) {
                sftpClient.put(new FileSystemFile(file), "./");
            }
            if (!isDirExist(directory)) {
                if (createDir(directory)) {
                    return false;
                } else {
                    sftpClient.put(new FileSystemFile(file), directory); // 上传文件
                    return true;
                }

            } else {
                sftpClient.put(new FileSystemFile(file), directory); // 上传文件
                return true;
            }

        } catch (IOException e) {
            logger.error("sftp 文件上传失败 错误信息{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     */
    public boolean upload(String directory, String sftpFileName, InputStream input) {
        if (sftpClient == null) {
            return false;
        }
        try {
            if (directory != null && !"".equals(directory)) {
                sftpClient.put(new CustomNameFile(sftpFileName, input), "./");
            }
            if (!isDirExist(directory)) {
                if (createDir(directory)) {
                    return false;
                } else {
                    sftpClient.put(new CustomNameFile(sftpFileName, input), directory); // 上传文件
                    return true;
                }

            } else {
                sftpClient.put(new CustomNameFile(sftpFileName, input), directory); // 上传文件
                return true;
            }

        } catch (IOException e) {
            logger.error("sftp 文件上传失败 错误信息{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 创建一个文件目录
     */
    public boolean createDir(String createpath) {
        try {
            sftpClient.mkdirs(createpath);
            return true;
        } catch (IOException e) {
            logger.error("sftp 创建文件夹失败 错误信息{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 判断目录是否存在
     */
    public boolean isDirExist(String directory) throws IOException {
        FileAttributes lstat = sftpClient.statExistence(directory);
        if (null == lstat) {
            return false;
        }
        return true;
    }

}
