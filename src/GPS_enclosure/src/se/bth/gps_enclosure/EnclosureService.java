package se.bth.gps_enclosure;

import se.bth.gps_enclosure.enclosure.Area;
import se.bth.gps_enclosure.enclosure.Enclosure;
import se.bth.gps_enclosure.trek.Movement;
import se.bth.gps_enclosure.trek.Pigeon;
import se.bth.gps_enclosure.trek.PigeonListener;
import se.bth.gps_enclosure.trek.Position;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class EnclosureService extends IntentService implements PigeonListener {

   private static String TAG = EnclosureService.class.getSimpleName();
   
   private static final Intent my_intent = new Intent();
   private static final long default_max_dist = 100;
   private static final long default_min_time = 60;
   
   private Pigeon my_pigeon;
   private Enclosure my_enclosure;
   
   public EnclosureService() {
      
      super(TAG);
      
   }
   
   @Override
   public void onCreate() {
      
      super.onCreate();
      
      Log.d(TAG, "Enter on Create");
      
      EnclosureApp app = (EnclosureApp) getApplication();
      
      this.my_pigeon = app.getPigeon();
      this.my_enclosure = app.getEnclosure();
      
      this.my_pigeon.addListener(this);
      
      
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      
      Log.i(TAG, "Enter Enclosure sevice...");
      
      Bundle cargo = intent.getExtras();
      
      if (!cargo.containsKey(Key.CARGO_LOCATION)) return;
      
      Location location = (Location) cargo.get(Key.CARGO_LOCATION);
      
      my_pigeon.updatePosition(location);
      sendPigeonBroadcast(EnclosureApp.PIGEON_STATUS, my_pigeon);

   }
   
   /**
    * Pigeon have left allocated area.
    */
   @Override
   public void onLeaveArea(Pigeon pigeon, Position pos) {
      
      Log.d(TAG, "Pigeon have left the area");
      
      long[] coord = pos.get();
      
      Area area = my_enclosure.getArea(coord);
      
      if (area == null) {
         
         sendPigeonBroadcast(EnclosureApp.PIGEON_OUTSIDE_FENCE, pigeon);
         
      } else if (area.hasFence() && !area.insideFence(coord)) {
         
         sendPigeonBroadcast(EnclosureApp.PIGEON_OUTSIDE_FENCE, pigeon);
         
      } else {
         
         pigeon.updateArea(area);
         
      }
      
   }

   @Override
   public void onDestroy() {
      
      super.onDestroy();
      
      Log.d(TAG, "Enter on Destroy");
      
      my_pigeon.removeListener();
      
      EnclosureApp app = (EnclosureApp) getApplication();
      
      app.putPigeon(my_pigeon);
      app.putEnclosure(my_enclosure);

   }
   
   private void sendPigeonBroadcast(String action, Pigeon pigeon) {
      
      Bundle cargo = new Bundle();
      
      long dist = my_pigeon.freedom();
      long time = Movement.time(dist, my_pigeon.speed());
      long[] at = my_pigeon.atPosition();
      
      cargo.putLong(Key.CARGO_PIGEON_RADIOUS, dist);
      cargo.putLong(Key.CARGO_PIGEON_TIME, time);
      cargo.putLong(Key.CARGO_PIGEON_X, at[Position.x]);
      cargo.putLong(Key.CARGO_PIGEON_Y, at[Position.y]);
      
      my_intent.setAction(action);
      my_intent.putExtras(cargo);
      
      sendBroadcast(my_intent);
   
   }
}
