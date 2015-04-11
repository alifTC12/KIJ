/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KIJ;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Ripas Filqadar
 */
public class hash {
    public static void main(String [] args)
    {
        try {
            
            String text="lalala";
            String hash_hasil="";
            hash_hasil = getHash(text);
            System.out.println(hash_hasil);
             
            hash_hasil = getHash("lalala");
            System.out.println(hash_hasil);
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
            
    }
    
    public static String getHash(String  text) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
    	return sb.toString();
        
    }
}
