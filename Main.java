package indi.zach;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        /* 1. prepare connection */
        Socket socket = new Socket();
        InputStream socketIn;
        String host = "127.0.0.1";
        int port = 8080;

        try {
            socket.setReceiveBufferSize(8192);
            socket.connect(new InetSocketAddress(InetAddress.getAllByName(host)[0], port));
            if (!socket.isConnected()) {
                throw new IOException("Cannot connect socket.");
            }
            socket.setSoTimeout(5_000);  // 5s
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(3);
        } catch (MalformedURLException e) {
            System.err.println("Invalid host!");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

        StringBuilder requestBuilder = new StringBuilder();
        // construct headers
        requestBuilder.append("GET /api HTTP/1.1\r\nHost: ").append(host).append(":").append(port).append("\r\n").append("\r\n");
        System.out.println(requestBuilder.toString());
        byte[] requestBytes = requestBuilder.toString().getBytes();

        // send request to server
        try {
            socket.getOutputStream().write(requestBytes, 0, requestBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // test infi loop behavior(fail to read socket buffer in the next part of code)
        int index = 0;
        while (index >= 0) {
            index += 1;
            if (index > 10000) {
                index = 1;
            }
        }

        // read receive socket buffer here
        try {
            int dataLen = 1024;
            socketIn = socket.getInputStream();
            int lastSocketAvai = socketIn.available();
            while (lastSocketAvai < dataLen) {  // read by 1024 bytes, wait until socket has enough data
                int curSocketAvai = socketIn.available();
                if (curSocketAvai > lastSocketAvai) {
                    lastSocketAvai = curSocketAvai;
                    System.out.println(curSocketAvai);
                }
            }
            byte[] inBuffer = new byte[dataLen];
            int readLen = socketIn.read(inBuffer, 0, dataLen);
            System.out.println(readLen);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
