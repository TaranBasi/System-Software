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
    
    client (String str) {
        this.packetStr = str;
    }
    
    public static void main() throws IOException {
        String host = "localhost";
        
        int port = 19999;
        
        try {
            System.out.println("Sending packet");
            Socket server = new Socket(host, port);
        
            DataOutputStream outToServer = new DataOutputStream(server.getOutputStream());
        
            outToServer.writeUTF(packetStr);
        
            server.close();
        }
        catch (IOException f) {}
        
    }
}
