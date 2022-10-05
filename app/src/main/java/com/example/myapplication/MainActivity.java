package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private static final int CAMERA_CODE_REQUEST = 891;
    private static final int CAMERA_CODE_REQUEST_PERM = 1764;
    private static final int STORAGE_REQUEST = 555;
    private static final int PICK_IMAGE_CODE_REQUEST = 2653;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    FirebaseAuth.AuthStateListener authStateListener;

    DatabaseReference reference;

    BottomNavigationView bottomNavigationView;
    Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar_main));


//        if (FirebaseDatabase.getInstance() == null) {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.loginFragment || navDestination.getId() == R.id.registerFragment || navDestination.getId() == R.id.welcomeFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                    System.out.println("Special IF has been called");
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    System.out.println("Special ELSE has been called");
                }
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);


//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.nearbyFragment, R.id.messagesFragment, R.id.profileFragment, R.id.favoritesFragment).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.home:
//                        switchFragment(homeFragment);
//                        return true;
//                    case R.id.favorites:
//                        switchFragment(favoritesFragment);
//                        return true;
//                    case R.id.nearby:
//                        switchFragment(nearbyFragment);
//                        return true;
//                    case R.id.messages:
//                        switchFragment(messagesFragment);
//                        return true;
//                    case R.id.profile:
//                        switchFragment(profileFragment);
//                        return true;
//                }
//                return false;
//            }
//        });


//        loginFragment = new LoginFragment();
//        registerFragment = new RegisterFragment();
//
//        homeFragment = new HomeFragment();
//        favoritesFragment = new FavoritesFragment();
//        nearbyFragment = new NearbyFragment();
//        messagesFragment = new ChatsFragment2();
//        profileFragment = new ProfileFragment();
//
//        RelativeLayout rootLayout = findViewById(R.id.root_layout);
//
//        switchFragment(homeFragment);
//
//
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { // user signed in
                    Toast.makeText(MainActivity.this, "" + getCallingActivity() + " Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    System.out.println("I am Inside MainActivity");
                    navGraph.setStartDestination(R.id.homeFragment);
                    navController.setGraph(navGraph);

                    showHiddenViews();

                } else {
                    Toast.makeText(MainActivity.this, "Please Log In " + "" + getCallingActivity(), Toast.LENGTH_SHORT).show();
                    navGraph.setStartDestination(R.id.welcomeFragment);
                    // send some kind of var to unhide buttons
                    navController.setGraph(navGraph);
                    Log.e("Splash_Check_LOGGED_OUT", "Please Log In " + "" + getCallingActivity());
                }
            }
        };

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


    public void showHiddenViews() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void status(String status) {
        if (currentUser != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("status", status);

            reference.updateChildren(hashMap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }


}