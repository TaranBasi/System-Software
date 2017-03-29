/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Daniel
 */
public class MultipleSocketServer implements Runnable{

    private Socket csocket;

    
    MultipleSocketServer(Socket s) {
        this.csocket = s;
    }
    
    public static void main(String[] args) {
        int port = 19999;
            
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

    
    
    @Override
    public void run() {
        try {
            
            
                System.out.println("trying");
 
                
                DataInputStream inFromClient = new DataInputStream(csocket.getInputStream());
                
                Thread.sleep(20000);
               
                String str = inFromClient.readUTF();

                               
                System.out.println("Recieved packet: " + str);
                
                inFromClient.close();
                csocket.close();
                
            
        } 
        catch (Exception e) {}
    }
    
    

}
