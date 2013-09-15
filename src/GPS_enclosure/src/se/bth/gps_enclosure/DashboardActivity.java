package se.bth.gps_enclosure;

import se.bth.gps_enclosure.trek.Pigeon;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the base class for activities in this application. 
 *
 * 
 */
public abstract class DashboardActivity 
   extends Activity implements OnSharedPreferenceChangeListener {

   private String TAG = DashboardActivity.class.getSimpleName();
   
   protected SharedPreferences my_prefs;
   protected static final IntentFilter location_changed_filter = new IntentFilter(EnclosureApp.LOCATION_CHANGED);
   protected static final IntentFilter pigeon_idle_filter = new IntentFilter(EnclosureApp.PIGEON_IDLE);
   protected static final IntentFilter pigeon_outside_fence_filter = new IntentFilter(EnclosureApp.PIGEON_OUTSIDE_FENCE);
   protected static final IntentFilter pigeon_status_filter = new IntentFilter(EnclosureApp.PIGEON_STATUS);
   
   /**
    * Not much to do here.
    */
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      
      this.my_prefs = getSharedPreferences(
            getString(R.string.preferences), Context.MODE_PRIVATE);
      
   }

   /**
    * 
    */
   protected void onRestart() {
      
      super.onRestart();
      
   }
   
   /**
    * 
    */
   protected void onStart() {
      
      super.onStart();
      
   }

   /**
    * Add listeners
    */
   protected void onResume() {
      
      super.onResume();
      
      this.my_prefs.registerOnSharedPreferenceChangeListener(this);
      
      registerReceiver(LocationAvailableReciver, location_changed_filter);
      registerReceiver(PigeonReciver, pigeon_status_filter);
      registerReceiver(PigeonReciver, pigeon_outside_fence_filter);
      registerReceiver(PigeonReciver, pigeon_idle_filter);
      
      
   }

   /**
    * Remove listeners
    */
   protected void onPause() {
      
      super.onPause();
      
      this.my_prefs.unregisterOnSharedPreferenceChangeListener(this);
      
      unregisterReceiver(LocationAvailableReciver);
      unregisterReceiver(PigeonReciver);
      
   }

   /**
    * 
    */
   protected void onStop() {
      
      super.onStop();
      
   }

   /**
    */
   protected void onDestroy() {
      
      super.onDestroy();
      
      this.my_prefs = null;
      
   }
   
   /**
    * Handle the click on the home button. The home button is the logo 
    * up in the left corner.
    * 
    * @param v Logo view
    */
   public void onClickDbLogo(View v) {
      
      goHome(this);
      
   }

   /**
    * Go back to the home activity, that is the dashboard. This 
    * remove the current activity from the stack. 
    * 
    * @param context Context
    */
   protected final void goHome(Context context) {
      
      final Intent intent = new Intent(context, HomeActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      
      context.startActivity(intent);
      
      return;
      
   }

   /**
    * Use the activity label to set the text in the activity's title. 
    * This is the text in the title bar. 
    * 
    * @param id Text view id for the text field in the title bar. 
    */
   protected void setActivityTitle(int id) {
      
      TextView tv = (TextView) findViewById(id);
      
      if (tv != null) {
         
         tv.setText(getTitle());
         
      }
      
      return;
      
   }
   
   protected void onLocationChanged(Location location) {
      
      info("Location have changed!");
      
   }
   
   /**
    * Update of pigeon status have occurred. 
    * 
    * @param pigeon The updated pigeon
    * @param time Estimated time to leave current allowed area
    * @param dist Estimated distance to edge of allowed area
    */
   protected void onPigeonStatus(Pigeon pigeon, long time, long dist) {
     
      info("Pigeon status available!");
      
   }
   
   /**
    * The pigeon have crossed the fence and is outside the enclosure.
    * 
    * @param pigeon The pigeon at large
    */
   protected void onPigeonOutsideFence(Pigeon pigeon) {
      
      info("Pigeon is outside the fence!");
      
   }
   
   /** 
    * The pigeon is idle
    * 
    * @param pigeon The pigeon at idle
    * @param time Estimated time to leave current allowed area
    * @param dist Estimated distance to edge of allowed area
    */
   protected void onPigeonIdle(Pigeon pigeon, long time, long dist) {
      
      info("Pigeon is idle!");
      
   }
   
   /**
    * Shared preferences have been changed. Two key actions is PREF_SERVICE
    * and PREF_AREA. The first action PREF_SERVICE sets the location service. 
    * The area to enclose is set by the PREF_AREA.
    * 
    * @param prefs Shared Preferences
    * @param key Key effected
    */
   @Override
   public void onSharedPreferenceChanged(
         SharedPreferences prefs, String key) {
      
      if(key.equals(Key.PREF_SERVICE)) {
         
         setService(prefs.getInt(key, -1));
         
      } else if (key.equals(Key.PREF_AREA)) {
         
         setArea(prefs.getInt(key, -1));
         
      } 
      
   }
   
   /**
    * Set location service. Take a id value as identifier for service 
    * requested. 
    * 
    * @param service
    */
   protected void setService(int service) {
      
      Bundle bundle = new Bundle();
      bundle.putInt(Key.CARGO_ALARM_DELAY, my_prefs.getInt(Key.PREF_ALARM_DELAY, 10));
      bundle.putInt(Key.CARGO_ALARM_STATUS, service);
      
      Intent intent = new Intent(EnclosureApp.LOCATION_UPDATE);
      intent.putExtras(bundle);
      
      sendBroadcast(intent);
      
   }
   
   private void setArea(int area) {
      
      Log.i(TAG, "Set area");
      
   }
   
   /** 
    * Get a tag for this activity, used for logging
    * to mark origin of message.
    */
   protected String getTag() {
      
      return TAG;
      
   }
   
   /** 
    * Set a tag for child activity, used for logging
    * to mark origin of message.
    */
   protected void setTag(Activity child) {
      
      this.TAG = child.getClass().getSimpleName();
      
   }

   /**
    * Send a message to the log.
    * 
    * @param msg Text message
    */
   public void info(String msg) {
      
      Log.i(TAG, msg);
      
   } 

   /**
    * Send a message to the debug log and display it using Toast.
    * 
    * @param msg Text message
    */
   public void trace(String msg) {
      
      Log.d(TAG, msg);
      
      Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      
      
   }
   
   private BroadcastReceiver LocationAvailableReciver = new BroadcastReceiver() {
      
      @Override
      public void onReceive(Context ctxt, Intent intent) {

         Log.d(TAG, "On recive: New location available");

         Bundle cargo = intent.getExtras();
         
         if (cargo.containsKey(Key.CARGO_LOCATION)) {
            
            onLocationChanged((Location) cargo.get(Key.CARGO_LOCATION));
            
         }

      }

   };
   
   private BroadcastReceiver PigeonReciver = new BroadcastReceiver() {
      
      @Override
      public void onReceive(Context ctxt, Intent intent) {
         
         final int PIGEON_IDLE = EnclosureApp.PIGEON_IDLE.hashCode();
         
         EnclosureApp app = (EnclosureApp) getApplication();
         Pigeon pigeon = app.getPigeon();
         
         Bundle cargo = intent.getExtras();
         long time = cargo.getLong(Key.CARGO_ALARM_DELAY, 300);
         long dist = cargo.getLong(Key.CARGO_RADIOUS, 50);
         
         String action = intent.getAction();
         
         if (action.equals(EnclosureApp.PIGEON_STATUS)) {
            
            onPigeonStatus(pigeon, time, dist);

         } else if (action.equals(EnclosureApp.PIGEON_OUTSIDE_FENCE)) {
            
            onPigeonOutsideFence(pigeon);
         
         } else if (action.equals(EnclosureApp.PIGEON_IDLE)) {
            
            onPigeonIdle(pigeon, time, dist);
            
         } else {
            
            trace("PigeonReciver - Unknown action: " + action);
            
         }

      }

   };

}
