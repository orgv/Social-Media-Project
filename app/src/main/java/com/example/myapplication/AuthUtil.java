package com.example.myapplication;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * provide same functionality for authentication with firebase
 */
public class AuthUtil {

    private static FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    CallbackManager callbackManager;


    final public int GOOGLE_SIGN_IN_REQUEST = 700;
    final public int FACEBOOK_SIGN_IN_REQUEST = 800;
    final public int TWITTER_SIGN_IN_REQUEST = 900;


    public static void signInWithEmailAndPasswordUtility(Fragment fragment, String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "Authentication{signInWithEmailAndPassword} succeed!");

                            // Navigate to main screen
                            //Intent intent = new Intent(fragment.getContext(), MainActivity2.class);
                            //fragment.startActivity(intent);
                            //finish();
                            // Navigate to main screen
                            //Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                            //startActivity(intent);
                            //finish();

                        } else
                            Toast.makeText(fragment.getActivity(), "Authentication{signInWithEmailAndPassword} failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void RegisterWithEmailAndPasswordUtility(Fragment fragment, String email, String firstName, String lastName, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(fragment.getContext(), "Created user successfully!", Toast.LENGTH_SHORT).show();

                            System.out.println(firstName);
                            System.out.println(lastName);

                            // Login and navigate to main screen
                            signInWithEmailAndPasswordUtility(fragment, email, password);

                        } else
                            Toast.makeText(fragment.getContext(), "Created user failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
