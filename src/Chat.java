import java.awt.*;
import java.io.IOException;

public class Chat extends Frame {
    Integer pin = null;
    NameService nameService = null;
    TextArea ecran=new TextArea(10,30);
    TextField pinField=new TextField("Pin",30);
    Button validateBtn=new Button("Validate");
    TextField usersField=new TextField("Users",30);
    TextField msgField=new TextField("Message", 30);
    Button sendBtn=new Button("Send");
    socket_professor sock=new socket_professor(ecran);

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

}
