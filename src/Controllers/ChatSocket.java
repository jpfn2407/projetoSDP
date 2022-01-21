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

public class ChatSocket implements Runnable {
    private Integer pin;
    //private NameService nameService = null;
    private Chat chat;
    private SecretKey secretKey;
    private Cipher desCipher;
    private boolean running;

    private DatagramSocket DS;
    private byte bp[]=new byte[1024];
    private TextArea ecran=new TextArea(10,30);

    ChatSocket(TextArea ta, Integer pin, SecretKey secretKey, Cipher desCipher, Chat chat){
        ecran=ta;
        this.pin = pin;
        this.chat = chat;
        //this.nameService = nameService;
        this.secretKey = secretKey;
        this.desCipher = desCipher;
        this.running = true;
        try{
            this.DS = new DatagramSocket(pin);
        }
        catch(IOException e){

        }
    }

    @Override
    public void run() {
        while(running) {
            System.out.println("im running");
            receiveDP();
        }
    }

    public void stop(){
        this.pin = null;
        this.running = false;
        this.DS.close();
    }

    public boolean isRunning(){
        return this.running;
    }

    public void receiveDP(){
        try{
            //Recebe um packet vindo da network
            DatagramPacket DP = new DatagramPacket(bp,1024);
            this.DS.receive(DP);

            if(DP.getPort() == 7999) {
                ArrayList<Object> receivedCommands = null;
                while (receivedCommands == null){
                    try{
                        byte[] listData = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(listData,1024);
                        this.DS.receive(datagramPacket);
                        byte[] listDataBytes = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
                        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listDataBytes));
                        receivedCommands = (ArrayList) inputStream.readObject();

                        if(receivedCommands.get(0).equals("isUserRegistered")){
                            if(!(boolean)receivedCommands.get(1)){
                                ecran.appendText("AVISO: Um dos utilizadores não se encontra registado. \n");
                            }
                        } else {
                            sendCommandPackage(new ArrayList<>(Arrays.asList("getPin", user)));
                        }

                    } catch (Exception e){
                        System.out.println(e);
                    }
                }

            }
            else {
                String receivingMsg = null;
                try {
                    //Apanha apenas cadeia de bytes da string vindo do packet
                    byte[] b = Arrays.copyOf(DP.getData(), DP.getLength());
                    //Faz decode da mensagem
                    desCipher.init(Cipher.DECRYPT_MODE, secretKey);
                    byte[] decodedSTR = desCipher.doFinal(b);
                    receivingMsg = new String(decodedSTR);

                } catch (Exception e){
                    System.out.println("Erro a fazer a decifragem do packet receptor.");
                    System.out.println(e);
                }

                //String receivingUser = this.nameService.getUser(String.valueOf(DP.getPort()));
                sendCommandPackage(new ArrayList<>(Arrays.asList("getUser", this.pin)));
                String receivingUser = (String) this.latestDP.get(0);
                ecran.appendText(receivingUser+": "+ receivingMsg + "\n");
            }

        }catch(IOException e){
            System.out.println("Erro a receber o packet.");
            System.out.println(e);
        }
    }

    public void sendDP(Integer port, String msg){
        try{
            //Faz encode da mensagem
            byte[] b = msg.getBytes(StandardCharsets.UTF_8);
            this.desCipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] encodedSTR = this.desCipher.doFinal(b);
            int len = msg.length();

            ecran.appendText("Mensagem encriptada enviada: " + new String(encodedSTR, 0, 0, len) + "\n");
            //Envia um packet para a network
            DatagramPacket DP = new DatagramPacket(encodedSTR, encodedSTR.length, InetAddress.getByName("127.0.0.1"), port);
            DS.send(DP);
        }catch(Exception e){
            System.out.println("Erro ao enviar o packet.");
            System.out.println(e);
        }
    }

    public void sendCommandPackage(ArrayList<Object> commandListPackage){
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

    }


    public void sendMessage(String msg, String[] usersList) {
        if (this.pin == null) {
            ecran.appendText("Ainda não validou um pin. \n");
        } else {
            for(String user : usersList){
                sendCommandPackage(new ArrayList<>(Arrays.asList("isUserRegistered", user)));

                sendCommandPackage(new ArrayList<>(Arrays.asList("getPin", user)));
                Integer port = Integer.parseInt((String) this.latestDP.get(0));
                sendDP(port, msg);
                }
            }
        }
    }
}
