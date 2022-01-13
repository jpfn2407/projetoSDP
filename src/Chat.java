import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.awt.*;
import java.io.IOException;

public class Chat extends Frame {
    Integer pin = null;
    NameService nameService = null;
    Socket sock= null;
    private static final String desEncodingPassword = "Password Super Secreta de SDP";
    SecretKey secretKey = null;
    Cipher desCipher = null;

    TextArea ecran=new TextArea(10,50);
    TextField pinField=new TextField("Pin",30);
    Button validateBtn=new Button("Validate");
    TextField usersField=new TextField("Users",30);
    TextField msgField=new TextField("Message", 30);
    Button sendBtn=new Button("Send");


    public Chat(String str, NameService nameService){
        super(str);
        this.nameService = nameService;
        resize(400,325);
        GUI();
        show();
    }

    public static void main(String[] args)throws IOException {

    }

    public void GUI(){
        setResizable(false);
        setBackground(Color.lightGray);
        ecran.setEditable(false);
        //GridBagLayout GBL=new GridBagLayout();
        //setLayout(GBL);

        Panel P1=new Panel();
        setLayout(new BorderLayout(10,10));
        //setLayout(new GridLayout(3,2));
        P1.add(pinField);
        P1.add(validateBtn);
        P1.add(usersField);
        P1.add(msgField);
        P1.add(sendBtn);
        P1.add(ecran);
        //GridBagConstraints P1C=new GridBagConstraints();
        //P1C.gridwidth=GridBagConstraints.REMAINDER;
        //GBL.setConstraints(P1,P1C);
        add(P1);
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
        if(i.target==validateBtn){
            String pin = this.pinField.getText();
            if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010){
                ecran.appendText("Pin invalido. Está fora do range 8000 e 8010. \n");
            } else if (!this.nameService.isPinRegistered(pin)) {
                ecran.appendText("Este Pin não se encontra registado. \n");
            } else if(this.pin != null){
                ecran.appendText("Serviço de chat já está a correr com o pin " + this.pin + ". \n");
            } else {
                try {
                    ecran.appendText("\n Utilizador " + this.nameService.getUser(pin) + " validado! \n \n");
                    this.pin = Integer.parseInt(pin);
                    DESKeySpec desKeySpec = new DESKeySpec(this.desEncodingPassword.getBytes());
                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                    this.secretKey = keyFactory.generateSecret(desKeySpec);
                    this.desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                    //this.desCipher = Cipher.getInstance("DES/ECB/NoPadding");

                    this.sock = new Socket(ecran, this.pin, this.nameService, this.secretKey, this.desCipher);
                    Thread thread = new Thread(this.sock);
                    thread.start();
                } catch (Exception e){
                    ecran.appendText("\n Erro ao abrir o socket. Ver a consola para mais info.");
                    System.out.println(e);
                }
            }
            return true;

        } else if (i.target==sendBtn){
            if (this.pin == null) {
                ecran.appendText("Ainda não validou um pin. \n");
            } else {
                String users = usersField.getText();
                String msg = msgField.getText();
                String[] usersList = users.split(";");
                for(String user : usersList){
                    if (!this.nameService.isUserRegistered(user)){
                        ecran.appendText("AVISO: Utilizador  '" + user + "' não se encontra registado. \n");
                    } else {
                        Integer port = Integer.parseInt(this.nameService.getPin(user));
                        sock.sendDP(port, msg);
                    }
                }
                msgField.setText("");
                return true;
            }
        }
        return false;
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
