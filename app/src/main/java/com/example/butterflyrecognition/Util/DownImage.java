package com.example.butterflyrecognition.Util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Dr.P on 2017/11/6.
 */

public class DownImage extends AsyncTask {

    private ImageView imageView;

//    public DownImage(ImageView imageView) {
//        this.imageView = imageView;
//    }

    @Override
    protected Object doInBackground(Object[] params) {
        String url = "http://120.78.72.153:8080" + params[0];
        String fileName = url.substring(url.lastIndexOf("/"));
        String[] strings = fileName.split("/");
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File imageFile=new File(directory+fileName);
        if (imageFile.exists()) {
            Log.d("ImagePath","图片已存在！");
            return imageFile.getPath();
        }
        try {
            //加载一个网络图片
            InputStream is = new URL(url).openStream();
            //            bitmap = BitmapFactory.decodeStream(is);
            byte[] data = readInputStream(is);
            FileOutputStream fileOutputStream=new FileOutputStream(imageFile);
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        return bitmap;
        Log.d("FilePath",imageFile.getPath());
        return imageFile.getPath();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[2048];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    //    @Override
//    protected void onPostExecute(Object result) {
//        imageView.setImageBitmap((Bitmap)result);
//    }
}
