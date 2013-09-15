package se.bth.gps_enclosure;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Location service for collecting best known location and request a new then
 * needed. A location is always broadcasted at a call to this service. It is
 * the best available location at that time given a desired accuracy and min 
 * time since last update.
 * 
 * @author Jim Gunnarsson
 */
public class LocationService extends IntentService {

   private static String TAG = LocationService.class.getSimpleName();
   
   private static volatile boolean pending_update;
   
   private LocationManager location_manager;
   private Criteria my_criteria;
   
   public LocationService() {
      
      super(TAG);
      
   }
   
   @Override
   public void onCreate() {
      
      super.onCreate();
      
      location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      
      // Any location is better then none.
      my_criteria = new Criteria();
      my_criteria.setAccuracy(Criteria.ACCURACY_LOW);
      
   }
   
   /**
    * Take action depending of depending on the content of the intent. The
    * intent is expected to contain a location or a accuracy and min time
    * since last update of location. A given location is simply broadcasted.
    * If intent contain a accuracy and min time a new best known location is
    * collected and a update of location is requested if needed. 
    * 
    * @param intent Intent
    */
   @Override
   public void onHandleIntent(Intent intent) {
      
      Location location;
      
      Bundle cargo = intent.getExtras();
      
      if (cargo.containsKey(Key.CARGO_LOCATION)) {
         
         // We have been given a location, use it.
         Log.i(TAG, "Got a location");
         location = (Location) cargo.get(Key.CARGO_LOCATION);
         
      } else {
         
         // Fetch best available location.
         Log.i(TAG, "Select a location");
         int min_dist = cargo.getInt(Key.CARGO_RADIOUS);
         long min_time = cargo.getLong(Key.CARGO_TIME);
         
         location = getLocation(min_dist, min_time);
         
      }
                          
      EnclosureApp app = (EnclosureApp) getApplication();
      
      app.updateLocation(location);
         
      locationChanged(location);

   }

   @Override
   public void onDestroy() {
      
      super.onDestroy();
      
   }
   
   /**
    * Send a location changed broadcast with new location.
    * 
    * @param location Location to send
    */
   private void locationChanged(Location location) {
      
      if (location == null) return;
      
      Intent intent = new Intent(EnclosureApp.LOCATION_CHANGED);
      
      Bundle cargo = new Bundle();
      cargo.putParcelable(Key.CARGO_LOCATION, new Location(location));
      intent.putExtras(cargo);

      sendBroadcast(intent);
      
   }
   
   /**
    * Returns the most accurate and timely location. The desired accuracy is
    * given by min_dist. A minimum time since last known location is set by
    * min_time. A location update is requested if last known location don't 
    * meet the desired requirements.
    * 
    * @param min_dist Minimum distance of accuracy.
    * @param min_time Minimum time required between location updates.
    * @return The most accurate and / or timely previously detected location.
    */
   private Location getLocation(int min_dist, long min_time) {

      Location result = null;

      float best_accuracy = Float.MAX_VALUE;
      long best_time = Long.MIN_VALUE;

      List<String> all_providers = location_manager.getAllProviders();

      for (String provider: all_providers) {

         Location location = location_manager.getLastKnownLocation(provider);

         if (location != null) {

            float accuracy = location.getAccuracy();
            long time = location.getTime();

            if ((time > min_time && accuracy < best_accuracy)) {
               
               result = location;
               best_accuracy = accuracy;
               best_time = time;

            } else if (
                  time < min_time && 
                  best_accuracy == Float.MAX_VALUE && 
                  time > best_time) {

               result = location;
               best_time = time;

            }
         }
      }
      
      // Request a new update of location if needed. If a location is already
      // requested the pending update flag is set.
      if ((best_time < min_time || best_accuracy > min_dist) && !pending_update) {
         
         Log.d(TAG, "Request single update");
         pending_update = true;
         // String provider = location_manager.getBestProvider(my_criteria, true);
         location_manager.requestLocationUpdates(
               LocationManager.GPS_PROVIDER, 0, 0, my_listener, this.getMainLooper());
               
      }

      return result;

   }
   
   /**
    * This will work on old systems and new alike. This listener remove location
    * future updates and thus make this to a signle update listener. 
    */
   private LocationListener my_listener = new LocationListener() {

      // private String TAG = LocationListener.class.getSimpleName();
      
      private Intent my_intent = new Intent(EnclosureApp.LOCATION_AVAILABLE);

      public void onLocationChanged(Location location) {
         
         location_manager.removeUpdates(this);
         
         Bundle cargo = new Bundle();
         cargo.putParcelable(Key.CARGO_LOCATION, location);
         my_intent.putExtras(cargo);

         sendBroadcast(my_intent);
         
         pending_update = false;

      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onProviderDisabled(String provider) {
      }
   };

}
