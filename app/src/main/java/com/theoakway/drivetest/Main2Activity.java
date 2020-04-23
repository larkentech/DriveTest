package com.theoakway.drivetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.theoakway.drivetest.Adapters.CompanyNameAdapter;
import com.theoakway.drivetest.Adapters.ItemListAdapter;
import com.theoakway.drivetest.HelperClasses.DriveFolder;
import com.theoakway.drivetest.HelperClasses.DriveServiceHelper;
import com.theoakway.drivetest.HelperClasses.DriveServiceHelperTwo;
import com.theoakway.drivetest.HelperClasses.GoogleDriveFileHolder;
import com.theoakway.drivetest.ModalClasses.CompanyModal;
import com.theoakway.drivetest.ModalClasses.ItemListModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.sephiroth.android.library.widget.HListView;

public class Main2Activity extends AppCompatActivity {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Drive mDrive;

    DriveServiceHelperTwo driveServiceHelper;
    private List<CompanyModal> list;
    HListView companyNamesList;
    CompanyNameAdapter adapter;

    //Items List
    ListView itemListView;
    List<ItemListModal> itemList;
    ItemListAdapter itemListAdapter;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching BillMaxo Data...");
        progressDialog.setCancelable(false);


        list = new ArrayList<>();
        requestSignIn();
        companyNamesList = findViewById(R.id.companyNamesList);
        adapter = new CompanyNameAdapter(Main2Activity.this,R.layout.single_company_name,list);
        companyNamesList.setAdapter(adapter);

        //Item List
        itemList = new ArrayList<>();
        itemListView  = findViewById(R.id.itemsList);
        itemListAdapter = new ItemListAdapter(Main2Activity.this,R.layout.single_item_list,itemList);
        itemListView.setAdapter(itemListAdapter);

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
                        GoogleAccountCredential credential = GoogleAccountCredential
                                .usingOAuth2(Main2Activity.this,Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccountName(googleSignInAccount.getAccount().name);
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("Resume")
                                .build();
                        driveServiceHelper = new DriveServiceHelperTwo(googleDriveService);
                        mDrive = googleDriveService;

                        progressDialog.show();
                        retrieveItemFile().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                itemListAdapter.notifyDataSetChanged();
                            }
                        });

                        retrieveFile().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });





    }

    public Task<String> retrieveFile(){
        return Tasks.call(executor, new Callable<java.lang.String>() {
            @Override
            public java.lang.String call() throws Exception {
                Drive.Files fieldsList = mDrive.files();
                FileList fileList = mDrive.files().list()
                        .setQ("mimeType='application/json'")
                        .execute();
                for(com.google.api.services.drive.model.File fs: fileList.getFiles()){
                    Log.v("TAg","Download=>"+fs.getId());
                    Log.v("TAg","FileNames=>"+fs.getName());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    mDrive.files().get(fs.getId())
                            .executeMediaAndDownloadTo(outputStream);
                    if (fs.getName().matches("BmCompanyList.json")){
                        Log.v("TAg","OutputStream=>"+outputStream);
                       readData(readJSON(outputStream));
                    }
                }
                return "Hello";
            }

        });
    }

    private void readData(String readJSON) {

        try {
            JSONArray array = new JSONArray(readJSON);
            Log.v("TAG","Array Length=>"+array.length());
            for (int i=0;i<array.length();i++){
                JSONObject companyObject = array.getJSONObject(i);
                String companyName = companyObject.getString("CompanyName");
                String companyGuid = companyObject.getString("CompanyGuid");
                Log.v("TAG","Company Names=>"+companyName);
                CompanyModal modal = new CompanyModal(companyName,companyGuid);
                list.add(modal);
            }

        } catch (JSONException e) {
            Log.e("TAG", "Problem parsing the earthquake JSON results", e);
        }
    }

    private String readJSON(ByteArrayOutputStream outputStream) throws IOException {

        ByteArrayInputStream inStream = new ByteArrayInputStream( outputStream.toByteArray() );
        StringBuilder output = new StringBuilder();
        if (inStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    public Task<String> retrieveItemFile(){
        return Tasks.call(executor, new Callable<java.lang.String>() {
            @Override
            public java.lang.String call() throws Exception {
                Drive.Files fieldsList = mDrive.files();
                FileList fileList = mDrive.files().list()
                        .setQ("mimeType='application/json'")
                        .execute();
                for(com.google.api.services.drive.model.File fs: fileList.getFiles()){
                    Log.v("TAg","Download=>"+fs.getId());
                    Log.v("TAg","FileNames=>"+fs.getName());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    mDrive.files().get(fs.getId())
                            .executeMediaAndDownloadTo(outputStream);
                    if (fs.getName().matches("ItemList.json")){
                        Log.v("TAg","OutputStream=>"+outputStream);
                        readItemData(readItemJSON(outputStream));
                    }
                }
                return "Hello";
            }

        });
    }

    private void readItemData(String readJSON) {

        try {
            JSONArray array = new JSONArray(readJSON);
            Log.v("TAG","Array Length=>"+array.length());
            for (int i=0;i<array.length();i++){
                JSONObject companyObject = array.getJSONObject(i);
                String barcode = companyObject.getString("Barcode");
                String name = companyObject.getString("ItemName");
                String uom = companyObject.getString("UOM");
                String cost = companyObject.getString("CostPrice");
                String retail = companyObject.getString("RetailPrice");
                ItemListModal modal = new ItemListModal(barcode,name,uom,cost,retail);
                itemList.add(modal);
            }

        } catch (JSONException e) {
            Log.e("TAG", "Problem parsing the earthquake JSON results", e);
        }
    }

    private String readItemJSON(ByteArrayOutputStream outputStream) throws IOException {

        ByteArrayInputStream inStream = new ByteArrayInputStream( outputStream.toByteArray() );
        StringBuilder output = new StringBuilder();
        if (inStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }




}
