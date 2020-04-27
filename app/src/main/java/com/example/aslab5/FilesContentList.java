package com.example.aslab5;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class FilesContentList extends AppCompatActivity {

    private ListView listView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        listView = findViewById(R.id.pdf_lv);
        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.setDividerHeight(15);

        listView.setAdapter(new CustomAdapter(this, getPDFs(), listView));

    }

    private ArrayList<PDFDocument> getPDFs() {
        ArrayList<PDFDocument> pdfDocs=new ArrayList<>();
        PDFDocument pdfDoc;
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)  + File.separator + "ASLab5PDF");

        if(path.exists())
        {
            File[] files = path.listFiles();

            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("pdf"))
                {
                    pdfDoc = new PDFDocument();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());

                    pdfDocs.add(pdfDoc);
                }

            }
        }
        return pdfDocs;
    }
}
