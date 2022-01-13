import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        NameService nameService = new NameService("Name Service");
        //nameService.GUI();
        //nameService.main(args);
        UserRegister userRegister = new UserRegister("Register", nameService);
        //userRegister.GUI();
        Chat chat1 = new Chat("Chat1", nameService);
        Chat chat2 = new Chat("Chat2", nameService);
        Chat chat3 = new Chat("Chat3", nameService);
    }
}
