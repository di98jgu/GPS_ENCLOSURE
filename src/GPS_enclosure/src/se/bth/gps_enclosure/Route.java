package se.bth.gps_enclosure;

public final class Route {
   
   public static final int x = 0;
   public static final int y = 1;
   
   public static final class Area {
      
      public static final String[] LIST = new String[] {"BTH in Karlskrona", "1 degree Square"};
      public static final Track[] TRACKS = new Track[] {new Route.BTH(), new Route.Square()};
      
   }
   
   public static final class BTH implements Track {
      
      public static final double[] VALUES = new double[] { 
         56.1806, 15.5939,
         56.1829, 15.5966,
         56.1847, 15.5942,
         56.1850, 15.5899,
         56.1825, 15.5892,
         56.1803, 15.5888,
         56.1800, 15.5914};
      
      public double[] values() {return VALUES; }
          
   }
   
   public static final class Square implements Track {
      
      public static final double[] VALUES = new double[] { 
         56.0000, 15.0000,
         56.9999, 15.9999};
      
      public double[] values() {return VALUES; }
          
   }
   
   public interface Track {
      
      public double[] values();
      
   }
  
}
