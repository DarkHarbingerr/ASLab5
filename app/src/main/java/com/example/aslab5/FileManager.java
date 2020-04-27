package com.example.aslab5;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;

import androidx.core.content.FileProvider;

public class FileManager {

    public static void OpenFile(Context context, String path)
    {
        File pdfFile = new File(path);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(FileProvider.getUriForFile(context, "com.example.aslab5", pdfFile), "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            context.startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean DeleteFile(Context context, String path)
    {
        File pdfFile = new File(path);
        boolean delete = false;
        if(pdfFile.exists()){
            delete = pdfFile.delete();
        }

        if(!delete){
            Toast.makeText(context, "File not deleted", Toast.LENGTH_SHORT).show();
        }

        return delete;
    }

}
