package com.example.ngokhai.photogalleryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Debug;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void FilterSearch(View v)
    {
        //Debug.startMethodTracing();
        EditText startDate = findViewById(R.id.editTextSDate);
        EditText endDate = findViewById(R.id.editTextEDate);
        //EditText tag = findViewById(R.id.editTextTag);
        Date parseSDate, parseEDate;
        try
        {
            parseSDate = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss").parse(startDate.getText().toString());
            parseEDate = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss").parse(endDate.getText().toString());
            String[] resultImg = SearchImg(parseSDate,parseEDate);
            String[] resultCap = SearchCap(parseSDate,parseEDate);
            final Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("01",resultImg);
            intent.putExtra("02",resultCap);
            setResult(RESULT_OK,intent);
            finish();
            /*View view = this.getCurrentFocus();
            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }*/
        }
        catch(Exception ex)
        {
            Toast.makeText(SearchActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
        }
        //Debug.stopMethodTracing();
    }

    private String[] SearchImg(final Date startDate, final Date endDate) throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File[] resultList = dir.listFiles();
        final String[] imageList = dir.list(new FilenameFilter()
        {
            String[] imgName;

            File newDir;
            @Override
            public boolean accept(File dir, String name)
            {
                imgName = name.toString().split("_");
                try
                {
                    Date imgDate = new SimpleDateFormat("ddMMyyyy-HHmmss").parse(imgName[1]);
                    if(name.endsWith(".jpg") && imgDate.after(startDate) && (imgDate.before(endDate)))
                    {
                        return true;
                    }
                    newDir = new File(dir.getAbsolutePath()+"/"+ name);
                    return newDir.isDirectory();
                }
                catch(Exception e)
                {
                    Toast.makeText(SearchActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
                }
            return false;
            }
        });
        //resultList = dir.listFiles();
        return imageList;
    }

    private String[] SearchCap(final Date startDate, final Date endDate) throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        final String[] captionList = dir.list(new FilenameFilter()
        {
            String[] capName;

            File newDir;
            @Override
            public boolean accept(File dir, String name)
            {
                capName = name.toString().split("_");
                try
                {
                    Date imgDate = new SimpleDateFormat("ddMMyyyy-HHmmss").parse(capName[1]);
                    if(name.endsWith(".txt") && imgDate.after(startDate) && (imgDate.before(endDate)))
                    {
                        return true;
                    }
                    newDir = new File(dir.getAbsolutePath()+"/"+ name);
                    return newDir.isDirectory();
                }
                catch(Exception e)
                {
                    Toast.makeText(SearchActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        //resultList = dir.listFiles();
        return captionList;
    }
}
