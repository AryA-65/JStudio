package org.JStudio.Login;

/**
 * Affine encryption and decryption calculator class
 * @author Theodore. Ahmet;
 */
public class EncryptionAndDecryption {
    private String toEncrypt_toDecrypt;
    private int key1;
    private int key2;
    private int inverse = 1;
    private int[] letterIndex;
    private int[] affineCalculation;
    private int[] finalOutput;

    // Constructor that creates an object with a word to decrypt and its keys
    public EncryptionAndDecryption(String toEncrypt_toDecrypt, int key1, int key2) {
        this.toEncrypt_toDecrypt = toEncrypt_toDecrypt;
        this.key1 = key1;
        this.key2 = key2;

    }

    /**
     * Calculates the multiplicative inverse of a number
     */
    private void multiplicativeInverse() {
        while ((key1*inverse)%26 != 1) {
            inverse++;
        }
    }

    /**
     * Converts all characters of a string to uppercase
     */
    private void toUpper() {
        letterIndex = new int[toEncrypt_toDecrypt.length()];
        toEncrypt_toDecrypt = toEncrypt_toDecrypt.toUpperCase();
    }

    /**
     * Calculates the index of the character used for encrypting/decrypting
     * based on ASCII codes
     */
    private void characterIndex(){
        char input;
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            input = toEncrypt_toDecrypt.charAt(i);
            letterIndex[i] = input - 65;
        }
    }

    /**
     * Calculates the new letter index of the encrypted text
     */
    private void affineCalc() {
        affineCalculation = new int[toEncrypt_toDecrypt.length()];
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            affineCalculation[i] = ((key1*letterIndex[i])+key2) % 26;
        }
    }

    /**
     * Converts the letter index back to ASCII code
     */
    private void asciiConverter() {
        finalOutput = new int[toEncrypt_toDecrypt.length()];
        for (int i = 0; i < toEncrypt_toDecrypt.length(); i++) {
            finalOutput[i] = affineCalculation[i] + 65;
        }
    }

    /**
     * Adds all encrypted/decrypted characters into a string
     * @return the encrypted/decrypted text
     */
    private String encryptedText() {
        String encryptedText = "";
        for (int i = 0; i < finalOutput.length; i++) {
            encryptedText += String.valueOf((char) finalOutput[i]);
        }
        return encryptedText;
    }

    /**
     * Calculates the new letter index of the decrypted text
     */
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

    /**
     * Full affine encryption calculations
     * @return the encrypted text
     */
    public String encryption() {
        multiplicativeInverse();
        toUpper();
        characterIndex();
        affineCalc();
        asciiConverter();
        return encryptedText();
    }

    /**
     * Full affine decryption calculations
     * @return the decrypted text
     */
    public String decryption() {
        multiplicativeInverse();
        toUpper();
        characterIndex();
        reverseAffineCalc();
        asciiConverter();
        return encryptedText();
    }

}
