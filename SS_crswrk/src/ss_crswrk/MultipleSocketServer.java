/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ss_crswrk;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import sun.applet.Main;

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
                String[] process = str.split("~");
                
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
                
                //Home Screen Updates
                else if (process[0].equals("connectedPeople")) {
                    System.out.println("Updating connected people");
                    updateConnectedPeople();
                }
                else if (process[0].equals("friendsList")) {
                    System.out.println("Loading friends list");
                    loadFriendsList(process[1]);
                }
                else if (process[0].equals("friendsInfo")) {
                    System.out.println("Loading friends info");
                    updateFriendsInfo(process[1]);
                }
                
                //Other functions
                else if (process[0].equals("playSong")) {
                    System.out.println("Play song function");
                    System.out.println(process[1]);
                    playSong(process[1]);
                }
                
                else if (process[0].equals("postToFile")) {
                    System.out.println("Post to file");
                    System.out.println(process[1]);
                    postToFile(process[1]);
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
        String[] packetArray = packet.split("~");
        boolean usernameExists = false;
        boolean passwordsMatch = false;

        try {        
            
            FileWriter fout = new FileWriter("userFile.txt",true);    //Creates file if it doesnt exist
            fout.close();
                
            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] strArray = line.split("~");
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
        
        String[] packetArray = packet.split("~");
        boolean usernameExists = false;

        try {           
            FileWriter fout = new FileWriter("userFile.txt",true);    //Creates file if it doesnt exist
            fout.close();

            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);

            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] lineArray = line.split("~");
                if (packetArray[1].equals(lineArray[0])){        //Checking each line in file to see if username exists
                    usernameExists = true;
                }
            }
            din.close();
            
            
        } catch (IOException e) {}
        
        if (!usernameExists) {
            String lineToWrite = packet.replace("register~", "");

            try {
                FileWriter fout = new FileWriter("userFile.txt",true);  
                PrintWriter pout =  new PrintWriter(fout, true);
                pout.println(lineToWrite); 
                pout.close();
               
                copyMusic(packet);
            } catch (IOException e) {}
            
            sendToClient("userCreated");
        } else {
            sendToClient("userAlreadyExists");
        }
    }
    
    private void logout(String packet) {

        String[] packetArray = packet.split("~");
        String compare;
        for (int i = 0; i < currentLoginList.size(); i++) {
            compare = currentLoginList.get(i);
            if (packetArray[1].equals(compare)) {
                currentLoginList.remove(i);
            }
        }
        
        sendToClient("loggedOut");
    }
    
    
    //Updating home screen functions
    
    private void updateConnectedPeople() {
        String peopleStr = "connectedPeople";
        
        try {
            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] strArray = line.split("~");
                peopleStr += "~" + strArray[0];
                //Checking to see if they are online
                for (int i = 0; i < currentLoginList.size(); i++) {
                    if (strArray[0].equals(currentLoginList.get(i))) {
                    
                        peopleStr += "*";
                    }
                }

                
            }
    
            
            din.close();
                    
        } catch (IOException e) {}
        
        sendToClient(peopleStr);
        
    }
    
    private void loadFriendsList(String user) {
        String friendsStr = "friendsList";
        try {
            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] strArray = line.split("~");
                friendsStr += "~" + strArray[0];        
            }
    
            din.close();
                    
        } catch (IOException e) {}
        
        sendToClient(friendsStr);
    }
    
    
    private void updateFriendsInfo(String user) {
        String infoStr = "friendsInfo~";
        
        try {
            FileReader fin = new FileReader("userFile.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
                String[] strArray = line.split("~");
                
                if (strArray[0].equals(user)) {
                    infoStr += line;        
                }
            }
    
            din.close();
                    
        } catch (IOException e) {}
        
        sendToClient(infoStr);
    }
    
    
    //Other functions
    
    private void copyMusic(String str) {
        String strArray[] = str.split("~");
        
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].contains(".wav")) {
                
                try {
                    File file = new File(strArray[i]); //Create the file to copy
                    String name = file.getName(); 
                    File target = new File(System.getProperty("user.dir/music")+ name); //Create the file destination
                    Path path = Paths.get(strArray[i]);     //Create a path for the file
                    
                    Files.copy(path, target.toPath(), REPLACE_EXISTING); //Copy the file
                } catch (IOException e) {}
            }
        }
    }
    
    private void playSong(String str) {
        String songTitle = str + ".wav";
        System.out.println("Playing song: " + songTitle);
        
//        Media song = new Media(new File(songTitle).toURI().toString());
//        MediaPlayer mediaPlayer = new MediaPlayer(song);
//        mediaPlayer.play();


        //Play music here!...
//        String song = sharedSongsListContents.getSelectedValue();
//        Media hit = new Media(new File(song).toURI().toString());
//        MediaPlayer mediaPlayer = new MediaPlayer(hit);
//        mediaPlayer.play();
        
        sendToClient("playingSong"); 
    }
    
    private void postToFile(String updatePost){
        System.out.println(updatePost);
        
        //String[] updatePostArray = updatePost.split("~");
        //String newPost = updatePost.replace("postToFile~", "");
        System.out.println(updatePost);
        
        
        try {
            FileWriter fout = new FileWriter("posts.txt",true);    //Creates file if it doesnt exist
            fout.close();
            
            
            PrintWriter pout =  new PrintWriter(fout, true);
            pout.println(updatePost);
            pout.close();
            fout.close();
            
        }
        catch (IOException e) {}
        sendToClient("postUpdated");
    }
}