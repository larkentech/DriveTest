package com.theoakway.drivetest;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Drive mDrive;
    private Context context;

    public DriveServiceHelper(Drive mDrive) {
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
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Drive.Files fieldsList = mDrive.files();
                FileList fileList = mDrive.files().list()
                        .setQ("mimeType='application/json'")
                        .execute();
                for(File fs: fileList.getFiles()){
                    Log.v("TAg","Download=>"+fs.getName());
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
}
