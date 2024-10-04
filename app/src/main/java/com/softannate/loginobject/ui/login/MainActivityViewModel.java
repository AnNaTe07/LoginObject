package com.softannate.loginobject.ui.login;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.loginobject.modelo.Usuario;
import com.softannate.loginobject.request.ApiClient;

public class MainActivityViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<Usuario> mUsuario;
    private MutableLiveData<String> mMensaje;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.context=application.getApplicationContext();
    }

    public LiveData<Usuario> getMUsuario(){
        if(mUsuario==null){
            mUsuario=new MutableLiveData<>();
        }
        return mUsuario;
    }

    public LiveData<String> getMMensaje(){
        if (mMensaje == null) {
            mMensaje=new MutableLiveData<>();
        }
        return mMensaje;
    }

    public void login(Context context,String email,String pass ){

        Usuario usuario = ApiClient.login(context, email, pass,mMensaje);

        if(usuario==null){
            //mMensaje.setValue("Datos incorrectos");
        }else{
            mUsuario.setValue(usuario);
        }
    }
}
