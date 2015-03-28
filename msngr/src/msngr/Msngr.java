package msngr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import msngrextras.Message;
import msngrextras.User;

/**
 *
 * @author Akshay Mahajan
 */
public class Msngr implements Runnable{
    
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    static String HOSTNAME = "127.0.0.1";
    static final int PORT = 4444;
    static Socket socket;
    
    public int Login (String username, String password) {
        int status = 0;
        try{
            socket = new Socket(HOSTNAME, PORT);
            System.out.println("Socket created successfully.");
            out = new ObjectOutputStream(socket.getOutputStream());   
            System.out.println("Output Stream created successfully.");
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Input Stream created successfully.");
            Object result;
            User user = new User(username, password);
            out.writeObject(user);
            if((result = in.readObject()) != null)
            {
                if (result instanceof User) {
                    User u = (User)result;
                    if("success".equals(u.getStatus())){
                        status = 1;                                                //Login successful
                    }
                    else
                        status = -1;                                              //Login failed. Please check your username and/or password.
                }
                else {
                    System.out.println("Did not receive a User object.");
                }
            }
        }catch(SocketException e){
            System.out.println("You were disconnected by the server.");
        }catch(IOException e){
            System.out.println("Error creating I/O streams.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    
    public void run() {
        try{
            System.out.println("Msngr started");
            if (out == null) {
                System.out.println("out null");
            }
            Object received;
            System.out.println("Created socket and i/o streams");
            while((received = in.readObject()) != null){
                System.out.println("started listening.");
                if (received instanceof Message) {
                    ChatWindow.addMessage((Message)received);
                }
            }
            System.out.println("Disconnected from the server.");
            ChatWindow.addMessage(new Message ("", "", "Disconnected from the server."));
        }catch(SocketException e){
            System.out.println("Connection refused by server.");
            ChatWindow.addMessage(new Message("Server", "", "Connection closed by server."));
        }catch(IOException e){
            System.out.println("IO Error.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Msngr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void sendMessage(Message message){
        try {
            out.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Msngr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
