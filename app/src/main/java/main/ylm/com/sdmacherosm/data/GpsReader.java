package main.ylm.com.sdmacherosm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.ylm.com.sdmacherosm.R;
import main.ylm.com.sdmacherosm.enity.GpsPoint;
import main.ylm.com.sdmacherosm.enity.SDEdge;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by YLM on 2018/3/1.
 */

public class GpsReader {

    private static GpsReader instance = null;

    private List<GpsPoint> gpsList = new ArrayList<>();

    private Context context;

    public static GpsReader getInstance(Context context) {
        if (instance == null) {
            instance = new GpsReader(context);
        }
        return instance;
    }

    public GpsReader(Context context){
        this.context = context;
        read();
    }

    public void read(){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences("sdmacher", MODE_PRIVATE);
            String path = sharedPreferences.getString("gpsfilepath","");
            Log.d("test", path);
            File file = new File(path);
            InputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //跳过第一行
            String str = reader.readLine();
            while (str!=null){
                str = reader.readLine();
                if (str!=null){
                    String[] tmp = str.split(",");
                    if (tmp.length==5){
                        GpsPoint gps = new GpsPoint(967790112671L,Long.parseLong(tmp[4])
                        ,Double.parseDouble(tmp[0]),Double.parseDouble(tmp[1]),Double.parseDouble(tmp[3])
                        ,Integer.parseInt(tmp[2]));
                        gpsList.add(gps);
                    }else{
                        Log.e("DataReader HandleError:",str);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List getData(){
        return gpsList;
    }
}
