package com.example.bitmproject.tourmate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bitmproject.tourmate.PosoClass.UserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private  EditText etEmail,etPassword;
    private FirebaseAuth firebaseAuth;
    private UserClass user;
    private ProgressDialog progressDialog;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        etEmail.setText("");
        etPassword.setText("");
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.emailEt);
        etPassword = findViewById(R.id.passwordEt);

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        etEmail.setText("");
        etPassword.setText("");



        //if we get a user direacly go to event
        goEventActivityLogedInUser();



    }



    /****** Login Button button*********/
    public void logIn(View view) {


        progressDialog.setMessage("Login.................");
        progressDialog.show();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            //all field is fillup
            checkLogin(email,password);
        }
        else {
            // all the field is not fill up
            progressDialog.cancel();
            Toast.makeText(this, "Please fill all field", Toast.LENGTH_SHORT).show();
        }
    }
    /****** Sign up button*********/
    public void signUp(View view)
    {
        startActivity(new Intent(this,SignUpActivity.class));
    }

    private void goEventActivityLogedInUser() {

        if (firebaseAuth.getCurrentUser() !=null){
            if (firebaseAuth.getCurrentUser().isEmailVerified())
            {
                LoginActivity.this.finish();
                startActivity(new Intent(this,EventActivity.class));

            }
        }

    }

    private void checkLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                        startActivity(new Intent(LoginActivity.this,EventActivity.class));

                }
            }
        });
    }

}