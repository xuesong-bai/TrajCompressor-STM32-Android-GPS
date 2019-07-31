package main.ylm.com.sdmacherosm.SDMacher;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.ylm.com.sdmacherosm.common.SDUtils;
import main.ylm.com.sdmacherosm.data.DataRecord;
import main.ylm.com.sdmacherosm.data.GpsReader;
import main.ylm.com.sdmacherosm.enity.GpsPoint;
import main.ylm.com.sdmacherosm.enity.SDEdge;
import main.ylm.com.sdmacherosm.enity.SearchNode;

/**
 * Created by YLM on 2018/1/30.
 */

public class CalculateNode {

    private Context context;

    private List<GpsPoint> gpsList = new ArrayList<>();

    private CandidateEdge candidateEdge;

    private boolean isConnect;

    private Map<GpsPoint,ArrayList<SDEdge>> gpsMap = new HashMap<>();

    private Map<GpsPoint,int[][]> connectMap = new HashMap<>();

    private Map<GpsPoint,ArrayList[][]> roadMap = new HashMap<>();

    private ArrayList<ArrayList<SDEdge>> allEdgeList = new ArrayList<>();

    private ArrayList<SDEdge> edgeList = new ArrayList<>();

    private LinkedHashSet<SearchNode> road = new LinkedHashSet<>();

    private ArrayList<SearchNode> compressNode = new ArrayList<>();

    private ArrayList<SDEdge> compressResult = new ArrayList<>();

    private LinkedHashSet<SDEdge> selectedData = new LinkedHashSet<>();

    private long threeTime;

    public CalculateNode(Context context,List<GpsPoint> list){
        this.context = context;
        gpsList = list;
        long macherStartTime = System.currentTimeMillis();
        initData();
        handleData();
        long macherTime = System.currentTimeMillis()-macherStartTime;
        long compressStartTime = System.currentTimeMillis();
        compress();
        long compressTime = System.currentTimeMillis() - compressStartTime;
        new DataRecord().writeTxtToFile(macherTime+" "+compressTime+" "+threeTime);
        Log.d("test","final node size:"+road.size());
    }

    public LinkedHashSet<SearchNode> getRoad(){
        return road;
    }

    public ArrayList<SDEdge> getCompressResult(){
        return compressResult;
    }

    private void initData(){

//        GpsPoint gpsPoint =  new GpsPoint(967790112671L,1517659582,29.60133753,106.4744208,20.16628647,212);
//        GpsPoint gpsPoint2 = new GpsPoint(967790112671L,1517659583,29.60119273,106.4743099,18.85077667,211);
//        GpsPoint gpsPoint3 = new GpsPoint(967790112671L,1517659584,29.60104683,106.4742029,19.21668243,211);
//        GpsPoint gpsPoint4 = new GpsPoint(967790112671L,1517659585,29.6008944,106.4740943,20.58114052,212);
//        GpsPoint gpsPoint5 = new GpsPoint(967790112671L,1517659586,29.60074701,106.4739838,19.57136536,211);
//
//        GpsPoint gpsPoint6 = new GpsPoint(967790112671L,1517659587,29.60059856,106.473877,19.97734451,211);
//        GpsPoint gpsPoint7 = new GpsPoint(967790112671L,1517659588,29.60045173,106.4737669,19.45020294,213);
//        GpsPoint gpsPoint8 = new GpsPoint(967790112671L,1517659589,29.60030596,106.4736565,19.39382935,211);
//        GpsPoint gpsPoint9 = new GpsPoint(967790112671L,1517659590,29.60016039,106.4735448,19.43606949,211);
//        GpsPoint gpsPoint10 = new GpsPoint(967790112671L,1517659591,29.60001347,106.4734356,19.72816658,211);
//
//        gpsList.add(gpsPoint);
//        gpsList.add(gpsPoint2);
//        gpsList.add(gpsPoint3);
//        gpsList.add(gpsPoint4);
//        gpsList.add(gpsPoint5);
//        gpsList.add(gpsPoint6);
//        gpsList.add(gpsPoint7);
//        gpsList.add(gpsPoint8);
//        gpsList.add(gpsPoint9);
//        gpsList.add(gpsPoint10);

        getCandidateEdge();
    }

