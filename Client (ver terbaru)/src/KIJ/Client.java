package KIJ;
import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client 
{
    private ListOnlineUser test;
    private InetAddress host;
    private int PORT;
    private String server,username;   
    private ListOnlineUser cGui;
    private ChatField cFieldGui;
    //private DataInputStream sInput;		
    //private DataOutputStream sOutput;	 
    private Socket socket;
    private BufferedReader reader;
    OutputStream os;
    InputStream is;
    
    //constructor
    Client(String server, int PORT, String username, ListOnlineUser cgui )
    {
       this.server = server;
       this.PORT = PORT;
       this.username = username;
       this.cGui = cgui;
      // this.cFieldGui = lgui;
    }
    
    public void NewChat(String sendTo, String username)
    {
         ChatField s = new ChatField(this, sendTo.replaceAll("\n", ""));
         this.cFieldGui = s;
         //s.sendTo = 
         System.out.print(s.sendTo);
         s.username = username;
         s.setVisible(true);
    }
    
    //start client
    public boolean start()
    {
        //try connect ke server
        try
        {
            host = InetAddress.getByName(server);
            socket = new Socket(host,PORT);
        }
                
        catch(Exception ec) 
        {
            System.out.println("Error connectiong to server:" + ec);
            displayClientForm("Error connectiong to server:" + ec);			
	    return false;
        }
        
        
        String msg = "> Connection accepted " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        //System.out.println(msg);
        displayClientForm(msg);
       
        /* Data Stream */
        new ListenFromServer().start();
        
	try
	 {
            //sInput  = new DataInputStream(socket.getInputStream());
            //sOutput = new DataOutputStream(socket.getOutputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
            is = new BufferedInputStream(socket.getInputStream());
	 }
        	
        catch (IOException eIO) 
         {
            System.out.println("Exception Input/output Streams: " + eIO);
            displayClientForm("Exception creating new Input/output Streams: " + eIO);
            return false;
         }
        
        
        // creates the Thread to listen from the server 
	new ListenFromServer().start();       
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try 
        {
            //sOutput.writeObject(username);
            
             String connect = "USER " + username + "\r\n";
             //System.out.print(connect);    
             os.write(connect.getBytes());
             os.flush(); // Send off the data
        } 
        catch (IOException eIO) 
        {
            displayClientForm("Exception doing login : " + eIO);
            disconnect();
            return false;
        }    
        
        // success we inform the caller that it worked
       
     
	return true;
        
    }
    
       //private void display(String msg) 
    
        //{
	//	if(cGui == null) System.out.println(msg);      // println in console mode
         ///       else cGui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	//}
       
       private void displayClientForm(String msg)
       {
           if(cFieldGui == null) System.out.println(msg);      // println in console mode
           else cFieldGui.append(msg + "\n");
       }
    
    /*
     * To send a message to the server
     */
     void sendMessage(String msg) 
     {
        try {
                os.write(msg.getBytes());
                os.flush(); // Send off the data
            }
	catch(IOException e) 
            {
		displayClientForm("Exception writing to server: " + e);
            }
      }
     
     /*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() 
        {
		try 
                { 
                   // if(sInput != null) sInput.close();
		}
                
		catch(Exception e) {} // not much else I can do
                
		try
                {
                   // if(sOutput != null) sOutput.close();
		}
                
		catch(Exception e) {} // not much else I can do
                
                try
                {
                    if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		//if(cGui != null)
		//	cGui.connectionFailed();
			
	}
        
    class ListenFromServer extends Thread 
    {
		public void run() 
                {
                    try 
                    {
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
			while(true) 
                        {
                            try 
                                {
					String msg = reader.readLine();
                                        System.out.println(msg);
                                        String[] protokol = msg.split(" ");
                                       
                                        if(protokol[0].equals("ONUSER"))
                                        {
                                        
                                            String[] usr_on;
                                            usr_on = protokol[1].split(",");
                                            cGui.hapus();
                                            for(int a=0; a<usr_on.length; a++)
                                            {
                                                cGui.updateOnlineList(usr_on[a] + "\n");
                                                //System.out.println(usr_on[a]);
                                                //cFieldGui.append("haha");
                                            }
                                        }
                                        else if(protokol[0].equals("TALKEDTO")) 
                                        {
                                            System.out.println("yse");
                                            //cFieldGui.append("haha");
                                            //String[] pesan =  protokol[1].split(":"); 
                                            for(int a=1; a<protokol.length; a++)
                                            cFieldGui.append(protokol[a] + " ");
                                            cFieldGui.append("\n");
                                        }
                                        
				}
                                /*
				catch(IOException e) 
                                {
					System.out.println("Server has close the connection: " + e);
					break;
				}*/
                                catch(NullPointerException r)
                                {
                                    
                                } catch (IOException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                ;
                            }
                                    
			}
		}
    }
}