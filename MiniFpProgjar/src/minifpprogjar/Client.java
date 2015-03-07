package multithreadserver;
import java.io.*;
import java.net.*;
import java.util.*;
public class Client {
    
    private InetAddress host;
    private int PORT ;
    //private int tport = 1212;
    private String server,username;   
    private ClientForm cGui;
    private ObjectInputStream sInput;		
    private ObjectOutputStream sOutput;	 
    private Socket socket;
    
    //constructor
    Client(String server, int PORT, String username, ClientForm cfr)
    {
       //System.out.println("this is ip " + server + "\n");
       //System.out.println("this is port " + PORT);
       this.server = server;
       this.PORT = PORT;
       this.cGui = cfr;
       this.username = username;
    }
    
    /*public void main (String[] args)
    {
      
        try
        {
            //host = InetAddress.getLocalHost();
            host = InetAddress.getByName(iphost);
        }
        catch(UnknownHostException uhEx)
                {
                    System.out.println("\nHost ID not Found!\n");
                    System.exit(1);
                }
        sendMessages();
    }*/
    //start client
    public boolean start()
    {
        
        //Socket socket = null;
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
         //System.out.println("this is hostasli " + host + "\n" );
        //System.out.println("this is tport " + PORT + "\n" );
        
        String msg = "> Connection accepted " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        System.out.println(msg);
        display(msg);
        
        /* Data Stream */
	try
	 {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
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
            sOutput.writeObject(username);
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
    
    private void display(String msg) {
		if(cGui == null)
			System.out.println(msg);      // println in console mode
		else
			cGui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
    
    /*
     * To send a message to the server
     */
     void sendMessage(chatHandler msg) 
     {
        try {
		sOutput.writeObject(msg);
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
        
    class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					// if console mode print the message and add back the prompt
					if(cGui == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cGui.append(msg);
					}
				}
				catch(IOException e) {
					System.out.println("Server has close the connection: " + e);
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
    }}
       
         
    
    

