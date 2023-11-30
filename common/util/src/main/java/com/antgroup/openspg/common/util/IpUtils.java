/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yangjin
 * @version : IpUtils.java, v 0.1 2023年11月30日 16:53 yangjin Exp $
 */
public class IpUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

    public static final String LOCALHOST = "127.0.0.1";
    public static final String IP_LIST   = String.join(",", getLocalIPList());

    /**
     * @return
     * @Description: 获取本地IP列表（针对多网卡或者虚礼机情况）
     * @author
     * @date
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                        if (LOCALHOST.equals(ip)) {
                            continue;
                        }
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error(String.format("getLocalIPList failed."), e);
        }
        return ipList;
    }
}
