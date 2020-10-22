package com.example.android.twende;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuth";
    private static final int RC_SIGN_IN = 100;

    private EditText phoneText;
    private Button submit;
    private TextView connecttosocial;
    String number;


    private FirebaseAuth firebaseAuth;
    CountryCodePicker ccp;
   private String verificationId;

    //private String phoneVerificationId;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent (SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            setContentView(R.layout.activity_signup);
        }
        getSupportActionBar().hide();

        submit = findViewById(R.id.submit);
        phoneText = findViewById(R.id.phoneText);
        connecttosocial = findViewById(R.id.connecttosocial);

        ccp= (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);


        //getcode

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phoneText.getText().toString().trim())) {

                    Toast.makeText(SignupActivity.this, "Phone Number is invalid", Toast.LENGTH_SHORT).show();

                } else {
                    String getPhone = ccp.getFullNumberWithPlus().replace("", "");
                    Authenticate(getPhone);

                }
            }




            });




//connecting through social media
        connecttosocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectToSocial();

            }
        });

    }


    private void connectToSocial() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                FirebaseUser user1 = firebaseAuth.getCurrentUser();
                databaseReference.child(user1.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "User data was saved", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "User data could not be saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

               Intent intent = new Intent(this, MainActivity.class);
             startActivity(intent);
                // ...
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                response.getError().getErrorCode();


            }
        }
    }
//authenticate the phone number
    private void Authenticate(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        Toast.makeText(SignupActivity.this, "confirmed", Toast.LENGTH_SHORT).show();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }
                    //code for sending verification code
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SignupActivity.this, "Verification has failed", Toast.LENGTH_SHORT).show();

                    }
                });        // OnVerificationStateChangedCallbacks


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            // ...
                        } else {

                            Toast.makeText(SignupActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}