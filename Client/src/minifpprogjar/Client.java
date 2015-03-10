package multithreadserver;
import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Client 
{
    private InetAddress host;
    private int PORT;
    private String server,username;   
    private ClientForm cGui;
    private DataInputStream sInput;		
    private DataOutputStream sOutput;	 
    private Socket socket;
    private BufferedReader reader;
    OutputStream os;
    InputStream is;
    
    //constructor
    Client(String server, int PORT, String username, ClientForm cgui)
    {
       this.server = server;
       this.PORT = PORT;
       this.username = username;
       this.cGui = cgui;
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
            display("Error connectiong to server:" + ec);			
	    return false;
        }
        
        
        String msg = "> Connection accepted " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        //System.out.println(msg);
        display(msg);
       
        /* Data Stream */
        new ListenFromServer().start();
        
	try
	 {
            sInput  = new DataInputStream(socket.getInputStream());
            sOutput = new DataOutputStream(socket.getOutputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
            is = new BufferedInputStream(socket.getInputStream());
	 }
        
        
		
        catch (IOException eIO) 
         {
            System.out.println("Exception Input/output Streams: " + eIO);
            display("Exception creating new Input/output Streams: " + eIO);
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
             System.out.print(connect);    
             os.write(connect.getBytes());
             os.flush(); // Send off the data
        } 
        catch (IOException eIO) 
        {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }    
        
        // success we inform the caller that it worked
       
     
	return true;
        
    }
    
       private void display(String msg) 
    
        {
		if(cGui == null)
			System.out.println(msg);      // println in console mode
		else
			cGui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
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
		display("Exception writing to server: " + e);
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
                    if(sInput != null) sInput.close();
		}
                
		catch(Exception e) {} // not much else I can do
                
		try
                {
                    if(sOutput != null) sOutput.close();
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
                    try {
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
			while(true) 
                        {
				try {
					String msg = reader.readLine();
					// if console mode print the message and add back the prompt
                                        String[] protokol = msg.split(" ");
                                        //System.out.println(protokol[1]);
                                        if(protokol[0].equals("ONUSER"))
                                        {
                                            String[] usr_on;
                                            usr_on = protokol[1].split(",");
                                            cGui.hapus();
                                            for(int a=0; a<usr_on.length; a++)
                                            {
                                                cGui.updateOnlineList(usr_on[a] + "\n");
                                                System.out.println(usr_on[a]);
                                            }
                                        }
                                        else if(protokol[0].equals("TALKEDTO")) 
                                        {
                                            //String[] pesan =  protokol[1].split(":"); 
                                            cGui.append(protokol[1] + "\n");
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