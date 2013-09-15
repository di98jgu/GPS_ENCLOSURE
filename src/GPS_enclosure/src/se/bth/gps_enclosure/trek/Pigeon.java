package se.bth.gps_enclosure.trek;

import se.bth.gps_enclosure.Key;
import se.bth.gps_enclosure.enclosure.Fence;
import android.content.SharedPreferences;
import android.location.Location;

public class Pigeon implements Storable {
   
   private static String REF_X = Key.VALUE_PIGEON_REF_X;
   private static String REF_Y = Key.VALUE_PIGEON_REF_Y;
   private static String REF_TIME = Key.VALUE_PIGEON_REF_TIME;
   private static String CLEARANCE = Key.VALUE_PIGEON_CLEARANCE;
   
   private static int x = Movement.x;
   private static int y = Movement.y;
   
   private Position my_position;
   private Space my_area;
   
   private long[] my_ref_position;
   private long my_ref_time;
   
   private long my_clearance;
   
   private PigeonListener my_listener;
   
   /**
    * New pigeon given initial position and area. The clearance is
    * a fuzzy zone around this pigeon in which any change of position
    * is ignored. The pigeon is viewed is idle.
    * 
    * @param pos Position of this pigeon
    * @param area Allowed area for pigeon
    * @param clearance Circle of clearance around pigeon
    */
   public Pigeon(Position pos, Space area, long clearance) {
      
      this.my_position = pos;
      this.my_area = area;
      this.my_clearance = clearance;
      
      this.my_ref_position = my_position.get();
      this.my_ref_time = timestamp();
      
   }
   
   /**
    * New pigeon given initial position and area. The clearance is
    * set by default to 10 units.
    * 
    * @param pos Position of this pigeon
    * @param area Allowed area for pigeon
    */
   public Pigeon(Position pos, Space area) {
      
      this(pos, area, 10);
      
   }
   
   /**
    * New pigeon given a source of shared preferences. This constructor
    * should only be called through {@link Storable#createFromPreferences 
    * Storable.createFromPreferences()}.
    * 
    * @param source Source of SharedPreferences
    */
   private Pigeon(SharedPreferences source) {
      
      this(
            Position.CREATOR.createFromPreferences(source), 
            Space.CREATOR.createFromPreferences(source),
            source.getLong(CLEARANCE, 10));
      
      my_ref_position[x] = source.getLong(REF_X, my_ref_position[x]);
      my_ref_position[y] = source.getLong(REF_Y, my_ref_position[y]);
      my_ref_time = source.getLong(REF_TIME, my_ref_time);
      
   }
   
   /**
    * Current speed of this pigeon
    * 
    * @return Speed of this pigeon
    */
   public float speed() {
      
      long[] position = my_position.get();
      long elapsed_time = timestamp() - my_ref_time;
      
      return Movement.speed(
            elapsed_time, Movement.distance(position, my_ref_position));
      
   }
   
   /**
    * Return the underlying Position object used by this pigeon. 
    * 
    * @return Position used by this pigeon
    */
   public Position position() {
      
      return my_position;
      
   }
   
   /**
    * Return current position of this pigeon
    * 
    * @return Current position
    */
   public long[] atPosition() {
      
      return my_position.get();
      
   }
   
   /**
    * Update the position of this pigeon using a Android location object. 
    * Only Longitude and latitude is currently used.
    * 
    * @param location Android location
    */
   public void updatePosition(Location location) {
      
      my_position.update(new long[] {
            (long) (location.getLongitude() * 100000),
            (long) (location.getLatitude() * 100000)});
      
      if (!(isIdle() || inArea())) {
         
         notifyLeaveArea(my_position);
         
      }
      
      if (my_area.hasFence()) {
         
         
         
      }
      
   }
   
   /** 
    * Return the underlying Space object used by this pigeon. This is the
    * space that defines the outer boundaries for this pigeon.
    * 
    * @return Area used by this pigeon
    */
   public Space area() {
      
      return my_area;
      
   }
   
   
   
