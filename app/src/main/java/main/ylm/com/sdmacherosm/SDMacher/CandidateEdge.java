package main.ylm.com.sdmacherosm.SDMacher;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ylm.com.sdmacherosm.Constant;
import main.ylm.com.sdmacherosm.common.SDUtils;
import main.ylm.com.sdmacherosm.data.DataReader;
import main.ylm.com.sdmacherosm.enity.GpsPoint;
import main.ylm.com.sdmacherosm.enity.SDEdge;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by YLM on 2018/1/30.
 */

public class CandidateEdge {

    private List<SDEdge> data = new ArrayList<>();

    private Map<Integer,List> cellMap = new HashMap<>();

    private DataReader dataReader;

    private float lonMin;

    private float lonMax;

    private float latMin;

    private float latMax;

    private float xlength;

    private float ylength;

    public CandidateEdge(Context context){
        initData(context);
    }

    private void initData(Context context){
        this.dataReader = DataReader.getInstance(context);
        data = dataReader.getData();
        lonMax = dataReader.getLonMax();
        lonMin = dataReader.getLonMin();
        latMax = dataReader.getLatMax();
        latMin = dataReader.getLatMin();
        xlength = latMax - latMin;
        ylength = lonMax - lonMin;
        cellMap = dataReader.getMap();
    }

    public ArrayList<SDEdge> calcuteGps(final GpsPoint point){
        int cell_id = getCellId(point.latitude,point.longitude);
        Log.d("test","cell_id:"+cell_id);
        ArrayList<SDEdge> cellEdges = getEdgeInCell(cell_id);
        Log.d("test","edge size:"+cellEdges.size());
        Collections.sort(cellEdges, new Comparator<SDEdge>() {
            @Override
            public int compare(SDEdge sdEdge, SDEdge t1) {
                double p1 = calculateP(point,sdEdge);
                double p2 = calculateP(point,t1);
                sdEdge.probability = p1;
                t1.probability = p2;
                if(p1>p2) {
                    return -1;
                }else if(p1<p2){
                    return 1;
                }else {
                    return 0;
                }
            }
        });
        return cellEdges;

    }

    private int getCellId(double lat,double lon){
        int cellId = 0;
        int y_num = (int)(((lon - lonMin)/ylength)*30);
        int x_num = (int)(((lat - latMin)/xlength)*30);
        cellId = y_num*30+x_num;
//        Log.d("test","cellid:"+cellId+"lonMin:"+lonMin+"latMin:"+latMin
//        +"ylength"+ylength+"xlength"+xlength+"lat"+lat+"lon"+lon);
        return cellId;
    }

    private ArrayList<SDEdge> getEdgeInCell(int cell_id){
        int[] cells = {cell_id,cell_id+1,cell_id-1
                , cell_id+30,cell_id+31,cell_id+29
                ,cell_id-30,cell_id-29,cell_id-31};
        ArrayList<SDEdge> list = new ArrayList<>();
        for (int i = 0; i < cells.length ; i++){
            int cell_tmp = cells[i];
            if (cell_tmp>=0&&cell_tmp<= Constant.CELL_MAX){
                ArrayList<SDEdge> tmp = (ArrayList<SDEdge>) cellMap.get(cell_tmp);
                if (tmp!=null&&tmp.size()!=0){
                    list.addAll(tmp);
                }else {
//                    Log.d("test","cell id is not exist");
                }
            }
        }
        return list;
    }

    private double calculateP(GpsPoint point,SDEdge edge){
        double p = 0;
        double direction = 0;
        double ptoL = 0;
        double angle = 0;
        double getAngle = 0;
        ptoL = pointToLine(edge.node1_lat,edge.node1_lon,
                edge.node2_lat,edge.node2_lon,point.latitude,point.longitude);
        getAngle = getAngle(point.headingDirection,edge.node1_lat,edge.node1_lon,
                edge.node2_lat,edge.node2_lon);
        angle = angle(getAngle);
        direction = distance(ptoL);
        p = angle*direction;
        //Log.d("cccc","ptol"+ptoL+"getAngel"+getAngle+"direction"+direction+"angle"+angle);

        return p;
    }

    private double distance(double distance){
        if (distance>60){
            return 0;
        }
        double a=0.3379,w=11.98,xc=-0.4567,y0=0.5005;
        double p;
        p=2*(y0+(a/(w*sqrt(PI/2)))*exp(-2*pow(((distance-xc)/w),2)))-1;
        return p;
    }
    private double angle(double angle){
        if (angle>60){
            return 0;
        }
        double a=0.4385,w=16.55,xc =-6.694,y0=0.5038;
        double p;
        p=2*(y0+(a/(w*sqrt(PI/2)))*exp(-2*pow(((angle-xc)/w),2)))-1;
        return p;
    }

    private double getAngle(double vehicle_direction,double lane1_lat,double lane1_lon,double lane2_lat,double lane2_lon){
        double delta_x=lane2_lat-lane1_lat,delta_y=lane2_lon-lane1_lon;
        double angle_lane_abs = 0;
        if (delta_x!=0){
            angle_lane_abs=atan(abs(delta_y/delta_x));
        }
        double angle_lane1 = 0,angle_lane2 = 0;
        if (delta_x>=0&&delta_y>=0){//第一象限
            angle_lane1=180+atan(angle_lane_abs)/PI*180;
            angle_lane2=atan(angle_lane_abs)/PI*180;
        }else if (delta_x>=0&&delta_y<=0){//<=0%第二象限
            angle_lane1=180-atan(angle_lane_abs)/PI*180;
            angle_lane2=360-atan(angle_lane_abs)/PI*180;
        } else if (delta_x<=0&&delta_y<=0){//第三象限
            angle_lane1=atan(angle_lane_abs)/PI*180;
            angle_lane2=180+atan(angle_lane_abs)/PI*180;
        } else if (delta_x<=0&&delta_y>=0){//%第四象限
            angle_lane1=360-atan(angle_lane_abs)/PI*180;
            angle_lane2=180-atan(angle_lane_abs)/PI*180;
        }
        double angle_diff1=abs(vehicle_direction-angle_lane1);
        double angle_diff2=abs(vehicle_direction-angle_lane2);
        double min1=min(angle_diff1,angle_diff2);
        double min2=min(abs(360-angle_diff1),abs(360-angle_diff2));
        double min=min(min1,min2);
        return min;
    }

    private double pointToLine(double line1_lat, double line1_lon, double line2_lat, double line2_lon, double node_lat,
                               double node_lon) {
        double space = 0;
        double a, b, c;
        a = SDUtils.dis_node(line1_lat, line1_lon, line2_lat, line2_lon);// 线段的长度
        b = SDUtils.dis_node(line1_lat, line1_lon, node_lat,node_lon);// (x1,y1)到点的距离
        c = SDUtils.dis_node(line2_lat, line2_lon, node_lat, node_lon);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            if ((b<=50||c<=50)&&a>=50){
                return 50;
            }else{
                return 9999999;
            }
        }
        if (b * b >= a * a + c * c) {
            if ((b<=50||c<=50)&&a>=50){
                return 50;
            }else{
                return 9999999;
            }
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }



}
