package se.bth.gps_enclosure.trek;

import se.bth.gps_enclosure.Key;
import se.bth.gps_enclosure.enclosure.Boundary;
import se.bth.gps_enclosure.enclosure.Fence;
import android.content.SharedPreferences;

/**
 * This is the allowed space within the pigeon is free to move. This space
 * may or may not contain fence. The idea behind space is that it is to
 * large extent independent of the management of the enclosure. That is why
 * space don't have any size or shape. It is the job of the enclosure to
 * take care of such details.
 * 
 * @author Jim Gunnarsson
 *
 */
public class Space implements Fence, Storable {
   
   private static String SIDE = Key.VALUE_FIELD_SIDE;
   private static String POS_X = Key.VALUE_FIELD_POS_X;
   private static String POS_Y = Key.VALUE_FIELD_POS_Y;
   private static String MARK_X = Key.VALUE_FIELD_MARK_X;
   private static String MARK_Y = Key.VALUE_FIELD_MARK_Y;
   
   Fence my_fence;
   long[] my_position;
   
   
   
   public Space() {
      
      this.my_fence = new InitFence(new long[] {0L, 0L}, 1L);
      this.my_position = new long[] {0L, 0L};
      
   }
   
   /**
    * New space given a source of shared preferences. This constructor 
    * should only be called through {@link Storable#createFromPreferences 
    * Storable.createFromPreferences()}.
    * 
    * @param source Source of SharedPreferences
    */
   private Space(SharedPreferences source) {
      
      long side = source.getLong(SIDE, 1L);
      long[] origin = new long[] {
            source.getLong(POS_X, 0L), source.getLong(POS_Y, 0L)};
      
      this.my_position = new long[] {
            source.getLong(MARK_X, 0L), source.getLong(MARK_Y, 0L)};
      
      this.my_fence = new InitFence(origin, side);
      
   }
   
   
   public void setFence(Fence fence) {
      
      if (fence == null) return;
      
      my_fence = fence;
      
   }
            
   @Override
   public boolean containPosition(long[] position) {
      
      my_position = position;
      
      return my_fence.containPosition(position);
      
   }
   
   @Override
   public boolean insideFence(long[] position) {
      
      my_position = position;
      
      return my_fence.insideFence(position);
      
   }
   
   @Override
   public boolean hasFence() {
      
      return my_fence.hasFence();
      
   }
   
   @Override
   public long distFence(long[] position) {
      
      return my_fence.distFence(position);
      
   }
   
   @Override
   public void writeToPreferences(SharedPreferences dest) {
      
      SharedPreferences.Editor editor = dest.edit();
      
      long dist = Math.round(0.7 * my_fence.distFence(my_position));
      
      editor.putLong(SIDE, (dist * 2L));
      editor.putLong(POS_X, (my_position[Position.x] - dist));
      editor.putLong(POS_Y, (my_position[Position.y] - dist));
      editor.putLong(MARK_X, my_position[Position.x]);
      editor.putLong(MARK_Y, my_position[Position.y]);
      
      editor.commit();
     
   }

   public static final Storable.Creator<Space> CREATOR = 
        new Storable.Creator<Space>() {
     
      public Space createFromPreferences(SharedPreferences source) {
         
          return new Space(source);
          
      }

      public Space[] newArray(int size) {
         
          return new Space[size];
          
      }
      
  };
  
   private class InitFence implements Fence {

      Boundary my_boundary;
      
      InitFence(long[] origin, long side) {
         
         this.my_boundary = new Boundary(
               origin, new long[] {side, side});
         
      }
      
      @Override
      public boolean containPosition(long[] position) {
         
         my_position = position;
         
         return my_boundary.contains(position);
         
      }

      @Override
      public boolean insideFence(long[] position) {
         
         my_position = position;
         
         return containPosition(position);
         
      }

      @Override
      public boolean hasFence() {
         
         return false;
         
      }

      @Override
      public long distFence(long[] position) {
         
         return my_boundary.minEdgeDistance(position);
         
      }

   }
   
}
