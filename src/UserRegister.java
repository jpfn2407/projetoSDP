import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class UserRegister extends Frame {
    NameService nameService = null;
    TextArea ecran=new TextArea(3,75);
    TextField nameField=new TextField("Nome",30);
    TextField pinField=new TextField("Pin",30);
    Button Register=new Button("Register");

    public UserRegister(String str, NameService nameService){
        super(str);
        this.nameService = nameService;
        resize(600,200);
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
            return true;
        }
        return false;
    }
}
