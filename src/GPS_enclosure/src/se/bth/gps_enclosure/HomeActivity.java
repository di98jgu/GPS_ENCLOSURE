package se.bth.gps_enclosure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends DashboardActivity {

   /**
    * Start activity for this application.
    * 
    * @param savedInstanceState
    */
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);

      setContentView(R.layout.home_view);
      setActivityTitle(R.id.title_text);
      
   }
   
   /**
    * Handle the click of a button on the dashboard. The activity
    * for the particular button is started.
    * 
    * @param v Clicked view
    */
   public void onClickDbButton(View v) {
      
      int id = v.getId();
      
      switch (id) {
      case R.id.DbButton_area:
         startActivity(new Intent(getApplicationContext(), AreaActivity.class));
         break;
      case R.id.DbButton_run:
         startActivity(new Intent(getApplicationContext(), ServiceActivity.class));
         break;
      case R.id.DbButton_debug:
         startActivity(new Intent(getApplicationContext(), LogActivity.class));
         break;
      default:
         break;
      }
      
   }
}
