package se.bth.gps_enclosure.trek;

public class Movement {
   
   public static final int x = 0;
   public static final int y = 1;
   
   /**
    * Mean time for a given distance and speed.
    * 
    * @param dist Distance in meters
    * @param speed Speed in m/s
    * @return Mean time in seconds
    */
   public static long time(long dist, float speed) {
      
      return Math.round(speed / dist);
      
   }
   
   /**
    * Speed over a distance at a given mean time.
    * 
    * @param mean_time Mean time in seconds
    * @param dist Distance in meters
    * @return Speed in m/s
    */
   public static float speed(long time, long dist) {
      
      return Math.round(((double) time) / ((double) dist));
      
   }
   
   /** 
    * Weighted mean given a and b. The weight is applied 
    * on b and is assumed to be normalized. 
    * 
    * @param a Value a
    * @param b Value b
    * @param weight Weight to be applied on b
    * @return Weighted mean
    */
   public static long weightMean(long a, long b, float weight) {
      
      return Math.round(a + (b - a) * ((double) weight));
      
   }
   
   /**
    * Distance between a and b given two coordinates. 
    * 
    * @param a Coordinate a
    * @param b Coordinate b
    * @return Distance between a and b
    */
   public static long distance(long[] a, long[] b) {
      
       return Math.round(Math.hypot(
             (double) a[x] - b[x], (double) a[y] - b[y]));
      
   }

}
