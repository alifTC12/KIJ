package KIJ;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
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
    private String textToDecrypt;
    private RC4 rc4;
    public String yourY;
    private int q;
    private int a;
    DiffieHellman dh = new DiffieHellman(q,a);
    OutputStream os;
    InputStream is;
    
    //constructor
    Client(String server, int PORT, String username, ListOnlineUser cgui )
    {
       this.server = server;
       this.PORT = PORT;
       this.username = username;
       this.cGui = cgui;
       rc4 = new RC4("testing");
      // this.cFieldGui = lgui;
    }
    
    public void NewChat(String sendTo, String username)
    {
         ChatField s = new ChatField(this, sendTo.replaceAll("\n", ""), this.dh);
         this.cFieldGui = s;
         s.username = username;
         s.setVisible(true);
         s.yourY = this.yourY;
    }
    
    //start client
    public boolean start()
    {
        //try connect ke server
        try
        {
            host = InetAddress.getByName(server);
            socket = new Socket(host,PORT);
            os = new BufferedOutputStream(socket.getOutputStream());
            is = new BufferedInputStream(socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
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
       
       /*
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
        */
        
        // creates the Thread to listen from the server 
	new ListenFromServer().start();   
        
        try 
        {
        //    String msg3 = reader.readLine();
          //  System.out.println(msg3);
             String connect = "USER " + username + "\r\n"; 
            // String getSeed = "GETSEED"; 
             os.write(connect.getBytes());
             //os.write(getSeed.getBytes());
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
     
     String MakeKey()
     {
         
        // System.out.println(dh.getY());
         return dh.getY().toString();
     }
     
     /*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
     void disconnect() 
        {
		try 
                { 
                    if(is != null) is.close();
		}
                
		catch(Exception e) {} // not much else I can do
                
		try
                {
                    if(os != null) os.close();
		}
                
		catch(Exception e) {} // not much else I can do
                
                try
                {
                    if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do	
	}
        
    class ListenFromServer extends Thread 
    {
		public void run() 
                {
//                    try 
//                    {
////                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    } 
//                    catch (IOException ex) 
//                    {
//                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    
			while(true) 
                        {
                            try 
                                {
                                        //sendMessage("GETSEED");
					String msg = reader.readLine();
                                        System.out.println(msg);
                                        String[] protokol = msg.split(" ");
                                        //for(int a=1; )
                                       
                                        if(protokol[0].equals("ONUSER"))
                                        {
                                            //System.out.println(protokol);
                                            String[] usr_on;
                                            //usr_on = protokol[1].split(" ");
                                            cGui.hapus();
                                            for(int z=1; z<protokol.length; z++)
                                            {
                                                //if(protokol[a].equalsIgnoreCase(username)) {} 
                                                //else 
                                                //{
                                                
                                                 // System.out.println(protokol.length);
                                                  if(z==protokol.length-1) dh.setA(protokol[z]);
                                                 // else if(z==protokol.length-2) dh.q = new BigInteger(Integer.parseInt(protokol[z]));
                                                   else if(z==protokol.length-2) dh.setQ(protokol[z]);
                                                  else cGui.updateOnlineList(protokol[z] + "\n");
                                                  
                                                 // System.out.println(q);
                                                 // System.out.println(a);                                                
                                                  
                                                //}
                                                //System.out.println(usr_on[a]);
                                                //cFieldGui.append("haha");
                                            }
                                            //MakeKey();
                                        }
                                        
                                        else if(protokol[0].equals("TALKEDTO")) 
                                        {
                                            textToDecrypt="";
                                            for(int a=2; a<protokol.length; a++) textToDecrypt += protokol[a];
                                            
                                            //System.out.println(rc4.decrypt(textToDecrypt.toCharArray()));
                                            //System.out.println(textToDecrypt);
                                            //textToDecrypt="";
                                             rc4 = new RC4(dh.GetKey(yourY).toString());
                                             System.out.println(("key decrypt:"+yourY));
                                            cFieldGui.append(new String(rc4.decrypt(textToDecrypt.toCharArray())));
                                            cFieldGui.append("\n");
                                            //textToDecrypt="";
                                        }
                                        else if (protokol[0].equals("KEY"))
                                        {
                                            if(!protokol[1].equals("0"))
                                            {yourY = protokol[1];}
                                            
                                            
                                        }
                                        
				}
                                catch(NullPointerException r)
                                {
                                    
                                } catch (IOException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                    
			}
		}
    }
}