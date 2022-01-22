package Controllers;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class NameServiceSocket implements Runnable{
    private boolean running = false;
    private final Integer port = 7999;
    private NameService nameService;
    private DatagramSocket datagramSocket;
    private SecretKey secretKey = null;
    private Cipher desCipher = null;
    private static final String desEncodingPassword = "PasswordSuperSecreta";

    NameServiceSocket(NameService nameService){
        this.running = true;
        this.nameService = nameService;
        try {
            this.datagramSocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            DESKeySpec desKeySpec = new DESKeySpec(this.desEncodingPassword.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            this.secretKey = keyFactory.generateSecret(desKeySpec);
            this.desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(running){
            receiveCommands();
            nameService.readFile();
        }
    }

    private void receiveCommands() {
        try {
            byte[] listData = new byte[1024];
            DatagramPacket DP = new DatagramPacket(listData,1024);
            this.datagramSocket.receive(DP);
            Integer port = DP.getPort();

            byte[] listDataBytes = Arrays.copyOf(DP.getData(), DP.getLength());

            desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decriptedList = desCipher.doFinal(listDataBytes);

            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(decriptedList));
            ArrayList<Object> receivedList = (ArrayList) inputStream.readObject();
            switch((String) receivedList.get(0)){
                case "registerUser":
                    String name = (String) receivedList.get(1);
                    String pin = (String) receivedList.get(2);
                    this.nameService.registerUser(name, pin);
                    sendResponse(new ArrayList<>(Arrays.asList("registerUser", true, port)));
                    break;
                case "isUserRegistered":
                    name = (String) receivedList.get(1);
                    sendResponse(new ArrayList<>(Arrays.asList("isUserRegistered", this.nameService.isUserRegistered(name), port)));
                    break;
                case "isPinRegistered":
                    pin = (String) receivedList.get(1);
                    sendResponse(new ArrayList<>(Arrays.asList("isPinRegistered", this.nameService.isPinRegistered(pin), port)));
                    break;
                case "getPin":
                    name = (String) receivedList.get(1);
                    sendResponse(new ArrayList<>(Arrays.asList("getPin", this.nameService.getPin(name), port)));
                    break;
                case "getUser":
                    pin = (String) receivedList.get(1);
                    sendResponse(new ArrayList<>(Arrays.asList("getUser", this.nameService.getUser(pin), port)));
                    break;
                case "validateUsersInMessage":
                    String msg = (String) receivedList.get(1);
                    ArrayList<Object> responseList = new ArrayList<>();
                    for(String user : (String[]) receivedList.get(2)){
                        if(this.nameService.isUserRegistered(user)){
                            responseList.add(this.nameService.getPin(user));
                        }
                    }
                    sendResponse(new ArrayList<>(Arrays.asList("validateUsersInMessage",msg, responseList, port)));
                    break;
            }


        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void sendResponse(ArrayList<Object> commandListPackage){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.desCipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            ObjectOutputStream outputStream = null;
            outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(commandListPackage);
            outputStream.close();
            byte[] listData = out.toByteArray();
            byte[] encodedListData = this.desCipher.doFinal(listData);
            DatagramPacket DP = new DatagramPacket(encodedListData, encodedListData.length, InetAddress.getByName("127.0.0.1"), (Integer) commandListPackage.get(commandListPackage.size()-1));
            this.datagramSocket.send(DP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
