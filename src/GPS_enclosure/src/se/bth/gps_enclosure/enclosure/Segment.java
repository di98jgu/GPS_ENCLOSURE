
package se.bth.gps_enclosure.enclosure;

/**
 * A segment is a straight line between a and b in Eucliden plane.
 */
public class Segment extends EuclidPlane {
   /**
    * Length between a and b.
    */
   private long length;
   
   /**
    * Need init values and thus this is private.
    */
   @SuppressWarnings("unused")
   private Segment() { }
   
   /**
    * Create a new segment. Calculate slope, distance
    * and direction between the two positions.
    * 
    * @param start Line start at this position
    * @param end Line end at this position
    * @param scale Scale of line
    */
   public Segment(long[] start, long[] end, int scale) {
      
      super(start, end, scale);
      
      this.length = vectorLength(deltaX(), deltaY());
      
   }
      
   /**
    * Return <code> true </code> if intersection numerator is
    * positive or zero for a given point.
    * 
    * @param p Point
    * @param a Start of segment
    * @param b End of segment
    * @return <code> true </code> if intersection numerator
    * is positive
    */
   private boolean pNum(long[] p, long[] a, long[] b) {
      
      return 
         (a[x] - p[x])*(b[y] - p[y]) >=
         (a[y] - p[y])*(b[x] - p[x]);
         
   }
   
   /**
    * Return <code> true </code> if this segment intersect given 
    * segment. The end points is included. 
    * 
    * @param segment A Segment
    * @return boolean <code> true </code> if segments intersect
    */
   public boolean intersect(Segment segment) {
      
      long[] a = new long[] {
         origin[x], origin[y]};
         
      long[] b = new long[] {
         origin[x] + deltaX(), origin[y] + deltaY()};
         
      long[] p = new long[] {
         segment.origin[x], segment.origin[y]};
         
      long[] q = new long[] {
         segment.origin[x] + segment.deltaX(), 
         segment.origin[y] + segment.deltaY()};
         
      // Two points on each side of a segment will have 
      // different sign if a line between the two points
      // intersect the segment. 
      // 
      // This neat trick was posted by Peter Wone at stackoverflow
      
      return 
         (pNum(a, p, q) != pNum(b, p, q)) && 
         (pNum(p, a, b) != pNum(q, a, b));
   
   }
   
   /**
    * Point of intersection between this segment and a given
    * segment. The point of intersection is returned as a value n
    * from which the intersection is given by (n*deltaX(), 
    * n*deltaY()). Thus if n is between 0 and 1 this and 
    * given segment intersect. 
    * 
    * @param segment A segment 
    * @return A value n from which the intersection is given 
    * by (n*deltaX(), n*deltaY()).
    */
   public double intersectAt(Segment segment) {
      
      long cross = crossProduct(
         new long[] {deltaX(), deltaY()},
         new long[] {segment.deltaX(), segment.deltaY()});
         
      if (cross == 0) {
         
         // Parallel lines so no intersection
         return Double.MAX_VALUE;
         
      }
      
      long num =
         segment.deltaX() * (origin[y] - segment.origin[y]) -
         segment.deltaY() * (origin[x] - segment.origin[x]);
      
      return ((double) num) / ((double) cross);
      
   }
   
   /** 
    * Shortest distance from a point to this segment
    * http://geomalgorithms.com/a02-_lines.html
    * @param point A point
    * @param Distance to point
    */
   public long distance(long[] point) {
      
      // Vector from origin to point
      long[] OP = new long[] {
         (point[x] - origin[x]), (point[y] -  origin[y])};
     
      long dot = dotProduct(
         new long[] {deltaX(), deltaY()}, OP);
      
      // Check if end points of this segment is closest to point
      if (dot <= 0) {
         
         return vectorLength(OP[x], OP[y]);
            
      } else if (dot >= (length * length)) {
         
         return vectorLength(
            point[x] - (origin[x] + deltaX()), 
            point[y] - (origin[y] + deltaY()));
               
      }
      
      long cross = Math.abs(crossProduct(
         new long[] {deltaX(), deltaY()}, OP));
      
      return Math.round(((double) cross) / ((double) length)); 
      
   }
   /** 
    * Return <code> true </code> if this line is horizontal.
    * 
    * @return <code> true </code> if horizontal
    */
   public boolean isHorizontal() {
      
      return (deltaY() == 0)? true: false;
      
   }
   
   /** 
    * Return <code> true </code> if this line is vertical.
    * 
    * @return <code> true </code> if vertical
    */
   public boolean isVertical() {
      
      return (deltaX() == 0)? true: false;
      
   }
   
   /**
    * Sign of slope or 0 if it is horizontal or vertical.
    * 
    * @return Sign of slope
    */
   public int sign() {
      
      return (int) sign_x * sign_y;
      
   }
   
   /** 
    * Expand this line by given amount without moving the origin.
    * 
    * @param amount Amount to expand this line
    */
   protected void expand(long[] amount) {
         
      super.expand(amount);
      
      length = vectorLength(deltaX(), deltaY());
   
      return;
      
   }
   
   /**
    * Magnitude of cross product between two vectors.
    * 
    * @param u Vector u
    * @param v Vector v
    * @return Magnitude of cross product
    */
   private long crossProduct(long[] u, long[] v) {
      
      return v[y] * u[x] - v[x] * u[y];
      
   }
   
   /**
    * Dot product between two vectors.
    * 
    * @param u Vector u
    * @param v Vector v
    * @return Dot product
    */
   private long dotProduct(long[] u, long[] v) {
      
      return u[x] * v[x] + u[y] * v[y];
      
   }
   
   /**
    * Return length of a vector.
    * 
    * @param dx Delta x
    * @param dy Delta y
    * @return Length of vector
    */
   private long vectorLength(long dx, long dy) {
      
      return Math.round(Math.hypot(
         ((double) dx), ((double) dy)));
         
   }

}
