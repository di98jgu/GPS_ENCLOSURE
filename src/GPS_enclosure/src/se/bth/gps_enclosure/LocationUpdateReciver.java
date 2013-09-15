package se.bth.gps_enclosure;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LocationUpdateReciver extends BroadcastReceiver {

   private static final String TAG = LocationUpdateReciver.class.getSimpleName();
   private static Intent update_location = new Intent(EnclosureApp.LOCATION_SERVICE);
   private static PendingIntent prev_operation;
   
   @Override
   public void onReceive(Context ctxt, Intent intent) {
      
      Bundle cargo = intent.getExtras();
      
      if (cargo == null) return;
      
      int delay = getDelay(cargo, 0);
      int type = getAlarmType(cargo, -1);
      
      cargo.putLong(Key.CARGO_TIME, getTime(delay));
      cargo.putInt(Key.CARGO_RADIOUS, 100);
      
      update_location.putExtras(cargo);
      
      switch (type) {
      
      case R.id.run_service:
         setPendingService(ctxt, delay);
         break;
      case R.id.stop_service:
         cancleService(ctxt);
         break;
      case R.id.manual_service:
         cancleService(ctxt);
         break;
      case R.id.service_update_now:
         
         Log.d(TAG, "Service update now");
       
         setService(ctxt);
         break;
         
      case R.id.call_service:
         
         Log.d(TAG, "Call service");
       
         setService(ctxt);
         break;
      default:
         break;
      }
      
   }
   
   private void setPendingService(Context ctxt, int delay) {
      
      PendingIntent operation = PendingIntent.getService(
            ctxt, -1, update_location, PendingIntent.FLAG_UPDATE_CURRENT);
                          
      AlarmManager alarm = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
      alarm.cancel(operation);
      
      Log.i(TAG, "Set pending service");
      
      alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (delay * 1000), operation);
      
      prev_operation = operation;
      
   }
   
   private void setService(Context ctxt) {
      
      if(prev_operation != null) {
         
         cancleService(ctxt);
         prev_operation = null;
         
      }
      
      Log.i(TAG, "Set service");
      
      ctxt.startService(update_location);
      
   }
   
   private void cancleService(Context ctxt) {
      
      if (prev_operation != null) {
                          
         AlarmManager alarm = 
               (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
         
         Log.i(TAG, "Cancle service");
         
         alarm.cancel(prev_operation);
         
      }
      
   }
   
   private int getDelay(Bundle bundle, int value) {
      
      return (bundle != null && bundle.containsKey(Key.CARGO_ALARM_DELAY))? 
            bundle.getInt(Key.CARGO_ALARM_DELAY): value;
      
   }
   
   private int getAlarmType(Bundle bundle, int value) {
      
      return (bundle != null && bundle.containsKey(Key.CARGO_ALARM_STATUS))? 
            bundle.getInt(Key.CARGO_ALARM_STATUS): value;
      
   }
   
   private long getTime(int delay) {

      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.add(Calendar.SECOND, delay);

      return calendar.getTimeInMillis();

   }

}
