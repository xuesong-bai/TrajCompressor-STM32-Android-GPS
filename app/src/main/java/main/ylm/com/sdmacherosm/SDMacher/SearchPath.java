package main.ylm.com.sdmacherosm.SDMacher;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ylm.com.sdmacherosm.Constant;
import main.ylm.com.sdmacherosm.common.SDUtils;
import main.ylm.com.sdmacherosm.data.DataReader;
import main.ylm.com.sdmacherosm.enity.GpsPoint;
import main.ylm.com.sdmacherosm.enity.SDEdge;
import main.ylm.com.sdmacherosm.enity.SearchNode;

/**
 * Created by YLM on 2018/1/30.
 */

public class SearchPath {

    private GpsPoint gpsPoint1;

    private GpsPoint gpsPoint2;

    private SearchNode endNode;

    private int time;

    private Map<Integer,List> cellMap = new HashMap<>();

    private ArrayList<SDEdge> selected  = new ArrayList<>();

    private ArrayList<SearchNode> result = null;

    private ArrayList<SearchNode> list = new ArrayList<>();

    private int startQuadrant;

    private int endQuadrant;

    public SearchPath(Context context , GpsPoint gp1, GpsPoint gp2){
        gpsPoint1 = gp1;
        gpsPoint2 = gp2;
        initData(context);
    }

    private void initData(Context context){
        time = (int)(gpsPoint2.timestamp - gpsPoint1.timestamp);
        cellMap = DataReader.getInstance(context).getMap();
        startQuadrant = SDUtils.getQuadrant(gpsPoint1.headingDirection);
        endQuadrant = SDUtils.getQuadrant(gpsPoint2.headingDirection);
    }

    public ArrayList<SDEdge> getSelected(){
        return selected;
    }


    public ArrayList<SearchNode> search(SDEdge edge_1,SDEdge edge_2){
        if (edge_1.edge_id==edge_2.edge_id){
            ArrayList<SearchNode> list = new ArrayList<>();
            list.add(new SearchNode(edge_1.node1_lat,edge_1.node1_lon,edge_1.edge_id));
            list.add(new SearchNode(edge_1.node2_lat,edge_1.node2_lon,edge_1.edge_id));
            //Log.d("test","same edge");
            return list;
        }
        //Log.d("test","edge1 n1lat:"+edge_1.node1_lat+"n1lon:"+edge_1.node1_lon+"n2lat:"+edge_1.node2_lat+"n2lon:"+edge_1.node2_lon);
        //Log.d("test","edge1 n1lat:"+edge_2.node1_lat+"n1lon:"+edge_2.node1_lon+"n2lat:"+edge_2.node2_lat+"n2lon:"+edge_2.node2_lon);
        ArrayList<SDEdge> tmp = getEdgeInCell(edge_1.cell_id);
        for (SDEdge edge : tmp){
            if (checkInRange(edge)){
                selected.add(edge);
            }else {
                //Log.d("test",edge.edge_id+"edge is not in range");
            }
        }
        endNode = SDUtils.chooseEndNode(edge_2,endQuadrant);
        SearchNode startNode = SDUtils.chooseStartNode(edge_1,startQuadrant);
        //Log.d("test","startNode lat"+startNode.latitude+"lon:"+startNode.longitude);
        //Log.d("test","endNode lat:"+endNode.latitude+"lon:"+endNode.longitude);
        searchPathTree(startNode);
        return result;
    }


    private boolean checkInRange(SDEdge edge){
        double distance = time*gpsPoint1.speed;
        if (edge.node1_lat<gpsPoint1.latitude+distance&&
                edge.node1_lat>gpsPoint1.latitude-distance&&
                edge.node1_lon<gpsPoint1.longitude+distance&&
                edge.node1_lon>gpsPoint1.longitude-distance){
            return true;
        }
        if (edge.node2_lat<gpsPoint1.latitude+distance&&
                edge.node2_lat>gpsPoint1.latitude-distance&&
                edge.node2_lon<gpsPoint1.longitude+distance&&
                edge.node2_lon>gpsPoint1.longitude-distance){
            return true;
        }
        return false;
    }

//    private void searchPathTree(SearchNode startNode,SearchNode endNode){
//        searchStack.push(new SearchNode(startNode.latitude,startNode.longitude));
//        while (!searchStack.isEmpty()){
//            if (searchStack.peek().longitude == endNode.longitude&&
//                    searchStack.peek().latitude == endNode.latitude){
//                //返回路径
//                break;
//            }
//            SearchNode searchNode = searchStack.pop();
//            ArrayList<SearchNode> tmp = filterNode(searchNode,findNodeChiledren(searchNode));
//            for (SearchNode node : tmp){
//                searchStack.push(node);
//            }
//        }
//    }

