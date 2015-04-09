/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package KIJ;

/**
 *
 * @author alif.sip
 */
public class InvalidKeyException extends Exception 
{
 
    private static final long serialVersionUID = 1L;
 
    public InvalidKeyException(String message) {
        super(message);
    }
}
