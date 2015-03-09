/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package multithreadserver;
import java.io.*;
/**
 *
 * @author Fathur
 */
public class chatHandler implements Serializable
{
        protected static final long serialVersionUID = 1112122200L;        
        static final int MESSAGE = 1, LOGOUT = 0, PRIVATE = 2;
        private int type;
        private String message;
        private String user;

            // constructor
            chatHandler(int type, String message) {
                    this.type = type;
                    this.message = message;
                    //System.out.println("ini messagenya :"+ message);
            }
            
            chatHandler(int type,String message, String username)
            {
                this.type = type;
                this.message = message;
                this.user = username;
            }

            // getters
           int getType() {
                    return type;
            }
           
           String getUserPM(){
               return user;
           }
           
            String getMessage() 
            {
                    return message;
            }
 }

