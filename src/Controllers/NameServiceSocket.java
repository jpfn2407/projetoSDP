package Controllers;

import java.awt.*;
import java.io.*;
import java.net.DatagramSocket;

public class NameServiceSocket implements Runnable{
    private boolean running = false;
    private Integer port = 7999;
    private NameService nameService;
    private DatagramSocket datagramSocket;

    NameServiceSocket(NameService nameService){
        this.running = true;
        this.nameService = nameService;
    }

    @Override
    public void run() {
        while(running) receiveCommands();
    }

    private void receiveCommands() {
        try {
            byte[] listData;
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listData));
            List list = inputStream.readObject();

        }catch (Exception e){
            System.out.println(e);
        }
    }

}
