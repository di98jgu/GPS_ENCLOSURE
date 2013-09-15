package se.bth.gps_enclosure.trek;

import se.bth.gps_enclosure.Key;
import android.content.SharedPreferences;

public class Position implements Storable {
   
   public static final int x = 0;
   public static final int y = 1;
   
   private static String COORD_X = Key.VALUE_POSITION_COORD_X;
   private static String COORD_Y = Key.VALUE_POSITION_COORD_Y;
   private static String WEIGHT = Key.VALUE_POSITION_WEIGHT;
   
   private long[] my_position;
   private float my_weight;
   
   /**
    * A new position. The weight is used to set the amount of dependency
    * between a position and previous positions.
    * 
    * @param weight
    * @param position
    */
   public Position(float weight, long[] position) {
      
      this.my_position = position;
      this.my_weight = weight;
      
   }
   
   /**
    * Create a new position from shared preferences.
    * 
    * @param source Shared preferences
    */
   private Position(SharedPreferences source) {
      
      this(
            source.getFloat(WEIGHT,  (float) 0.5), 
            new long[] {
               source.getLong(COORD_X, 0), 
               source.getLong(COORD_Y, 0)});
      
   }
   
   /**
    * Update this position with a new position.
    * 
    * @param position Coordinates
    */
   public void update(long[] position) {
      
      my_position = position;
  
   }
   
   /**
    * Get current position
    * 
    * @return Coordinates for this position
    */
   public long[] get() {
      
      return my_position;
      
   }
   
   @Override
   public void writeToPreferences(SharedPreferences dest) {
      
      SharedPreferences.Editor editor = dest.edit();
      
      editor.putFloat(WEIGHT, my_weight);
      editor.putLong(COORD_X, my_position[x]);
      editor.putLong(COORD_Y, my_position[y]);
      
      editor.commit();
     
   }

   public static final Storable.Creator<Position> CREATOR = 
        new Storable.Creator<Position>() {
     
      public Position createFromPreferences(SharedPreferences source) {
         
          return new Position(source);
          
      }

      public Position[] newArray(int size) {
         
          return new Position[size];
          
      }
      
  };

}
