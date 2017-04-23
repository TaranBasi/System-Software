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
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

/**
 *
 * @author Daniel
 */
public class MultipleSocketServer implements Runnable{

    private Socket csocket;
    static private ArrayList<String> currentLoginList = new ArrayList<String>();
    private static AudioStream audioStream;
    private static Player player;

    
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
                String[] strArray = str.split("~");
                
                //if statements to detemrine what to do with the packet
                
                if (strArray[0].equals("login"))
                {
                    //run login function
                    System.out.println("Running login writing function");
                    checkLoginFile(str);
                }
                else if (strArray[0].equals("register"))
                {
                    //run register function
                    System.out.println("Running register function");
                    checkRegisterFile(str);
                } 
                else if (strArray[0].equals("logout")) {
                    System.out.println("Logging out");
                    logout(str);
                }
                
                //Home Screen Updates
                else if (strArray[0].equals("connectedPeople")) {
                    System.out.println("Updating connected people");
                    updateConnectedPeople();
                }
                else if (strArray[0].equals("friendsList")) {
                    System.out.println("Loading friends list");
                    loadFriendsList(strArray[1]);
                }
                else if (strArray[0].equals("friendsInfo")) {
                    System.out.println("Loading friends info");
                    updateFriendsInfo(strArray[1]);
                }
                
                //Other functions
                else if (strArray[0].equals("playSong")) {
                    System.out.println("Play song function");
                    System.out.println(strArray[1]);
                    playSong(strArray[1]);
                }          
                else if (strArray[0].equals("stopSong")) {
                    System.out.println("Stop song function");
                    stopSong();
                }   
                else if (strArray[0].equals("postToFile")) {
                    System.out.println("Post to file");
                    System.out.println(strArray[1]);
                    postToFile(strArray[1]);
                }
                else if (strArray[0].equals("updateFeed")) {
                    System.out.println("Updating feed");
                    updateFeed();
                }
                
                //FRIEND FUNCTIONS
                else if (strArray[0].equals("requestFriend")) {
                    System.out.println("Adding to friendship request list");
                    requestFriend(str);
                }
                else if (strArray[0].equals("updateFriendRequests")) {
                    System.out.println("Updating friend request list");
                    updateFriendRequests(strArray[1]);
                }
                else if (strArray[0].equals("acceptFriend")) {
                    System.out.println("Accepting friend");
                    acceptFriend(str);
                }
                else if (strArray[0].equals("removeRequest")) {
                    System.out.println("Removing request");
                    removeRequest(str);
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
            fin.close();
                    
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
            fin.close();
            
            
        } catch (IOException e) {}
        
        if (!usernameExists) {
            String lineToWrite = packet.replace("register~", "");

            try {
                //Write info to the userFile
                FileWriter fout = new FileWriter("userFile.txt",true);  
                PrintWriter pout =  new PrintWriter(fout, true);
                pout.println(lineToWrite); 
                pout.close();
                fout.close();
               
                copyMusic(packet);
                
                //Add name to the friend requests file
                FileWriter requestFout = new FileWriter("friendRequests.txt", true);
                PrintWriter requestPout =  new PrintWriter(requestFout, true);
                requestPout.println(packetArray[1] + ":"); 
                requestPout.close();
                requestFout.close();
                
                //Add name to the friend requests file
                FileWriter friendFout = new FileWriter("friends.txt", true);
                PrintWriter friendPout =  new PrintWriter(friendFout, true);
                friendPout.println(packetArray[1] + ":"); 
                friendPout.close();
                friendFout.close();
                
                
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
            fin.close();
                    
        } catch (IOException e) {}
        
        sendToClient(peopleStr);
        
    }
    
