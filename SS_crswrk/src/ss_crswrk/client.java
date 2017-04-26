/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.net.*;
import java.io.*;

/**
 *
 * @author Daniel
 */
public class client {
    
    static private String packetStr;
    static private String message;
    public chatClient cc;
    
    
    client (String str) {
        this.packetStr = str;
        
    }
    
    public void main() throws IOException {
        String host = "localhost";
        
        int port = 19999;
        
        try {           
            
            if (packetStr.equals("chat")) {
                
                System.out.println("Received chat");

                cc = new chatClient();
                  
                Thread t = new Thread(cc);
                t.start();

                
            } else {
            
                    
                Socket server = new Socket(host, port);

                DataOutputStream outToServer = new DataOutputStream(server.getOutputStream());

                outToServer.writeUTF(packetStr);


                //Recieve message from server
                DataInputStream inFromServer = new DataInputStream(server.getInputStream());

                message = inFromServer.readUTF();

                server.close();
            
            }
        }
        catch (IOException f) {}
        
    }
    
    //Client functions
    public static String getMessage() {
        return message;
    }
    
    
    public chatClient getChatClient() {
        return cc;
    }
    


    
    
    
    //Chat client class
    class chatClient extends Thread {
        
        String host = "localhost";
        int port = 19998;
        Socket connection;
        DataOutputStream out;
        DataInputStream in;
        chatClientListener cl;


        
        //Constructor
        chatClient() {
            
        }
        
        
        @Override
        public void run() {
            
            try {
                
                connection = new Socket(host, port);

                System.out.println("Connection established on :" + connection);
                
                out = new DataOutputStream(connection.getOutputStream());
                
                in = new DataInputStream(connection.getInputStream());
                
                
                
                cl = new chatClientListener(in);
                  
                Thread t = new Thread(cl);
                t.start();
                
               
            
            } catch (IOException e) {}
            
        }
            
        //Chat client functions
        public void sendMessage(String msg) {
        
            try {
                out.writeUTF(msg);
                
            } catch (IOException e) {}
            
        }
        

        
        
        
        public class chatClientListener extends Thread { 
            
            String chatMessage = "";
            DataInputStream in;
            
            //Constructtor
            chatClientListener(DataInputStream _in) {
                this.in = _in;
            }         
            
            @Override
            public void run() {
                
                try {
                    
                                        
                    while (true) {
                        chatMessage = in.readUTF();
                                     
                    }
                } catch (IOException e) {}
                
            }
            
            
        }
    
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
