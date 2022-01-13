package Controllers;

import javax.crypto.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Socket implements Runnable {
    Integer pin = null;
    NameService nameService = null;
    SecretKey secretKey = null;
    Cipher desCipher = null;
    boolean running = false;

    InetAddress ER,IPr;
    DatagramSocket DS;
    byte bp[]=new byte[1024];
    TextArea ecran=new TextArea(10,30);

    Socket(TextArea ta, Integer pin, NameService nameService, SecretKey secretKey, Cipher desCipher){
        ecran=ta;
        this.pin = pin;
        this.nameService = nameService;
        this.secretKey = secretKey;
        this.desCipher = desCipher;
        this.running = true;
    }

    @Override
    public void run() {
        try{
            DS=new DatagramSocket(pin);
        }
        catch(IOException e){

        }
        while(running) receiveDP();
    }

    public void stop(){
        this.running = false;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void receiveDP(){
        try{
            //Recebe um packet vindo da network
            DatagramPacket DP = new DatagramPacket(bp,1024);
            DS.receive(DP);

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

            String receivingUser = this.nameService.getUser(String.valueOf(DP.getPort()));
            ecran.appendText(receivingUser+": "+ receivingMsg + "\n");
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
            DatagramPacket DP=new DatagramPacket(encodedSTR, encodedSTR.length, InetAddress.getByName("127.0.0.1"), port);
            DS.send(DP);
        }catch(Exception e){
            System.out.println("Erro ao enviar o packet.");
            System.out.println(e);
        }
    }


}
