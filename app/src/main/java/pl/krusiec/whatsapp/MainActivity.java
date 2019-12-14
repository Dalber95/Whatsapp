package pl.krusiec.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Boolean loginModeActive = false;

    public void redirectIfLoggedIn() {
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, UserListActivity.class);
            startActivity(intent);
        }
    }

    public void toggleLoginMode(View view) {
        Button loginSignUpButton = findViewById(R.id.loginSignUpButton);
        TextView toggleLoginModeTextView = findViewById(R.id.toggleLoginModeTextView);

        if (loginModeActive) {
            loginModeActive = false;
            loginSignUpButton.setText("Sign Up");
            toggleLoginModeTextView.setText("Or, Log In");
        } else {
            loginModeActive = true;
            loginSignUpButton.setText("Log In");
            toggleLoginModeTextView.setText("Or, Sign Up");
        }
    }

    public void signUpLogin(View view) {
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        final String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (loginModeActive) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        redirectIfLoggedIn();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                .child("email").setValue(email);
                        redirectIfLoggedIn();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        redirectIfLoggedIn();
    }
}
