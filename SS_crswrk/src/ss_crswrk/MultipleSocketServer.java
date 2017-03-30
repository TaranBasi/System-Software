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
    
    public static void main(String args[]) {
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
               
                String str = inFromClient.readUTF();

                
                //Recieved packet including process to do - string[0].
                System.out.println("Recieved packet: " + str);
                
                //split string to get the process to do - login, register, etc.
                String[] process = str.split("/");
                
                //if statements to detemrine what to do with the packet
                System.out.println(process[0]);
                System.out.println(process[1]);
                System.out.println(process[2]);
                
                if (process[0].equals("login"))
                {
                    //run login function
                    System.out.println("Running login function");
                    
                }
                else if (process[0].equals("register"))
                {
                    //run register function
                    System.out.println("Running register function");
                }
                
                
                inFromClient.close();
                csocket.close();
                
        } 
        catch (Exception e) {}
    }
    
    

}