    private void loadFriendsList(String user) {
        String friendsStr = "friendsList";
        
        try {
            
            FileReader fin = new FileReader("friends.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {
               
                String[] strArray = line.split(":");
                
                if (strArray[0].equals(user)) {
                    System.out.println("user is: " + user);
                    
                    if (strArray.length > 1) {
                        String[] friendArray = strArray[1].split("~");
                        for (int i = 0; i < friendArray.length; i++) {
                            friendsStr += "~" + friendArray[i];
                        }
                    }
                }
                
                System.out.println("Friends are: " + friendsStr);
            }
    
            din.close();
            fin.close();
                    
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
            fin.close();
                    
        } catch (IOException e) {}
        
        sendToClient(infoStr);
    }
    
    private void updateFeed() {
        String posts = "updateFeed";
        try {
            FileReader fin = new FileReader("posts.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
           
            String line = null;
            while ((line = din.readLine()) != null) {
                posts += "~" + line; 
            }
    
            din.close();
            fin.close();
                    
        } catch (IOException e) {}
        
        sendToClient(posts);
    }
    
    
    //Other functions
    
    private void copyMusic(String str) {
        String strArray[] = str.split("~");
        
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].contains(".wav") || strArray[i].contains(".mp3")) {
                
                try {
                    File file = new File(strArray[i]); //Create the file to copy
                    String name = file.getName(); 
                    System.out.println("name of file: " + name);
                    File target = new File(System.getProperty("user.dir")+"/music",name);
                    
                    System.out.println(target.toString());
                    Path path = Paths.get(strArray[i]);     //Create a path for the file
                    
                    Files.copy(path, target.toPath(), REPLACE_EXISTING); //Copy the file
                } catch (IOException e) {}
            }
        }
    }
    
    private void playSong(String str) {
        
        System.out.println(str);
        
            try {
                
                File file = new File(System.getProperty("user.dir")+"/music", str);

                
                
                System.out.println("file: " + file.toString());
                InputStream in = new FileInputStream(file);
                
                BufferedInputStream bis = new BufferedInputStream(in);
                
               
                player = new Player(bis);
                
                player.play();
                

                //audioStream = new AudioStream(in);

                //AudioPlayer.player.start(audioStream);

                System.out.println("Song playing");
                
                

            } catch (Exception e) {}
        

        sendToClient("playingSong"); 
    }
    
    private void stopSong() {
        
        try {
            
            player.close();
            //AudioPlayer.player.stop(audioStream);    
            
        } catch (Exception e) {}
                
        sendToClient("stoppingSong");  
    }
    
    private void postToFile(String updatePost){
        
        //String[] updatePostArray = updatePost.split("~");
        //String newPost = updatePost.replace("postToFile~", "");
        System.out.println(updatePost);
        
        
        try {

            FileWriter fout = new FileWriter("posts.txt",true);  
            PrintWriter pout =  new PrintWriter(fout, true);
            pout.println(updatePost); 
            pout.close();
            fout.close();
        }
        catch (IOException e) {}
        sendToClient("postUpdated");
    }
    
    
    //Friend functions
    
    private void requestFriend(String str) {
        String strArray[] = str.split("~");

        
        //strArray[2] is the current user
        
        try {
            
            FileWriter fout = new FileWriter("friendRequests.txt",true);    //Creates file if it doesnt exist
            fout.close();
            
            BufferedReader file = new BufferedReader(new FileReader("friendRequests.txt"));
            String line;
            String inputStr = "";

            while ((line = file.readLine()) != null) {
                String lineArray[] = line.split(":"); //Get the first name in the line
                if (lineArray[0].equals(strArray[1])) {
                    line += "~" + strArray[2];
                }
                inputStr += line + "\n";
                
            }
            
            
            String[] inputStrArray = inputStr.split("\n");

            FileWriter writer = new FileWriter("friendRequests.txt",false);
            PrintWriter pout =  new PrintWriter(writer, true);
            
            
            for (int i = 0; i < inputStrArray.length; i++) {
                pout.println(inputStrArray[i]);
            }

            pout.close();
            writer.close();
            file.close();
            
        } catch (IOException e) {}
        
        
        sendToClient("friendRequestSent");
    }
    
    private void updateFriendRequests(String str) {
        String requestStr = "updateFriendRequests";

        try {
            
            FileReader fin = new FileReader("friendRequests.txt");        //Opening file for reading
            BufferedReader din = new BufferedReader(fin);
            
            String line = null; //Declare variable to store a line of text
            while ((line = din.readLine()) != null) {

                String[] strArray = line.split(":");    //Split the line to find the user

                if (str.equals(strArray[0])) {    //Finding the users name        
                    if (strArray.length > 1) { //Ensure the user actually has friendship requests
                        String[] lineArray = strArray[1].split("~");    //Split the rest of the line to find all the users friends requests

                        for (int i = 1; i < lineArray.length; i++) {
                            requestStr += "~" + lineArray[i];
                        }
                    }
                }
            }
           
            din.close();
            fin.close();
            
        } catch (IOException e) {}

        
        sendToClient(requestStr);
    }
    
    private void acceptFriend(String str) {
        
        //strArray[2] is the current user
        
        String[] strArray = str.split("~");
        
            try {
            
            FileWriter fout = new FileWriter("friends.txt",true);    //Creates file if it doesnt exist
            fout.close();
            
            BufferedReader file = new BufferedReader(new FileReader("friends.txt"));
            String line;
            String inputStr = "";

            while ((line = file.readLine()) != null) {
                String lineArray[] = line.split(":"); //Get the first name in the line
                if (lineArray[0].equals(strArray[2])) {
                    line += "~" + strArray[1];
                }
                if (lineArray[0].equals(strArray[1])) {
                    line += "~" + strArray[2];
                }
                inputStr += line + "\n";

            }
            
            String[] inputStrArray = inputStr.split("\n");

            FileWriter writer = new FileWriter("friends.txt",false);
            PrintWriter pout =  new PrintWriter(writer, true);
            
            
            for (int i = 0; i < inputStrArray.length; i++) {
                pout.println(inputStrArray[i]);
            }

            pout.close(); 
            writer.close();
            file.close();
            
            } catch (IOException e) {}
            
            
            //Removes the friend request from the file
            removeRequest(str);
            
    }
    
    private void removeRequest(String str) {
        //Delete the friend request from the file
        
        String strArray[] = str.split("~");
            
        try {
        
            FileWriter requestFout = new FileWriter("friendRequests.txt",true);    //Creates file if it doesnt exist
            requestFout.close();
            
            BufferedReader requestFile = new BufferedReader(new FileReader("friendRequests.txt"));

            String requestInputStr = "";
            String line;

            while ((line = requestFile.readLine()) != null) {
                String lineArray[] = line.split(":"); //Get the first name in the line

                
                
                if (lineArray[0].equals(strArray[2])) {
                    if (lineArray.length > 1) {
                        String requestArray[] = lineArray[1].split("~");
                        for (int i = 1; i < requestArray.length; i++) {
                            line = lineArray[0] + ":";

                            if ((!requestArray[i].equals(strArray[1])) && (!requestArray[i].equals(""))) {   
                                System.out.println("requestArray[i]: " + requestArray[i]);
                                System.out.println("strArray[1]: " + strArray[1]);
                                line += "~" + requestArray[i];

                                System.out.println("LINE: " + line);
                            }
                        }
                    
                    }
                     
                    
                }

                requestInputStr += line + "\n";

            }
            
            String[] requestInputStrArray = requestInputStr.split("\n");

            FileWriter requestWriter = new FileWriter("friendRequests.txt",false);
            PrintWriter requestPout =  new PrintWriter(requestWriter, true);
            
            
            for (int i = 0; i < requestInputStrArray.length; i++) {
                requestPout.println(requestInputStrArray[i]);
            }

            requestPout.close();
            requestWriter.close();
            requestFile.close();

            
        } catch (IOException e) {}
        
        sendToClient("RequestRemoved");
            
    }
    
    
}