package main.ylm.com.sdmacherosm;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import android.database.sqlite.SQLiteDatabase;
import cn.ittiger.database.SQLiteDB;
import cn.ittiger.database.SQLiteDBConfig;
import cn.ittiger.database.SQLiteDBFactory;
import cn.ittiger.database.listener.IDBListener;
import cn.ittiger.database.annotation.PrimaryKey;
import cn.ittiger.database.annotation.Column;

import main.ylm.com.sdmacherosm.SDMacher.CalculateNode;
import main.ylm.com.sdmacherosm.common.CoordinateTransformUtil;
import main.ylm.com.sdmacherosm.data.DataReader;
import main.ylm.com.sdmacherosm.data.GpsReader;
import main.ylm.com.sdmacherosm.enity.GpsPoint;
import main.ylm.com.sdmacherosm.enity.SDEdge;
import main.ylm.com.sdmacherosm.enity.SearchNode;

public class MainActivity extends AppCompatActivity {

    public AMapLocationClient mLocationClient = null;

    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器 `
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                    //update(""+amapLocation.getLatitude(),""+amapLocation.getLongitude(),
             //               ""+amapLocation.getSpeed(),""+amapLocation.getBearing());
                    Log.d("ylm","lat:"+amapLocation.getLatitude()+"lon:"+amapLocation.getLongitude()+
                    "getSpeed:"+amapLocation.getSpeed()+"dirction:"+amapLocation.getBearing());
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private static final int MSG_READ = 0;//读取到蓝牙消息

    private static final int REQUEST_ENABLE_BT = 991;

    private static final int FILE_SELECT_CODE = 19;
    private static final int GPS_FILE_SELECT_CODE = 29;


    private BluetoothAdapter mBluetoothAdapter;

    private MapView mMapView = null;
    private AMap aMap;

    private ArrayList<LatLng> geoPoints = new ArrayList<>();
    private ArrayList<GpsPoint> old_gpsPoints = new ArrayList<>();
    private List<GpsPoint> data;
    private ArrayList<GpsPoint> bldata;

    private SQLiteDB db;

    private SQLiteDBConfig config;

    private int count = 0;


    private boolean isTest = true;

    private double lastedgenode1_lat = 0;

    private TextView textView;

    private TextView chooseFile;

    private TextView chooseGpsFile;

    private TextView latTextView;

    private TextView lonTextView;

    private TextView speedTextView;

    private TextView dircTextView;

    private TextView compressTextView;

    private int totalEdgeNum;

    private int copressEdgeNum;

    private long lastTime = 0;

    private long time = 0;

    private DataReader dataReader;

    private ConnectedThread connectedThread;

    private DbThread dbThread;




    //一、生成的zip解压后改名再生成zip，只要里面的一个文件夹生成zip，放到sdcard\osmdroid下面

    //二、代码上不能完全按照官网的代码，增加map.setTileSource(TileSourceFactory.MAPNIK);
    // 删掉map.setTileSource(new XYTileSource（））这一句

    //删掉缓存文件，即tiles文件下的所有。

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_READ:
                    if(!isTest){
//                        Location location;
                        bldata = parseGpsData((String)msg.obj);
                        if(!bldata.isEmpty()){
                            bluetoothSDMacher(bldata);


//                            Log.d("test", "The size of bldata is " + bldata.size());
//                            ArrayList<SDEdge> result = bluetoothSDMacher(bldata);
//                            location = new Location(1, bldata.get(0).timestamp, bldata.get(0).timestamp, bldata.get(0).latitude, bldata.get(0).longitude, bldata.get(0).speed, bldata.get(0).headingDirection);
//                            if(!result.isEmpty())
//                            {
//                                location.setRoute(result.get(0).node1_lon, result.get(0).node1_lat, result.get(0).node2_lon, result.get(0).node2_lat);
//                            }
//                            long time_insert = System.currentTimeMillis() / 1000l;
//                            double time_insert2 = Math.floor(time_insert);
//                            location.setTime_insert((long) time_insert2);
//                            if(db.save(location) > 0){
//                                Log.d("test", "Database save successfully.");
//                            }
                        }
                    }
                    break;
            }
        }
    };

    final Handler handler = new Handler(){          // handle
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    sdMacher();
                    break;
            }
            super.handleMessage(msg);
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsRequest();
        String str = sHA1(this);
        Log.d("zxc",str);

        Context ctx = getApplicationContext();
        setContentView(R.layout.activity_main);
        dataReader = DataReader.getInstance(this);
        mMapView = (MapView) findViewById(R.id.map);
        chooseFile = (TextView)findViewById(R.id.choose);
        chooseGpsFile = (TextView)findViewById(R.id.chooseGps);
        latTextView = (TextView)findViewById(R.id.latitude);
        lonTextView = (TextView)findViewById(R.id.longitude);
        compressTextView = (TextView)findViewById(R.id.copress);
        speedTextView = (TextView)findViewById(R.id.speed);
        dircTextView = (TextView)findViewById(R.id.direction);


        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        chooseGpsFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGpsFileChooser();
            }
        });



        config = new SQLiteDBConfig(this, "/", "Result.db");
//        dbThread = new DbThread();
//        dbThread.start(config);
        initData();

        drawMap();


        BLE();

//        sdMacher();

        if(isTest){
            new Thread(new MyThread()).start();
        }





//        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
//        mLocationClient.setLocationListener(mLocationListener);

//        mLocationOption = new AMapLocationClientOption();
//        mLocationClient.startLocation();
    }


    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
    }

    private void initData(){
        data = GpsReader.getInstance(this).getData();
    }

    private void sdMacher(){
        Log.d("test","sdmacher");
        SharedPreferences sharedPreferences = getSharedPreferences("sdmacher", MODE_PRIVATE);
        String path = sharedPreferences.getString("filepath","");
        String gpspath = sharedPreferences.getString("gpsfilepath","");
        Log.d("test", path);
        Log.d("test", gpspath);

        if (!path.isEmpty()&&!gpspath.isEmpty()){
            List<GpsPoint> tmp = new ArrayList<>();
            int flag = 0;
            for (int i = count;i<data.size();i++){
                count++;
//                Log.d("test",""+i+"flag:"+flag);
                GpsPoint gps = data.get(i);
                if (gps.latitude>dataReader.getLatMax()||gps.latitude<dataReader.getLatMin()||
                        gps.longitude>dataReader.getLonMax()||gps.longitude<dataReader.getLonMin()){
                    Log.d("test","超出范围");
                    continue;
                }
                flag++;
                long time = gps.timestamp - lastTime;
                lastTime = gps.timestamp;
                if (time>5*60){
                    tmp.clear();
                    geoPoints.clear();
                    Log.d("test","time too long");
                }
                tmp.add(gps);
                if (flag==10){
                    for (GpsPoint gpsPoint : tmp){
                        //drawPoint(gpsPoint.latitude,gpsPoint.longitude);
                    }
                    CalculateNode calculateNode = new CalculateNode(this,tmp);
                    LinkedHashSet<SearchNode> result = calculateNode.getRoad();
                    ArrayList<SDEdge> compress = calculateNode.getCompressResult();
                    Iterator it =  result.iterator();
                    while (it.hasNext()){
                        SearchNode sn = (SearchNode) it.next();
                        drawLine(sn.latitude,sn.longitude);
                    }
                    GpsPoint showGps = tmp.get(tmp.size()-1);
                    long time2 = tmp.get(0).timestamp;
                    tmp.clear();
                    byte[] send_msg;
                    StringBuilder send_msg2 = new StringBuilder();
                    String s;
                    for (SDEdge edge : compress){
                        Log.d("ylm","%%%");
                        drawLineByEdge(edge.node1_lat, edge.node1_lon, edge.node2_lat, edge.node2_lon);
                        if(lastedgenode1_lat == 0)
                        {
                            if(send_msg2.length() == 0){
                                send_msg2 = new StringBuilder(String.valueOf(time2) + "," + String.valueOf(edge.node1_lon) + "," + String.valueOf(edge.node1_lat) + "," + String.valueOf(edge.node2_lon) + "," + String.valueOf(edge.node2_lat));
                                lastedgenode1_lat = edge.node1_lat;
                            }
                            else{
                                send_msg2.append(";").append(String.valueOf(edge.node1_lon)).append(",").append(String.valueOf(edge.node1_lat)).append(",").append(String.valueOf(edge.node2_lon)).append(",").append(String.valueOf(edge.node2_lat));
                                lastedgenode1_lat = edge.node1_lat;
                            }
                            Log.d("test", String.valueOf(edge.edge_id));
                            s = String.valueOf(edge.node1_lon) + "," + String.valueOf(edge.node1_lat) + "," + String.valueOf(edge.node2_lon) + "," + String.valueOf(edge.node2_lat);
                            TxtThread txtThread = new TxtThread();
                            txtThread.write(s);

                        }
                        if(edge.node1_lat != lastedgenode1_lat)
                        {
                            if(send_msg2.length() == 0){
                                send_msg2 = new StringBuilder(String.valueOf(time2) + "," + String.valueOf(edge.node1_lon) + "," + String.valueOf(edge.node1_lat) + "," + String.valueOf(edge.node2_lon) + "," + String.valueOf(edge.node2_lat));
                                lastedgenode1_lat = edge.node1_lat;
                            }
                            else{
                                send_msg2.append(";").append(String.valueOf(edge.node1_lon)).append(",").append(String.valueOf(edge.node1_lat)).append(",").append(String.valueOf(edge.node2_lon)).append(",").append(String.valueOf(edge.node2_lat));
                                lastedgenode1_lat = edge.node1_lat;
                            }
                            Log.d("test", String.valueOf(edge.edge_id));
                            s = String.valueOf(edge.node1_lon) + "," + String.valueOf(edge.node1_lat) + "," + String.valueOf(edge.node2_lon) + "," + String.valueOf(edge.node2_lat);
                            TxtThread txtThread = new TxtThread();
                            txtThread.write(s);
                        }

                    }
                    send_msg = send_msg2.toString().getBytes();
                    Log.d("test", send_msg2.toString());
                    if (geoPoints.size()!=0){
                        LatLng latLngLast = geoPoints.get(geoPoints.size()-1);
                        geoPoints.clear();
                        geoPoints.add(latLngLast);
                    }
                    copressEdgeNum+=compress.size();
                    totalEdgeNum+=10;
                    update(showGps.latitude,showGps.longitude,showGps.speed,showGps.headingDirection);
                    break;
                }

            }
        }else {
            Log.d("test","未选择文件");
            if(path.isEmpty())
                Log.d("test", "path is empty.");
            else
                Log.d("test", "gpspath is empty.");
            Toast.makeText(this,"未选择文件",Toast.LENGTH_LONG);
        }
    }

    private void bluetoothSDMacher(ArrayList<GpsPoint> data){
        SharedPreferences sharedPreferences = getSharedPreferences("sdmacher", MODE_PRIVATE);
        String path = sharedPreferences.getString("filepath","");
        if (!path.isEmpty()) {
            CalculateNode calculateNode = new CalculateNode(this, data);
            LinkedHashSet<SearchNode> result = calculateNode.getRoad();
            ArrayList<SDEdge> compress = calculateNode.getCompressResult();
            Iterator it = result.iterator();
            while (it.hasNext()) {
                SearchNode sn = (SearchNode) it.next();
                Log.d("ylm", "lat:" + sn.latitude + "lon:" + sn.longitude);
                drawLine(sn.latitude, sn.longitude);
            }

            long time2 = data.get(data.size() - 1).timestamp;
            byte[] send_msg;
            StringBuilder send_msg2 = new StringBuilder();

            for (SDEdge edge : compress) {
                Log.d("ylm", "%%%");
                drawLineByEdge(edge.node1_lat, edge.node1_lon, edge.node2_lat, edge.node2_lon);
                if (lastedgenode1_lat == 0) {
                    copressEdgeNum++;
                }
                else if (edge.node1_lat != lastedgenode1_lat) {
                    copressEdgeNum++;
                }
                lastedgenode1_lat = edge.node1_lat;
                if (send_msg2.length() == 0) {
                    send_msg2 = new StringBuilder(String.valueOf(time2) + "," + String.valueOf(edge.node1_lon) + "," + String.valueOf(edge.node1_lat) + "," + String.valueOf(edge.node2_lon) + "," + String.valueOf(edge.node2_lat));
                } else {
                    send_msg2.append(";").append(String.valueOf(edge.node1_lon)).append(",").append(String.valueOf(edge.node1_lat)).append(",").append(String.valueOf(edge.node2_lon)).append(",").append(String.valueOf(edge.node2_lat));
                }
            }
            send_msg = send_msg2.toString().getBytes();
            Log.d("test", send_msg2.toString());
//            connectedThread.write(send_msg);
            Log.d("ylm", "          ");
            if (geoPoints.size() != 0) {
                LatLng latLngLast = geoPoints.get(geoPoints.size() - 1);
                geoPoints.clear();
                geoPoints.add(latLngLast);
            }
            GpsPoint showGps = data.get(data.size()-1);
            totalEdgeNum+=1;
            update(showGps.latitude,showGps.longitude,showGps.speed,showGps.headingDirection);
            dbThread = new DbThread();
            dbThread.start(config);
            dbThread.write(bldata, compress);
        }
    }

    private ArrayList<GpsPoint> parseGpsData(String data){
        ArrayList<GpsPoint> result = new ArrayList<>();
        Log.d("test","handler:"+data);
        String[] gps = data.split(";");
        for (String g:gps){
            String[] gpsdata = g.split(",");
            if(gpsdata.length != 5) return result;
            String temp = gpsdata[1];
            gpsdata[1] = gpsdata[2];
            gpsdata[2] = temp;
//            gpsdata[1] = "29.559587";
//            gpsdata[2] = "106.536193";
//            gpsdata[3] = "18.2047";
//            gpsdata[4] = "217";

            if (gpsdata[0].isEmpty() | gpsdata[1].isEmpty() | gpsdata[2].isEmpty() | gpsdata[3].isEmpty() | gpsdata[4].isEmpty())return result;
            if(Double.parseDouble(gpsdata[1]) == 0.0)return result;
            time = Long.valueOf(gpsdata[0]);

            GpsPoint gpsPoint = new GpsPoint(1,Long.valueOf(gpsdata[0]),Double.parseDouble(gpsdata[1]),Double.parseDouble(gpsdata[2]),
                    Double.parseDouble(gpsdata[3]), (int) Double.parseDouble(gpsdata[4]));
            Log.d("test",Long.valueOf(gpsdata[0])+" "+Double.parseDouble(gpsdata[1])+" "+Double.parseDouble(gpsdata[2])+" "+
                    Double.parseDouble(gpsdata[3])+" "+ (int) Double.parseDouble(gpsdata[4]));
            if (gpsPoint.latitude>dataReader.getLatMax()||gpsPoint.latitude<dataReader.getLatMin()||
                    gpsPoint.longitude>dataReader.getLonMax()||gpsPoint.longitude<dataReader.getLonMin()){
                Log.d("test","超出范围");
            }
            else{
                old_gpsPoints.add(gpsPoint);
            }
        }
        if (old_gpsPoints.size() >= 11)
        {
            for(int i = 11;i>0;i--)
            {
                result.add(old_gpsPoints.get(old_gpsPoints.size()-i));
            }
            old_gpsPoints = result;
        }
        else
        {
            result = old_gpsPoints;
        }



        return result;
    }

    private void BLE(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d("test","Device does not support Bluetooth ");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            Log.d("test","bluetooth is opened");
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")){
                    new Thread(new ClientThread(device)).start();
//                    new AcceptThread().start();
                }
                // Add the name and address to an array adapter to show in a ListView
                Log.d("test","name:"+device.getName()+" address:"+device.getAddress());
            }
        }


    }

    private class Location {
        private long id;

        @PrimaryKey(isAutoGenerate=true)
        private long number;


        private long timestamp;
        private long time_insert;
        private double latitude;
        private double longitude;
        private double speed;
        private long headingDirection;
        private double node1_lon;
        private double node1_lat;
        private double node2_lon;
        private double node2_lat;

        public Location() {
        }

        public Location(long id, long timestamp, long time_insert, double latitude, double longitude, double speed, long headingDirection) {
            super();
            this.id = id;
            this.timestamp = timestamp;
            this.time_insert = time_insert;
            this.latitude = latitude;
            this.longitude = longitude;
            this.speed = speed;
            this.headingDirection = headingDirection;
        }

        public void setLocation(long id, long timestamp, long time_insert, double latitude, double longitude, double speed, long headingDirection) {
            this.id = id;
            this.timestamp = timestamp;
            this.time_insert = time_insert;
            this.latitude = latitude;
            this.longitude = longitude;
            this.speed = speed;
            this.headingDirection = headingDirection;
        }

        public void setRoute(double node1_lon, double node1_lat, double node2_lon, double node2_lat) {
            this.node1_lon = node1_lon;
            this.node1_lat = node1_lat;
            this.node2_lon = node2_lon;
            this.node2_lat = node2_lat;
        }

        public void setTime_insert(long time_insert) {
            this.time_insert = time_insert;
        }


    }

    private void manageReadConnectedSocket(BluetoothSocket socket){
        Log.d("test","manage ble socket");
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    private class TxtThread extends  Thread {


        public void write(String s) {
            StringBuilder contents = new StringBuilder();
            File root = Environment.getExternalStorageDirectory();
            File logFile = new File(root, "result.txt");
            if(!logFile.exists())
            {
                try
                {
                    logFile.createNewFile();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(root.canWrite())
            {
                try {
//                    BufferedReader input =  new BufferedReader(new FileReader(logFile));
//                    String line = null;
//                    while (( line = input.readLine()) != null){
//                        contents.append(line);
//                        contents.append("\n");
//                    }
//                    input.close();
//                    FileWriter logWriter = new FileWriter(logFile);
//                    BufferedWriter out = new BufferedWriter(logWriter);
//                    out.write(contents.toString());////<---HERE IS MY QUESTION
//                    out.newLine();
//                    out.write(s);
//                    out.newLine();
//                    out.close();
                    BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
                    out.write(s);
                    out.newLine();
                    out.flush();
                    out.close();
                    Log.d("test", "Stored Successfully.");
                }
                catch (IOException e) {
                    Log.e("test", "Could not read/write file " + e.getMessage());
                }
            }
        }
    }

    private class DbThread extends Thread {
        private Location location;

        public DbThread() {
            this.location = new Location();
        }

        public void start(SQLiteDBConfig config) {

            config.setDbListener(new IDBListener() {
                @Override
                public void onUpgradeHandler(SQLiteDatabase db, int oldVersion, int newVersion) {
                    Log.d("test", "数据库升级成功");
                }

                @Override
                public void onDbCreateHandler(SQLiteDatabase db) {
                    Log.d("test", "数据库创建成功");
                }
            });

            db = SQLiteDBFactory.createSQLiteDB(config);

        }

        public void write(ArrayList<GpsPoint> bldata, ArrayList<SDEdge> result) {
            location.setLocation(1, bldata.get(0).timestamp, bldata.get(0).timestamp, bldata.get(0).latitude, bldata.get(0).longitude,
                    bldata.get(0).speed, bldata.get(0).headingDirection);
            if(!result.isEmpty())
            {
                this.location.setRoute(result.get(0).node1_lon, result.get(0).node1_lat, result.get(0).node2_lon, result.get(0).node2_lat);
            }
            long time_insert = System.currentTimeMillis() / 1000l;
            double time_insert2 = Math.floor(time_insert);
            location.setTime_insert((long) time_insert2);
            if(db.save(location) > 0){
                Log.d("test", "Database save successfully.");
            }
        }
    }



    private class ClientThread extends Thread {
        private BluetoothDevice device;

        public ClientThread(BluetoothDevice device) {
            this.device = device;
        }


        public void run() {

            BluetoothSocket socket = null;

            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Log.d("test", "连接服务端...");
                Thread.sleep(500);
                socket.connect();
                Log.d("test", "连接建立.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageReadConnectedSocket(socket);

            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("HC-05", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    Log.d("test","start accept");
                    socket = mmServerSocket.accept();
                    Log.d("test","accept");
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageReadConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            int lengt = 5000;
            Log.d("test","lengt:"+lengt);
            byte[] buffer = new byte[lengt];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(new String(buffer, 0, bytes));
                    Log.d("test","read:"+stringBuilder.toString());
                    Message msg =Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                    msg.obj = stringBuilder.toString();
                    msg.what=MSG_READ; //标志消息的标志
                    mHandler.sendMessage(msg);
                    // Send the obtained bytes to the UI activity


                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(500);
                }catch (Exception e) {
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            if (mmOutStream != null) {
                try {
                    mmOutStream.write(bytes);
//                    Log.d("test", "send: " + bytes);
                } catch (IOException e) {
                    Log.e("test", "outputStream.write null");
                }
            }
            else{

            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e) {
                Log.d("test","close() of connect socket failed", e);
            }
        }
    }

    public class MyThread implements Runnable {      // thread
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(6000);     // sleep 1000ms
                    Log.d("test","timedelay");
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    private void drawMap(){


        //
    }

    public void drawLine(double x,double y){
        double[] resultTmp = CoordinateTransformUtil.wgs84togcj02(y,x);
        LatLng point = new LatLng(resultTmp[1],resultTmp[0]);
        geoPoints.add(point);
        Polyline polyline =aMap.addPolyline(new PolylineOptions().
                addAll(geoPoints).width(10).color(Color.argb(255, 43,213,77)));
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(x,y),15,30,0));
        aMap.moveCamera(mCameraUpdate);
    }

    public void drawLineByEdge(double x1,double y1,double x2,double y2){
        List<LatLng> latLngs = new ArrayList<LatLng>();
        double[] resultTmp = CoordinateTransformUtil.wgs84togcj02(y1,x1);
        double[] resultTmp2 = CoordinateTransformUtil.wgs84togcj02(y2,x2);
        LatLng point = new LatLng(resultTmp[1],resultTmp[0]);
        LatLng point2 = new LatLng(resultTmp2[1],resultTmp2[0]);
        latLngs.add(point2);
        latLngs.add(point);
        Log.d("ylm","node1:"+x1+" "+y1);
        Log.d("ylm","node2:"+x2+" "+y2);
        Polyline polyline =aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 0,51,255)));
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(resultTmp[1],resultTmp[0]),15,30,0));
        aMap.moveCamera(mCameraUpdate);
    }

    public void drawPoint(double x,double y){
        double[] resultTmp = CoordinateTransformUtil.wgs84togcj02(y,x);
        LatLng latLng = new LatLng(resultTmp[1],resultTmp[0]);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(zoomImg(BitmapFactory
                .decodeResource(getResources(),R.drawable.dian),20,20)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果

        final Marker marker = aMap.addMarker(markerOption);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,14,30,0));
        aMap.moveCamera(mCameraUpdate);
    }

    public void permissionsRequest() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else
        {
            successPermission();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        } else
        {
            successPermission();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);
        } else
        {
            successPermission();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

    }

    public void successPermission() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                successPermission();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public String sHA1(Context context) {
        Log.d("zxc","start");
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void update(double lat,double lon,double speed,double dirction){
        DecimalFormat    df   = new DecimalFormat("######0.000");
        latTextView.setText("Lat："+df.format(lat));
        lonTextView.setText("Lon："+df.format(lon));
        speedTextView.setText("Speed："+df.format(speed));
        dircTextView.setText("Dirction："+dirction);
        double copress = (totalEdgeNum*0.04)/(copressEdgeNum*0.014);
        Log.d("test","gps:"+totalEdgeNum+", edge:"+copressEdgeNum);
        compressTextView.setText("ComRate："+df.format(copress));
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showGpsFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), GPS_FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("test", "File Uri: " + uri.toString());
                    // Get the path
                    String path = getPath(this, uri);
                    Log.d("test", "File Path: " + path);

                    SharedPreferences sharedPreferences = getSharedPreferences("sdmacher", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("filepath", path);

                    //保存key-value对到文件中
                    editor.commit();
                    sdMacher();

//                    File file = new File(path);
//                    Log.d("test","ss"+file.exists());
//                    String content = "";
//                    try{
//                        InputStream in = new FileInputStream(file);
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                        Log.d("test",reader.readLine());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

//                        if (in != null) {
//




//                    Log.d("test",content);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
            case GPS_FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("test", "File Uri: " + uri.toString());
                    // Get the path
                    String path;
                    if(uri.toString().contains("fileprovider"))
                    {
                        String [] path_list = uri.toString().split("fileprovider");
                        path = path_list[path_list.length - 1];
                    }
                    else
                    {
                        path = getPath(this, uri);
                    }
                    Log.d("test", "File Path: " + path);

                    SharedPreferences sharedPreferences = getSharedPreferences("sdmacher", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("gpsfilepath", path);

                    //保存key-value对到文件中
                    editor.commit();
                    sdMacher();

//                    File file = new File(path);
//                    Log.d("test","ss"+file.exists());
//                    String content = "";
//                    try{
//                        InputStream in = new FileInputStream(file);
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                        Log.d("test",reader.readLine());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

//                        if (in != null) {
//




//                    Log.d("test",content);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Context context, Uri uri){
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it  Or Log it.
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


}
