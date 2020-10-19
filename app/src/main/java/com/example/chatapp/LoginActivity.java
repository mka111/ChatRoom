package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //userETLogin is the email of the user.
    EditText userETLogin, passETLogin;
    Button LoginBtn, RegisterBtn;
    private static final String LOG_TAG = "MyActivity";

    //instantiate the firebase
    //Firebase:
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    TextView forgot_password;

    @Override
    protected void onStart() {
        //Checking for users existence

        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null ){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        userETLogin = findViewById(R.id.usertextLogin);
        passETLogin = findViewById(R.id.passtextLogin);
        LoginBtn = findViewById(R.id.buttonLogin);
        RegisterBtn = findViewById(R.id.buttonMember);
        forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        //Firebase Auth:

        auth = FirebaseAuth.getInstance();
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Sign up Button : to navigate to the registration page

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });




        //Login Button:

        //when the user enters the username and password, then this data need to be grabbed and sent to firebase. Firebase will authenticate the login credentials.

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username_text = userETLogin.getText().toString();
                String pass_text = passETLogin.getText().toString();

                //Checking if it is empty:

                if(TextUtils.isEmpty(username_text) || TextUtils.isEmpty(pass_text)){
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(username_text, pass_text)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        if(auth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();

                                        }else{

                                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                    else{
                                        try{
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidUserException e){
                                            userETLogin.setError("Invalid Email ID");
                                            userETLogin.requestFocus();

                                        }catch(FirebaseAuthInvalidCredentialsException e) {
                                            Log.d(LOG_TAG, "email :" + username_text);
                                            userETLogin.setError("Invalid Password");
                                            userETLogin.requestFocus();

                                        }catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });



    }
}