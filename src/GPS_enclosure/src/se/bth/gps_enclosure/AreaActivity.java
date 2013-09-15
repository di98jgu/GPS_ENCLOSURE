package se.bth.gps_enclosure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AreaActivity extends DashboardActivity {

   private ListAdapter my_adapter;
   
   private ListView my_list;
   
   /**
    * Load a list of locations
    * 
    * @param savedInstanceState
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);

      setContentView(R.layout.area_view);
      setActivityTitle(R.id.title_text);
      
      my_adapter = new ArrayAdapter<String>(
            this, R.layout.radiolist, R.id.radiolist_text, Route.Area.LIST);
      
      my_list = (ListView) findViewById(R.id.area_list);
      
      my_list.setAdapter(my_adapter);
      my_list.setItemsCanFocus(false);
      my_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      my_list.setOnItemClickListener(my_click_listener);
      // my_list.setItemChecked(my_prefs.getInt(Key.PREF_AREA, 0), true);
      
   }
   
   
   public void onListItemClick(
         ListView parent, CheckedTextView v, int position, long id) {
      
      info("Area selected: " + Route.Area.LIST[position]);
      
      SharedPreferences.Editor editor = my_prefs.edit();
      editor.putString(Key.PREF_AREA_NAME, Route.Area.LIST[position]);
      editor.putInt(Key.PREF_AREA, position);
      
      editor.commit();

   }
   
   /*
   private static final String[] GENRES = new String[] {
       "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
       "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
   };
   */
   
   private AdapterView.OnItemClickListener my_click_listener = 
         new AdapterView.OnItemClickListener() {

      public void onItemClick(
            AdapterView<?> parent, View v, int position, long id) {

         onListItemClick((ListView) parent, (CheckedTextView) v, position, id);

      }

   };

}
   
