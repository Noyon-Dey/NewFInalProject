package com.example.final_bitm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    private EditText emailET,nameET,passwordET,confirmPasswordET;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;
    private ProgressDialog progressDialog;
    private EventClass event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //inatilize everything
        nameET=findViewById(R.id.nameEt);
        emailET=findViewById(R.id.emailEtS);
        passwordET=findViewById(R.id.passwordEtS);
        confirmPasswordET=findViewById(R.id.confirmPasswordEtS);
        progressDialog = new ProgressDialog(this);



        firebaseAuth=FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User");

        nameET.setText("");
        emailET.setText("");
        passwordET.setText("");
        confirmPasswordET.setText("");

    }

    public void signUp(View view) {

        final String name = nameET.getText().toString();
        final String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {

            //all the field have value

            if(password.equals(confirmPassword)) {

                progressDialog.setMessage("Registering.............");
                progressDialog.show();

                //email pasword are matched
                firebaseAuth.createUserWithEmailAndPassword(email, password).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // sign up succesful

                            //sending user data to database
                            String id=firebaseAuth.getCurrentUser().getUid();
                            UserClass user=new UserClass(name,email);
                            userDatabaseReference.child(id).setValue(user);
                            //for verify email
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(SignUpActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            Toast.makeText(SignUpActivity.this, "Please verify your email before login", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                        }
                        else {
                            progressDialog.cancel();
                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            //password matching
            else {
                Toast.makeText(this, "Passwords are not match", Toast.LENGTH_SHORT).show();
            }
        }
        //checking all field is abailable
        else {
            Toast.makeText(SignUpActivity.this, "Please fill up all the field", Toast.LENGTH_SHORT).show();
        }
    }
}
