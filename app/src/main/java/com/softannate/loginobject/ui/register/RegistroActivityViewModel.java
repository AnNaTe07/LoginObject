package com.softannate.loginobject.ui.register;


import static com.softannate.loginobject.request.ApiClient.guardar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.loginobject.modelo.Usuario;

public class RegistroActivityViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> mensajeError;
    private MutableLiveData<String> mensaje;
    private MutableLiveData<Usuario> mUsuario;

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

    public void registrar(String nombre, String apellido, String dni, String email, String pass) {

        if (!nombre.isEmpty() && !apellido.isEmpty() && !dni.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {

            if (validarDNI(dni)  && validarEmail(email)) {
                try{
                    Long doc = Long.parseLong(dni);
                    Usuario usuario = new Usuario(nombre, apellido, doc, email, pass);
                    guardar(context, usuario,mensaje);
                    Log.d("RegistroActivity", "Usuario: " + usuario.toString());
                    mensaje.setValue("Usuario registrado/modificado con exito");
                    Log.d("usuario guardado",usuario.toString());

                }catch (NumberFormatException n){
                    mensajeError.setValue("Error con el dni ingresado. Intenta nuevamente");
                }

            }else {
                StringBuilder mensajeE=new StringBuilder();
                if(!validarDNI(dni)) {
                    mensajeE.append("DNI inválido. Debe estar compuesto por 7 u 8 digitos. ");
                }
                if(!validarEmail(email)){
                    mensajeE.append("Email inválido. Verifique que el email ingresado es correcto.");
                }
                mensajeError.setValue(mensajeE.toString());
            }
        }else {
            mensajeError.setValue("Todos los campos son obligatorios.");
        }

    }

    private boolean validarDNI(String dni) {
        // Si el DNI está vacío y no tiene entre 7 y 8 dígitos
        if (dni.isEmpty() || dni.length() < 7 || dni.length() > 8) {
            return false;
        }
        // \\d(solo digitos)  +(tiene que ser mas de uno)
        return dni.matches("\\d+");
        //matches(String regex) se usa para comparar la cadena con la expresión regular proporcionada. Devuelve true si coincide y false si no.
    }
    private boolean validarEmail(String email) {
        // Regex para validar el formato del correo electrónico
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public void mostrar(Intent intent){
        Usuario usuario= (Usuario)intent.getSerializableExtra("usuario");
        if (usuario != null) {
            if(mUsuario==null){
                mUsuario=new MutableLiveData<>();
            }
            mUsuario.setValue(usuario);
        }
    }
}
