package msngr.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import msngrextras.Message;

/**
 *
 * @author Akshay Mahajan
 */
public class ChatThread extends Thread{

    protected final Socket client;
    private final String username;
    ObjectOutputStream out;
    ObjectInputStream in;
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " started.");
        //get input and output objects from connected user's socket.
        try{
            String receiver, msg;
            Object input;
            //wait for user inut
            System.out.println("Entering try in ChatThread");
            while((input = in.readObject())!=null){
                System.out.println("ChatThread user started listening.");
                System.out.println(Thread.currentThread().getName() + " received input.");
                
                //get the receiver and message from the input
                
                if (input instanceof Message) {
                    Message m = (Message)input;
                    receiver = m.getReceiver();
                    msg = m.getMessage();
                    //get the thread of the receiver
                    ChatThread recThread = getReceiver(receiver);
                    if(recThread != null){
                        //send message to receiver thread
                        boolean sent = recThread.sendMessage(m);

                        if(sent!=true)
                            out.writeObject(new Message("Server", "", "There was an error sending your message."));
                    }
                    else{
                        System.out.println("recThread is null!");
                        ChatThread senderThread = getReceiver(username);
                        senderThread.sendMessage(new Message("Server", "", "The user "+receiver+" is offline."));
                    }
                    System.out.println(Thread.currentThread().getName() + " echoed back.");
                }
            }
            System.out.println("ChatThread user stopped listening.!!!");
            System.out.println("Stopping user "+Thread.currentThread().getName());
            Thread.currentThread().interrupt();
            removeReceiver(Thread.currentThread().getName());
        }catch(SocketException e){
            System.out.println("Connection terminated by user.");
        } catch (EOFException e) {
            removeReceiver(username);
            MsngrServer.Server.log(username + " logged out.");
        } catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ChatThread(Socket client, String username, ObjectInputStream in, ObjectOutputStream out){
        super(username);                        //create new thread for every user
        this.client = client;                   //get user socket object
        this.username = username;               //get username
        this.in = in;                           //get input stream
        this.out = out;                         //get output stream
    }
    
    private boolean sendMessage(Message msg){
        try{
            this.out.writeObject(msg);
            return true;
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    //Search for receiver thread in the servers list of threads and return if found
    //else return null
    private ChatThread getReceiver(String receiverUsername){    
        for(ChatThread t : MsngrServer.connections){
            if(t.getName().equals(receiverUsername))
                return t;
        }
        return null;
    }
    
    private void removeReceiver(String receiverUsername){
        ListIterator<ChatThread> it = MsngrServer.connections.listIterator();
        while(it.hasNext()){
            ChatThread t = it.next();
            if(t.getName().equals(receiverUsername))
                it.remove();
        }
    }
}