    private void handleData(){
        if (gpsList.size()<2){
            Log.d("test", "This is from Bluetooth.");

        }else{
            for (int i = 0;i < gpsList.size()-1;i++){
                Log.d("###############test","filterEdge:"+i);
                filterEdge(gpsList.get(i),gpsList.get(i+1));
            }
        }

        //打印联通矩阵
//        Log.d("test","                    ");
//        Log.d("test","$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//        Log.d("test","                    ");
        for (int k = 0;k<gpsList.size()-1;k++){
            GpsPoint gps = gpsList.get(k);
            int[][] map = connectMap.get(gps);
            for (int i = 0;i<6;i++){
                String str = "";
                for (int j = 0;j<6;j++){
                    str+=" "+map[i][j];
                }
//                Log.d("test",str);
            }
//            Log.d("test","                ");
        }

        for (int k = 0;k<gpsList.size();k++){
            GpsPoint gps = gpsList.get(k);
            ArrayList<SDEdge> tmp = gpsMap.get(gps);
            for (int i = 0;i<6;i++){
                if (tmp.get(i)!=null){
                    Log.d("test","saved"+i);
                }
            }
//            Log.d("test","                ");
        }
        long threeStartTime = System.currentTimeMillis();
        //遍历所有可能路径，选择最大的
        ArrayList<SDEdge> sdList = gpsMap.get(gpsList.get(0));
        for (int i = 0;i<sdList.size();i++){
            if (sdList.get(i)!=null){
                getAllRoad(0,1,gpsList.size()-1,i);
            }
        }
        Log.d("test","count:"+allEdgeList.size());
        int index = getMaxRoad();
        if (index != -1){
            ArrayList<SDEdge> result = allEdgeList.get(index);
            for (SDEdge edge : result){
//                Log.d("test","result:"+edge.edge_id);
//                Log.d("test","result: 1lat:"+edge.node1_lat+" 1lon:"+edge.node1_lon+" 2lat:"+edge.node2_lat+" 2lon"+edge.node2_lon);
            }
//            for (int i = 0;i<allEdgeList.get(index).size();i++){
//              Log.d("ylm","result:"+allEdgeList.get(index).get(i).edge_id);
//            }

            getResult(result);
        }
        threeTime = System.currentTimeMillis() - threeStartTime;
    }

    private void getResult(ArrayList<SDEdge> data){
        for (int i = 0;i<gpsList.size()-1;i++){
            GpsPoint gps = gpsList.get(i);
            GpsPoint nextGps = gpsList.get(i+1);
            ArrayList<SDEdge> edgeList = gpsMap.get(gps);
            ArrayList<SDEdge> nextEdgeList = gpsMap.get(nextGps);
            ArrayList<SearchNode>[][] saveRoad = roadMap.get(gps);
            int index=0,nextIndex=0;
            for (int j = 0;j<edgeList.size();j++){
                SDEdge edge = edgeList.get(j);
                if (edge!=null){
                    if (edge.edge_id==data.get(i).edge_id){
                        index = j;
                    }
                }

            }
            for (int k = 0;k<nextEdgeList.size();k++){
                SDEdge edge = nextEdgeList.get(k);
                if (edge!=null){
                    if (edge.edge_id==data.get(i+1).edge_id){
                        nextIndex = k;
                    }
                }
            }
            ArrayList<SearchNode> node = saveRoad[index][nextIndex];
            //添加开始边的节点
            SDEdge edge = data.get(i);
            SearchNode sn1 = SDUtils.chooseStartNode(edge,SDUtils.getQuadrant(gps.headingDirection));
            SearchNode sn2 = SDUtils.chooseOtherStartNode(edge,SDUtils.getQuadrant(gps.headingDirection));
            if (road.contains(sn1)){
                //Log.d("test","start contains"+"lat:"+edge.node1_lat+"lon:"+edge.node1_lon);
            }else {
                //Log.d("test","start add"+"lat:"+edge.node1_lat+"lon:"+edge.node1_lon);
                road.add(sn1);
            }
            if (road.contains(sn2)){
                //Log.d("test","start contains"+"lat:"+edge.node2_lat+"lon:"+edge.node2_lon);
            }else {
                //Log.d("test","start add"+"lat:"+edge.node2_lat+"lon:"+edge.node2_lon);
                road.add(sn2);
            }
            //添加联通路径的节点
            if (node==null){
                Log.d("test","null"+"x"+index+"y"+nextIndex);
            }else {
                Log.d("test","x"+index+"y"+nextIndex);
                for (SearchNode n : node){
                    if (road.contains(n)){
//                        Log.d("test","connect contains"+"lat:"+n.latitude+"lon:"+n.longitude);
                    }else {
//                        Log.d("test","connect add"+"lat:"+n.latitude+"lon:"+n.longitude);
                        road.add(n);
                    }
                }
            }
            //添加结束边的节点
            SDEdge edge2 = data.get(i+1);
            SearchNode en1 = SDUtils.chooseEndNode(edge2,SDUtils.getQuadrant(nextGps.headingDirection));
            SearchNode en2 = SDUtils.chooseOtherEndNode(edge2,SDUtils.getQuadrant(nextGps.headingDirection));
            if (road.contains(en1)){
//                Log.d("test","end contains"+"lat:"+edge2.node1_lat+"lon:"+edge.node1_lon);
            }else {
//                Log.d("test","end add"+"lat:"+edge2.node1_lat+"lon:"+edge.node1_lon);
                road.add(en1);
            }
            if (road.contains(en2)){
//                Log.d("test","end contains"+"lat:"+edge2.node2_lat+"lon:"+edge.node2_lon);
            }else {
//                Log.d("test","end add"+"lat:"+edge2.node2_lat+"lon:"+edge.node2_lon);
                road.add(en2);
            }
        }
    }

