package com.example.myapplication.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final int CAMERA_CODE_REQUEST = 891;
    private static final int CAMERA_CODE_REQUEST_PERM = 1764;
    private static final int STORAGE_REQUEST = 555;
    private static final int PICK_IMAGE_CODE_REQUEST = 2653;


    public static String TAG = "MyDebuggingTAG";

    final int CAMERA_REQUEST = 1;
    final int WRITE_PERMISSION_REQUEST = 2;
    final int SELECT_IMAGE = 3;
    final int CAMERA_REQUEST_SLIDER = 4;
    final int SELECT_IMAGE_SLIDER = 5;

    String storagePath = "Aqua/ProfilePics/";


    Context context;

    CircleImageView profileImage;
    TextView fullNameTv;

    //DatabaseReference databaseReference;

    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;

    StorageReference storageReference;

    String cameraPermissions[];
    String storagePermissions[];

    private Uri imageUri;
    private StorageTask uploadTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        showUserProfile(currentUser);
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        StorageReference profileRef = storageReference.child(currentUser.getUid()).child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this).load(uri).into(profileImage);
                //Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage = view.findViewById(R.id.profile_image);
        fullNameTv = view.findViewById(R.id.full_name_tv);


        Button signOutBtn = view.findViewById(R.id.sign_out_btn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                fullNameTv.setText(user.getFirstName() + " " + user.getLastName());
//                if (user.getImageUrl().equals("default")) {
//                    profileImage.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(getContext()).load(user.getImageUrl()).into(profileImage);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        return view;
    }

//    private void openImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, IMAGE_CODE_REQUEST);
//    }
//
//    private String getFileExtension(Uri uri) {
//        ContentResolver contentResolver = getContext().getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return MimeTypeMap.getFileExtensionFromUrl(contentResolver.getType(uri));
//    }
//
//    private void uploadImage() {
//        final ProgressBar pb = new ProgressBar(getContext());
//
//        if (imageUri != null) {
//            StorageReference fileReference = storageReference.child(System.currentTimeMillis())
//                    + "." + getFileExtension(imageUri);
//
//            uploadTask = fileReference.getFile(imageUri);
//            uploadTask.continueWith(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    return null;
//                }
//            })
//        }
//    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        // Extracting user reference from database for registered users

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    fullNameTv.setText(user.getFirstName() + " " + user.getLastName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_profile_image_layout);

        LinearLayout displayPic = bottomSheetDialog.findViewById(R.id.display_pic_layout);
        LinearLayout takePic = bottomSheetDialog.findViewById(R.id.take_pic_layout);
        LinearLayout chooseFromGallery = bottomSheetDialog.findViewById(R.id.choose_from_gallery_layout);
        LinearLayout deletePic = bottomSheetDialog.findViewById(R.id.delete_pic_layout);

        displayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
            }
        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture(CAMERA_REQUEST);
                // Saving in internal storage/Pictures, Default, therefore no need to requestany storage poermissions while taking a picture,
                // but only when accessing the storage, like the gallery...
                takePicture();
//                System.out.println(Arrays.toString(cameraPermissions));
//
//                if (!checkCameraPermissions()) {
//                    System.out.println("Does not have permission, requesting...");
//                    requestCameraPermissions();
//                } else {
//                    System.out.println("Have permission, taking a picture...");
//                    takePicture();
//                }

//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_CODE_REQUEST);

                bottomSheetDialog.dismiss();
            }
        });

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, PICK_IMAGE_CODE_REQUEST);

                bottomSheetDialog.dismiss();
            }
        });

        deletePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //deleteImage();

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

//    private void deleteImage() {
//        StorageReference fileRef = storageReference.child("Uploads").child(currentUser.getUid()).child("profile.jpg");
//
//
//        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(context, "Image has been uploaded successfully", Toast.LENGTH_SHORT).show();
//                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Glide.with(ProfileFragment.this).load(uri).into(profileImage);
//                        //Picasso.get().load(uri).into(profileImage);
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }


