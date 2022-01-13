import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.awt.*;
import java.util.*;
public class encodeSTR {
    // Password must be at least 8 characters
    private static final String password = "antonio manuel caldeira";
    public static void main(String args[]) throws Exception {
// Create Key
        byte key[] = password.getBytes();
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
// Create Cipher
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
// Clear message
        byte[] clearSTR1="Esta mensagem vai ser cifrada com o algoritmo DES.".getBytes();
        String cSTR1=new String(clearSTR1);
// Encode message
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encodedSTR=desCipher.doFinal(clearSTR1);
        String eSTR=new String(encodedSTR);
// Decode message
        desCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearSTR2=desCipher.doFinal(encodedSTR);
        String cSTR2=new String(clearSTR2);
// Output
        System.out.println("\nMensagem na origem (em claro):\n"+cSTR1);
        System.out.println("\nMensagem na rede (cifrada):\n"+eSTR);
        System.out.println("\nMensagem no destino (em claro):\n"+cSTR2);
    }
}