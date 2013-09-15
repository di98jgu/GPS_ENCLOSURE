package se.bth.gps_enclosure.enclosure;

public interface Fence {
           
   public boolean containPosition(long[] position);
      
   public boolean insideFence(long[] position);
      
   public boolean hasFence();
   
   public long distFence(long[] position);

}
