package se.bth.gps_enclosure.enclosure;


/**
 * This is a Euclidean plane defined by two points. All coordinates and
 * data is given as integers to avoid issues regarding floating point 
 * values. A scale factor is provide mean of transforming integers to
 * floating point values.
 */
public class EuclidPlane {
   
   /**
    * Index of x
    */
   public static final int x = 0;
   
   /**
    * Index of y
    */
   public static final int y = 1;
   
   /**
    * Origin or the position of this plane.
    */
   protected long[] origin;
   
   /**
    * Length of the two sides of this plane.
    */
   protected long[] side;
   
   /**
    * Lower left corner of this plane
    */
   protected long[] base;
   
   /**
   * Sign of delta x or zero if this plane is a vertical line 
   */
   protected int sign_x;
   
   /** 
    * Sign of delta y or zero if this plane is a horizontal line
    */
   protected int sign_y;
   
   /**
   * Scale factor this plan, default 1.
   */
   protected int scale;
   
   /**
    * Need some basic info about the plane in order to create it so 
    * the default constructor is only for subclasses.
    */
   protected EuclidPlane() {}
   
   /**
    * Create a new plane with a default scale of 1. Scale is used
    * for translating long coordinates to double. 
    * 
    * @param origin Origin of plane
    * @param plane Sides of this plane
    */
   public EuclidPlane(long[] origin, long[] plane) {
   
      this(origin, plane, 1);
   
   }
   
   /**
    * Create a new plane with a given scale. Scale is used
    * for translating long coordinates to double. 
    * 
    * @param origin Origin of plane
    * @param plane Sides of this plane
    * @param scale Scale of this plane
    */
   public EuclidPlane(long[] origin, long[] plane, int scale) {
   
      this.origin = origin;
      this.scale = scale;
      
      long dx = plane[x];
      long dy = plane[y];
      
      this.sign_x = Long.signum(dx);
      this.sign_y = Long.signum(dy);
      
      this.side = new long[] {
         Math.abs(dx), Math.abs(dy)};
      
      this.base = new long[] {
         Math.min(origin[x], plane[x]), 
         Math.min(origin[y], plane[y])};
      
   
   }
   
   /**
    * Position of this plane or origin. 
    * 
    * @return Position of this plane
    */
   public long[] position() {
      
      return new long[] {origin[x], origin[y]};
      
   }
   
   /**
    * Size of this plane.
    * 
    * @return Size of this plane
    */
   public long[] size() {
      
      return new long[] {side[x], side[y]};
      
   }
   
   /**
    * Difference between origin and boundary of plane along the x-axis.
    */
   public long deltaX() {
   
      return (side[x] ^ sign_x) - sign_x;
   
   }
   
   /**
    * Difference between origin and boundary of plane along the y-axis.
    */
   public long deltaY() {
   
      return (side[y] ^ sign_y) - sign_y;
   
   }
   
   /** 
    * Relative position to origin of this plane given a absolute 
    * position. A position greater then this plane is folded back.
    * 
    * @param position Absolute position
    * @return Relative position
    */
   public long[] relativePos(long[] position) {
      
      return new long[] {
         position[x] - origin[x] % deltaX(),
         position[y] - origin[y] % deltaY()};
      
   }
   
   /**
    * Default scale factor for this plane.
    * 
    * @return Scale of this plane.
    */
   public int getScale() {
   
      return scale;
   
   }
   
   /**
    * Set default scale factor for this plane.
    * 
    * @return Scale of this plane.
    */
   public void setScale(int scale) {
   
      this.scale = scale;
   
   }
   
   /**
    * Return <code> true </code> if a given position is contained 
    * within this plane.
    * 
    * @param position Position to test
    * @return <code> true </code> if position is inside 
    * else <code> false </code
    */
   public boolean contains(long[] position) {
         
      return (
         (position[x] > base[x]) && 
         (position[y] > base[y]) &&
         (position[x] < (base[x] + side[x])) &&
         (position[y] < (base[y] + side[y])));
      
   }
   
   /**
    * Return <code> true </code> if a given plane overlap this 
    * plane. A plane may completely enclose another. 
    * 
    * @param plane A plane
    * @return <code> true </code> if plane overlap
    */
   public boolean overlap(EuclidPlane plane) {
      
      // (X1 or X2) and (Y1 or Y2)
      return 
         (((plane.base[x] > base[x]) && 
         (plane.base[x] < (base[x] + side[x]))) || 
         ((base[x] >= plane.base[x]) && 
         (base[x] < (plane.base[x] + plane.side[x])))) && 
         (((plane.base[y] > base[y]) && 
         (plane.base[y] < (base[y] + side[y]))) || 
         ((base[y] >= plane.base[y]) && 
         (base[y] < (plane.base[y] + plane.side[y]))));
      
   }
   
   /** 
    * Expand this plane by given amount without moving the origin.
    * 
    * @param amount Amount to expand this plane
    */
   protected void expand(long[] amount) {
         
      long dx = deltaX() + amount[x];
      long dy = deltaY() + amount[y];
      
      sign_x = Long.signum(dx);
      sign_y = Long.signum(dy);
      
      side[x] = Math.abs(dx);
      side[y] = Math.abs(dy);
      
      base[x] = Math.min(origin[x], origin[x] + dx);
      base[y] = Math.min(origin[y], origin[y] + dy);
   
      return;
      
   }
}
