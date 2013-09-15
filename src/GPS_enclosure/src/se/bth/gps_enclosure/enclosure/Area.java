package se.bth.gps_enclosure.enclosure;

import java.util.Iterator;
	
/**
 * This class holds a reference to a tile. This allow this class to select
 * then it is needed to collect the tile itself. 
 */
public class Area implements Fence {
   
   /**
   * The grid to which this area belong
   */
   private Grid grid;
   
   /**
   * Tile table with all tiles
   */
   private TileTable tiles;
   
   /**
   * Location of tile, the area.
   */
   private int location = 0;
   
   /**
   * This tile in the TileTable
   */
   private Tile tile;
   
   /**
   * The boundary of this area
   */
   private Boundary boundary;
   
   /**
   * Create a new adapter for tile at given location.
   */
   public Area(Grid grid, TileTable table, int tile_location) {
      
      this.grid = grid;
      this.tiles = table;
      this.location = tile_location;
      
      this.tile = table.getTile(tile_location);
      this.boundary = new Boundary(
            grid.cellPosition(tile_location), grid.cellSize(), grid.getScale());
   
   }
   
   /** 
    * Return <code>true</code> if given position is in this area
    * 
    * @param position Reference position
    * @return <code>true</code> if position is in this area
    */
   @Override
   public boolean containPosition(long[] position) {
      
      return grid.contains(position);
      
   }
   
   /**
   * Check if given position is inside the fence at this area. This
   * of course requires that given position is within this area.
   * 
   * @param position Reference position
   * @return <code> true </code> if position is at this area and 
   * inside the fence
   * 
   */
   @Override
   public boolean insideFence(long[] position) {
   
      int type = tile.getType();
      
      if (!boundary.contains(position) || type == TileTable.OUTSIDE) {
         
         return false;
         
      } else if(type == TileTable.INSIDE) {
      
         return true; 
      
      }
      
      long[] cell = grid.cellPosition(location);
      Segment seg = new Segment(position, cell, boundary.getScale());
      
      boolean odd_crossings = 
         ((intersectCount(seg, tile) % 2) == 1)? true: false;
      
      // A odd number of crossings means a switch from outside to inside
      // or vice verse.
      return tile.isInside(Tile.BL_CORNER) ^ odd_crossings;
   
   }
   
   /**
   * Min distance to fence assuming this area has a fence. If
   * this area has no fence then min distance to edge area is returned.
   * 
   * @param marker Reference position
   * @return Min distance to fence
   */
   @Override
   public long distFence(long[] position) {
      
      long fence = 0;
      long edge = 0;
      
      if(tile.getType() == TileTable.FENCE) {
       
         fence = minFenceDistance(position, tile);
   
      }
      
      edge = boundary.minEdgeDistance(position);
      
      return Math.min(fence, edge);
   
   }
   
   /**
   * Return <code< true </code> if this area has a part of the 
   * fence.
   * 
   * @return <code> true </code> if this area has part of fence
   */
   @Override
   public boolean hasFence() {
      
      return (tile.getType() == TileTable.FENCE)? true: false;
   
   }
   
   /**
   * Tile boundary for this tile
   * 
   * @return Boundary for tile
   */
   public Boundary boundary() {
      
      return boundary;
      
   }
   
   /**
    * Count number of intersections between a segment and all sections
    * of the fence crossing given tile. The segment start at a and end
    * at b. 
    * 
    * @param a Start position of segment
    * @param b End position of segment
    * @param tile Tile with sections
    * @return Number of intersections
    */
   private int intersectCount(Segment ref_seg, Tile tile) {
   
      Iterator<Section> sections = tile.sectionIterator();
      int count = 0;
      
      while(sections.hasNext()) {
      
         Section section = sections.next();
         Iterator<Segment> segments = section.segmentIterator(ref_seg.getScale());
         
         while(segments.hasNext()) {
         
            Segment segment = segments.next();
            
            if(segment.intersect(ref_seg)) count++;
         
         }
      }
      
      return count;
   
   }
   
   private long minFenceDistance(long[] p, Tile tile) {
   
      long ref_dist = Long.MAX_VALUE;
      long dist;
      
      Iterator<Section> sections = tile.sectionIterator();
      
      while(sections.hasNext()) {
      
         Section section = sections.next();
         Iterator<Segment> segments = section.segmentIterator(1);
         
         while(segments.hasNext()) {
         
            Segment segment = segments.next();
            
            dist = segment.distance(p);
            if (ref_dist > dist) ref_dist = dist;
         
         }
      }
      
      return ref_dist;
   
   }
   
}
	
