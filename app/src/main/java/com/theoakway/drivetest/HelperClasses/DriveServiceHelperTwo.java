package com.theoakway.drivetest.HelperClasses;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.theoakway.drivetest.ModalClasses.CompanyModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelperTwo {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Drive mDrive;
    private Context context;

    public DriveServiceHelperTwo(Drive mDrive) {
        this.mDrive = mDrive;
    }

    public Task<String> createFilePDF(final String path){
        return Tasks.call(executor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                File fileMetaData = new File();
                fileMetaData.set("MyPDF", 0);
                java.io.File filePath = new java.io.File(path);
                FileContent mediaContent = new FileContent("application/pdf", filePath);
                File myFile = null;
                myFile = mDrive.files().create(fileMetaData,mediaContent)
                        .execute();
                if (myFile == null)
                {
                }
                return myFile.getId();
            }
        });
    }

    public Task<String> retrieveFile(){
        return Tasks.call(executor, new Callable<String>() {
            @Override
            public String call() throws Exception {
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



    public String loadJSONFromAsset(ByteArrayOutputStream outputStream) {
        String json = null;
        try {
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public List<CompanyModal> getCompanyNames() throws IOException {
        List<CompanyModal> list = new ArrayList<>();
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
            }

        }
        return list;

    }

    private List<CompanyModal> readData(String readJSON) {

        List<CompanyModal> list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(readJSON);
            for (int i=0;i<array.length();i++){
                JSONObject companyObject = array.getJSONObject(i);
                String companyName = companyObject.getString("CompanyName");
                String companyGuid = companyObject.getString("CompanyGuid");
                Log.v("TAG","Company Names=>"+companyName);
                CompanyModal modal = new CompanyModal(companyName,companyGuid);
            }

        } catch (JSONException e) {
            Log.e("TAG", "Problem parsing the earthquake JSON results", e);
        }

        return list;
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



}
