package org.JStudio.Login;

/**
 * 
 * @author Theodore. Ahmet
 *
 * Encryption encryption = new Encryption("Heart", 7, 20);
 * Encryption decryption = new Encryption("Rwujx", 7,20);
 * System.out.println(decryption.decryption());
 */
public class EncryptionAndDecryption {
    private String toEncrypt_toDecrypt;
    private int key1;
    private int key2;
    private int inverse = 1;
    private int[] letterIndex;
    private int[] affineCalculation;
    private int[] finalOutput;


    public EncryptionAndDecryption(String toEncrypt_toDecrypt, int key1, int key2) {
        this.toEncrypt_toDecrypt = toEncrypt_toDecrypt;
        this.key1 = key1;
        this.key2 = key2;

    }

    private void multiplicativeInverse() {
        while ((key1*inverse)%26 != 1) {
            inverse++;
        }
    }

    private void toUpper() {
        letterIndex = new int[toEncrypt_toDecrypt.length()];
        toEncrypt_toDecrypt = toEncrypt_toDecrypt.toUpperCase();
    }

    private void characterIndex(){
        char input;
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            input = toEncrypt_toDecrypt.charAt(i);
            letterIndex[i] = input - 65;
        }
    }

    private void affineCalc() {
        affineCalculation = new int[toEncrypt_toDecrypt.length()];
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            affineCalculation[i] = ((key1*letterIndex[i])+key2) % 26;
        }
    }

    private void asciiConverter() {
        finalOutput = new int[toEncrypt_toDecrypt.length()];
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            finalOutput[i] = affineCalculation[i] + 65;
        }
    }

    private String encryptedText() {
        String encryptedText = "";
        for (int i = 0; i < finalOutput.length; i++) {
            encryptedText += String.valueOf((char) finalOutput[i]);
        }
        return encryptedText;
    }

    public void reverseAffineCalc(){
        affineCalculation = new int[toEncrypt_toDecrypt.length()];
        int temp;
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            temp = (inverse*(letterIndex[i]-key2));
            if (temp > 0) {
                affineCalculation[i] = (temp % 26);
            } else {
                while(temp < 0) {
                    temp += 26;
                }
                affineCalculation[i] = temp;
            }
        }
    }

    public String encryption() {
        multiplicativeInverse();
        toUpper();
        characterIndex();
        affineCalc();
        asciiConverter();
        return encryptedText();
    }

    public String decryption() {
        multiplicativeInverse();
        toUpper();
        characterIndex();
        reverseAffineCalc();
        asciiConverter();
        return encryptedText();
    }

}

