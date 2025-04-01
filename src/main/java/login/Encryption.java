package login;

import java.util.Arrays;

/**
 * 
 * @author Theodore. Ahmet
 */
public class Encryption {

    public static void main(String[] args) {
        String code = "HEART";
        int key1 = 7; // Max 27
        int key2 = 20;
        int inverse = 1;
        
        // Mutliplicative inverse algorithm
        while ((key1*inverse)%26 != 1) {
            inverse++;
        }
        System.out.println("Inverse: "+inverse);
        
        // Uppercase
        int[] letterIndex = new int[code.length()];
        code = code.toUpperCase();
        
        // Save index value of character
        char input;
        for (int i = 0; i < code.length(); i++) {
            input = code.charAt(i);
            letterIndex[i] = input - 65;
        }
        System.out.println(Arrays.toString(letterIndex));
        
        // Next step
        int[] affineEquationValue = new int[code.length()];
        for (int i = 0; i < code.length(); i++) {
            affineEquationValue[i] = ((key1*letterIndex[i])+key2) % 26;
        }
        System.out.println(Arrays.toString(affineEquationValue));
        
        // Convert back to ASCII
        int[] encryptLetterIndex = new int[code.length()];
        for (int i = 0; i < code.length(); i++) {
            encryptLetterIndex[i] = affineEquationValue[i] + 65;
            System.out.println(encryptLetterIndex[i]);
        }
        
        // Make the string encryption
        String encryptedText = "";
        for (int i = 0; i < encryptLetterIndex.length; i++) {
            encryptedText += String.valueOf((char) encryptLetterIndex[i]);
        }
        System.out.println(encryptedText);
    }
}
