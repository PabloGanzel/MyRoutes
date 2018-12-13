package com.pablo.myroutes;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Paul on 11.12.2018.
 */

class Backup {

    static ArrayList<File> backupFiles;

    //    public ArrayList<String> getBackupFiles(){
//            backupFilesPath = new ArrayList<>();
//
//        }
//    }
    static String[] getFiles() {
        backupFiles = new ArrayList<>();
        ArrayList<String> backupFilesPath = new ArrayList<>();
//        String directoryPath = Environment.getExternalStorageDirectory().getPath();
//        File directory = new File(directoryPath);
        File[] files = new File(Environment
                .getExternalStorageDirectory()
                .getPath()).listFiles();
        for (File file:files) {
            if (file.getName().endsWith(".backup")||file.getName().endsWith(".rbackup")) {
                backupFiles.add(file);
                backupFilesPath.add(file.getName());
            }
        }
//        String[] result = new String[files.length];
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].getName().endsWith("backup")) { //routes.backup
//                result[i] = files[i].getName();
//            }
////            result[i].
//        }
        return backupFilesPath.toArray(new String[backupFiles.size()]);
    }

    static Object[] restore(int id) throws IOException,ClassNotFoundException {
        FileInputStream fos = new FileInputStream(backupFiles.get(id));
        ObjectInputStream ois = new ObjectInputStream(fos);
        Object[] objects = (Object[]) ois.readObject();
        ois.close();
        fos.close();

        return objects;
    }

    public static boolean saveData(Object[] objects) throws IOException {
            FileOutputStream fos = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory(), Helper.getNumbericDate().concat(".rbackup")));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(objects);
            oos.close();
            fos.close();

            return true;
    }
}