//    private Boolean checkStoragePermission() {
//        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//    }
//    private void requestStoragePermission() {
//        requestPermissions(storagePermission, STORAGE_REQUEST);
//    }


    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to firebase storage

        StorageReference fileRef = storageReference.child(currentUser.getUid()).child("profile.jpg");
        System.out.println(imageUri + "INSIDE INSIDE INSIDE UPLOAD FUNC ******&*&*&*&");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Image has been uploaded successfully", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ProfileFragment.this).load(uri).into(profileImage);
                        //Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private void takePicture(int requestCode) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "from");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestCode);
    }

//    private void askStoragePermissions(int requestCode) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int hasWritePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            int hasReadPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
//            if (hasWritePermission != PackageManager.PERMISSION_GRANTED && hasReadPermission != PackageManager.PERMISSION_GRANTED) { //no permissions
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
//            } else { //have permissions
//                //openGallery(requestCode);
//                System.out.println("s");
//            }
//        }
//    }
//
//
//    private void askCameraPermissions(int requestCode) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
//            boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//            if (result && result1) { //have permissions
//                takePicture(requestCode);
//
//
//            } else {  //no permissions
//                //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
//                requestPermissions(cameraPermission, CAMERA_CODE_REQUEST);
//            }
//        }
//    }


    private boolean checkCameraPermissions() {
        boolean cameraPermResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean extStoragePermResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return cameraPermResult && extStoragePermResult;
    }

    private void requestCameraPermissions() {
        System.out.println("Requesting CameraPermissions");


        requestPermissions(cameraPermissions, CAMERA_CODE_REQUEST_PERM);
        //requestPermissions(cameraPermissions, CAMERA_CODE_REQUEST);

        //request(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);

    }


    private void takePicture() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "My Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "My Description");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_CODE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "**************ON ACTIVITY RESULT************** " + requestCode);


        if (requestCode == PICK_IMAGE_CODE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            //profileImage.setImageURI(imageUri);

            uploadImageToFirebase(imageUri);
        } else if (requestCode == CAMERA_CODE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (imageUri != null)
                uploadImageToFirebase(imageUri);
            else {
                Toast.makeText(context, "A problem has been occurred, please try again later.", Toast.LENGTH_SHORT).show();
            }
//            String filePath = storagePath + "_" + currentUser.getUid();
//            StorageReference storageReference2nd = storageReference.child(filePath);
//            storageReference2nd.putFile(imageUri).
//
//            Uri imageUri = data.getData();
            //profileImage.setImageURI(imageUri);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "**************FRAGMENT: ON REQUEST PERMISSIONS RESULT************** " + requestCode);

//        if (requestCode == CAMERA_CODE_REQUEST_PERM) {
//            if (grantResults.length > 0) {
//                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                if (cameraAccepted && writeStorageAccepted) {
//                    takePicture();
//                } else {
//                    Toast.makeText(getActivity(), "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
//                }
//            }
////
////            }
////            break;
////            case STORAGE_REQUEST_CODE: {
////
////                if (grantResults.length > 0) {
////
////                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
////                    if (writeStorageAccepted) {
////                        pickFromGallery();
////                    } else {
////                        Toast.makeText(getActivity(), "Please enable storage permissions", Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////            break;
//        }
    }


}


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//
//        System.out.println("**************ON REQUEST PERMISSIONS RESULT************** " + requestCode);
//
//        switch (requestCode) {
//            case CAMERA_CODE_REQUEST: {
//                if (grantResults.length > 0) {
//                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (cameraAccepted && writeStorageAccepted) {
//                        takePicture();
//                    } else {
//                        Toast.makeText(getActivity(), "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
//                    }
//                }
////
////            }
////            break;
////            case STORAGE_REQUEST_CODE: {
////
////                if (grantResults.length > 0) {
////
////                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
////                    if (writeStorageAccepted) {
////                        pickFromGallery();
////                    } else {
////                        Toast.makeText(getActivity(), "Please enable storage permissions", Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////            break;
//            }
//        }
//
//
//    }
//}



/*

Note: If you are using the camera by invoking an existing camera app, your application does not need to request this permission.

You do not need any permission using ACTION_PICK, ACTION_GET_CONTENT or ACTION_OPEN_DOCUMENT.


https://medium.com/@bilalhameed0800/is-permission-required-for-android-capture-image-from-camera-and-gallery-intent-4812964e8c9a


// TODO: FIX https://stackoverflow.com/questions/71409859/mediastore-action-image-capture-crash-for-some-devices



 */
