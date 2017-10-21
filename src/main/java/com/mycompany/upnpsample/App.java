package com.mycompany.upnpsample;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */


public class App {
    
    private static final String BLOG_URL ="https://objectpartners.com/2014/03/25/a-groovy-time-with-upnp-and-wemo/";
    public static void main(String[] args) {
        System.out.println("Hello World!");
//        getLocalIPAddress();
        discover();
        listen();
    }

    public void initMethod() {
        try {
            
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    private static void discover() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);
            MulticastSocket socket = new MulticastSocket(null);
            try {
                socket.bind(new InetSocketAddress("10.0.0.33", 1901));
                StringBuilder packet = new StringBuilder();
                packet.append("M-SEARCH * HTTP/1.1\r\n");
                packet.append("HOST: 239.255.255.250:1900\r\n");
                packet.append("MAN: \"ssdp:discover\"\r\n");
                packet.append("MX: ").append("2").append("\r\n");
//                packet.append("ST: ").append("ssdp:all").append("\r\n").append("\r\n"); // Gives router
//                packet.append( "ST: " ).append( "upnp:rootdevice" ).append( "\r\n" ).append( "\r\n" ); //Gives belkin
            packet.append( "ST: " ).append( "urn:Belkin:device:controllee:1" ).append( "\r\n" ).append( "\r\n" ); //Gives belkin
                byte[] data = packet.toString().getBytes();
                System.out.println("sending discovery packet");
                socket.send(new DatagramPacket(data, data.length, socketAddress));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.disconnect();
                socket.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private String getIpOfInterface(String iface) {
//        try {
//            NetworkInterface ni = NetworkInterface.getByName(iface);
//            InetAddress address = ni.getInetAddresses()[];// find { it.siteLocalAddress };
//            return address.hostAddress;
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//
        return null;
    }

    public static void listen() {
        try {
            MulticastSocket recSocket = new MulticastSocket(null);
            recSocket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 1901));
            recSocket.setTimeToLive(10);
            recSocket.setSoTimeout(1000);
            recSocket.joinGroup(InetAddress.getByName("239.255.255.250"));

            int timer = 0;
//        while (timer<2) {  //inService is a variable controlled by a thread to stop the listener
            for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(4); stop > System.nanoTime();) {

                byte[] buf = new byte[2048];
                DatagramPacket input = new DatagramPacket(buf, buf.length);
                try {
                    recSocket.receive(input);
                    String originaldata = new String(input.getData());
                    String originalAddr =  input.getAddress().getHostName();
                    System.out.println("Data: "+originaldata);
                    System.out.println("Host Name: "+originalAddr);

                } catch (Exception e) {
                }
                timer++;
            }
            recSocket.disconnect();
            recSocket.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void getLocalIPAddress() {
        try {
            System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements();) {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();) {
                    InetAddress addr = a.nextElement();
                    System.out.println("  " + addr.getHostAddress());
                }
            }

        } catch (UnknownHostException uxp) {
            uxp.printStackTrace();
        } catch (SocketException sexp) {
            sexp.printStackTrace();
        }
    }
}
