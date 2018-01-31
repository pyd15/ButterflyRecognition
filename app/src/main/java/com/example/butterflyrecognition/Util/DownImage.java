package com.example.butterflyrecognition.Util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.example.butterflyrecognition.db.InfoDetail;

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
    private InfoDetail infoDetail;
    //    private static final String _URL="http://120.78.72.153:8080";
    private static final String _URL = "http://40.125.207.182:8080";

    private InputStream is;
    private FileOutputStream fileOutputStream;
    //    private File file=new File("G:\\resource.txt");
    //    private FileWriter fw;

    public DownImage(InfoDetail infoDetail) {
        this.infoDetail = infoDetail;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String imagePaths = (String) params[0];
        Log.d("ImagePaths", imagePaths);
        String pathlist = "";
        String imagepath = "";
        String[] strings = imagePaths.split(",");
        //        try {
        //            fw = new FileWriter(file, true);
        //            fw.append(infoDetail.getName()+":");
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        for (int i = 0; i < strings.length; i++) {
            Log.d("Image strings", strings[i]);
        }
        for (int i = 0; i < strings.length; i++) {
            imagepath = _URL + strings[i];
            Log.d("Imagepath", imagepath);
            //            String fileName = imagepath.substring(imagepath.lastIndexOf("/"));
            String fileName = infoDetail.getName() + i + ".jpg";
            Log.d("ImagefileName", fileName);
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File dir = new File(directory + "/btf/");
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (dir.exists() && dir.canWrite()) {
                File imageFile = new File(dir.getAbsolutePath() + "/" + fileName);//directory
                Log.d("file", imageFile.getName());
                imageFile.setReadable(true);
                imageFile.setWritable(true);
                if (imageFile.exists()) {
                    pathlist = pathlist + "," + imageFile.getPath();
                    continue;
                }
                try {
                    //加载一个网络图片
                    is = new URL(imagepath).openStream();
                    byte[] data = readInputStream(is);
                    fileOutputStream = new FileOutputStream(imageFile);
                    fileOutputStream.write(data);
                    fileOutputStream.close();
                    //                if (i==strings.length-1)
                    //                    fw.write(imageFile.getPath() + "\r\n---" + imageFile.length() / 8000 + "kb\r\n");
                    //                else
                    //                    fw.append()
                    //                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("FilePath", imageFile.getPath());
                pathlist = pathlist + "," + imageFile.getPath();
            }
        }
        //        try {
        //            fw.write("\r\n");
        //            fw.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

        Log.d("ImagePathlist", pathlist);
        //        String[] paths=pathlist.split(",");

        return pathlist;
        //        String url = "http://120.78.72.153:8080" + params[0];
        //        String fileName = url.substring(url.lastIndexOf("/"));
        //        String[] strings = fileName.split("/");
        //        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        //        File imageFile=new File(directory+fileName);
        //        if (imageFile.exists()) {
        //            Log.d("ImagePath","图片已存在！");
        //            return imageFile.getPath();
        //        }
        //        try {
        //            //加载一个网络图片
        //            InputStream is = new URL(url).openStream();
        //            //            bitmap = BitmapFactory.decodeStream(is);
        //            byte[] data = readInputStream(is);
        //            FileOutputStream fileOutputStream=new FileOutputStream(imageFile);
        //            fileOutputStream.write(data);
        //            fileOutputStream.close();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //        //        return bitmap;
        //        Log.d("FilePath",imageFile.getPath());
        //        return imageFile.getPath();
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
