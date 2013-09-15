package se.bth.gps_enclosure.enclosure;

import java.util.LinkedList;
import java.util.List;

public class Util {
   
   // Coordinate 
   public static List<FenceCoord> toCoordinateList(double[] vertices, short precision) {
      
      int record = 2;
      int offset = 0;
      int x = EuclidPlane.x;
      int y = EuclidPlane.y;
      
      List<FenceCoord> list = new LinkedList<FenceCoord>();
      
      while ((offset + record) < vertices.length) {
         
         FenceCoord coord = new FenceCoord();
         coord.value_x = vertices[offset + x];
         coord.value_y = vertices[offset + y];
         coord.precision = precision;
         
         list.add(coord);
         
         offset += record;
       
      }
      
      return list;
      
   }

}
