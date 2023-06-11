package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class FileUtil {
    public static String getExtensionFromNameFile (String nameFile) {
        return nameFile.replaceAll("^.+\\.([a-zA-Z\\d]+)$", "$1");
    }

    public static boolean removeFile(String path) {
        File file = new File(path);

        if(file.exists()) {
            file.delete();
        }

        return true;
    }

    public static boolean saveBytesToFile(byte[] bytes, File file) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static String generateRandomName() {
        String chars = "abcdefghijklmnopqrstuwvxyz0123456789";
        String name = "";

        for (int i = 0; i < 30; i++) {
            String nChar = String.valueOf(chars.charAt((int) Math.round(Math.random() * (chars.length() - 1))));

            name += Math.round(Math.random()*100)%2 == 0 ? nChar : nChar.toUpperCase();
        }

        return System.currentTimeMillis() + "_" + name;
    }

    public static File createNonExistsFile(File dir, String format) {
        File file;

        do{
            file = new File(dir, generateRandomName() + "." + format);
        } while(file.exists());

        return file;
    }
}
