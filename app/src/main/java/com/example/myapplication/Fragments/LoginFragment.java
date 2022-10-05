package com.example.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    //boolean isAllFieldsValid = false;
    String TAG = "LoginFragment";

    Context context;

    EditText etEmail;
    EditText etPassword;

    Button regularLoginBtn;
    Button googleLoginBtn;
    Button facebookLoginBtn;
    Button twitterLoginBtn;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private FirebaseAuth firebaseAuth;


    final public int GOOGLE_SIGN_IN_REQUEST = 700;
    final public int FACEBOOK_SIGN_IN_REQUEST = 800;
    final public int TWITTER_SIGN_IN_REQUEST = 900;

    private final View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.regular_login_btn:
                    signInWithEmailAndPassword();
                    break;

                case R.id.google_login_btn:
                    signInWithGoogle();
                    break;

                case R.id.facebook_login_btn:
                    // trigger the callback defined in initFirebaseFacebookSignIn method inside OnCreate, by clicking the fb button
                    LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList("email", "public_profile"));
                    break;

                case R.id.twitter_login_btn:
                    initFirebaseTwitterSignIn();
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance(); // init firebase authentication
        initFirebaseGoogleSignIn();
        initFirebaseFacebookSignIn();
        //initFirebaseTwitterLogIn();
        //initFirebaseAuthState();

    }

    private void signInWithGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST);
    }

    private void signInWithEmailAndPassword() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Authentication{signInWithEmailAndPassword} succeed!");
                            //Toast.makeText(context, "Authentication{signInWithEmailAndPassword} succeed!", Toast.LENGTH_SHORT).show();

                            System.out.println("I am Inside LoginFragment");


                            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeFragment);


                        } else
                            //Toast.makeText(context, "Authentication{signInWithEmailAndPassword} failed!", Toast.LENGTH_SHORT).show();
                            System.out.println("Authentication{signInWithEmailAndPassword} failed!");
                    }
                });
    }

    interface OnLoginFragmentListener {
        void onLoginClicked(User user);

    }

    OnLoginFragmentListener loginCallback;
    CallbackManager callbackManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.email_et);
        etPassword = view.findViewById(R.id.password_et);
        regularLoginBtn = view.findViewById(R.id.regular_login_btn);
        googleLoginBtn = view.findViewById(R.id.google_login_btn);
        facebookLoginBtn = view.findViewById(R.id.facebook_login_btn);
        twitterLoginBtn = view.findViewById(R.id.twitter_login_btn);
        TextView signUpRef = view.findViewById(R.id.register_ref);

        regularLoginBtn.setOnClickListener(myOnClickListener);

        googleLoginBtn.setOnClickListener(myOnClickListener);

        facebookLoginBtn.setOnClickListener(myOnClickListener);

        twitterLoginBtn.setOnClickListener(myOnClickListener);

        signUpRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //comment this if you want to pass your result to the activity.
        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);

                // If we reach here, Google Sign In succeeded, then authenticate it with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getContext(), "Google Sign In Succeeded!", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // If we reach here, Google Sign In failed, update UI appropriately
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == FACEBOOK_SIGN_IN_REQUEST) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
//        else if (requestCode == TWITTER_SIGN_IN_REQUEST) {
//            .onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    //    private boolean checkAllFields() {
//
//    }

    private void initFirebaseGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        gsc = GoogleSignIn.getClient(getContext(), gso);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authenticate to Firebase using Google succeeded. Update UI with the signed-in user's data.
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Firebase & Google Authentication Succeeded!", Toast.LENGTH_SHORT).show();
                            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.homeFragment);

//                            Intent intent = new Intent(getContext(), MainActivity2.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user. (Firebase Authentication Phase failed!)
                            Toast.makeText(getActivity(), "Firebase & Google Authentication Failed!", Toast.LENGTH_SHORT).show();

                            // add more code for handling this case
                        }
                    }
                });
    }


    private void initFirebaseFacebookSignIn() {
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook", "Login Succeeded");
                firebaseAuthWithFacebook(loginResult.getAccessToken()); // handle facebook access token
            }

            @Override
            public void onCancel() {
                Log.d("Facebook", "Login Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook", "Login Failed");
            }
        });

    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authenticate to Firebase using Facebook succeeded. Update UI with the signed-in user's data.
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getContext(), "Firebase & Facebook Authentication Succeeded!", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();

                            //Intent intent = new Intent(getContext(), MainActivity2.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user. (Firebase Authentication Phase failed!)
                            Toast.makeText(getContext(), "Firebase & Facebook Authentication Failed!", Toast.LENGTH_SHORT).show();

                            // add more code for handling this case
                        }
                    }
                });
    }


    private void initFirebaseTwitterSignIn() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", "en");

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
                                    Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
        } else {
            firebaseAuth
                    .startActivityForSignInWithProvider(/* activity= */ getActivity(), provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();
                                    Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        }
    }
}
