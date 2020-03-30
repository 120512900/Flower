package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.baidu.aip.imageclassify.*;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ////////////////////////
    private AipImageClassify aipImageClassify;
    private LodingView lodingView;
    public static final String APP_ID = "18788200";//你的APP_ID
    public static final String API_KEY = "tKHf5haqP6K0EGopsYC3ZKLe"; //你的API_KEY
    public static final String SECRET_KEY = "iOiTIAyD4D2KYl31sUv49iRn0bHGbp3c";//你的SECRET_KEY
    ////////////////////////
    private TextView tv;
    ///////////////////////
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    private String path0;
    private String path1;
    private Bitmap bitmap;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 在对sd卡进行读写操作之前调用这个方法 * Checks if the app has permission to write to device storage *
     * If the app does not has permission then the user will be prompted to grant permissions
     */

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);//缺少什么权限就写什么权限
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lodingView = new LodingView(this);
        tv = findViewById(R.id.tv);
        ImageButton takePhoto = (ImageButton) findViewById(R.id.take_photo);
        ImageButton chooseFromAlbum = (ImageButton) findViewById(R.id.choose_from_album);
        ImageButton history = (ImageButton) findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT < 24) {
                    path0 = outputImage.getPath();
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    path0 = outputImage.getPath();
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.cameraalbumtest.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }

        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Log.d("TAG", "onActivityResult: " + getContentResolver().openInputStream(imageUri));
                        //picture.setImageBitmap(bitmap);
                        lodingView.show();
                        askCameraAPI(bitmap);

                    } catch (Exception e) {
                        Log.d("TAG", "onActivityResult: ");
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        Log.d("TAG", "onActivityResult: ");
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);

            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                Log.d("TAG", "handleImageOnKitKat: " + uri.getAuthority());
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {

        Uri uri = data.getData();

        String imagePath = getImagePath(uri, null);

        displayImage(imagePath);

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void askCameraAPI(final Bitmap bitmap3) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] content = getBitmapByte(bitmap3);
                aipImageClassify = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                aipImageClassify.setConnectionTimeoutInMillis(2000);
                aipImageClassify.setSocketTimeoutInMillis(6000);
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("baike_num", "3");
                JSONObject res = aipImageClassify.plantDetect(content, options);
                try {
                    showCameResponse(res.toString(10));
                    lodingView.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ).start();
    }

    private void askAPI(final Bitmap bitmap3) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] content = getBitmapByte(bitmap3);
                aipImageClassify = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                aipImageClassify.setConnectionTimeoutInMillis(2000);
                aipImageClassify.setSocketTimeoutInMillis(6000);
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("baike_num", "3");
                JSONObject res = aipImageClassify.plantDetect(content, options);
                try {
                    showResponse(res.toString(10));
                    lodingView.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ).start();
    }

    private void displayImage(final String imagePath) {
        if (imagePath != null) {
            path1 = imagePath;
            final Bitmap bitmap2 = getBitmapFromUri(this, getImageContentUri(this, imagePath));
            picture.setImageBitmap(bitmap2);
            Toast toast = Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT);
            toast.show();
            lodingView.show();
            askAPI(bitmap2);
///////////////////////////////////////////
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCameResponse(final String response) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              // 在这里进行UI操作，将结果显示到界面上
                              String path1 = imageUri.toString();
                              //  tv.setText(String.format("%s%s", response, imageUri.toString()));
                              //Log.d("TAG", "00run: "+response);
                              Intent intent = new Intent(MainActivity.this, FlowerActivity.class);
                              intent.putExtra("version", "camera");
                              intent.putExtra("response", response);
                              intent.putExtra("uri", path1);
                              startActivity(intent);
                              //finish();
                              //Log.d("TAG", "run: "+response);
                          }
                      }
        );
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              // 在这里进行UI操作，将结果显示到界面上
                              // tv.setText(response);
                              //Log.d("TAG", "00run: "+response);
                              Intent intent = new Intent(MainActivity.this, FlowerActivity.class);
                              intent.putExtra("version", "choose");
                              intent.putExtra("response", response);
                              intent.putExtra("path", path1);
                              startActivity(intent);
                              // finish();
                              //Log.d("TAG", "run: "+response);
                          }
                      }
        );
    }

    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