    private void searchPathTree(SearchNode startNode){
        list.add(startNode);
        if (startNode.latitude==endNode.latitude&&
                startNode.longitude==endNode.longitude){
            //Log.d("test","finded end node"+list.size());
            result = new ArrayList<>();
            result.addAll(list);
        }else {
            //Log.d("test","this is not end node,lat:"+startNode.latitude+"lon"+startNode.longitude);
        }
        ArrayList<SearchNode> tmp = filterNode(startNode,findNodeChiledren(startNode));
        //Log.d("test","child num is "+tmp.size());

        for (SearchNode node : tmp){
//            Log.d("test","lat:"+node.latitude+"lon"+node.longitude);
            searchPathTree(node);
        }
        list.remove(list.size()-1);
    }

    private ArrayList<SearchNode> findNodeChiledren(SearchNode node){
        ArrayList<SearchNode> tmp = new ArrayList<>();
        for (SDEdge edge : selected){
            if (edge.node1_lon==node.longitude&&edge.node1_lat==node.latitude){
                tmp.add(new SearchNode(edge.node2_lat,edge.node2_lon,edge.edge_id));
                //Log.d("test","finded same edge id"+edge.edge_id+" node lat:"+edge.node2_lat+"lon:"+edge.node2_lon);
            }else if (edge.node2_lat==node.latitude&&edge.node2_lon==node.longitude){
                tmp.add(new SearchNode(edge.node1_lat,edge.node1_lon,edge.edge_id));
                //Log.d("test","finded same edge id"+edge.edge_id+"node lat:"+edge.node1_lat+"lon:"+edge.node1_lon);
            }
        }
        return tmp;
    }

    private ArrayList<SearchNode> filterNode(SearchNode parentNode,ArrayList<SearchNode> list){
        ArrayList<SearchNode> tmp = new ArrayList<>();
        for (SearchNode node : list){
            //Log.d("test","node lat"+node.latitude+"lon:"+node.longitude);
            Boolean angle = checkAngle(parentNode,node);
            Boolean distance = checkDistance(parentNode,node);
            //Log.d("test","angle:"+angle+"distance:"+distance);
            if (angle&&distance){
//                Log.d("test","this node is passed filterNode ,lat" + node.latitude+"lon:"+node.longitude);
                tmp.add(node);
            }else {
//                Log.d("test","this node is not pass filterNode ,lat" + node.latitude+"lon:"+node.longitude);
            }
        }
        return tmp;
    }



    private ArrayList<SDEdge> getEdgeInCell(int cell_id){
        int[] cells = {cell_id,cell_id+1,cell_id-1
                , cell_id+30,cell_id+31,cell_id+29
                ,cell_id-30,cell_id-29,cell_id-31};
        ArrayList<SDEdge> list = new ArrayList<>();
        for (int i = 0; i < cells.length ; i++){
            int cell_tmp = cells[i];
            if (cell_tmp>=0&&cell_tmp<= Constant.CELL_MAX){
                if (cellMap.containsKey(cell_tmp)){
                    list.addAll(cellMap.get(cell_tmp));
                }else {
//                    Log.d("test","cell id is not exist");
                }
            }
        }
        return list;
    }

    private boolean checkAngle(SearchNode parentNode,SearchNode childNode){
        double angle = SDUtils.getAngle(parentNode.latitude,parentNode.longitude,childNode.latitude,childNode.longitude);
        double angle2 = SDUtils.getAngle(gpsPoint1.latitude,gpsPoint1.longitude,gpsPoint2.latitude,gpsPoint2.longitude);
        //Log.d("test","angle:"+angle+"angle2:"+angle2);
        double angle_abs = 0;
        if (angle>=0&&angle<=90&&angle2>=270&&angle2<=360){
            angle_abs = 360-angle2+angle;
        }else if (angle2>=0&&angle2<=90&&angle>=270&&angle<=360){
            angle_abs = 360-angle+angle2;
        }else {
            angle_abs = Math.abs(angle-angle2);
        }
        //TODO:改为110，180是因为第一个点的问题
        if (angle_abs<=110){
            return true;
        }
        return false;
    }

    private boolean checkDistance(SearchNode parentNode,SearchNode childNode){
        if (SDUtils.dis_node(parentNode.latitude,parentNode.longitude,endNode.latitude,endNode.longitude)>
                SDUtils.dis_node(childNode.latitude,childNode.longitude,endNode.latitude,endNode.longitude)){
            return true;
        }
        return false;
    }




}