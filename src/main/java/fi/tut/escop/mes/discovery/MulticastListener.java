/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.constants.HostPortandConfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class MulticastListener implements Runnable {

    String network;
    String group;
    int port;
    MulticastSocket socket;
    ObjectMapper om = new ObjectMapper();

    private static final Logger LOG = Logger.getLogger(MulticastListener.class.getName());

    public MulticastListener() throws IOException {
        this(HostPortandConfig.PROBING_GROUP, HostPortandConfig.PROBING_PORT);
    }

    public MulticastListener(String group, int port) throws IOException {
        this(InetAddress.getLocalHost().toString(), group, port);
    }

    public MulticastListener(String network, String group, int port) throws IOException {
        this.group = group;
        this.port = port;

        socket = new MulticastSocket(port);
        socket.setReuseAddress(true);
        socket.setInterface(InetAddress.getLoopbackAddress());
        socket.joinGroup(InetAddress.getByName(group));

        network = socket.getInterface().toString();
        LOG.log(Level.INFO, "Creating multicast listener: \n +Multicast listener\n|--Network: {2}\n|--Socket: {0}\n|--Group: {1}", new Object[]{this.port, this.group, network});

    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String parseData(DatagramPacket data) {
        byte[] bt = data.getData();
        return new String(bt).trim();
    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];
        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(data);
//                LOG.log(Level.INFO, "Multicasted message received");
                String message = "";
                message = parseData(data);
//                LOG.log(Level.INFO, "\n--------BODY--------\n{0}\n------BODY END----", message);
                Probing probe;
                try {
                    if (!message.equals("")) {
                        ObjectMapper om = new ObjectMapper();
                        probe = om.readValue(message, Probing.class);
                        if ((!probe.dc.equals("")) && HostPortandConfig.DISCOVERY) {
                            HostPortandConfig.RPL_PROBE_DC = probe.dc;
                            HostPortandConfig.RPL_AVAILABLE = true;
                            if (HostPortandConfig.CONFIG_DONE) {
                                probe.postSwagger();
                            }
                        }
                    } else {
                        HostPortandConfig.RPL_AVAILABLE = false;
                    }
                } finally {

                }
            } catch (IOException ex) {
                Logger.getLogger(MulticastListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MulticastListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