    //获得候选边
    private void getCandidateEdge(){
        candidateEdge = new CandidateEdge(context);
        for (GpsPoint gpsPoint : gpsList){
            ArrayList<SDEdge> result = new ArrayList<>();
            ArrayList<SDEdge> list = (ArrayList<SDEdge>) candidateEdge.calcuteGps(gpsPoint);
            Log.d("test","gpsPoint:"+gpsPoint.timestamp);
            Log.d("test","gpssize:"+list);
            if (list!=null&&list.size()!=0){
                for (int i = 0;i<6;i++){
                    if (list.get(i)==null){
                    }else {
//                        Log.d("test","probability:"+list.get(i).probability);
                        result.add(new SDEdge(list.get(i)));
//                        Log.d("test","sdedge id:"+list.get(i).edge_id);
//                        Log.d("test","edge node1 lat"+list.get(i).node1_lat+"lon"+list.get(i).node1_lon);
//                        Log.d("test","edge node2 lat"+list.get(i).node2_lat+"lon"+list.get(i).node2_lon);
                    }
                }
                gpsMap.put(gpsPoint,result);

            }else {
                Log.d("test","candidateEdge size error:"+list.size());
            }
        }
    }

    //过滤，筛除多余边
    private void filterEdge(GpsPoint gpsPoint,GpsPoint gpsPoint2){
        ArrayList<SDEdge> list = gpsMap.get(gpsPoint);
        if (list==null||list.size()==0){
            return;
        }
        SDEdge error  = new SDEdge();
        for (SDEdge se : list){
            if (se!=null){
                error=se;
                break;
            }
        }
        ArrayList<SDEdge> list2 = gpsMap.get(gpsPoint2);
        int[][] storeEdge = new int[6][6];
        ArrayList<SearchNode>[][] storeRoad = new ArrayList[6][6];
        for (int i = 0;i<6;i++){
            //Log.d("#########cccc","filter one:"+i);
            SDEdge edge = list.get(i);
            if (edge!=null){
                for (int j = 0;j<6;j++){
                    SDEdge edge2 = list2.get(j);
                    if(edge.probability==0||edge2.probability==0){
                        storeRoad[i][j]=null;
                    }else{
                        SearchPath searchPath = new SearchPath(context,gpsPoint,gpsPoint2);
                        ArrayList<SearchNode> tmp = searchPath.search(edge,edge2);
                        selectedData.addAll(searchPath.getSelected());
                        if (tmp!=null){
                            //Log.d("cccc","filter two "+j+" edge mate success");
                            storeEdge[i][j] = 1;
                            storeRoad[i][j] = tmp;
                        }else {
                            storeRoad[i][j] = null;
                            //Log.d("test","filter two "+j+" edge mate failed");
                        }
                    }
                }
            }else {
                //Log.d("test","time: "+i+" gps:"+gpsPoint.timestamp);
            }

        }
        for (int i = 0;i<6;i++){
            isConnect = false;
            for (int j = 0;j<6;j++){
                if (storeEdge[i][j]==1){
                    isConnect=true;
                }
            }
            if (!isConnect){
                list.set(i,null);
            }
        }
        for (int i = 0;i<6;i++){
            isConnect = false;
            for (int j = 0;j<6;j++){
                if (storeEdge[j][i]==1){
                    isConnect=true;
                }
            }
            if (!isConnect){
                list2.set(i,null);
            }
        }
        boolean allnull = true;
        for(SDEdge e : list){
            if (e!=null){
                allnull=false;
            }
        }
        if (allnull){

        }

        gpsMap.put(gpsPoint,list);
        gpsMap.put(gpsPoint2,list2);
        connectMap.put(gpsPoint,storeEdge);
        roadMap.put(gpsPoint,storeRoad);
        //循环筛选所有的
    }

