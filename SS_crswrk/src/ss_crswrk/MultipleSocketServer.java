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
    static private ArrayList<String> currentLoginList = new ArrayList<String>();

    
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
                
                if (process[0].equals("login"))
                {
                    //run login function
                    System.out.println("Running login writing function");
                    checkLoginFile(str);
                }
                else if (process[0].equals("register"))
                {
                    //run register function
                    System.out.println("Running register function");
                    checkRegisterFile(str);
                } 
                else if (process[0].equals("logout")) {
                    System.out.println("Logging out");
                    logout(str);
                }
                
                
                inFromClient.close();
                csocket.close();
                
        } 
        catch (Exception e) {}
    }
    
    
    //Custom functions
   
    private void sendToClient(String packet) {
        try {
            DataOutputStream outToClient = new DataOutputStream(csocket.getOutputStream());
        
            outToClient.writeUTF(packet);
            
        } catch (IOException e) {}
        
    }
    
    private void checkLoginFile(String packet) {
        String[] packetArray = packet.split("/");
        boolean usernameExists = false;
        boolean passwordsMatch = false;

        try {        
            
            FileWriter fout = new FileWriter("userFile.txt",true);    //Creates file if it doesnt exist
            fout.close();
                


            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] strArray = line.split("/");
                if (packetArray[1].equals(strArray[0])){        //Checking each line in file to see if username exists
                    usernameExists = true;
                        
                    if (packetArray[2].equals(strArray[1])) {  //If the username exists, then need to check if the passwords match
                        passwordsMatch = true;
                    }
                }
            }
    
            din.close();
                    
        } catch (IOException e) {}
        
        
        if (usernameExists) {
            if (passwordsMatch) {
                //store login details in current login array? *****************
                sendToClient("correctLogin");
                
                //Add user to the currentLoginList
                currentLoginList.add(packetArray[1]);
                System.out.println(currentLoginList);
            } else {
                //Username exists but password is incorrect
                sendToClient("incorrectPassword");
            }
        } else {
            //display unknown username error message
            sendToClient("unknownUsername");
            //Send unknown username message back to client
        }
    }
    
    private void checkRegisterFile(String packet) {
        
        String[] packetArray = packet.split("/");
        boolean usernameExists = false;

        try {           
            FileWriter fout = new FileWriter("userFile.txt",true);    //Creates file if it doesnt exist
            fout.close();

            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);

            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] lineArray = line.split("/");
                if (packetArray[1].equals(lineArray[0])){        //Checking each line in file to see if username exists
                    usernameExists = true;
                    System.out.println("username already exists");
                }
            }
            din.close();
            
            
        } catch (IOException e) {}
        
        if (!usernameExists) {
            String lineToWrite = packet.replace("register/", "");
            System.out.println(lineToWrite);
            try {
                FileWriter fout = new FileWriter("userFile.txt",true);  
                PrintWriter pout =  new PrintWriter(fout, true);
                pout.println(lineToWrite); 
                pout.close();
            } catch (IOException e) {}
            
            sendToClient("userCreated");
        } else {
            sendToClient("userAlreadyExists");
        }
    }
    
    private void logout(String packet) {
        System.out.println("Looping through current users");
        String[] packetArray = packet.split("/");
        String compare;
        for (int i = 0; i < currentLoginList.size(); i++) {
            compare = currentLoginList.get(i);
            if (packetArray[1].equals(compare)) {
                currentLoginList.remove(i);
                System.out.println("User removed");
            }
        }
        
        sendToClient("loggedOut");
    }
}
    
    
    
    
    
    
    



