/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Daniel
 */
public class chatServer {
    
    
    chatServer() {
        
    } 
    
    
    public static void main(String args[]) {
        
    int port = 19998;

               
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("Socket connected");
            
            while (true){
                Socket socket = socket1.accept();
                 
                Runnable runnable = new MultipleSocketServer(socket);
                Thread thread = new Thread(runnable);
                thread.start();
                    
                System.out.println("thread started");
            } 
        }
        catch (Exception e) { }
        
    }
}