    private void getAllRoad(int startPoint,int nextPoint,int endPoint,int nowEdge){
        SDEdge edge = gpsMap.get(gpsList.get(startPoint)).get(nowEdge);
        edgeList.add(edge);
        if (startPoint == endPoint ){
            ArrayList<SDEdge> tmp = new ArrayList<>();
            tmp.addAll(edgeList);
            allEdgeList.add(tmp);
        }else {
            ArrayList<SDEdge> tmp = getChildren(gpsList.get(startPoint),gpsList.get(nextPoint),nowEdge);
            for (int i = 0;i<tmp.size();i++){
                if (tmp.get(i)!=null){
                    getAllRoad(startPoint+1,nextPoint+1,endPoint,i);
                }
            }
        }
        edgeList.remove(edgeList.size()-1);
    }

    private ArrayList<SDEdge> getChildren(GpsPoint parentPoint,GpsPoint childrenPoint,int edgeIndex){
        int[][] connect = connectMap.get(parentPoint);
        int[] edgeConnect = connect[edgeIndex];
        ArrayList<SDEdge> childrens = gpsMap.get(childrenPoint);
        ArrayList<SDEdge> tmp = new ArrayList<>();
        for (int i = 0;i<6;i++){
            if (edgeConnect[i]==1){
                tmp.add(childrens.get(i));
            }else {
                tmp.add(null);
            }
        }
        return tmp;
    }

    private int getMaxRoad(){
        double max = Double.MIN_VALUE;
        double sum = 0;
        int index = -1;
        for (int i = 0; i< allEdgeList.size(); i++){
            ArrayList<SDEdge> list = allEdgeList.get(i);
            sum = 0;
            for (SDEdge edge : list){
                sum += edge.probability;
            }
            if (sum>max){
                max = sum;
                index = i;
            }
        }
        return index;
    }

    public ArrayList<SDEdge> compress(){
        Log.d("ylm","selected size"+selectedData.size());
        Iterator it = road.iterator();
        while (it.hasNext()){
            compressNode.add((SearchNode) it.next());
        }
        ArrayList<SDEdge> compressEdge = new ArrayList<>();
        Log.d("test","compressNode size:"+compressNode.size());
        for (int i = 0;i<compressNode.size()-1;i++){
            SearchNode node1 = compressNode.get(i);
            SearchNode node2 = compressNode.get(i+1);
            compressEdge.add(new SDEdge(node1.latitude,node1.longitude,node2.latitude,node2.longitude));
        }
        ArrayList<Integer> dirc = new ArrayList<>();
        for (SDEdge edge : compressEdge){
            dirc.add(SDUtils.getDirection(SDUtils.getAngle(edge.node1_lat,edge.node1_lon,edge.node2_lat,edge.node2_lon)));
        }
        if (compressEdge.size()!=0){
            compressResult.add(compressEdge.get(0));
        }
        for (int i = 1;i<compressEdge.size();i++){
            SDEdge edge = compressEdge.get(i);
            ArrayList<SDEdge> otherRoad = isOtherRoad(edge.node1_lat,edge.node1_lon);
            if (otherRoad.size()>2){
                if (dirc.get(i)!=dirc.get(i-1)){
                    compressResult.add(edge);
                }else{
                    if (isSameDirction(otherRoad,dirc.get(i),edge.node1_lat,edge.node1_lon)){
                        compressResult.add(edge);
                    }
                }
            }
        }
        return compressResult;
    }

    private ArrayList<SDEdge> isOtherRoad(double latitude,double longitude){
        ArrayList<SDEdge> tmp = new ArrayList<>();
        for (SDEdge edge : selectedData){
            if (edge.node1_lon==longitude&&edge.node1_lat==latitude){
                tmp.add(edge);
                //Log.d("test","finded same edge id"+edge.edge_id+" node lat:"+edge.node2_lat+"lon:"+edge.node2_lon);
            }else if (edge.node2_lat==latitude&&edge.node2_lon==longitude){
                tmp.add(edge);
                //Log.d("test","finded same edge id"+edge.edge_id+"node lat:"+edge.node1_lat+"lon:"+edge.node1_lon);
            }
        }
        return tmp;
    }

    public boolean isSameDirction(ArrayList<SDEdge> data,int dirction,double lat,double lon){
        int count = 0;
        for (SDEdge edge : data){
            int edgeDirect = 0;
            if (edge.node1_lat==lat&&edge.node1_lon==lon){
                edgeDirect = SDUtils.getDirection(SDUtils.getAngle(edge.node1_lat,edge.node1_lon,edge.node2_lat,edge.node2_lon));
            }else if (edge.node2_lat==lat&&edge.node2_lon==lon){
                edgeDirect = SDUtils.getDirection(SDUtils.getAngle(edge.node2_lat,edge.node2_lon,edge.node1_lat,edge.node1_lon));
            }
            if (edgeDirect==dirction){
                count++;
            }
        }
        if (count>=2){
            return true;
        }else {
            return false;
        }
    }
}