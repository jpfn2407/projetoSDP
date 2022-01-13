import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class UserRegister extends Frame {
    NameService nameService = null;
    TextArea ecran=new TextArea(1,75);
    TextField nameField=new TextField("Nome",30);
    TextField pinField=new TextField("Pin",30);
    Button Register=new Button("Register");

    public UserRegister(String str, NameService nameService){
        super(str);
        this.nameService = nameService;
        resize(600,120);
        GUI();
        show();
    }

    public static void main(String[] args)throws IOException {
        //UserRegister userRegister=new UserRegister("Register", null);
        //userRegister.resize(600,200);
        //userRegister.GUI();
        //userRegister.show();

    }

    public void GUI(){
        setResizable(false);
        setBackground(Color.lightGray);
        //ecran.setEditable(false);

        Panel P1=new Panel();
        P1.add("North",nameField);
        P1.add("West",pinField);
        P1.add("East",Register);
        P1.add("South",ecran);

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
        if(i.target==Register){
            String name=nameField.getText();
            String pin=pinField.getText();
            //sock.sendDP(8080,msg,end);
            //text.setText("");
            if(this.nameService.isUserRegistered(name)){
                ecran.setText("Este utilizador já existe associado ao pin: " + this.nameService.getPin(name));
            } else if(this.nameService.isPinRegistered(pin)){
                ecran.setText("Este pin já está associado ao utilizador: " + this.nameService.getUser(pin));
            } else if (!isNumber(pin) || Integer.parseInt(pin) < 8000 || Integer.parseInt(pin) > 8010){
                ecran.setText("Pin invalido. Está fora do range 8000 e 8010.");
            } else {
                this.nameService.registerUser(name, pin);
                this.nameService.readFile();
                ecran.setText("Registado com sucesso!");
            }
            return true;
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
