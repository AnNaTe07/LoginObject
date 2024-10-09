package com.softannate.loginobject.ui.register;


import static com.softannate.loginobject.request.ApiClient.guardar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import static android.app.Activity.RESULT_OK;
import com.softannate.loginobject.modelo.Usuario;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegistroActivityViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> mensajeError;
    private MutableLiveData<String> mensaje;
    private MutableLiveData<Usuario> mUsuario;
    private MutableLiveData<Bitmap> foto;


    public RegistroActivityViewModel(@NonNull Application application) {
        super(application);
        this.context=application.getApplicationContext();
    }

    public LiveData<String> getMensajeError(){
        if(mensajeError==null){
            mensajeError=new MutableLiveData<>();
        }
        return mensajeError;
    }

    public LiveData<String>getMensaje(){
        if(mensaje==null){
            mensaje=new MutableLiveData<>();
        }
        return mensaje;
    }

    public LiveData<Usuario> getMUsuario(){
        if(mUsuario==null){
            mUsuario=new MutableLiveData<>();
        }
        return mUsuario;
    }
    public LiveData<Bitmap> getFoto() {
        if (foto == null) {
            foto = new MutableLiveData<>();
        }
        return foto;
    }
    public void cargar() {
        File archivo =new File(context.getFilesDir(),"foto1.png");
        Bitmap imageBitmap=BitmapFactory.decodeFile(archivo.getAbsolutePath());
        if(imageBitmap!=null) {
            foto.setValue(imageBitmap);
        }
    }

    public void respuetaDeCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE) {
        Log.d("salida", requestCode + "");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Recupero los datos provenientes de la camara.
            Bundle extras = data.getExtras();
            //Casteo a bitmap lo obtenido de la camara.
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (imageBitmap != null) {
                //Rutina para optimizar la foto,
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                foto.setValue(imageBitmap);

                //Rutina para convertir a un arreglo de byte los datos de la imagen
                byte[] b = baos.toByteArray();

                //Aquí podría ir la rutina para llamar al servicio que recibe los bytes.
                File archivo = new File(context.getFilesDir(), "foto1.png");
                if (archivo.exists()) {
                    archivo.delete();
                }
                try {
                    FileOutputStream fo = new FileOutputStream(archivo);
                    BufferedOutputStream bo = new BufferedOutputStream(fo);
                    bo.write(b);
                    bo.flush();
                    bo.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        public void registrar (String nombre, String apellido, String dni, String email, String pass,byte[] foto)
        {

            if (!nombre.isEmpty() && !apellido.isEmpty() && !dni.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {

                if (validarDNI(dni) && validarEmail(email)) {
                    try {
                        Long doc = Long.parseLong(dni);
                        Usuario usuario = new Usuario(nombre, apellido, doc, email, pass,foto);
                        guardar(context, usuario, mensaje);
                        Log.d("RegistroActivity", "Usuario: " + usuario.toString()+ foto);
                        mensaje.setValue("Usuario registrado/modificado con exito");
                        Log.d("usuario guardado", usuario.toString());

                    } catch (NumberFormatException n) {
                        mensajeError.setValue("Error con el dni ingresado. Intenta nuevamente");
                    }

                } else {
                    StringBuilder mensajeE = new StringBuilder();
                    if (!validarDNI(dni)) {
                        mensajeE.append("DNI inválido. Debe estar compuesto por 7 u 8 digitos. ");
                    }
                    if (!validarEmail(email)) {
                        mensajeE.append("Email inválido. Verifique que el email ingresado es correcto.");
                    }
                    mensajeError.setValue(mensajeE.toString());
                }
            } else {
                mensajeError.setValue("Todos los campos son obligatorios.");
            }

        }

        private boolean validarDNI (String dni){
            // Si el DNI está vacío y no tiene entre 7 y 8 dígitos
            if (dni.isEmpty() || dni.length() < 7 || dni.length() > 8) {
                return false;
            }
            // \\d(solo digitos)  +(tiene que ser mas de uno)
            return dni.matches("\\d+");
            //matches(String regex) se usa para comparar la cadena con la expresión regular proporcionada. Devuelve true si coincide y false si no.
        }
        private boolean validarEmail (String email){
            // Regex para validar el formato del correo electrónico
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            return email.matches(emailRegex);
        }

        public void mostrar (Intent intent){
            Usuario usuario = (Usuario) intent.getSerializableExtra("usuario");
            if (usuario != null) {
                if (mUsuario == null) {
                    mUsuario = new MutableLiveData<>();
                }
                mUsuario.setValue(usuario);
            }
        }

    }
