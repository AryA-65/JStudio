import org.JStudio.Login.EncryptionAndDecryption;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionDecryptionTest {
    
    public EncryptionDecryptionTest() {
    }

    /**
     * Test of encryption method, of class EncryptionAndDecryption.
     */
    @Test
    public void testEncryption() {
        EncryptionAndDecryption encryption1 = new EncryptionAndDecryption("HEART", 7, 20);
        EncryptionAndDecryption encryption2 = new EncryptionAndDecryption("MATH", 21, 4);
        EncryptionAndDecryption encryption3 = new EncryptionAndDecryption("MATH", 19, 12);
        EncryptionAndDecryption encryption4 = new EncryptionAndDecryption("MATH", 19, 3);
        EncryptionAndDecryption encryption5 = new EncryptionAndDecryption("PIE", 7, 20);
        EncryptionAndDecryption encryption6 = new EncryptionAndDecryption("HOTEL", 5, 8);
        EncryptionAndDecryption encryption7 = new EncryptionAndDecryption("ALPHA", 5, 8);
        EncryptionAndDecryption encryption8 = new EncryptionAndDecryption("MIKE", 5, 8);
        EncryptionAndDecryption encryption9 = new EncryptionAndDecryption("TANGO", 5, 8);
        EncryptionAndDecryption encryption10 = new EncryptionAndDecryption("OPERATION", 9, 11);
        
        String expected1 = "RWUJX";
        String expected2 = "WENV";
        String expected3 = "GMJP";
        String expected4 = "XDAG";
        String expected5 = "VYW";
        String expected6 = "RAZCL";
        String expected7 = "ILFRI";
        String expected8 = "QWGC";
        String expected9 = "ZIVMA";
        String expected10 = "HQVILAFHY";
        
        String result1 = encryption1.encryption();
        String result2 = encryption2.encryption();
        String result3 = encryption3.encryption();
        String result4 = encryption4.encryption();
        String result5 = encryption5.encryption();
        String result6 = encryption6.encryption();
        String result7 = encryption7.encryption();
        String result8 = encryption8.encryption();
        String result9 = encryption9.encryption();
        String result10 = encryption10.encryption();
        
        assertEquals(expected1, result1);
        assertEquals(expected2, result2);
        assertEquals(expected3, result3);
        assertEquals(expected4, result4);
        assertEquals(expected5, result5);
        assertEquals(expected6, result6);
        assertEquals(expected7, result7);
        assertEquals(expected8, result8);
        assertEquals(expected9, result9);
        assertEquals(expected10, result10);
    }

    /**
     * Test of decryption method, of class EncryptionAndDecryption.
     */
    @Test
    public void testDecryption() {
        EncryptionAndDecryption decryption1 = new EncryptionAndDecryption("RWUJX", 7, 20);
        EncryptionAndDecryption decryption2 = new EncryptionAndDecryption("WENV", 21, 4);
        EncryptionAndDecryption encryption3 = new EncryptionAndDecryption("GMJP", 19, 12);
        EncryptionAndDecryption decryption4 = new EncryptionAndDecryption("XDAG", 19, 3);
        EncryptionAndDecryption decryption5 = new EncryptionAndDecryption("VYW", 7, 20);
        EncryptionAndDecryption encryption6 = new EncryptionAndDecryption("RAZCL", 5, 8);
        EncryptionAndDecryption decryption7 = new EncryptionAndDecryption("ILFRI", 5, 8);
        EncryptionAndDecryption decryption8 = new EncryptionAndDecryption("QWGC", 5, 8);
        EncryptionAndDecryption decryption9 = new EncryptionAndDecryption("ZIVMA", 5, 8);
        EncryptionAndDecryption decryption10 = new EncryptionAndDecryption("HQVILAFHY", 9, 11);
        
        String expected1 = "HEART";
        String expected2 = "MATH";
        String expected3 = "MATH";
        String expected4 = "MATH";
        String expected5 = "PIE";
        String expected6 = "HOTEL";
        String expected7 = "ALPHA";
        String expected8 = "MIKE";
        String expected9 = "TANGO";
        String expected10 = "OPERATION";
        
        String result1 = decryption1.decryption();
        String result2 = decryption2.decryption();
        String result3 = encryption3.decryption();
        String result4 = decryption4.decryption();
        String result5 = decryption5.decryption();
        String result6 = encryption6.decryption();
        String result7 = decryption7.decryption();
        String result8 = decryption8.decryption();
        String result9 = decryption9.decryption();
        String result10 = decryption10.decryption();
        
        assertEquals(expected1, result1);
        assertEquals(expected2, result2);
        assertEquals(expected3, result3);
        assertEquals(expected4, result4);
        assertEquals(expected5, result5);
        assertEquals(expected6, result6);
        assertEquals(expected7, result7);
        assertEquals(expected8, result8);
        assertEquals(expected9, result9);
        assertEquals(expected10, result10);
    }
    
}
