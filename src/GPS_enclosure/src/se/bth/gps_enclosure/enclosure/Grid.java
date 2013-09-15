
package se.bth.gps_enclosure.enclosure;

/**
 * Describe a uniform grid in Euclidean plane. This grid consists of gridlines 
 * and squares. Squares have a position and size but content is not defined. The 
 * grid start at position a and end at b. A The corner closest to position a is 
 * the position for the square. Its location is counted from a starting at 0 and 
 * then row by row to b. 
 * 
 * @author Jim Gunnarsson
 */
public class Grid extends EuclidPlane {
   
   /**
    * Grid data for this grid
    */
   private GridData grid = null;
   
   /**
    * Create a new grid. Number of cells is set by resolution. Size
    * is the min size of this grid. The size is adjusted so that
    * the size of each cell is equal.
    * 
    * @param res Resolution of this grid, number of cells.
    * @param origin Position for grid.
    * @param size Total size for grid
    */
   public Grid(int[] res, long[] origin, long[] size) {
      
      this(res, origin, size, 1);
         
   }
   
   
   /**
    * Create a new grid. Number of square is set by resolution. Size
    * is the total size of this grid. The size is adjusted so that
    * the size of cells is equal. Since grid uses long and not double a 
    * scale might be provided to get desired precision. Default is 1.
    * 
    * @param res Resolution of this grid, number of squares.
    * @param origin Position for grid.
    * @param size Total size for grid, make sure that this greater
    * then origin if you don't wired result.
    * @param scale Default scale factor for gridplane
    */
   public Grid(int[] res, long[] origin, long[] size, int scale) {
      
      super(origin, size, scale);
      
      this.grid = new GridData();
      
      this.grid.row = res[x];
      this.grid.column = res[y];
      
      this.grid.cell_size = new long[] { 
         (long) Math.ceil((double) size[x] / res[x]),
         (long) Math.ceil((double) size[y] / res[y])};
         
      this.grid.cell_total = grid.row * grid.column;
      
      this.grid.cell_type = new int[grid.cell_total];
      
      this.grid.lines_x = 
         gridLines(origin[x], grid.row, grid.cell_size[x]);
         
      this.grid.lines_y = 
         gridLines(origin[y], grid.column, grid.cell_size[y]);
      
      // Need to adjust grid plane so it match the actually size
      // of this grid.
      long[] diff = new long[] {
         this.grid.lines_x[grid.row] - size[x],
         this.grid.lines_y[grid.column] - size[y]};
         
      expand(diff);
      
   }
   
   /**
   * Number of cells in x and y direction for this grid.
   * 
   * @return Number of cells in x and y direction
   */
   public int[] resolution() {
      
      return new int[] {
         grid.row,
         grid.column};
      
   }
   
   /**
    * Return location of cell for a position within this grid. A negative 
    * value is returned if position falls outside the grid. 
    * 
    * @param position A position within this grid.
    * @return int Location of cell or -1 in case position is outside the grid
    */
   public int location(long[] position) {
      
      int loc = -1;
      int[] index;
      
      if (contains(position)) {
         
         long[] r_pos = relativePos(position);
         
         index = new int[] {
            (int) Math.floor(
               ((double) r_pos[x]) / grid.cell_size[x]),
            (int) Math.floor(
               ((double) r_pos[y]) / grid.cell_size[y])};
         
         loc = index[y] * grid.row + index[x];
         
      }
      
      return loc;
      
   }
   
   /**
    * Location of cell given a current location and a offset. The
    * offset is given as columns and rows. 
    * 
    * @param location Current location
    * @param offset Columns and rows to new location
    * @return int Location of cell or -1 in case location is 
    * outside the grid
    */
   public int cellLocation(int location, int[] offset) {
   
      location += offset[x] + offset[y] * grid.row;
      
      return 
            ((location >= 0) && (location < grid.cell_total))? location: -1;
   
   }
   /**
   * Position for a cell. The position for a cell is the corner 
   * closest to origin of this grid.
   * 
   * @param location Location of cell
   * @return Position of cell
   * 
   */
   public long[] cellPosition(int location) {
      
      long[] pos = new long[] {
         grid.lines_x[location % grid.row],
         grid.lines_y[(location - location % grid.row) / grid.row]};
         
      return pos;
      
   }
   
   /**
   * Size of cell in this grid. All cells have the same size.
   * 
   * @return Size of cell
   */
   public long[] cellSize() {
      
      return grid.cell_size;
      
   }
   
   /**
   * Set cell type for cell at given location.
   * 
   * @param location Location of cell
   * @param type New cell type
   */
   public void cellType(int location, int type) {
      
      grid.cell_type[location] = type;
   
      return;
      
   }
   
   /**
   * Get cell type for cell at given location.
   * 
   * @param location Cell location
   * @return type Type of cell
   */
   public int cellType(int location) {
      
      return grid.cell_type[location];
      
   }
   
   /**
   * Number of cells in this grid.
   * 
   * @return Number of cells
   */
   public int numOfCells() {
      
      return grid.cell_total;
      
   }
   
   /** 
    * Create gridlines for this grid. 
    * 
    * @param offset Position of first line
    * @param cells Number of cells
    * @param size Size of cell
    * @return Position gridlines
    */
   private long[] gridLines(long offset, int cells, long size) {
      
      // Grid start with a line and end with a line.
      long[] lines = new long[cells + 1];
         
      lines[0] = offset;
      
      for (int i = 1; i <= cells; i++) {
         
         lines[i] = lines[i-1] + size;
            
      }
      
      return lines;
      
   }
   
   class GridData {
      
      /**
      * Type of cell
      */
      public int[] cell_type;
      
      /**
      * Size of a cell in this grid. Every cell have a uniform size.
      */
      public long[] cell_size;
      
      /**
      * Total number of cells in this grid.
      */
      public int cell_total;
      
      /**
      * Number of cells in a row.
      */
      public int row;
      
      /**
       * Number of cells in a column.
       */
      public int column;
      
      /**
      * Grid lines in x direction starting at 0.
      */
      public long[] lines_x;
      
      /**
      * Grid lines in y direction starting at 0.
      */
      public long[] lines_y;
   
   }

}
