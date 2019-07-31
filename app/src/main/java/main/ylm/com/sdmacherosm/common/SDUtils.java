package main.ylm.com.sdmacherosm.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import main.ylm.com.sdmacherosm.Constant;
import main.ylm.com.sdmacherosm.enity.SDEdge;
import main.ylm.com.sdmacherosm.enity.SearchNode;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;

/**
 * Created by YLM on 2018/1/30.
 */

public class SDUtils {

    public SDUtils(){

    }

    public static int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context){
        WindowManager windowManager = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static double dis_node(double node1_lat,double node1_lon,double node2_lat,double node2_lon){
        int R=6370000;
        double beta=Math.abs(node1_lat - node2_lat);
        double aefa=Math.abs(node1_lon - node2_lon);
        double flag=R * ( Math.sqrt( Math.pow(beta,2) + Math.pow(aefa,2))*Math.PI/180.0);
        return flag;
    }



    public static double getAngle(double lane1_lat,double lane1_lon,double lane2_lat,double lane2_lon) {
//
//        double y = Math.sin(lng_b-lng_a) * Math.cos(lat_b);
//        double x = Math.cos(lat_a)*Math.sin(lat_b) - Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        if(brng < 0)
//            brng = brng +360;
//        return brng;
        double delta_x=lane2_lat-lane1_lat,delta_y=lane2_lon-lane1_lon;
        double angle_lane_abs = 0;
        if (delta_x!=0){
            angle_lane_abs=atan(abs(delta_y/delta_x));
        }
        double angle_lane = 0;
        if (delta_x>=0&&delta_y>=0){//第一象限
            angle_lane=atan(angle_lane_abs)/PI*180;
        }else if (delta_x>=0&&delta_y<=0){//<=0%第二象限
            angle_lane=360-atan(angle_lane_abs)/PI*180;
        } else if (delta_x<=0&&delta_y<=0){//第三象限
            angle_lane=180+atan(angle_lane_abs)/PI*180;
        } else if (delta_x<=0&&delta_y>=0){//%第四象限
            angle_lane=180-atan(angle_lane_abs)/PI*180;
        }

        return angle_lane;

    }

    public static SearchNode chooseStartNode(SDEdge edge, int direction){
        switch (direction){
            case Constant.QUADRANT_ONE:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_TWO:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_THREE:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_FOUR:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            default:
                return null;
        }
    }
    public static SearchNode chooseOtherStartNode(SDEdge edge, int direction){
        switch (direction){
            case Constant.QUADRANT_ONE:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_TWO:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_THREE:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_FOUR:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            default:
                return null;
        }
    }

    public static SearchNode chooseEndNode(SDEdge edge,int direction){
        switch (direction){
            case Constant.QUADRANT_ONE:

                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_TWO:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_THREE:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_FOUR:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            default:
                return null;
        }
    }

    public static SearchNode chooseOtherEndNode(SDEdge edge,int direction){
        switch (direction){
            case Constant.QUADRANT_ONE:

                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_TWO:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_THREE:
                if (edge.node1_lat<edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            case Constant.QUADRANT_FOUR:
                if (edge.node1_lat>edge.node2_lat){
                    return new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id);
                }else {
                    return new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id);
                }
            default:
                return null;
        }
    }

    public static int getQuadrant(int direction){
        if (direction<=90&&direction>=0){
            return Constant.QUADRANT_ONE;
        }else if (direction>90&&direction<=180){
            return Constant.QUADRANT_FOUR;
        }else if (direction>180&&direction<=270){
            return Constant.QUADRANT_THREE;
        }else if (direction>270&&direction<=360){
            return Constant.QUADRANT_TWO;
        }else {
            return -1;
        }
    }

    public static int getDirection(double direction){
        if (direction<=135&&direction>=45){
            return Constant.NORTH;
        }else if (direction>135&&direction<=225){
            return Constant.WEST;
        }else if (direction>225&&direction<=315){
            return Constant.SOUTH;
        }else{
            return Constant.EAST;
        }
    }



}
