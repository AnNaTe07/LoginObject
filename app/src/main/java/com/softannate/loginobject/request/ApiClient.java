package com.softannate.loginobject.request;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.softannate.loginobject.modelo.Usuario;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ApiClient {
    private static File archivo;

    private static File conectar (Context context){
        if(archivo==null){
            archivo= new File(context.getFilesDir(), "Datos.obj");
        }
        return  archivo;
    }
    public static void guardar(Context context, Usuario usuario, MutableLiveData<String> mensaje) {
        File archivo = conectar(context);

        try {
            FileOutputStream fos = new FileOutputStream(archivo, false);//false para que se sobreescriba
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos) ;
             oos.writeObject(usuario);
             oos.flush();
             fos.close();

        } catch (FileNotFoundException e) {
            mensaje.setValue("Error al guardar el usuario.");
        } catch (IOException e) {
            mensaje.setValue("Error al guardar el usuario.");
        }
    }
   /* public static void guardar(Context context, Usuario usuario, MutableLiveData<String> mensaje) {
            File archivo = conectar(context);

            try (FileOutputStream fos = new FileOutputStream(archivo, false);//false para que se sobreescriba
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos) ){

                oos.writeObject(usuario);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                mensaje.setValue("Error al guardar el usuario.");
            }
    }*/

       /* public static Usuario leer(Context context, MutableLiveData<String>mensaje) {
            File archivo = conectar(context);
            Usuario usuario = null;

            if (!archivo.exists() || archivo.length() == 0) {// Archivo no encontrado o vacío
                return null;
            }

                try (FileInputStream fis = new FileInputStream(archivo);
                     BufferedInputStream bis = new BufferedInputStream(fis);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {

                    usuario = (Usuario) ois.readObject();
                } catch (IOException e) {
                    //error de entrada/salida
                    mensaje.setValue("Error al leer los datos del usuario.");
                } catch (ClassNotFoundException e) {
                    //clase no encontrada
                    mensaje.setValue("Datos de usuario inválidos.");
                }
            return usuario;
        }*/
    public static Usuario leer(Context context, MutableLiveData<String>mensaje) {
        File archivo = conectar(context);
        Usuario usuario = null;

        if (!archivo.exists() || archivo.length() == 0) {// Archivo no encontrado o vacío
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(archivo);

        BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis);

            usuario = (Usuario) ois.readObject();
            fis.close();
        } catch (FileNotFoundException e) {
            mensaje.setValue("Error al leer los datos del usuario.");
        } catch (IOException e) {
            //error de entrada/salida
            mensaje.setValue("Error al leer los datos del usuario.");
        } catch (ClassNotFoundException e) {
            //clase no encontrada
            mensaje.setValue("Datos de usuario inválidos.");
        }
        return usuario;
    }

        public static Usuario login(Context context, String email1, String pass1, MutableLiveData<String>mensaje) {

            if (email1.isEmpty() || pass1.isEmpty()) {
                mensaje.setValue("Por favor, complete todos los campos.");
                return null;
            }
            Usuario usuario = leer(context,mensaje);

            if (usuario!=null && email1.equalsIgnoreCase(usuario.getEmail()) && pass1.equalsIgnoreCase(usuario.getPass())) {
                return usuario;
            }else{
                mensaje.setValue("Datos inválidos");
            }
            return null;
        }

    /*private static class MyObject extends ObjectOutputStream {

        MyObject(OutputStream o) throws IOException
        {
            super(o);
        }
        public void writeStreamHeader() throws IOException
        {
            reset();// para no sobreescribir el encabezado
            //return;
        }
    }*/
}