package top.lytree.sftp;

import java.net.Socket;

public class SFTPClient {
    /** The timeout to use after opening a socket. */
    protected int _timeout_;

    /** The hostname used for the connection (null = no hostname supplied). */
    protected String _hostname_;

    /** The default port the client should connect to. */
    protected int _defaultPort_;

}
