import javax.crypto.*;
import javax.crypto.spec.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;

public class Socket implements Runnable {
    Integer pin = null;
    NameService nameService = null;
    SecretKey secretKey = null;
    Cipher desCipher = null;

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
    }

    @Override
    public void run() {
        try{
            DS=new DatagramSocket(pin);
        }
        catch(IOException e){

        }
        while(true) receiveDP();
    }

    public void receiveDP(){
        try{
            DatagramPacket DP = new DatagramPacket(bp,1024);
            DS.receive(DP);

            System.out.println(this.secretKey.toString());

            String receivingMsg = null;
            try {
                byte payload[] = DP.getData();
                String encodedMSG = new String(payload,0,0,DP.getLength());
                payload = encodedMSG.getBytes();
                this.desCipher.init(Cipher.DECRYPT_MODE, this.secretKey);
                byte[] receivingString = desCipher.doFinal(payload);
                receivingMsg = new String(receivingString,0,0,DP.getLength());
            } catch (Exception e){
                System.out.println(e);
            }

            //byte Payload[] = DP.getData();
            //int len = DP.getLength();
            //String receivingMsg = new String(Payload,0,0,len);
            String receivingUser = this.nameService.getUser(String.valueOf(DP.getPort()));
            ecran.appendText(receivingUser+": "+ receivingMsg + "\n");
        }catch(IOException e){

        }
    }

    public void sendDP(Integer port, String msg){
        try{
            byte[] b = msg.getBytes();
            this.desCipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

            System.out.println(this.secretKey.toString());

            byte[] encodedSTR = desCipher.doFinal(b);
            int len = msg.length();
            //String eSTR=new String(encodedSTR);

            //int len=msg.length();
            //byte b[]=new byte[len];
            //msg.getBytes(0,len,b,0);
            DatagramPacket DP=new DatagramPacket(encodedSTR, len, InetAddress.getByName("127.0.0.1"), port);
            DS.send(DP);
        }catch(Exception e){

        }
    }


}
