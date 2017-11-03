package com.gursimran.shopviz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gursimran.shopviz.R;
import com.gursimran.shopviz.Util.PermissionUtils;
import com.gursimran.shopviz.Util.util;
import com.gursimran.shopviz.modal.ChatMessage;
import com.gursimran.shopviz.modal.chat_rec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AIListener {
    public static final String FILE_NAME = "temp.jpg";
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn, uploadImage;
    DatabaseReference ref;
    FirebaseRecyclerAdapter<ChatMessage, chat_rec> adapter;
    String UserIdFirebase = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Boolean flagFab = true;

    private AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(getApplicationContext(), UserIdFirebase, Toast.LENGTH_LONG).show();
        intit();

    }

    void intit() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

//UI
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        addBtn = findViewById(R.id.addBtn);
        uploadImage = findViewById(R.id.uploadImage);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);

        /*
        * API.AI Configuration
        * */
        final AIConfiguration config = new AIConfiguration(util.DflowKey,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
   /*
        * API.AI Inintializing AIService with the above configuration
        * */
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();

        //UPLOAD IMAGE

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();



            }});
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    ChatMessage chatMessage = new ChatMessage(message, UserIdFirebase); //HERE CHANGE USER TO User.getUserName
                    ref.child("chat").child(UserIdFirebase).push().setValue(chatMessage); //change chat_user1 to Userid Chat //SENT MESSAGES

                    aiRequest.setQuery(message);
                    new AsyncTask<AIRequest, Void, AIResponse>() {

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(AIResponse response) {
                            if (response != null) {

                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                ChatMessage chatMessage = new ChatMessage(reply, "bot");
                                ref.child("chat").child(UserIdFirebase).push().setValue(chatMessage);  //RESPONSE FROM DIALOGUEFLOW
                            }
                        }
                    }.execute(aiRequest);
                } else {
                    aiService.startListening();
                }

                editText.setText("");

            }
        });
// CHANGING IMAGE WHILE WRITING
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView) findViewById(R.id.fab_img); //IMAGE IS BEING CHANGED (MIC TO SEND)
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length() != 0 && flagFab) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img1);
                    flagFab = true;

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Change ref.child("chat")) to chat_userid to get users chat
        adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(ChatMessage.class, R.layout.msglist, chat_rec.class, ref.child("chat").child(UserIdFirebase)) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) { //POPULATING THE msglLIST xml file

                if (model.getMsgUser().equals(UserIdFirebase)) { //STRING>contains user


                    viewHolder.rightText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                } else {
                    viewHolder.leftText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };
        //UPDATING ACCORDING TO NEW DATA
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);

                }

            }
        });

        recyclerView.setAdapter(adapter);
    }


    //BUTTON ANIMATION

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    /*
    * CAMERA FUNCTIONALITY
    *
    * ///////////////////////////////////////
    * ///////////////////////////////////////
    * ///////////////////////////////////////
    *
    *
    * */
    //CAMERA FUNCTIONS
    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//Adding permissions to intent https://developer.android.com/reference/android/support/v4/content/FileProvider.html
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST); //ACTIVITY gallery chooser started to get  photo not onActivityREsult is called
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
            // Log.d(TAG, "URI PHOTO*********** "+photoUri);

        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callFirebaseStorageAndCloudSight(bitmap);
                // mMainImage.setImageBitmap(bitmap); SETTING IMAGE ON TEXT VIEW!!

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callFirebaseStorageAndCloudSight(final Bitmap bitmap) throws IOException {

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog dialog;

            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(MainActivity.this);
                Log.d(TAG, "onPreExecute: PREEXECUTOIN");
                dialog.setMessage("Upoading...");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                StorageReference imagesReference = storageReference.child("images/" + UserIdFirebase + "/" + format);
                // Switch text to loading

                //mImageDetails.setText(R.string.loading_message); TEXT VIEW IMAGE
                // Toast.makeText(getApplicationContext(), "Firebase Storage Called", Toast.LENGTH_LONG).show();


                // Convert the bitmap to a JPEG
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                //Uploading File to Firebase
                UploadTask uploadTask = imagesReference.putBytes(imageBytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getApplicationContext(), "Image Uploaded " + downloadUrl, Toast.LENGTH_SHORT).show();
                    }


                });

                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                Log.d(TAG, "onPostExecute: POSTEXECUTE");

                if (dialog != null) {
                    dialog.dismiss();
                }


            }
        }.execute();

    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    /*CAMERA FUNCTIONALITY OVER
    *
    *
    *
    * ///////////////////////////
    *
    *
    * */
//RESPONSE FOR RECORDING
    //GETTING THE RESPONSE FROM SERVER(API.AI) and saving on firebase https://github.com/dialogflow/dialogflow-android-client
    @Override
    public void onResult(ai.api.model.AIResponse response) {


        Result result = response.getResult();

        String message = result.getResolvedQuery();
        ChatMessage chatMessage0 = new ChatMessage(message, UserIdFirebase);
        ref.child("chat").child(UserIdFirebase).push().setValue(chatMessage0);


        String reply = result.getFulfillment().getSpeech();
        ChatMessage chatMessage = new ChatMessage(reply, "bot");
        ref.child("chat").child(UserIdFirebase).push().setValue(chatMessage);


    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


}







