package se.bth.gps_enclosure.trek;

import android.content.SharedPreferences;

/**
 * Interface for storing objects. Obviously loosely based on Parcelable but
 * the aim here is to store and restore data from disk. This interface was
 * created for the application Enclosure. A more generic container should 
 * of course be used but there is no need for such. 
 * 
 * @author Jim Gunnarsson
 */

public interface Storable {
   
   /**
    * Store the content of this object in shared preferences. 
    * 
    * @param dest
    */
   public void writeToPreferences(SharedPreferences dest);
   
   /** 
    * Interface that must be implemented and provided as a public CREATOR
    * field that generates instances of your Storable class from shared 
    * preferences.
    * 
    * @author Jim Gunnarsson
    *
    * @param <T> The Storable class
    */
   public interface Creator<T> {
      
      /**
       * Create a new instance of the Storable class, instantiating it from
       * given shared preferences whose data had previously been written by
       * {@link Storable#writeToPreferences Storable.writeToPreferences()}.
       * 
       * @param source
       * @return
       */
      public T createFromPreferences(SharedPreferences source);
      
      /**
       * Create a new array of the Storable class.
       * 
       * @param size Size of array
       * @return A array of the Storable class
       */
      public T[] newArray(int size);

   }

}
