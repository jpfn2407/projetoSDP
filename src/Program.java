import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        NameService nameService = new NameService("Name Service");
        nameService.GUI();
        UserRegister userRegister = new UserRegister("Register", nameService);
        userRegister.GUI();
    }
}
