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

import com.example.chatapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Widgets
    EditText passET, emailET, pass2ET, userET;
    TextView LogIn;
    Button registerBtn;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;
    boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        //Intializing widgets
        userET = findViewById(R.id.userEditText);
        passET = findViewById(R.id.passEditText);
        emailET = findViewById(R.id.emailEditText);
       // pass2ET = findViewById(R.id.pass2EditText);
        registerBtn = findViewById(R.id.buttonRegister);
        LogIn = findViewById(R.id.LogAcct2);

        //Firebase Auth
        auth = FirebaseAuth.getInstance();

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //Adding Event Listener to the Button Register, so that when user enter username, email and password, the register button grabs
        //all the information and then connects to firebase and store it there in   the database.
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username_text = userET.getText().toString();
                final String email_text = emailET.getText().toString();
                final String pass_text = passET.getText().toString();

               // String pass2_text = pass2ET.getText().toString();


                if ( TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text) || TextUtils.isEmpty(username_text) ) {
                    Toast.makeText(RegisterActivity.this, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
                }else if(pass_text.length() < 6){
                    Snackbar.make(findViewById(R.id.myLayout), "Your password must be at least 6 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }else {


                    exists = false;
                    //FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();

                            for (DataSnapshot user : userChildren){
                                Users u = user.getValue(Users.class);
                                //Log.d("username", u.getUsername() );
                                //Log.d("usernameCURRENT", username_text );

                                if(u.getUsername().equalsIgnoreCase(username_text)){
                                   // Snackbar.make(findViewById(R.id.myLayout), "Please choose another username", Snackbar.LENGTH_LONG).show();
                                    exists = true;
                                    break;
                                }
                            }

                            if(exists == true){
                                Snackbar.make(findViewById(R.id.myLayout), "Please choose another username", Snackbar.LENGTH_LONG).show();
                                return;

                            }else{
                                RegisterNow(username_text, email_text, pass_text);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                       /* RegisterNow(username_text, email_text, pass_text);
*/

                    }

                }


                /*else if(!pass_text.equals(pass2_text)){
                    Snackbar.make(findViewById(R.id.myLayout),"Both password fields must match", Snackbar.LENGTH_SHORT).show();
                }*/



            });



    }

    //if not final in front of username, it will be an error cox username needs to be passed as final.
    private void RegisterNow(final  String username, final String email, String password){
        //will deal with auth system of firebase by creating the user with email and password



        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    //Email Verificaition

                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(RegisterActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    //myRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    //HashMaps : the list will be stored in MyUsers and every user will be child inside ths node

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", username.toLowerCase());

                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                   FirebaseAuth.getInstance().signOut(); //register activity also sign in the user. Had to do this to make sure the user login again after the account registration.




                    //Opening the Main Activity after Successful Registration

                   myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                               /* Toast.makeText(RegisterActivity.this, "Successfully Signed up", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegisterActivity.this, SuccessSignUp.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();*/
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "ERRRORRRRR", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }else{


                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException invalidEmail)
                    {
                        Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                    } catch (FirebaseAuthUserCollisionException existEmail) {

                        Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        /*if (!auth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(RegisterActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }



                }


            }
        });


    }


}