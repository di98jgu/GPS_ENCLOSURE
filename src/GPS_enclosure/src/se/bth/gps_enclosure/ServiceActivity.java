package se.bth.gps_enclosure;

import java.util.Locale;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ServiceActivity extends DashboardActivity 
   implements RadioGroup.OnCheckedChangeListener {

   /**
    * Select location service. It can be deactivated, automatic or manual. 
    * 
    * @param savedInstanceState
    */
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);

      setContentView(R.layout.service_view);
      setActivityTitle(R.id.title_text);
      
      int service = my_prefs.getInt(Key.PREF_SERVICE, -1);
      
      RadioGroup my_group = (RadioGroup) findViewById(R.id.radiogroup_service);
      my_group.check(service);
      my_group.setOnCheckedChangeListener(this);
      
      checkUpdateNow(service == R.id.manual_service);
      
   }
   
   /**
    * Show current position for user if a position is available.
    * 
    * @param v View
    */
   public void onUpdateNow(View v) {
      
      setService(R.id.service_update_now);
      
      setStatusInfo("Updating location...");
      
   }
   
   /**
    * Check button update now, that is enable or disable it.
    * 
    * @param enable Enable button update now
    */
   private void checkUpdateNow(boolean enable) {
      
      Button button = (Button) findViewById(R.id.service_update_now);
      
      button.setEnabled(enable);
      
   }
   
   /**
    * The user have switched status of location service.
    */
   @Override
   public void onCheckedChanged(RadioGroup group, int checkedId) {
      
      SharedPreferences.Editor editor = my_prefs.edit();
      editor.putInt(Key.PREF_SERVICE, checkedId);
      editor.putString(Key.PREF_SERVICE_NAME, getResources().getResourceEntryName(checkedId));
      
      editor.commit();
      
      checkUpdateNow(checkedId == R.id.manual_service);
      
      setStatusInfo("");
      
   }
   
   @Override
   public void onLocationChanged(Location location) {
      
      String info = String.format(
            Locale.ENGLISH, "Longitude: %f\nLatitude: %f", 
            location.getLongitude(), location.getLatitude());
      
      setStatusInfo(info);
      
   }
   
   /**
    * Show info for user. 
    * 
    * @param info Message to show
    */
   private void setStatusInfo(String info) {
      
       TextView txt = (TextView) findViewById(R.id.text_service_info);
      
       txt.setText(info);
      
   }

}
