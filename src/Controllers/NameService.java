package Controllers;

import java.awt.*;
import java.io.*;

public class NameService extends Frame{
    private double screenWidth;
    private double screenHeight;
    private TextArea userField = new TextArea(25,15);
    private TextArea pinField = new TextArea(25,15);


    private static final String csvFilePath = "./src/names.csv";
    private NameServiceSocket nameServiceSocket = null;
    private Thread sockThread;

    public static void main(String[] args) {
        //Trata da criação do ficheiro CSV
        File file = new File(csvFilePath);
        if(file.exists() && !file.isDirectory()){
            System.out.println("Ficheiro de nomes já existe.");
        } else {
            System.out.println("Criando um novo ficheiro de nomes.");
            try (FileWriter writer = new FileWriter(csvFilePath)) {

                StringBuilder sb = new StringBuilder();
                sb.append("name");
                sb.append(',');
                sb.append("pin");
                sb.append('\n');

                writer.write(String.valueOf(sb));
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public NameService(String str){
        super(str);

        NameServiceSocket nameServiceSocket = new NameServiceSocket(this);
        this.sockThread = new Thread(nameServiceSocket);
        this.sockThread.start();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.getWidth();
        this.screenHeight = screenSize.getHeight();
        resize((int)Math.round(this.screenWidth * 0.156),(int)Math.round(this.screenHeight * 0.416));
        this.userField = new TextArea((int)Math.round(this.screenHeight * 0.023),(int)Math.round(this.screenWidth * 0.008));
        this.pinField = new TextArea((int)Math.round(this.screenHeight * 0.023),(int)Math.round(this.screenWidth * 0.008));
        GUI();
        show();
        main(null);
        readFile();
    }

    public void GUI() {
        setResizable(false);
        setBackground(Color.lightGray);
        userField.setEditable(false);
        pinField.setEditable(false);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel P1 = new Panel();
        P1.add("East", userField);
        add(P1);
        P1.add("West", pinField);
        add(P1);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - getSize().width) / 5, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 5);
    }

    public void readFile() {
        userField.setText("");
        pinField.setText("");
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                //System.out.println(str[0] + "," + str[1]);
                //String word = (str[0] + " // " + str[1] + '\n');
                userField.append(str[0] + '\n');
                pinField.append(str[1] + '\n');
                userField.append("----------------------- \n");
                pinField.append("----------------------- \n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean handleEvent(Event i){
        if(i.id==Event.WINDOW_DESTROY){
            dispose();
            System.exit(0);
            return true;
        }
        return super.handleEvent(i);
    }

    public void registerUser(String name, String pin){
        try (FileWriter writer = new FileWriter(csvFilePath, true)) {

            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(',');
            sb.append(pin);
            sb.append('\n');

            writer.write(String.valueOf(sb));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserRegistered(String name){
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                if(str[0].equals(name)){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPinRegistered(String pin){
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                if(str[1].equals(pin)){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPin(String name){
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                if(str[0].equals(name)){
                    return str[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUser(String pin){
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                if(str[1].equals(pin)){
                    return str[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