   /** 
    * Will return true if this pigeon is in the area. In other words
    * if this pigeon is within allowed space. Clearance is used as a
    * fuzzy edge. This pigeon is allowed to be outside allowed space 
    * with a distance less then clearance.
    * 
    * @return <code>true</code> if pigeon is in allowed area.
    */
   public boolean inArea() {
      
      long[] position = my_position.get();
      
      if (my_area.containPosition(position)) {
         
         if (my_area.hasFence()) {
            
            if (my_area.insideFence(position)) {
               
               // Pigeon in space and within the fence
               return true;
               
            } else {
               
               // Pigeon in space but outside the fence
               return my_area.distFence(position) < my_clearance;
               
            }
            
         } else {
            
            // Pigeon is within the boundaries of a fenceless space
            return true;
            
         }
          
      }
      
      // Pigeon is outside boundaries of space
      return my_area.distFence(position) < my_clearance;
      
   }
   
   /**
    * Replace current area with a new area. This do not effect onLeaveArea
    * listener even if this pigeon will outside allowed space after this
    * call.
    * 
    * @param area New area
    */
   public void updateArea(Fence area) {
      
      my_area.setFence(area);
      
   }
   
   /**
    * If this pigeon have not changed position since last update of position
    * then pigeon is idle. More precisely the pigeon is within the circle of
    * clearance. 
    * 
    * @return <code>true</code> if pigeon is idle
    */
   public boolean isIdle() {
      
      return Movement.distance(my_position.get(), my_ref_position) < my_clearance;
      
   }
   
   /**
    * Radius of freedom this pigeon can move without crossing a fence or
    * leave its allocated area. 
    * 
    * @return
    */
   public long freedom() {
      
      return my_area.distFence(my_position.get());
      
   }
   
   /** 
    * Add a listener to the pigeon. One listener is allowed. 
    * 
    * @param listener PigeonListener
    */
   public void addListener(PigeonListener listener) {
      
      if (listener == null) return;
      
      my_listener = listener;
      
   }
   
   /** 
    * Remove listener from this pigeon if there is any.
    */
   public void removeListener() {
      
      my_listener = null;
      
   }
   
   /**
    * Return true if given position in within space of this pigeon.
    * 
    * @param pos Position
    */
   private boolean inSpace(long[] pos) {
      
      return my_area.containPosition(pos);
      
   }
   
   /**
    * Return true if given position is within allowed space of
    * this pigeon 
    * 
    * @param pos Position
    */
   private boolean inAllowedSpace(long[] pos) {
      
      if (my_area.insideFence(pos)) {
         
         return true;
            
      }
      
      return false;
      
   }
   
   /**
    * Notify listener on leave area event. The pigeon have left
    * the allowed area. 
    * 
    * @param pos Last know position of the pigeon. 
    */
   private void notifyLeaveArea(Position pos) {
      
      my_listener.onLeaveArea(this, pos);
      
   }
   
   /** 
    * Current time in seconds
    * 
    * @return Current time in seconds.
    */
   private long timestamp() {
      
      return System.currentTimeMillis() / 1000;
      
   }
   
   @Override
   public void writeToPreferences(SharedPreferences dest) {
      
      SharedPreferences.Editor editor = dest.edit();
      
      editor.putLong(REF_X, my_ref_position[x]);
      editor.putLong(REF_Y, my_ref_position[y]);
      editor.putLong(REF_TIME, my_ref_time);
      editor.putLong(CLEARANCE, my_clearance);
      
      editor.commit();
      
      my_position.writeToPreferences(dest);
      my_area.writeToPreferences(dest);
     
   }

   public static final Storable.Creator<Pigeon> CREATOR = 
        new Storable.Creator<Pigeon>() {
     
      public Pigeon createFromPreferences(SharedPreferences source) {
         
          return new Pigeon(source);
          
      }

      public Pigeon[] newArray(int size) {
         
          return new Pigeon[size];
          
      }
      
  };
   
}













