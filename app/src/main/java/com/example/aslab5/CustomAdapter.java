package com.example.aslab5;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<PDFDocument> pdfDocs;
    ListView listView;

    public CustomAdapter(Context context, ArrayList<PDFDocument> pdfDocs, ListView listView) {
        this.context = context;
        this.pdfDocs = pdfDocs;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return pdfDocs.size();
    }

    @Override
    public Object getItem(int i) {
        return pdfDocs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.model,viewGroup,false);
        }

        final PDFDocument pdfDoc = (PDFDocument) this.getItem(i);

        TextView nameTxt= (TextView) view.findViewById(R.id.nameTxt);
        ImageView img= (ImageView) view.findViewById(R.id.pdfImage);
        Button btn = (Button) view.findViewById(R.id.delete_btn);

        nameTxt.setText(pdfDoc.getName());
        img.setImageResource(R.drawable.ic_picture_as_pdf_black_24dp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManager.DeleteFile(context, pdfDoc.getPath());
                pdfDocs.remove(i);
                CustomAdapter.this.notifyDataSetChanged();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.OpenFile(context, pdfDoc.getPath());
            }
        });
        return view;
    }
}
