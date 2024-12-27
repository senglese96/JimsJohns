package com.example.jimsjohns;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private boolean isCreating;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button submitButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameInput = findViewById(R.id.usernameText);
        passwordInput = findViewById(R.id.passwordText);
        emailInput = findViewById(R.id.emailText);
        submitButton = findViewById(R.id.submitButton);
    }

    public void onSwitchFlip(View v) {
        if(isCreating) {
            isCreating = false;
            usernameInput.setVisibility(View.INVISIBLE);
            submitButton.setText("Log In");
        } else {
            isCreating = true;
            usernameInput.setVisibility(View.VISIBLE);
            submitButton.setText("Create Account");
        }
    }

    public void onSubmitPress(View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Text Fields cannot be empty",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (isCreating) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User dataUser = new User();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    dataUser.setEmail(email);
                                    dataUser.setUsername(usernameInput.getText().toString());
                                    dataUser.setId(user.getUid());
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference("Users");
                                    databaseReference.child(dataUser.getId()).setValue(dataUser);

                                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    }
}