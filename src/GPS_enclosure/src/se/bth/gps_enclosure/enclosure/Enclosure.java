package se.bth.gps_enclosure.enclosure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Enclosure {
   
   private int x = EuclidPlane.x;
   
   private int y = EuclidPlane.y;
   
   /**
    * Tile table
    */
   private TileTable tiles;
   
   /**
    * Fence sections table
    */
   private SectionTable sections;
   
   /**
    * The grid used by this enclosure
     */
   private Grid grid;
   
   /**
    * Represent a cell in the grid
    */
   private Cell cell = new Cell();
   
   /**
    * Create a new enclosure given a fence. 
    */
   public Enclosure(List<FenceCoord> fence, int[] grid_size) {
   
      this.grid = initGrid(fence, grid_size);
      
      this.sections = new SectionTable();
      this.tiles = new TileTable(grid, sections);
      
      initEnclosure(new FenceIterator(fence), grid, tiles);
   
   }
   
   /**
   * Select tile at position given by a position. A min area of one
   * tile is returned if marker is within this enclosure. If no matching
   * tile is found for given position a null value is returned. 
   *  
   * @param position Reference position
   * @return Area at position if such exist else null
   */
   public Area getArea(long[] position) {
      
      if (grid.contains(position)) {
         
         return new Area(grid, tiles, grid.location(position));
         
      }
      
      return null;
      
   }
   
   /**
   * Check if tile at given position is part of the fence.
   * 
   * @return <code>true</code> if tile is part of fence
   */
   public boolean atFence(long[] position) {
      
      int type = grid.cellType(grid.location(position));
      
      return (type == TileTable.FENCE)? true: false;
      
   }
   
   /**
   * The boundary box for this enclosure. 
   */
   public Boundary boundery() {
      
      return new Boundary(grid);
      
   }
   
   private Grid initGrid(List<FenceCoord> fence, int[] resolution) {
      
      double[] max_value = new double[] {Double.MIN_VALUE, Double.MIN_VALUE};
      double[] min_value = new double[] {Double.MAX_VALUE, Double.MAX_VALUE};
      
      for (FenceCoord coord: fence) {
         
         max_value[x] = 
            (coord.value_x > max_value[x])? coord.value_x: max_value[x];
         max_value[y] =
            (coord.value_y > max_value[y])? coord.value_y: max_value[y];
         min_value[x] =
            (coord.value_x < min_value[x])? coord.value_x: min_value[x];
         min_value[y] =
            (coord.value_y < min_value[y])? coord.value_y: min_value[y];
            
      }
      
      int scale = (int) Math.pow(10, fence.get(0).precision);
      
      long[] origin = new long[] {
         Math.round(min_value[x] * scale),
         Math.round(min_value[y] * scale)};
         
      long[] size = new long[] {
         Math.round((max_value[x] - min_value[x]) * scale),
         Math.round((max_value[y] - min_value[y]) * scale)};
         
      return new Grid(resolution, origin, size, scale);
      
   }
   
   private void initEnclosure(
      FenceIterator fence, Grid grid, TileTable tiles) {
      
      // Grid cell data
      cell.size = grid.cellSize();
      cell.location = grid.location(fence.positionAt(0));
      
      Tile tile = tiles.newTile(cell.location);
      Section section = tile.newSection();
      
      double candle;
      double length_x;
      double length_y;
      
      long[] cut = new long[2];
      
      int edge = Tile.NO_EDGES;
      int prev_edge = Tile.NO_EDGES;
      
      while (fence.hasNext()) {
         
         FenceSegment seg = fence.next();
         
         cell.pos = grid.cellPosition(cell.location);
         
         cell.span[x] = Math.abs(cell.size[x] * seg.norm[x]);
         cell.offset[x] = seg.sign[x];
         
         // Distance from vertex ax to edge of cell
         if (seg.sign[x] > 0) {
            
            length_x = 
                  (cell.size[x] + cell.pos[x] - seg.vertex_a[x]) * seg.norm[x];
         
         } else {
            
            length_x = 
                  (seg.vertex_a[x] - cell.pos[x]) * seg.norm[x] * seg.sign[x];
         
         }
         
         cell.span[y] = Math.abs(cell.size[y] * seg.norm[y]);
         cell.offset[y] = seg.sign[y];
         
         if (seg.sign[y] > 0) {
            
            length_y = 
                  (cell.size[y] + cell.pos[y] - seg.vertex_a[y]) * seg.norm[y];
         
         } else {
               
            length_y = 
                  (seg.vertex_a[y] - cell.pos[y]) * seg.norm[y] * seg.sign[y];
               
         }
      
         // The total sum of length x and y may fall short of segment length 
         // since length of segment is rounded off to nearest integer.
         candle = 1.0;
         
         while (candle < seg.length) {
            
            if (length_x <= length_y) {
               
               candle += length_x;
               length_y -= length_x;
               length_x = cell.span[x];
               
               if (candle < seg.length) {
                  
                  if (seg.sign[x] > 0) {
                     
                     edge = Tile.RIGHT_EDGE;
                     cut[x] = cell.pos[x] + cell.size[x];
                     
                  }
                  else {
                     
                     edge = Tile.LEFT_EDGE;
                     cut[x] = cell.pos[x];
                     
                  }
                  
                  cut[y] = seg.vertex_a[y] + Math.round(candle / seg.norm[x]);
                  cell.location = grid.cellLocation(
                        cell.location, new int[] {cell.offset[x], 0});
               
               }
               else {
                  
                  edge = Tile.NO_EDGES;
                  cut = seg.vertex_b;
                  
               }
            }
            else {
               
               candle += length_y;
               length_x -= length_y;
               length_y = cell.span[y];
               
               if (candle < seg.length) {
                  
                  if (seg.sign[y] > 0) {
                     
                     edge = Tile.TOP_EDGE;
                     cut[y] = cell.pos[y] + cell.size[y];
                     
                  }
                  else {
                     
                     edge = Tile.BOTTOM_EDGE;
                     cut[y] = cell.pos[y];
                     
                  }
                  
                  cut[x] = seg.vertex_a[x] + Math.round(candle / seg.norm[x]);
                  cell.location = grid.cellLocation(
                        cell.location, new int[] {0, cell.offset[y]});
               
               }
               else {
                  
                  edge = Tile.NO_EDGES;
                  cut = seg.vertex_b;
                  
               }
            }
            
            section.addVertex(cut);
            
            if (edge != Tile.NO_EDGES) {
                  
                  tile.addSection(section, prev_edge, edge);
                  tiles.putTile(tile);
                  
                  tile = tiles.newTile(cell.location);
                  section = tile.newSection();
                  section.addVertex(cut);
                  // Flip edge since we leave one tile and 
                  // enter next
                  prev_edge = flipEdge(edge);
                  
            }
      
         }
         
      }
      
      tiles.update();
         
   }
   
   /**
    * Flip edges of the tile. Right to left and top to bottom and vice
    * verse. 
    * 
    * @param edge Edge register
    */
   private int flipEdge(int edge) {
      
      int ma = Tile.RIGHT_EDGE | Tile.TOP_EDGE;
      int mb = Tile.LEFT_EDGE | Tile.BOTTOM_EDGE;
      int mc = ~Tile.ALL_EDGES;
      
      return (edge & mc) | ((ma & edge) << 2) | ((mb & edge) >> 2);
      
   }
   
   private class Cell {
      
      public long[] size = new long[2];
      public int location = 0;
      public long[] pos = new long[2];
      public double[] span = new double[2];
      public int[] offset = new int[2];
      
   }
   
   private class FenceIterator implements Iterator<FenceSegment> {

      /**
      * The fence, a list of vertices.
      */
      private Iterator<FenceCoord> fence;
      
      private List<FenceCoord> vertices;
      /**
       * Current segment
       */
      private Enclosure.FenceSegment segment;
      
      /**
       * Scale of grid. Set by precision in first vertex.
       */
      public int scale = 1;
      
      /**
       * Walk around the fence vertex by vertex. Take a list 
       * of vertices and a grid. Note that it is assumed that
       * precision is the same for all vertices.
       * 
       * @param vertices List with vertices
       */
      public FenceIterator(List<FenceCoord> vertices) {
         
         this.vertices = vertices;
         
         // The fence is closed, should start and end at the same vertex.
         LinkedList<FenceCoord> _vertices = new LinkedList<FenceCoord>(vertices);
         FenceCoord ref_coord = _vertices.pollFirst();
         _vertices.addLast(ref_coord);
         
         this.fence = _vertices.iterator();
         
         this.scale = (int) Math.pow(10, ref_coord.precision);
         
         this.segment = new FenceSegment(
            new long[] {
               Math.round(ref_coord.value_x * scale),
               Math.round(ref_coord.value_y * scale)});
         
      }
      
      /**
       * 
       */
      public long[] positionAt(int index) {
         
         FenceCoord coord = vertices.get(index);
         
         return new long[] {
            Math.round(coord.value_x * scale), 
            Math.round(coord.value_y * scale)};
         
      }
      
      /**
      * Go to next vertex in the fence. This updates all attributes.
      */
      @Override
      public FenceSegment next() {
         
         FenceCoord coord = fence.next();
         
         long[] v = new long[] {
            Math.round(coord.value_x * scale), 
            Math.round(coord.value_y * scale)};
            
         segment.populate(v);
         
         return segment;
         
      }
      
      /**
      * Return <code> true </code> if there is more of fence to
      * walk.
      * 
      * @return <code> True </code> if more of fence remines.
      */
      @Override
      public boolean hasNext() {
         
         return fence.hasNext();
         
      }
      
      /** 
       * Not implemented
       */
      @Override
      public void remove() { }
      
   }
   
   private class FenceSegment {
      /**
      * First vertex.
      */
      public long[] vertex_a = new long[2];
      
      /**
      * Second vertex
      */
      public long[] vertex_b = new long[2];
      
      /**
      * Hypotunuse of a and b
      */
      public long length;
      
      /**
      * Difference between a and b.
      */
      public long[] delta = new long[2];
      
      /**
       * Normalized delta value
       */
      public double[] norm = new double[2];
      
      /**
      * Sign of slope, if b[x] is greater or less then a[x].
      */
      public int[] sign = new int[2];
      
      public FenceSegment(long[] v) {
         
         vertex_b = v;

      }
      
      public void populate(long[] v) {
         
         vertex_a[x] = vertex_b[x];
         vertex_a[y] = vertex_b[y];
         vertex_b[x] = v[x];
         vertex_b[y] = v[y];

         delta[x] = vertex_b[x] - vertex_a[x];
         delta[y] = vertex_b[y] - vertex_a[y];
      
         length = Math.round(
            Math.hypot((double) delta[x], (double) delta[y]));
         
         // Prevent zero division by adding a small number,
         norm[x] = length / ((double) delta[x] + 0.1);
         norm[y] = length / ((double) delta[y] + 0.1);
         
         sign[x] = (delta[x] >= 0)? 1: -1;
         sign[y] = (delta[y] >= 0)? 1: -1;
         
      }
   }
}
