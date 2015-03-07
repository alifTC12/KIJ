package multithreadserver;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;

public class MultiThreadServer
 {
        // ID untuk setiap connection
	private static int clientId;   
        // class GUI
        private ServerForm sGui;
        
        //private static ServerSocket serverSocket;
        
        //port
        private int PORT;
        //untuk loop handling
        private boolean keepgoing;
        // array untuk menyimpan list client
        // class client handler ada di bagian bawahh
        private ArrayList<ClientHandler> al;
        private ArrayList<String> DAFTAR_USER;
        // untuk menampilkan waktu login (event log)
	private SimpleDateFormat sdf;
        
        /*/constructor server dengan parameter port jika console
        public MultiThreadServer(int port) {
		this(PORT, null);
	}*/
        //constructor server dengan parameter port serta GUI
        public MultiThreadServer(int port, ServerForm sf) 
        {
                // System.out.println("ini adlaaaaaah port input " + port);
		this.sGui = sf;
		this.PORT = port;
                //System.out.println("ini adalah port input " + PORT);        
		// hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList untuk Client list
		al = new ArrayList<ClientHandler>();
                DAFTAR_USER = new ArrayList<String>();
	}
       
        public void start()
         {
             //System.out.println("ini adlah port input di start()" + PORT);
             keepgoing = true;
             
            //membuat socket server dan menunggu koneksi
            try
             {
                 //membuat socket baru dengan port yang ditentukan
                 ServerSocket serverSocket = new ServerSocket(PORT);
                 
                 //loop untuk menunggu koneksi
                 while(keepgoing)
                 {
                    display("Server membuka koneksi Clients pada port " + PORT + ".");
                    // accept connection
                    Socket socket = serverSocket.accept();  	
                    // ketika terdapat koneksi baru, dan diminta untuk berhenti (menghindari freeze pada GUI)
                    if(!keepgoing) break;
                    // buat thread baru
                    // client handler berfungsi untuk membuat thread baru
                    ClientHandler clthread = new ClientHandler(socket);  
                    
                    // save di ArrayList
                    al.add(clthread);
                    DAFTAR_USER.add(clthread.getUsername());
                    clthread.start();
                 }
                 
                 //ketika diminta untuk berhenti
                 try
                 {
                    serverSocket.close();
                    //perulangan array yang menyimpan thread client
                    for(int i = 0; i < al.size(); ++i)
                    {
                            ClientHandler ch = al.get(i);
				try {
                                    //menutup stream dan socket
                                       ch.sInput.close();
                                       ch.sOutput.close();
                                       ch.socket.close();
                                    }
                                catch(IOException ioE) 
                                {
                                    System.out.println("\nError ClientHandler! Problem in ArrayList!");
                                }
                    }
                 }
                 
                 catch(Exception e){display("Exception closing the server and clients: " + e);}
             }
            
            //membuat catch exception dari pembuatan try newsocket
            catch (IOException e)
            {
                String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
		display(msg);
            }
         }
    /*
     * Ketika menggunakan tombol stop pada GUI, server akan connect ke localhost
     */
	protected void stop() 
        {
		keepgoing = false;
		// connect sebagai Client untuk exit statement 
		// Socket socket = serverSocket.accept();
		try 
                {
			new Socket("localhost", PORT);
		}
		catch(Exception e) 
                {
			
		}
	}
        
        /*
	 * Displayevent 
	 */
	private void display(String msg) 
        {
            String time = sdf.format(new Date()) + " " + msg;
            if(sGui == null) System.out.println(time);
            else sGui.appendEvent(time + "\n");
	}
        
        /*
	 *   broadcast message ke semua Clients
	 */
	private void broadcast(String message) 
        {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String msgtime = time + " " + message + "\n";
                //System.out.println("ini msgtime" + msgtime);
		sGui.appendRoom(msgtime);     
		
                //loop dengan reverse order, karena memungkinkan bagi server untuk menemukan client yang sedang disconnect
		for(int i = al.size(); --i >= 0;) 
                {
			ClientHandler ch = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ch.writeMsg(msgtime)) 
                        {
				al.remove(i);
				display("Disconnected Client " + ch.username + " removed from list.");
			}
		}
	}
        
        /*PM message*/
        private void PM(String msg, String userPM)
        {
                String time = sdf.format(new Date());
		String msgtime = time + " " + msg + "\n";
                System.out.println("ini msgtime" + msgtime);
		for(int i = 0; i<=DAFTAR_USER.size() ; i++ )
                {
                    if(userPM.equals(DAFTAR_USER.get(i)))
                    sGui.appendRoom(msgtime);     
                }
		
                //loop dengan reverse order, karena memungkinkan bagi server untuk menemukan client yang sedang disconnect
		for(int i = al.size(); --i >= 0;) 
                {
			ClientHandler ch = al.get(i);
			// try to write to the Client if it fails remove it from the list
                        //System.out.println()
			if(!ch.writeMsg(msgtime)) 
                        {
				al.remove(i);
				display("Disconnected Client " + ch.username + " removed from list.");
			}
		}
        }
        
        // for a client who logoff using the LOGOUT message
	private void remove(int id) 
        {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientHandler ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
        }
        
        /** One instance of this thread will run for each client */
	class ClientHandler extends Thread 
        {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		chatHandler ch;
		// the date I connect
		String date;

		// Constructore
		ClientHandler(Socket socket) 
                {
			// a unique id
			id = ++clientId;
			this.socket = socket;
			/* Creating both Data Stream */
			//System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) 
                        {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) 
                        {
			}
                        
                        date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() 
                {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) 
                        {
				// read a String (which is an object)
				try 
                                {
					ch = (chatHandler) sInput.readObject();
				}
				catch (IOException e) 
                                {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = ch.getMessage();
                                String userPM = ch.getUserPM();
                                System.out.println("ini user :"+ username);
                                int tipe = ch.getType();
				// Switch on the type of message receive
                                System.out.println("ini messagenya :"+ message);
                                System.out.println("ini tipenya :"+ tipe);
                                if(tipe==1)
                                {
                                    broadcast(username + ": " + message);
                                }
                                else if(tipe==0)
                                {
                                    display(username + " disconnected with a LOGOUT message.");
                                    keepGoing = false;
                                }
                                else
                                {
                                    PM(username + ": " + message, userPM);
                                    for(int i = 0; i<=DAFTAR_USER.size() ; i++ )
                                        System.out.println(DAFTAR_USER.get(i));
                                }
				/*switch(ch.getType()) {

				case chatHandler.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case chatHandler.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				/*case ChatExample.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}*/
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		private String getUsername(){
                    return this.username;
                }
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) 
                {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}
