package com.softannate.loginobject.ui.register;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softannate.loginobject.R;
import com.softannate.loginobject.databinding.ActivityRegistroBinding;
import com.softannate.loginobject.modelo.Usuario;
import com.softannate.loginobject.ui.login.MainActivity;
import com.softannate.loginobject.ui.login.MainActivityViewModel;

import static android.Manifest.permission.CAMERA;

import java.io.ByteArrayOutputStream;


public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private RegistroActivityViewModel vm;
   // private ImageView imagen1;
    private static int REQUEST_IMAGE_CAPTURE=1;
    private ImageView fotoLeer;
   // private boolean cargarImagen ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        binding.foto.setImageResource(R.drawable.ic_launcher_background);
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);

        EdgeToEdge.enable(this);

        setContentView(binding.getRoot());

        configView();
        //fotoLeer=findViewById(R.id.foto);
        vm.mostrar(getIntent());
        //  cargarImagen = getIntent().getBooleanExtra("cargarImagen", false);
        binding.btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtengo la imagen del ImageView
                Bitmap bitmap = ((BitmapDrawable) binding.foto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                vm.registrar(binding.etNombre.getText().toString(), binding.etApellido.getText().toString(), binding.etDni.getText().toString(), binding.etEmail2.getText().toString(), binding.etPass2.getText().toString(), byteArray);

            }
        });
        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                // Mostrar el mensaje
                Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                // Handler para retrasar el cierre de la actividad
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        limpiar();
                        finish(); // Cierra el activity
                    }
                }, 1500); // 2 segundos de retraso
            }
        });

        vm.getMensajeError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(RegistroActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getMUsuario().observe(RegistroActivity.this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {

                binding.etNombre.setText(usuario.getNombre());
                binding.etApellido.setText(usuario.getApellido());
                binding.etDni.setText(usuario.getDni()+"");
                binding.etEmail2.setText(usuario.getEmail());
                binding.etPass2.setText(usuario.getPass());
                if (usuario.getAvatar() != null) {
                    // Convertir byte[] a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(usuario.getAvatar(), 0, usuario.getAvatar().length);
                    binding.foto.setImageBitmap(bitmap);
                }
            }
        });
        vm.getFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                fotoLeer.setImageBitmap(bitmap);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Verifico si se debe cargar la imagen

        //if (cargarImagen) {
        //    vm.cargar(); // Si es true, carga la imagen
        //}
        vm.cargar();
    }



    public void configView(){
        fotoLeer=findViewById(R.id.foto);
        // et1=findViewById(R.id.editText);
        validaPermisos();
        vm = new ViewModelProvider(this).get(RegistroActivityViewModel.class);

        vm.getFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                fotoLeer.setImageBitmap(bitmap);
            }
        });
    }
    public void tomarFoto(View v) {
        if (!validaPermisos()) {
            requestPermissions(new String[]{Manifest.permission_group.CAMERA}, 100);
        } else {
//startActivityForResult es otra forma de iniciar una activity, pero esperando desde donde la llamé un resultado
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //cargarImagen = true;
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //Este método es llamado automáticamente cuando retorna de la cámara.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "Request Code: " + requestCode + ", Result Code: " + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                //
                //vm.cargar();
                if (imageBitmap != null) {
                    // Aquí puedes establecer el valor de la foto en el ViewModel
                    vm.respuetaDeCamara(requestCode, resultCode, data, REQUEST_IMAGE_CAPTURE);
                    //cargarImagen = true;
                } else {
                    Log.e("MainActivity", "Error: Bitmap es nulo");
                }
            } else {
                Log.e("MainActivity", "Error: Intent data o extras son nulos");
            }
        }
    }

    private boolean validaPermisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        cargarDialogoRecomendacion();
        return false; // Solo retorna si no tiene permisos
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){

            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistroActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{Manifest.permission_group.CAMERA},100);
            }
        });
        dialogo.show();
    }


    private void limpiar(){
        binding.etNombre.setText("");
        binding.etApellido.setText("");
        binding.etDni.setText("");
        binding.etEmail2.setText("");
        binding.etPass2.setText("");
       // binding.foto.setImageResource(R.drawable.ic_launcher_background);
        //fotoLeer.setImageBitmap(null) ;// Limpiar el valor de la fotoValue();
    }
}