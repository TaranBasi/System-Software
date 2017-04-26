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
    
    ArrayList<user> userAL = new ArrayList<user>();

    
    
    
    class user {
        public String username;
        public Socket socket;
        
        user(String _username, Socket _socket) {
            this.username = _username;
            this.socket = _socket;
        }
    }    
    
    
    
    
    
    public ServerHandler(Socket socket, ArrayList<user> userAL) {
        this.socket = socket;
        this.userAL = userAL;
    }

    
    
    
    @Override
    public void run() {
        try {
                            
            DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
           
            //DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                       
            
            String line  = "";
            while (true) {
                line = inFromClient.readUTF();
                System.out.println("Received message: " + line + " from socket: " + socket);
                
                String[] strArray = line.split("~");

                
                if (strArray[0].equals("add")) {

                    
                    
                    

                        
                    
                    user u = new user(strArray[1], socket);

                    userAL.add(u);
                                        
                } else {
                    
                    
                    //NORMAL MESSAGE FORMAT ---- strArray[0] = user ---- strArray[1] = friend ---- strArray[2] = message


                    for (int i = 0; i < userAL.size(); i++) {
                        if ((userAL.get(i).username).equals(strArray[1])) {
                            DataOutputStream outToClient = new DataOutputStream(userAL.get(i).socket.getOutputStream());
                            outToClient.writeUTF(strArray[0] + "~" + strArray[2]);
                        }
                        
                    }                   
                                 
                }
                
            }
            
        } catch (Exception e) {}
    }
}
