package ceng.estu.filehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author reuzun
 */
public class FileHandler {


    //first string is guild id second string is channel id
    //this is for adjusting channels for the identified guilds.
    public static Map<String, String> map = new HashMap<>();


    static File file = new File("channels.txt");


    public static void writeToFile(String guildId, String channelId) throws IOException {
        if(!file.exists())file.createNewFile();

        Scanner scan  = new Scanner(file);

        StringBuilder sb = new StringBuilder();
        while(scan.hasNextLine()){
            String abc = scan.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(abc);
            String str1 = tokenizer.nextToken();
            String str2 = tokenizer.nextToken();
            System.out.println("str1 is : " + str1 + "\nstr2 is : " + str2);
            if(str1.equals(guildId)){
                continue;
            }
            else{
                sb.append(str1 + " " + str2 +"\n");
            }
        }
        scan.close();

        PrintWriter writer = new PrintWriter(file);
        sb.append(guildId + " " + channelId + "\n");
        writer = new PrintWriter(file);
        writer.append(sb.toString());
        writer.flush();
        writer.close();

        handleMap();
    }

    public static void handleMap(){
        map = new HashMap<>();
        Scanner scan = null;
        try {

            scan = new Scanner(file);
        }catch (Exception e){}
        if(file.exists()){
            while(scan.hasNextLine()) {
                StringTokenizer tokenizer = new StringTokenizer(scan.nextLine());
                map.put(tokenizer.nextToken(), tokenizer.nextToken());
            }
        }
        else return;
        scan.close();
    }



}
