package main.ylm.com.sdmacherosm.enity;

/**
 * Created by YLM on 2018/1/30.
 */

public class SDEdge {

    public long edge_id;

    public long node1_id;

    public long node2_id;

    public double node1_lat;

    public double node1_lon;

    public double node2_lat;

    public double node2_lon;

    public int cell_id;

    public int cell_id2;

    public double  probability;

    public SDEdge(){

    }

    public SDEdge(SDEdge edge){
        this.edge_id = edge.edge_id;
        this.node1_lat = edge.node1_lat;
        this.node1_lon = edge.node1_lon;
        this.node2_lon = edge.node2_lon;
        this.node2_lat = edge.node2_lat;
        this.probability = edge.probability;
        this.cell_id = edge.cell_id;
        this.cell_id2 = edge.cell_id2;
        this.node1_id = edge.node1_id;
        this.node2_id = edge.node2_id;
    }

    public SDEdge(double node1_lat,double node1_lon,double node2_lat,double node2_lon){
        this.node1_lat = node1_lat;
        this.node1_lon = node1_lon;
        this.node2_lat = node2_lat;
        this.node2_lon = node2_lon;
    }


    public void updatevalue(double node1_lat,double node1_lon,double node2_lat,double node2_lon)
    {
        this.node1_lat = node1_lat;
        this.node1_lon = node1_lon;
        this.node2_lat = node2_lat;
        this.node2_lon = node2_lon;
    }

    @Override
    public int hashCode(){
        return Double.valueOf(node1_lat).hashCode()+
                Double.valueOf(node1_lon).hashCode()+
                Double.valueOf(node2_lat).hashCode()+
                Double.valueOf(node2_lon).hashCode();
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
        SDEdge edge = (SDEdge)obj;
        if (edge.node1_lat==node1_lat
                &&edge.node1_lon==node1_lon
                &&edge.node2_lat==node2_lat
                &&edge.node2_lon==node2_lon){
            return true;
        }else if (edge.node1_lat==node2_lat
                &&edge.node1_lon==node2_lon
                &&edge.node2_lat==node1_lat
                &&edge.node2_lon==node1_lon){
            return true;
        }else {
            return false;
        }

    }
}

