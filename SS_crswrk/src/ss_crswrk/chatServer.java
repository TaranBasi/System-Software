/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Daniel
 */
public class chatServer {
    
    static ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();
    static ArrayList<Socket> clientsSockets = new ArrayList<Socket>();
    
    
    chatServer() {
        
    } 
    
    
    public static void main(String args[]) {
        
        int port = 19998;
        
        try {
            ServerSocket ss = new ServerSocket(port);
            
            while (true) {
                
                Socket socket = ss.accept();
                
                clientsSockets.add(socket);
                
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                clients.add(os);
                
                
                                                
                ServerHandler sHandler = new ServerHandler(socket, clientsSockets);
                
                Thread t = new Thread(sHandler);
                t.start();
    
                
            }
            
        }  catch (Exception e) {}
        
    }
}
