package com.softannate.loginobject.ui.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softannate.loginobject.R;
import com.softannate.loginobject.databinding.ActivityRegistroBinding;
import com.softannate.loginobject.modelo.Usuario;


public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private RegistroActivityViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegistroBinding.inflate(getLayoutInflater());
        vm= ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);

        EdgeToEdge.enable(this);

        setContentView(binding.getRoot());
        vm.mostrar(getIntent());

        binding.btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.registrar(binding.etNombre.getText().toString(), binding.etApellido.getText().toString(), binding.etDni.getText().toString(), binding.etEmail2.getText().toString(), binding.etPass2.getText().toString());

            }
        });
        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                limpiar();
                finish();//cierro la activity actual
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
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void limpiar(){
        binding.etNombre.setText("");
        binding.etApellido.setText("");
        binding.etDni.setText("");
        binding.etEmail2.setText("");
        binding.etPass2.setText("");
    }
}