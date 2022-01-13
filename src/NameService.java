
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class NameService extends Frame implements Runnable {
    TextArea ecran=new TextArea(25,30);

    public static void main(String[] args) {


        //Trata da criação do ficheiro CSV
        File file = new File("./src/names.csv");
        if(file.exists() && !file.isDirectory()){
            System.out.println("Ficheiro de nomes já existe.");
        } else {
            System.out.println("Criando um novo ficheiro de nomes.");
            try (FileWriter writer = new FileWriter("./src/names.csv")) {

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
        resize(300,500);
        GUI();
        show();
    }

    public void GUI() {
        setResizable(false);
        setBackground(Color.lightGray);
        ecran.setEditable(false);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel P1 = new Panel();
        P1.add("North",ecran);
        add(P1);
    }

    public void readFile() {
        String currentLine;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("./src/names.csv"));
            while ((currentLine = br.readLine()) != null) {
                String[] str = currentLine.split(",");
                //System.out.println(str[0] + "," + str[1]);
                String word = (str[0] + " , " + str[1]);
                ecran.append(word);
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

    @Override
    public void run() {
        readFile();
    }
}
