/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Daniel
 */
class ServerHandler extends Thread {
    
    private Socket socket;
    ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();
    ArrayList<Socket> clientsSockets = new ArrayList<Socket>();
        
    public ServerHandler(Socket socket, ArrayList<Socket> clientsSockets) {
        this.socket = socket;
        this.clientsSockets = clientsSockets;
    }

    
    @Override
    public void run() {
        try {
            
                       
          //  ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();
           // ArrayList<String> clientNames = new ArrayList<String>();
            
                                     
            DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
            
         //   DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            
            
            //clients.add(outToClient);
            
            System.out.println("OutputStream added");
            System.out.println("Number of sockets: " + clientsSockets.size());
            
            
            String line  = null;
            while (true) {
                line = inFromClient.readUTF();
                System.out.println("Received message: " + line);
                
                                
                for (int i = 0; i < clientsSockets.size(); i++) {
                    Socket clientSocket = clientsSockets.get(i);
                    DataOutputStream oss = new DataOutputStream(clientSocket.getOutputStream());
                    oss.writeUTF(line);
                    System.out.println("sent");
                    oss.flush();
                }
                
            }
            
        } catch (Exception e) {}
    }
}
