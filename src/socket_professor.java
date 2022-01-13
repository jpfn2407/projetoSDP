import java.io.*;
import java.net.*;
import java.awt.*;
public class socket_professor extends Thread{
    InetAddress ER,IPr;
    DatagramSocket DS;
    byte bp[]=new byte[1024];
    TextArea ecran=new TextArea(10,30);
    socket_professor(TextArea ta){ecran=ta;}
    public void run(){
        try{DS=new DatagramSocket(8080);}
        catch(IOException e){}
        while(true) receiveDP();
    }
    public void receiveDP(){
        try{
            DatagramPacket DP=new DatagramPacket(bp,1024);
            DS.receive(DP);
            IPr=DP.getAddress();
            byte Payload[]=DP.getData();
            int len=DP.getLength();
            String res=new String(Payload,0,0,len);
            String tmp=IPr.toString();
            String temp=tmp.substring(1);
            ecran.appendText("\n"+temp+": "+res);
        }catch(IOException e){}
    }
    public void sendDP(int Pr,String msg,String end){
        int len=msg.length();
        byte b[]=new byte[len];
        msg.getBytes(0,len,b,0);
        try{
            ER=InetAddress.getByName(end);
            DatagramPacket DP=new DatagramPacket(b,len,ER,Pr);
            DS.send(DP);
        }catch(IOException e){}
    }
}