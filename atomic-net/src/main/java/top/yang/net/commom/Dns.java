package top.yang.net.commom;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author pride
 */
public interface Dns {

    List<InetAddress> lookup(String hostname) throws UnknownHostException;
}
