package com.example.ngokhai.photogalleryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.ngokhai.photogalleryapp.Image;

public class MainActivity extends AppCompatActivity
{
    File currentImage;
    File currentCaption;
    File fileGallery;
    String[] imageList;
    String[] captionList;
    Date currentStamp;
    int imgPosition;
    int capPosition;
    Double longitude,latitude;
    List<Image> gallery;
    int index;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Debug.startMethodTracing();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery = new ArrayList<>();
        try
        {
            createGallery();
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
            readGallery();
            Toast.makeText(MainActivity.this, ""+index, Toast.LENGTH_SHORT).show();

        }
        catch(Exception e)
        {
            Toast.makeText(MainActivity.this, "No images!", Toast.LENGTH_SHORT).show();
        }
        //Debug.stopMethodTracing();
    }

    private void createGallery()
    {
        try
        {
            String fileName = "Gallery.txt";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File gallery = new File(storageDir,fileName);
            gallery.createNewFile();
            fileGallery = gallery;
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "Could not create Gallery File!", Toast.LENGTH_SHORT).show();

        }
    }

    private void readGallery()
    {
        String[] image;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileGallery));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null)
            {
                image = line.split(",");
                try
                {
                    gallery.add(new Image(Integer.parseInt(image[0]),image[1],image[2],Double.parseDouble(image[3]),Double.parseDouble(image[4])));
                }
                catch(Exception e)
                {
                    Toast.makeText(MainActivity.this, "Could not create add file to array!", Toast.LENGTH_SHORT).show();
                }
                i++;
            }
            index = i;
            reader.close();
        }
        catch(Exception e)
        {

        }
    }

    private void writeGallery()
    {
        if (fileGallery != null)
        {
            try
            {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileGallery, true));
                writer.write(gallery.get(index).getId() + ",");
                writer.write(gallery.get(index).getImageName() + ",");
                writer.write(gallery.get(index).getCapName() + ",");
                writer.write(gallery.get(index).getLatitude() + "," + gallery.get(index).getLongitude());
                writer.write('\n');
                writer.close();
                //Toast.makeText(MainActivity.this, " Saved!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(MainActivity.this, "Cant Write to Gallery!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Gallery Path!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImage() throws Exception
    {
        String[] time;
        ImageView capturedImage = findViewById(R.id.imageViewResult);
        EditText captionImage = findViewById(R.id.editTextCaption);
        TextView timeStamp = findViewById(R.id.textViewtime);
        StringBuilder text = new StringBuilder();
        try
        {
            time = currentImage.getName().split("_");
            try
            {
                currentStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").parse(time[1]);
            }
            catch (java.text.ParseException e)
            {

            }
            BufferedReader reader = new BufferedReader(new FileReader(currentCaption));
            String line;
            while ((line = reader.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            reader.close();
        }
        catch(Exception e)
        {

        }
        finally
        {
            captionImage.setText(text,TextView.BufferType.EDITABLE);
            capturedImage.setImageBitmap(BitmapFactory.decodeFile(currentImage.getAbsolutePath()));
            timeStamp.setText(currentStamp.toString());
        }
    }

    private File readImageFile() throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageList = dir.list(new FilenameFilter()
        {
            File image;
            @Override
            public boolean accept(File dir, String name)
            {
                if (name.endsWith(".jpg"))
                {
                    return true;
                }
                image = new File(dir.getAbsolutePath() + "/" + name);
                return image.isDirectory();
            }
        });
        return new File(dir, imageList[imgPosition]);
    }

    private File readCaptionFile() throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        captionList = dir.list(new FilenameFilter()
        {
            File caption;
            @Override
            public boolean accept(File dir, String name)
            {
                if (name.endsWith(".txt"))
                {
                    if (name.contains("Gallery"))
                    {

                    }
                    else
                    {
                        return true;
                    }
                }
                caption = new File(dir.getAbsolutePath() + "/" + name);
                return caption.isDirectory();
            }
        });
        return new File(dir, captionList[capPosition]);
    }

    private File createImageFile() throws Exception
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(new Date());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        File caption = File.createTempFile(
                imageFileName,
                ".txt",
                storageDir
        );
        currentImage = image;
        currentCaption = caption;
        return image;
    }
    public void SnapPicture(View v)
    {
        Debug.startMethodTracing();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch(Exception ex)
            {

            }
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ngokhai.photogalleryapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
        Debug.stopMethodTracing();
    }

    public void SaveCaption(View v)
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        TextView captionImage = findViewById(R.id.editTextCaption);
        if (currentCaption != null)
        {
            try
            {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(currentCaption));
                writer.append('\n');
                writer.append(captionImage.getText().toString());
                writer.close();
                Toast.makeText(MainActivity.this, "Caption Saved!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {

            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveLeft(View v)
    {
        try
        {
            if (imgPosition > 0 && capPosition > 0)
            {
                imgPosition = imgPosition - 1;
                capPosition = capPosition - 1;
            }
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "No Image Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveRight(View v)
    {
        try
        {
            if (imgPosition < imageList.length - 1 && capPosition < captionList.length - 1)
            {
                imgPosition = imgPosition + 1;
                capPosition = capPosition + 1;
            }
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "No Image Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void SearchActivity(View v)
    {
        final Intent intent = new Intent(this,SearchActivity.class);
        startActivityForResult(intent,1);
    }

    private double convertToDegree(String stringDMS)
    {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0/S1;

        result = Double.valueOf(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    }

    private void getLocation(File image)
    {
        try
        {
            ExifInterface exif = new ExifInterface(image.getAbsolutePath());

            String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if (latRef.equals("N")) {
                latitude = convertToDegree(lat);
            } else {
                latitude = 0 - convertToDegree(lat);
            }

            if (lngRef.equals("E")) {
                longitude = convertToDegree(lng);
            } else {
                longitude = 0 - convertToDegree(lng);
            }
            /*if (currentCaption != null)
            {
                try
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(currentCaption));
                    writer.write(longitude.toString());
                    writer.append("\n");
                    writer.append(latitude.toString());
                    writer.close();
                    Toast.makeText(MainActivity.this, "Location Saved!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Could not save location!", Toast.LENGTH_SHORT).show();
                }
            }*/
            //Toast.makeText(MainActivity.this, latitude + " " + longitude, Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(MainActivity.this, "Cant get location! Please enable GPS and Location Tags in the Camera App.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageList = data.getStringArrayExtra("01");
            captionList = data.getStringArrayExtra("02");
            for (int i = 0; i < imageList.length; i ++)
            {
                Toast.makeText(MainActivity.this, imageList[i], Toast.LENGTH_SHORT).show();
            }
        }*/
        try {
            //currentImage = readImageFile();
            //currentCaption = readCaptionFile();
            setImage();
            getLocation(currentImage);
            gallery.add(new Image(index,currentImage.getName(), currentCaption.getName(), latitude, longitude));
            writeGallery();
            readGallery();
            Toast.makeText(MainActivity.this, ""+ index, Toast.LENGTH_SHORT).show();
            imgPosition = index - 1;
            capPosition = index - 1;
            //imgPosition = imageList.length;
            //capPosition = captionList.length;

        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "Error OnActivityResult!", Toast.LENGTH_SHORT).show();
        }
    }
}
