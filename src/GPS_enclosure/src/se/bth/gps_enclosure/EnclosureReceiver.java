package se.bth.gps_enclosure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class EnclosureReceiver extends BroadcastReceiver {
   
   private static String TAG = EnclosureReceiver.class.getSimpleName();
   
   private static Intent service = new Intent(EnclosureApp.ENCLOSURE_SERVICE);

   @Override
   public void onReceive(Context ctxt, Intent intent) {
      
      Log.d(TAG, "Update enclosure");
      
      Bundle cargo = intent.getExtras();
      service.putExtras(cargo);
      
      ctxt.startService(service);

   }
   
}
