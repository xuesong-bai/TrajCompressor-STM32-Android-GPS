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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ylm.com.sdmacherosm.R;
import main.ylm.com.sdmacherosm.enity.SDEdge;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by YLM on 2018/1/30.
 */

public class DataReader {

    private static DataReader instance = null;

    private Context context;

    private List<SDEdge> sdData = new ArrayList<>();

    private Map<Integer,List> cellMap = new HashMap<>();

    private float lonMin = Float.MAX_VALUE;

    private float lonMax = Float.MIN_VALUE;

    private float latMax = Float.MIN_VALUE;

    private float latMin = Float.MAX_VALUE;

    private float xlength = 0;

    private float ylength = 0;

    public static DataReader getInstance(Context context) {
        if (instance == null) {
            instance = new DataReader(context);
        }
        return instance;
    }

    public DataReader(Context context){
        this.context = context;
        read();
        handleData();
//        test();
    }

    private void test(){
        List<SDEdge> list = cellMap.get(2);
        if (list!=null&&!list.isEmpty()){
            for (SDEdge sdEdge : list){
                Log.d("test","id:"+sdEdge.edge_id);
            }
        }
    }

    private void read(){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences("sdmacher", MODE_PRIVATE);
            String path = sharedPreferences.getString("filepath","");

            File file = new File(path);
            InputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //跳过第一行
            String str = reader.readLine();
            while (str!=null){
                str = reader.readLine();
                if (str!=null){
                    String[] tmp = str.split(",");
                    getMaxMin(tmp);
                    if (tmp.length==7){
                        SDEdge sdEdge = new SDEdge();
                        sdEdge.edge_id = Long.parseLong(tmp[0]);
                        sdEdge.node1_id = Long.parseLong(tmp[1]);
                        sdEdge.node2_id = Long.parseLong(tmp[2]);
                        sdEdge.node1_lon = Double.parseDouble(tmp[4]);
                        sdEdge.node1_lat = Double.parseDouble(tmp[3]);
                        sdEdge.node2_lon = Double.parseDouble(tmp[6]);
                        sdEdge.node2_lat = Double.parseDouble(tmp[5]);
                        sdData.add(sdEdge);
                    }else{
                        Log.e("DataReader HandleError:",str);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleData(){
        Log.d("sdmacher","latMax:"+latMax+"latMin:"+latMin+"lonMax:"+lonMax+"lonMin:"+lonMin);
        xlength = latMax - latMin;
        ylength = lonMax - lonMin;
        int size = sdData.size();
        for (int i = 0;i<size;i++){
            SDEdge sdEdge = sdData.get(i);
            List<Integer> tmp = calculateCellId(sdEdge);
            for (int cellid : tmp){
                if (cellMap.get(cellid)!=null){
                    List<SDEdge> cellEdge = cellMap.get(cellid);
                    cellEdge.add(sdEdge);
                    cellMap.put(cellid,cellEdge);
                }else{
                    List<SDEdge> cellEdge = new ArrayList<>();
                    cellEdge.add(sdEdge);
                    cellMap.put(cellid,cellEdge);
                }
            }
        }
        //parseNodeData
//        Log.d("test","lonmin:"+lonMin+"latmin"+latMin);
//        for (int i = 0;i<size;i++){
//            node1_lon[i] = (int)((node1_lon_tmp[i] - lonMin)*1000);
//            node1_lat[i] = (int)((node1_lat_tmp[i] - latMin)*1000);
//            node2_lon[i] = (int)((node2_lon_tmp[i] - lonMin)*1000);
//            node2_lat[i] = (int)((node2_lat_tmp[i] - latMin)*1000);
//            Log.d("test","node1_lon:"+node1_lon[i]+"node1_lat:"+node1_lat[i]);
//            Log.d("test","node2_lon:"+node2_lon[i]+"node2_lat:"+node2_lat[i]);
//        }

    }

    private List<Integer> calculateCellId(SDEdge sdEdge){
        List<Integer> cellId = new ArrayList<>();
        int y_num = (int)(((sdEdge.node1_lon - lonMin)/ylength)*30);
        int x_num = (int)(((sdEdge.node1_lat - latMin)/xlength)*30);
        int node1_cell_id = y_num*30+x_num;
        y_num = (int)(((sdEdge.node2_lon - lonMin)/ylength)*30);
        x_num = (int)(((sdEdge.node2_lat - latMin)/xlength)*30);
        int node2_cell_id = y_num*30+x_num;
        if (node1_cell_id==node2_cell_id){
            cellId.add(node1_cell_id);
            sdEdge.cell_id = node1_cell_id;
        }else {
            cellId.add(node1_cell_id);
            cellId.add(node2_cell_id);
            sdEdge.cell_id = node1_cell_id;
            sdEdge.cell_id2 = node2_cell_id;
        }
        return cellId;
    }

    private void getMaxMin(String[] data){
        float onelon = Float.parseFloat(data[4]);
        float twolon = Float.parseFloat(data[6]);
        float onelat = Float.parseFloat(data[3]);
        float twolat = Float.parseFloat(data[5]);
        lonMin = onelon>twolon?(twolon>lonMin?lonMin:twolon):(onelon>lonMin?lonMin:onelon);
        lonMax = onelon>twolon?(onelon>lonMax?onelon:lonMax):(twolon>lonMax?twolon:lonMax);
        latMax = onelat>twolat?(onelat>latMax?onelat:latMax):(twolat>latMax?twolat:latMax);
        latMin = onelat>twolat?(twolat>latMin?latMin:twolat):(onelat>latMin?latMin:onelat);
    }


    public List getData(){
        return sdData;
    }

    public Map getMap(){
        return cellMap;
    }

    public float getLonMin(){
        return lonMin;
    }

    public float getLonMax(){
        return lonMax;
    }

    public float getLatMax(){
        return latMax;
    }

    public float getLatMin(){
        return latMin;
    }


}

