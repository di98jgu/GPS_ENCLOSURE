package se.bth.gps_enclosure.enclosure;

/**
 * Describe a boundary box for a object in Euclidean plane. This box is not mutable. 
 */
public class Boundary extends EuclidPlane {
   
   /**
    * A boundary without bounds makes no sense
    */
   @SuppressWarnings("unused")
   private Boundary() {}
   
   /**
    * Create a new boundary box with a given scale factor.
    * 
    * @param origin Origin of boundary box
    * @param size Size of boundary box
    * @param scale Scale of this boundary
    */
   public Boundary(long[] origin, long[] size, int scale) {
      
      super(origin, size, scale);
      
   }
   
   /**
    * Create a new boundary box. Take a point of origin and opposite
    * corner.
    * 
    * @param origin Origin of boundary box
    * @param size Size of boundary box
    */
   public Boundary(long[] origin, long[] size) {
      
      this(origin, size, 1);
      
   }
   
   public Boundary(EuclidPlane plane) {
      
      this(
         plane.origin, 
         new long[] {
            plane.origin[x] + (plane.side[x] * plane.sign_x), 
            plane.origin[y] + (plane.side[y] * plane.sign_y)},
         plane.scale);
      
   }
   
   /**
    * Size of this boundary as a double. The default scale factor is
    * used to scale the result. Result = size / scale.
    * 
    * @return Size of this boundary
    */
   public double[] sizeDouble() {
      
      double k = 1 / ((double) scale);
      
      return _sizeDouble(k);
      
   }
   
   /**
    * Size of this boundary. A zoom factor might be provided to scale 
    * return value. This does not effect the default scale factor.
    * Result = (size * zoom) / scale.
    * 
    * @param zoom Zoom factor
    * @return Size of this boundary
    */
   public double[] sizeDouble(float zoom) {
      
      double k = zoom / ((double) scale);
      
      return _sizeDouble(k);
      
   }
   
   /**
    * Size of this boundary.
    * 
    * @param k Scale factor
    * @return Size of this boundary
    */
   private double[] _sizeDouble(double k) {
      
      return new double[] {
         ((double) side[x]) * k, ((double) side[y]) * k};
      
   }
   
   /**
    * Position of this boundary as double.
    * 
    * @return Position of this boundary
    */
   public double[] positionDouble() {
      
      double k = 1 / ((double) scale);
      
      return _position(k);
      
   }
   
   /**
    * Position of this boundary as a double. A zoom factor might be 
    * given.
    * 
    * @param zoom Zoom factor 
    * @return Position of this boundary
    */
   public double[] positionDouble(int zoom) {
      
      double k = zoom / ((double) scale);
      
      return _position(k);
      
   }
   
   /**
    * Position of this boundary. 
    * 
    * @param k Scale factor
    * @return Position of this boundary
    */
   public double[] _position(double k) {
      
      long[] pos = position();
      
      return new double[] {
         ((double) pos[x]) * k, ((double) pos[y]) * k};
      
   }
   
   /**
    * Return <code> true </code> if a given position is contained 
    * within this boundary box.
    * 
    * @param pos Position to test
    * @return <code> true </code> if position is inside else 
    * <code> false </code
    */
   public boolean contains(double[] position) {
      
      long[] pos = new long[] {
         Math.round(position[x] * scale),
         Math.round(position[y] * scale)};
         
      return super.contains(pos);
      
   }
   
   /**
    * Minimum distance to edge of this boundary given a position
    * 
    * @param position Reference position
    * @return Minimum distance to edge
    */
   public long minEdgeDistance(long[] position) {
      
      long min_x = Math.min(
         Math.abs(position[x] - base[x]),
         Math.abs(position[x] - (base[x] + side[x])));
      
      long min_y = Math.min(
         Math.abs(position[y] - base[y]),
         Math.abs(position[y] - (base[y] + side[y])));
         
      return Math.min(min_x, min_y);
      
   }
   
   /**
    * Minimum distance to edge of this boundary given a position
    * 
    * @param position Reference position
    * @return Minimum distance to edge
    */
   public long minEdgeDistance(double[] position) {
      
      long[] pos = new long[] {
         Math.round(position[x] * scale),
         Math.round(position[y] * scale)};
         
      return minEdgeDistance(pos);
      
   }
   
   /** 
    * Expanding this boundary is not allowed.
    * 
    * @param amount Not used
    */
   public void expand(long[] amount) { }
   
}
   
