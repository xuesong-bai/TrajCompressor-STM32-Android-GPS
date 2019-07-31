package main.ylm.com.sdmacherosm.enity;

/**
 * Created by YLM on 2018/1/30.
 */

public class SearchNode {

    public double latitude;

    public double longitude;

    public long edge_id;

    public SearchNode(double latitude,double longitude){
        this(latitude,longitude,0);
    }

    public SearchNode(double latitude,double longitude,long edge_id){
        this.edge_id = edge_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int hashCode(){
        return 7*Double.valueOf(latitude).hashCode()+9*Double.valueOf(longitude).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (obj==this){
            return true;
        }
        if (obj==null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        SearchNode node = (SearchNode)obj;
        if (node.latitude==latitude&&node.longitude==longitude){
            return true;
        }else {
            return false;
        }

    }


}
