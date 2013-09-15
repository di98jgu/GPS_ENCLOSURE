package se.bth.gps_enclosure;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogActivity extends DashboardActivity {

   private LogAdapter my_adapter;
   
   private ListView my_list;
   
   
   /**
    * View all known preferences for the user.
    * 
    * @param savedInstanceState
    */
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);

      setContentView(R.layout.log_view);
      setActivityTitle(R.id.title_text);
      
      Map<String, String> log = getLog();
      
      my_adapter = new LogAdapter(this, log);
      
      my_list = (ListView) findViewById(R.id.LogList);
      
      my_list.setAdapter(my_adapter);
      my_list.setItemsCanFocus(false);
      
   }
   
   /**
    * Wrap all preferences from shared preferences into a map. The issue
    * here is that the preference value might be of any type. 
    * 
    * @return Map with all preferences
    */
   private Map<String, String> getLog() {
      
      Map<String, ?> pref_map = my_prefs.getAll();
      Map<String, String> map = new HashMap<String, String>();
      
      for (Map.Entry<String, ?> entry: pref_map.entrySet()) {
         map.put(entry.getKey(), ((Object) entry.getValue()).toString());
      }
      
      return map;
      
   }
   
   
   private static class LogAdapter extends BaseAdapter {
      
      private LayoutInflater my_inflater;
      private int my_resource = R.layout.log_entry;
      
      private Map<String, String> my_data;
      private String[] my_keys = new String[1];
      

      public LogAdapter(Context ctxt, Map<String, String> rows) {
         
          // Cache the LayoutInflate to avoid asking for a new one each time.
          my_inflater = LayoutInflater.from(ctxt);
          
          my_keys = (rows.keySet()).toArray(my_keys);
          my_data = rows;

      }

      /**
       * The number of items in the list is determined by the number of speeches
       * in our array.
       *
       * @see android.widget.ListAdapter#getCount()
       */
      public int getCount() {
         
          return my_keys.length;
          
      }

      /**
       * Since the data comes from an array, just returning the index is
       * sufficient to get at the data. If we were using a more complex data
       * structure, we would return whatever object represents one row in the
       * list.
       *
       * @see android.widget.ListAdapter#getItem(int)
       */
      public Object getItem(int position) {
         
          return my_data.get(my_keys[position]);
          
      }

      /**
       * Use the array index as a unique id.
       *
       * @see android.widget.ListAdapter#getItemId(int)
       */
      public long getItemId(int position) {
         
          return position;
          
      }

      /**
       * Make a view to hold each row.
       *
       * @see android.widget.ListAdapter#getView(int, android.view.View,
       *      android.view.ViewGroup)
       */
      public View getView(int position, View convertView, ViewGroup parent) {
          
          LogEntry holder;

          if (convertView == null) {
             
              convertView = my_inflater.inflate(my_resource, null);
              
              holder = new LogEntry(convertView);
              convertView.setTag(holder);
              
          } else {
              
              holder = (LogEntry) convertView.getTag();
              
          }

          
          holder.populate(my_keys[position], my_data.get(my_keys[position]));

          return convertView;
      }

      private class LogEntry {
         
          TextView my_name;
          TextView my_value;
          
          public LogEntry(View v) {
             
             my_name = (TextView) v.findViewById(R.id.logEntryName);
             my_value = (TextView) v.findViewById(R.id.logEntryValue);
             
          }
          
          public void populate(String name, String value) {
             
             my_name.setText(name);
             my_value.setText(value);
             
          }
          
      }  

  }
   

} // end class
