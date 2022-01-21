package Controllers;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.awt.*;
import java.security.InvalidKeyException;

public class Chat extends Frame {
    private Integer pin = null;
    //private NameService nameService = null;
    private ChatSocket sock = null;
    private ChatValidationSocket validationSocket = null;
    private Thread sockThread = null;
    private SecretKey secretKey = null;
    private Cipher desCipher = null;
    private static final String desEncodingPassword = "PasswordSuperSecreta";

    private double screenWidth;
    private double screenHeight;
    private TextArea ecran=new TextArea(10,50);
    private TextField pinField=new TextField("Pin",30);
    private TextField usersField=new TextField("Users",30);
    private TextField msgField=new TextField("Message", 30);
    private Button validateBtn=new Button("Validate");
    private Button sendBtn=new Button("Send");

    public Chat(String str){
        super(str);
        //this.nameService = nameService;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.getWidth();
        this.screenHeight = screenSize.getHeight();

        //resize(400,325);
        resize((int)Math.round(this.screenWidth * 0.208), (int)Math.round(this.screenHeight * 0.285));
        this.ecran = new TextArea((int)Math.round(this.screenHeight * 0.009),(int)Math.round(this.screenWidth * 0.026));
        this.pinField = new TextField("Pin", (int)Math.round(this.screenWidth * 0.018));
        this.usersField = new TextField("Users",(int)Math.round(this.screenWidth * 0.023));
        this.msgField = new TextField("Message", (int)Math.round(this.screenWidth * 0.0195));
        GUI();
        show();

        try {
            DESKeySpec desKeySpec = new DESKeySpec(this.desEncodingPassword.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            this.secretKey = keyFactory.generateSecret(desKeySpec);
            this.desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //this.sock = new ChatSocket(ecran, this.pin, this.secretKey, this.desCipher, this);
        this.validationSocket = new ChatValidationSocket(ecran, this.pin, this.secretKey, this.desCipher, this);

    }

    public void GUI(){
        setResizable(false);
        setBackground(Color.lightGray);
        ecran.setEditable(false);
        Panel P1=new Panel();
        setLayout(new BorderLayout(10,10));
        P1.add(pinField);
        P1.add(validateBtn);
        P1.add(usersField);
        P1.add(msgField);
        P1.add(sendBtn);
        P1.add(ecran);
        add(P1);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
    }

    public boolean handleEvent(Event i){
        if(i.id==Event.WINDOW_DESTROY){
            //System.exit(0);
            if( this.sock != null && this.sock.isRunning()){
                this.sock.stop();
                this.sockThread.stop();
                this.sock = null;
                this.sockThread = null;
            }
            dispose();
            return true;
        }
        return super.handleEvent(i);
    }

    public boolean action(Event i,Object o){
        if(i.target==validateBtn){
            String pin = this.pinField.getText();
            //if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010){
            //    ecran.appendText("Pin invalido. Está fora do range 8000 e 8010. \n");
//
            //} else if (!this.nameService.isPinRegistered(pin)) {
            //    ecran.appendText("Este Pin não se encontra registado. \n");
//
            //} else if(this.pin != null){
            //    ecran.appendText("Serviço de chat já está a correr com o pin " + this.pin + ". \n");
//
            //} else {
            //    try {
            //        ecran.appendText("\n Utilizador " + this.nameService.getUser(pin) + " validado! \n \n");
            //        this.pin = Integer.parseInt(pin);
            //        setTitle(this.nameService.getUser(pin));
//
            //        //Cria o conteudo necessario para fazer encode e decode de mensagens
            //        DESKeySpec desKeySpec = new DESKeySpec(this.desEncodingPassword.getBytes());
            //        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //        this.secretKey = keyFactory.generateSecret(desKeySpec);
            //        this.desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//
            //        //Cria um novo socket e associa a uma thread
            //        this.sock = new ChatSocket(ecran, this.pin, this.secretKey, this.desCipher, this);
            //        this.sockThread = new Thread(this.sock);
            //        this.sockThread.start();
            //    } catch (Exception e){
            //        ecran.appendText("\n Erro ao abrir o socket. Ver a consola para mais info.");
            //        System.out.println(e);
            //    }
            //}

            Integer port = this.validationSocket.validateMethod(pin);
            System.out.println(port);
            if(port != null){
                this.sock = new ChatSocket(ecran, port, this.secretKey, this.desCipher, this);
                this.sockThread = new Thread(this.sock);
                this.sockThread.start();
            }
            return true;

        }
        else if (i.target==sendBtn){
            //if (this.pin == null) {
            //    ecran.appendText("Ainda não validou um pin. \n");
            //} else {
            //    String users = usersField.getText();
            //    String msg = msgField.getText();
            //    String[] usersList = users.split(";");
            //    for(String user : usersList){
            //        if (!this.nameService.isUserRegistered(user)){
            //            ecran.appendText("AVISO: Utilizador  '" + user + "' não se encontra registado. \n");
            //        } else {
            //            Integer port = Integer.parseInt(this.nameService.getPin(user));
            //            sock.sendDP(port, msg);
            //        }
            //    }
            //    msgField.setText("");
            //    return true;
            //}
            this.sock.sendMessage(msgField.getText(), usersField.getText().split(";"));
            msgField.setText("");
        }
        return false;
    }

}
