package login;

import java.util.Arrays;

/**
 * 
 * @author Theodore. Ahmet
 */
public class Decryption {
    public static void main(String[] args) {
        String code = "RWUJX";
        int key1 = 7; // Max 27
        int key2 = 20;
        int inverse = 1;
        
        // Mutliplicative inverse algorithm
        while ((key1*inverse)%26 != 1) {
            inverse++;
        }
        System.out.println("Inverse: "+inverse);
        
        int[] letterIndex = new int[code.length()];
        code = code.toUpperCase();
        
        // Save index value of character
        char input;
        for (int i = 0; i < code.length(); i++) {
            input = code.charAt(i);
            letterIndex[i] = input - 65;
        }
        System.out.println(Arrays.toString(letterIndex));
        
        // Reverse affine equation
        int[] reverseAffineEquationValue = new int[code.length()];
        int temp;
        for (int i = 0; i < code.length(); i++) {
            temp = (inverse*(letterIndex[i]-key2));
            if (temp > 0) {
                reverseAffineEquationValue[i] = (temp % 26);
            } else {
                while(temp < 0) {
                    temp += 26;
                }
                reverseAffineEquationValue[i] = temp;
            }
        }
        
        System.out.println(Arrays.toString(reverseAffineEquationValue));
        
        // Convert back to ASCII
        int[] textLetterIndex = new int[code.length()];
        for (int i = 0; i < code.length(); i++) {
            textLetterIndex[i] = reverseAffineEquationValue[i] + 65;
        }
        
        // Make the string encryption
        String encryptedText = "";
        for (int i = 0; i < textLetterIndex.length; i++) {
            encryptedText += String.valueOf((char) textLetterIndex[i]);
        }
        System.out.println(encryptedText);
    }
}
