package se.bth.gps_enclosure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LocationAvailableReciver extends BroadcastReceiver {

   protected static String TAG = LocationAvailableReciver.class.getSimpleName();
   
   private static Intent service = new Intent(EnclosureApp.LOCATION_SERVICE);
   
   @Override
   public void onReceive(Context ctxt, Intent intent) {

      Log.d(TAG, "On recive: New location available");

      Bundle cargo = intent.getExtras();
      
      if (cargo.containsKey(Key.CARGO_LOCATION)) {
         
         service.putExtras(cargo);
         ctxt.startService(service);
         
      }

   }

}
