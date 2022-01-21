package Controllers;

import javax.crypto.*;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatValidationSocket implements Runnable {
    private Integer pin;
    //private NameService nameService = null;
    private Chat chat;
    private SecretKey secretKey;
    private Cipher desCipher;
    private boolean running;

    private DatagramSocket DS;
    private byte bp[]=new byte[1024];
    private TextArea ecran=new TextArea(10,30);

    ChatValidationSocket(TextArea ta, Integer pin, SecretKey secretKey, Cipher desCipher, Chat chat){
        ecran=ta;
        this.pin = pin;
        this.chat = chat;
        //this.nameService = nameService;
        this.secretKey = secretKey;
        this.desCipher = desCipher;
        this.running = false;
    }

    @Override
    public void run() {
    }

    public void stop(){
        this.pin = null;
        this.running = false;
        this.DS.close();
    }

    public boolean isRunning(){
        return this.running;
    }

    public Integer validateMethod(String pin){
        try {
            if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010){
                ecran.appendText("Pin invalido. Está fora do range 8000 e 8010. \n");
                return null;
            }
            if(this.DS == null){
                this.DS = new DatagramSocket(Integer.parseInt(pin));
                if (!(boolean) sendCommandPackage(new ArrayList<>(Arrays.asList("isPinRegistered", pin))).get(1)) {
                    ecran.appendText("Este Pin não se encontra registado. \n");
                    this.DS.close();
                    this.DS = null;
                    return null;
                }
                this.DS.close();
                this.DS = null;
            }
            if(this.pin != null){
                ecran.appendText("Serviço de chat já está a correr com o pin " + this.pin + ". \n");
            } else {
                try {
                    this.pin = Integer.parseInt(pin);
                    this.DS = new DatagramSocket(this.pin);
                    ecran.appendText("\n Utilizador " +
                            (String) sendCommandPackage(new ArrayList<>(Arrays.asList("getUser", pin))).get(1) +
                            " validado! \n \n");
                    this.running = true;
                    this.DS.close();
                    this.DS = null;
                    return this.pin;
                    //setTitle(this.nameService.getUser(pin));

                } catch (Exception e){
                    ecran.appendText("\n Erro ao abrir o socket. Ver a consola para mais info.");
                    System.out.println(e);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isNumber(String str){
        try {
            int val = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public ArrayList<Object> sendCommandPackage(ArrayList<Object> commandListPackage){
        try {
            //Transforma a lista em package de bytes para enviar
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ObjectOutputStream outputStream = null;
            outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(commandListPackage);
            outputStream.close();
            byte[] listData = out.toByteArray();
            DatagramPacket DP = new DatagramPacket(listData, listData.length, InetAddress.getByName("127.0.0.1"), 7999);
            this.DS.send(DP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Object> receivedCommands = null;
        while (true){
            try{
                byte[] listData = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(listData,1024);
                this.DS.receive(datagramPacket);
                byte[] listDataBytes = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
                ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listDataBytes));
                receivedCommands = (ArrayList) inputStream.readObject();
                if(receivedCommands != null){
                    break;
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }
        return receivedCommands;
    }


}
