package com.ite5year.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeHandler<T> {
    public void writeToFile(T object, String path) {
        try {
        FileOutputStream fout = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(object);
        oos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public T readFromFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);

            @SuppressWarnings("unchecked")
            T obj = (T) ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
