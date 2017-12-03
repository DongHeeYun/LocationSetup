package com.locationsetup;

import android.Manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by inter on 2017-11-28.
 */

public class FileManager {
    private static final int RC_STORAGE = 1;

    private static FileManager fileManager = null;

    private String title = "player.dat";

    public static ArrayList<LocationItem> items;

    File file;

    FileOutputStream fout;
    FileInputStream fin;
    ObjectOutputStream oout;
    ObjectInputStream oin;

    public static FileManager getFileManager(){
        if(fileManager==null){
            fileManager = new FileManager();
        }
        return fileManager;
    }

    private FileManager(){

        file = new File(MainActivity.context.getFilesDir(),title);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try{
            fin = new FileInputStream(file);
            oin = new ObjectInputStream(fin);
            items = (ArrayList<LocationItem>)oin.readObject();
        }catch (Exception e){
            items = new ArrayList<LocationItem>();
        }finally {
            try{
                fin.close();
                oin.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveFile(){

        try{
            fout = new FileOutputStream(file);
            oout = new ObjectOutputStream(fout);
            oout.writeObject(items);
            oout.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fout.close();
                oout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addItem(LocationItem item){
        items.add(item);
    }

    public ArrayList getItems(){
        return items;
    }
}
