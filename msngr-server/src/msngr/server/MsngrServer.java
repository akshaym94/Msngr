package msngr.server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;
import msngrextras.User;

/**
 *
 * @author Akshay Mahajan
 */

public class MsngrServer{

    private static final int portNumber = 4444;
    private static boolean listening = true;
    protected static ArrayList<ChatThread> connections = new ArrayList<>();
    private final JTextArea console;
    static MsngrServer Server;
    
    public MsngrServer() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
           // handle exception
        }

        JFrame frame = new JFrame("Server");
        console = new JTextArea();
        DefaultCaret caret = (DefaultCaret)console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        console.setEditable(false);
        console.setLineWrap(true);
        JScrollPane pane = new JScrollPane(console);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(pane);
        frame.setSize(450,300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    void log(String s) {
        console.append(new Date() + " : " + s + "\n");
    }
    
    public static void main(String[] args) {
        Server = new MsngrServer();
        ServerSocket server = null;
        try {
            server = new ServerSocket(portNumber);
        } catch (IOException ex) {
            listening = false;
            Server.log("Error starting server!");
        }
        Server.log("Server Started.");
        while(listening){
            if (server == null) {
                break;
            }
            try {
                Socket client = server.accept();
                System.out.println("Accepted connection");
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                Object request = in.readObject();
                String username="", password="";
                User user;
                if (request instanceof User) {
                    user = (User)request;
                    username = user.getUsername();
                    password = user.getPassword();
                    if(Login.login(username, password))
                    {    
                        user.setStatus("success");
                        out.writeObject(user);
                        connections.add(new ChatThread(client, username, in, out));
                        connections.get(connections.size()-1).start();
                        Server.log(username + " Connected.");
                    }
                    else{
                        user.setStatus("error. login failed.");
                        out.writeObject(user);
                    }
                }
                else {
                    user = new User("","");
                    user.setStatus("error");
                    out.writeObject(user);
                }
            } catch(IOException e){
                Server.log("Error connecting to user.");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MsngrServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}