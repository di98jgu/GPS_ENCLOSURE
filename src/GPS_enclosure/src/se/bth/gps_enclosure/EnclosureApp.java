package se.bth.gps_enclosure;

import java.util.List;

import se.bth.gps_enclosure.enclosure.Enclosure;
import se.bth.gps_enclosure.enclosure.FenceCoord;
import se.bth.gps_enclosure.enclosure.Util;
import se.bth.gps_enclosure.trek.Pigeon;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class EnclosureApp extends Application {

   private String TAG = DashboardActivity.class.getSimpleName();
                                               
   public static final String LOCATION_UPDATE = "se.bth.gps_enclosure.LOCATION_UPDATE";
   public static final String LOCATION_AVAILABLE = "se.bth.gps_enclosure.LOCATION_AVAILABLE";
   public static final String LOCATION_SERVICE = "se.bth.gps_enclosure.LOCATION_SERVICE";
   public static final String LOCATION_CHANGED = "se.bth.gps_enclosure.LOCATION_CHANGED";
   
   public static final String ENCLOSURE_SERVICE = "se.bth.gps_enclosure.ENCLOSURE_SERVICE";
   
   public static final String PIGEON_STATUS = "se.bth.gps_enclosure.PIGEON_STATUS";
   public static final String PIGEON_OUTSIDE_FENCE = "se.bth.gps_enclosure.PIGEON_OUTSIDE_FENCE";
   public static final String PIGEON_IDLE = "se.bth.gps_enclosure.PIGEON_IDLE";
   
   private Pigeon my_pigeon;
   private Enclosure my_enclosure;
   
   public EnclosureApp() {
      
      
      
   }
   
   public Enclosure getEnclosure() {
      
      if (my_enclosure == null) {
         
         my_enclosure = initEnclosure();
         
      }
      
      return my_enclosure;
      
   }
   
   public void putEnclosure(Enclosure enclosure) {
      
      my_enclosure = enclosure;
      
   }
   
   private Enclosure initEnclosure() {
      
      SharedPreferences prefs = getPreferences();

      int area = prefs.getInt(Key.PREF_AREA, -1);
      
      if (area == -1) return null;
      
      double[] values = Route.Area.TRACKS[area].values();
      
      List<FenceCoord> fence = Util.toCoordinateList(values, (short) 5);
      int[] grid = new int[] {30, 30};
      
      return new Enclosure(fence, grid);
      
   }
   
   public Pigeon getPigeon() {
      
      if (my_pigeon == null) {
         
         my_pigeon = Pigeon.CREATOR.createFromPreferences(getPreferences());
         
      }
      
      return my_pigeon;
      
   }
   
   public void putPigeon(Pigeon pigeon) {
   
      my_pigeon = pigeon;
      
      pigeon.writeToPreferences(getPreferences());
      
   }
   
   public void updateLocation(Location location) {
      
      SharedPreferences.Editor editor = getPreferences().edit();
      
      if (location != null) {
         editor.putFloat(Key.PREF_ACCURACY, location.getAccuracy());
         editor.putLong(Key.PREF_LATITUDE, (long) (location.getLatitude() * 100000));
         editor.putLong(Key.PREF_LONGITUDE, (long) (location.getLongitude() * 100000));
         editor.putLong(Key.PREF_TIME, location.getTime());
         editor.putFloat(Key.PREF_SPEED, location.getSpeed());
         editor.putBoolean(Key.PREF_LOCATION, true);
      }
      else {
         editor.putBoolean(Key.PREF_LOCATION, false);
      }
         
     
      editor.commit();
      
   }
   
   public SharedPreferences getPreferences() {
      
      return getSharedPreferences(
            getString(R.string.preferences), Context.MODE_PRIVATE);
      
   }

}
