package com.theoakway.drivetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ammarptn.debug.gdrive.lib.GDriveDebugViewActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.theoakway.drivetest.HelperClasses.DriveFolder;
import com.theoakway.drivetest.HelperClasses.DriveServiceHelper;
import com.theoakway.drivetest.HelperClasses.GoogleDriveFileHolder;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DriveServiceHelper driveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

        if(account == null){
            requestSignIn();
        }
        else {

            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccountName(account.getAccount().name);
            com.google.api.services.drive.Drive googleDriveService =
                    new com.google.api.services.drive.Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("AppName")
                            .build();
            driveServiceHelper = new DriveServiceHelper(googleDriveService);
            uploadPdfFile();
           // driveServiceHelper.createFolder("Hello Man",null);
            driveServiceHelper.createFilePickerIntent();
            File file = new File("/storage/emulated/0/");
            GoogleDriveFileHolder googleDriveFileHolder  = new GoogleDriveFileHolder();
            googleDriveFileHolder = driveServiceHelper.searchFile("BmCompanyList",DriveFolder.TYPE_JSON).getResult();
            driveServiceHelper.downloadFile(file,googleDriveFileHolder.getId());
           
        }



    }

    private void requestSignIn() {

        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this,gso);
        startActivityForResult(client.getSignInIntent(),400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         switch (requestCode){
             case 400:
                 if (resultCode == RESULT_OK){
                     handleSignInIntent(data);
                 }
                 break;
         }
    }

    private void handleSignInIntent(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                       Log.v("TaG","Token ID=>"+ googleSignInAccount.getIdToken());
                        GoogleAccountCredential credential = GoogleAccountCredential
                                .usingOAuth2(MainActivity.this,Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccountName(googleSignInAccount.getAccount().name);
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("Resume")
                                .build();
                        driveServiceHelper = new DriveServiceHelper(googleDriveService);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void uploadPdfFile()
    {
        Intent openActivity = new Intent(MainActivity.this, GDriveDebugViewActivity.class);
        startActivity(openActivity);
    }

}
