package com.banksaku.banksaku.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewRegister,textViewTitleLogin,textViewNotHaveAccount;
    private TextInputEditText editTextEmail,editTextPassword;
    private Button buttonLogin;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewNotHaveAccount = findViewById(R.id.textViewNotHaveAccount);
        textViewTitleLogin = findViewById(R.id.textViewTitleLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        //Setup Firebase Authentification
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        //Setup Progress Bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Checking user already login or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewRegister){
            getPageLogin();
        }else if(view.getId() == R.id.buttonLogin){
            if (buttonLogin.getText().toString().equals("Daftar")){
                addUser();
            }else {
                loginUser();
            }
        }
    }

    private void getPageLogin(){
        if (textViewRegister.getText().toString().equals("Registrasi")){
            textViewTitleLogin.setText("Daftar");
            textViewRegister.setText("Masuk");
            textViewNotHaveAccount.setText("Sudah memiliki akun?");
            buttonLogin.setText("Daftar");
            clearForm();
        }else {
            textViewTitleLogin.setText("Masuk");
            textViewRegister.setText("Registrasi");
            textViewNotHaveAccount.setText("Belum memiliki akun?");
            buttonLogin.setText("Masuk");
            clearForm();
        }
    }

    private void addUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Silahkan Masukan Email");
            return;
        }else if (password.isEmpty()){
            editTextPassword.setError("Silahkan Masukan Password");
            return;
        }
        if (password.length()<6){
            Toast.makeText(this, "Minimal password 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Berhasil Daftar", Toast.LENGTH_SHORT).show();
                        }else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(LoginActivity.this, "Akun sudah terdaftar", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Format email tidak sesuai", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void loginUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Silahkan Masukan Email");
            return;
        }else if (password.isEmpty()){
            editTextPassword.setError("Silahkan Masukan Password");
            return;
        }
        if (password.length()<6){
            Toast.makeText(this, "Minimal password 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Email atau password salah", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void clearForm(){
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}
