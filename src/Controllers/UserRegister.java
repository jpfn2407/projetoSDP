package Controllers;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class UserRegister extends Frame {
    private double screenWidth;
    private double screenHeight;
    //private NameService nameService = null;
    private DatagramSocket datagramSocket;
    private final Integer port = 7998;

    private TextArea ecran=new TextArea(1,75);
    private TextField nameField=new TextField("Nome",30);
    private TextField pinField=new TextField("Pin",30);
    private Button register = new Button("Register");
    private Button newChatWindow = new Button("New Chat Window");

    public UserRegister(String str, NameService nameService){
        super(str);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.getWidth();
        this.screenHeight = screenSize.getHeight();
        //this.nameService = nameService;
        try {
            this.datagramSocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //GUI elements
        resize((int)Math.round(this.screenWidth * 0.3125),(int)Math.round(this.screenHeight * 0.13));
        this.ecran = new TextArea((int)Math.round(this.screenHeight * 0.0009),(int)Math.round(this.screenWidth * 0.04));
        this.nameField = new TextField("Nome",(int)Math.round(this.screenWidth * 0.016));
        this.pinField = new TextField("Pin",(int)Math.round(this.screenWidth * 0.016));
        GUI();
        show();
    }


    public void GUI(){
        setResizable(false);
        setBackground(Color.lightGray);
        //ecran.setEditable(false);
        Panel P1=new Panel();
        P1.add("North",nameField);
        P1.add("West",pinField);
        P1.add("East", register);
        P1.add("South",ecran);
        P1.add("South", newChatWindow);
        add(P1);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 10);
    }

    public boolean handleEvent(Event i){
        if(i.id==Event.WINDOW_DESTROY){
            dispose();
            System.exit(0);
            return true;
        }
        return super.handleEvent(i);
    }

    public boolean action(Event i,Object o){
        if(i.target== register){
            String name=nameField.getText();
            String pin=pinField.getText();

            //if(this.nameService.isUserRegistered(name)){
            //    ecran.setText("Este utilizador já existe associado ao pin: " + this.nameService.getPin(name));
            //} else if(this.nameService.isPinRegistered(pin)){
            //    ecran.setText("Este pin já está associado ao utilizador: " + this.nameService.getUser(pin));
            //} else if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010){
            //    ecran.setText("Pin invalido. Está fora do range 8000 e 8010.");
            //} else {
            //    this.nameService.registerUser(name, pin);
            //    this.nameService.readFile();
            //    ecran.setText("Registado com sucesso!");
            //}

            if((boolean) sendCommandPackage(new ArrayList<>(Arrays.asList("isUserRegistered", name))).get(1)){
                ecran.setText("Este utilizador já existe associado ao pin: " +
                        (String) sendCommandPackage(new ArrayList<>(Arrays.asList("getPin", name))).get(1));
            }
            else if((boolean) sendCommandPackage(new ArrayList<>(Arrays.asList("isPinRegistered", pin))).get(1)){
                ecran.setText("Este utilizador já existe associado ao pin: " +
                        (String) sendCommandPackage(new ArrayList<>(Arrays.asList("getUser", pin))).get(1));
            }
            else if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010) {
                ecran.setText("Pin invalido. Está fora do range 8000 e 8010.");
            }
            else{
                sendCommandPackage(new ArrayList<>(Arrays.asList("registerUser", name, pin)));
                ecran.setText("Registado com sucesso!");
            }
            return true;

        } else if ( i.target == newChatWindow ){
            new Chat("Chat");
        }
        return false;
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
            this.datagramSocket.send(DP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Object> receivedCommands = null;
        while (true){
            try{
                byte[] listData = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(listData,1024);
                this.datagramSocket.receive(datagramPacket);
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

    public boolean isNumber(String str){
        try {
            int val = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

}
