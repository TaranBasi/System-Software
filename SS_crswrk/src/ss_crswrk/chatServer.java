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
    
    private static ArrayList<ServerHandler.user> userAL = new ArrayList<ServerHandler.user>();
    
    chatServer() {
        
    }
            

    
        
     
    
    
    public static void main(String args[]) {
        
        int port = 19998;
        
        try {
            ServerSocket ss = new ServerSocket(port);
            
            while (true) {
                
                Socket socket = ss.accept();
                
                
                                                
                ServerHandler sHandler = new ServerHandler(socket, userAL);
                
                Thread t = new Thread(sHandler);
                t.start();
    
                
            }
            
        }  catch (Exception e) {}
        
    }
    
    
}
