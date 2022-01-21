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
    private String name;
    //private NameService nameService = null;
    private Chat chat;
    private SecretKey secretKey;
    private Cipher desCipher;
    private boolean running;

    private DatagramSocket DS;
    private byte bp[]=new byte[1024];
    private TextArea ecran=new TextArea(10,30);

    ChatSocket(TextArea ta, Integer pin, String name, SecretKey secretKey, Cipher desCipher, Chat chat){
        ecran=ta;
        this.pin = pin;
        this.name = name;
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
            try {
                byte[] listDataBytes = Arrays.copyOf(DP.getData(), DP.getLength());
                ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listDataBytes));
                ArrayList<Object> receivedList = (ArrayList) inputStream.readObject();
                String validation = (String) receivedList.get(0);


                if(validation.equals("validateUsersInMessage")){
                    String msg = (String) receivedList.get(1);
                    ArrayList<String> usersList = (ArrayList) receivedList.get(2);
                    for(String portStr : usersList){
                        Integer port = Integer.parseInt(portStr);
                        if(port != null){
                            String finalMsg = new String(this.name + ":" + msg);
                            byte[] b = finalMsg.getBytes(StandardCharsets.UTF_8);
                            this.desCipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
                            byte[] encodedSTR = this.desCipher.doFinal(b);
                            int len = finalMsg.length();

                            ecran.appendText("Mensagem encriptada enviada: " + new String(encodedSTR, 0, 0, len) + "\n");
                            //Envia um packet para a network
                            DatagramPacket sendDP = new DatagramPacket(encodedSTR, encodedSTR.length, InetAddress.getByName("127.0.0.1"), port);
                            DS.send(sendDP);
                        }
                    }
                }
            } catch (Exception e){
                String receivingMsg = null;
                try {
                    //Apanha apenas cadeia de bytes da string vindo do packet
                    byte[] b = Arrays.copyOf(DP.getData(), DP.getLength());
                    //Faz decode da mensagem
                    desCipher.init(Cipher.DECRYPT_MODE, secretKey);
                    byte[] decodedSTR = desCipher.doFinal(b);
                    receivingMsg = new String(decodedSTR);

                    //String receivingUser = (String.valueOf(DP.getPort()));
                    ecran.appendText( receivingMsg + "\n");
                } catch (Exception e2){
                    System.out.println("Erro a fazer a decifragem do packet receptor.");
                    System.out.println(e2);
                }
                System.out.println("Packet recebido.");
                //System.out.println(e);
            }

            //String receivingMsg = null;
            //try {
            //    //Apanha apenas cadeia de bytes da string vindo do packet
            //    byte[] b = Arrays.copyOf(DP.getData(), DP.getLength());
            //    //Faz decode da mensagem
            //    desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            //    byte[] decodedSTR = desCipher.doFinal(b);
            //    receivingMsg = new String(decodedSTR);
            //} catch (Exception e){
            //    System.out.println("Erro a fazer a decifragem do packet receptor.");
            //    System.out.println(e);
            //}


            //String receivingUser = this.nameService.getUser(String.valueOf(DP.getPort()));
            //ecran.appendText( receivingUser + ":" + receivingMsg + "\n");


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


    public void sendMessage(String msg, String[] usersList) {
        try {
            //Transforma a lista em package de bytes para enviar
            ArrayList<Object> commandListPackage = new ArrayList<>();
            commandListPackage.add("validateUsersInMessage");
            commandListPackage.add(msg);
            commandListPackage.add(usersList);

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
}
