package se.bth.gps_enclosure.enclosure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
/**
 * A fence is a set of coordinates on a plane. The plane is divided 
 * into sections, a grid with tiles. There is three types of tiles, 
 * INSIDE, OUTSIDE and FENCE. The tile of type FENCE contain a 
 * section of the fence. The two other types of tiles is as the type 
 * suggest either inside or outside the fence. To avoid mixed meaning 
 * for same name border is used instead of fence. A fence or section 
 * of the fence is a set of coordinates, the fence posts so to speak. A 
 * border is the imaginary line drawn between the coordinates.
 */
public class TileTable {
   
   /**
   * The grid for this table. Every tile is of the same size. 
   */
   private Grid grid;
   
   /**
    * Container for all tile sections
    */
   private SectionTable section_table;
   
   
   private Map<Integer, TileData> tile_data;
   /** 
    * Flag to mark that tile is not yet part of grid
    */
   public static final int NONE = 0;
   /**
   * Flag to mark that tile is inside the border.
   */
   public static final int INSIDE = 1;
   
   /**
   * Flag to mark that tile is outside the border.
   */
   public static final int OUTSIDE = 2;
   
   /**
   * Flag to mark that tile contain part of border.
   */
   public static final int FENCE = 3;
   
   /**
   * Need initial values for creating this table.
   */
   @SuppressWarnings("unused")
   private TileTable() {}
   
   /**
    * Create a new table. Need a grid and section table. 
    * 
    * @param Boundery of this table 
    * @param Resolution of grid 
    */
   @SuppressLint("UseSparseArrays")
   public TileTable(Grid grid, SectionTable section_table) {
      
      this.grid = grid;
      this.section_table = section_table;
      
      // Assume that 10 % of the grid cells will contain part of fence.
      tile_data = new HashMap<Integer, TileData>((int) (grid.numOfCells() / 10));
      
   }
   
   /**
    * This create and return a new tile at given location.
    * 
    * @param location Location of this tile in the grid
    * @return New tile
    */
   public Tile newTile(int location) {
      
      return (Tile) new TileEntry(null, section_table, location, TileTable.NONE);
      
   }
   
   /**
    * Return tile at a given location. Note that a tile is returned 
    * only if it exist. 
    * 
    * @param location Location of tile
    * @return Tile Tile at location
    */
   public Tile getTile(int location) {
      
      TileData data;
      int type = grid.cellType(location);
      
      if (type == NONE) {
         
         return null;
         
      } else if (type != FENCE) {
         
         data = null;
         
      }
      else {
         
         data = tile_data.get(Integer.valueOf(location));
         
      }
      
      return (Tile) new TileEntry(data, section_table, location, type);
      
   }
   
   /**
   * Put tile to this table. Old tile is returned.
   * 
   * @param tile New tile
   * @return Old tile
   */
   public Tile putTile(Tile tile) {
      
      int location = tile.getLocation();
      int type = tile.getType();
      
      Tile old_tile = null;
      
      if (type == FENCE) {
         
         grid.cellType(location, type);
         tile.trim();
         old_tile = (Tile) tile.toMap(tile_data);
         
      }
      else  {
         
         grid.cellType(location, type);
         tile_data.remove(Integer.valueOf(location));
         
      }

      return old_tile;
      
   }
   
   /**
   * Return type of tile at a given location. Three types exist
   * INSIDE, OUTSIDE and FENCE.
   * 
   * @param location Location of tile
   * @return Flag marking the type of tile
   */
   public int tileType(int location) {
      
      return grid.cellType(location);
      
   }
   
   /** 
    * Update tile table after adding new tiles. This is not
    * done automatically because a update affect all tiles. So first add
    * all tiles to this table and then update table.
    */
   public void update() {
      
      markCorners(grid, tile_data);
      
      return;
      
   }
   
   /**
    * Mark corners of all tiles as inside or outside. 
    * 
    */
   private void markCorners(Grid grid, Map<Integer, TileData> table) {
      
      // Bitwise shift
      int shift_tr = Integer.numberOfTrailingZeros(Tile.TR_CORNER);
      int shift_tl = Integer.numberOfTrailingZeros(Tile.TL_CORNER);
      int shift_bl = Integer.numberOfTrailingZeros(Tile.BL_CORNER);
      int shift_br = Integer.numberOfTrailingZeros(Tile.BR_CORNER);
      
      // Index for corners
      int BL = 0;
      int TL = 1;
      int BR = 2;
      int TR = 3;
      
      int MARK = 1;
      
      int offset = 0;
      
      // Row at 0 and column at 1
      int row = (grid.resolution())[0];
      int cell_count = grid.numOfCells();
      
      int[] corners = new int[(row + 1) * 2];
      int cell_index = 0;
      
      TileData data;
      Integer key;
      
      while (cell_index < cell_count) {
         
         offset += 2;
         
         if((cell_index % row) == 0) {
 
            // New row, this flip upper and lower corners
            BL = (5 - BL) % 4;
            TL = (5 - TL) % 4;
            BR = (5 - BR) % 4;
            TR = (5 - TR) % 4;
            
            offset = 0;
            
         }
         
         if (grid.cellType(cell_index) == FENCE) {
            
            key = Integer.valueOf(cell_index);
            data = table.get(key);
            
            corners[offset + TR] = 
               ((data.flags & Tile.TOP_PARITY) == Tile.TOP_PARITY)? 
               corners[offset + TL] ^ MARK: corners[offset + TL];
            
            data.flags &= ~Tile.ALL_CORNERS;
            data.flags |= 
               (corners[offset + TR] << shift_tr) |
               (corners[offset + TL] << shift_tl) |
               (corners[offset + BL] << shift_bl) |
               (corners[offset + BR] << shift_br);
               
            table.put(key, data);
            
         }
         else {
            
            // All corners is the same
            corners[offset + TR] = corners[offset + TL];
            
            // Cell type not yet known
            int type = 
               ((corners[offset + TR] << shift_tr) == Tile.TR_CORNER)? 
                     TileTable.INSIDE: TileTable.OUTSIDE;
                     
            grid.cellType(cell_index, type);
            
         }
         
         cell_index++;
            
      }
      
      return;
      
   }
   
   private class TileEntry implements Tile {
      
      /**
       * Holds all data for this tile
       */
      private TileData data;
      
      /**
       * Container for section data
       */
      private SectionTable section_table;
      
      /**
       * A tile can be inside, outside or fence.
       */
      private int type = TileTable.NONE;
      
      /**
       * Location of this tile
       */
      private int location;
      
      /**
       * Total length of records
       */
      private int space;
      
      /**
       * Last record of sections
       */
      private int offset;
      
      /**
       * Length of every record
       */
      private int record = 1;
      
      private int section_id = 0;
      
      private int section_flag = 0;
   
      /**
       * Create a new tile entry. Its purpose is manage tile data.
       * 
       * @param data Tile data for this entry
       * @param location Location of tile in grid
       * @param type Type of tile.
       */
      public TileEntry(
         TileData data, 
         SectionTable section_table,
         int location, int type) {
            
         if (data == null) {
            
            data = new TileData();
            data.sections = new int[10 * record];
            data.section_flags = new int[10 * record];
            
         }
         
         this.data = data;
         this.section_table = section_table;
         this.location = location;
         this.type = type;
         
         space = data.sections.length;
         offset = data.section_count * record;
         
      }
      
      /** 
       * Minimize internal data records.
       */
      public void trim() {
         
         expand((offset - space)/record);
         
         return;
         
      }

      /**
       * Put this tile data to map. The location of this tile is used
       * as key. Old tile data if such exist is returned.
       * 
       * @param map Map to store tile data
       * @return Old tile data 
       */
      public TileData toMap(Map<Integer, TileData> map) {
         
         Integer key = Integer.valueOf(location);
         
         return map.put(key, data);
         
      }
      
      /** 
       * Return true if selected corner is inside.
       * 
       * @param Selected corner
       * @return <code>true</code> if corner is inside the fence
       */
      public boolean isInside(int corner) {
         
         int flag_corners = data.flags & ALL_CORNERS;
         
         return (flag_corners & corner) == corner;
         
      }
      
      /** 
       * Get which type this tile is. Valid types is found in
       * TileTable. 
       * 
       * @return Type of tile
       */
      public int getType() {
         
         return type;
         
      }
      
      /** 
       * Get grid location of this tile.
       * 
       * @return Location of this tile
       */
      public int getLocation() {
         
         return location;
         
      }
   
      /**
       * Create a new section for this tile.
       */
      public Section newSection() {
         
         long seed = (long) ((location * 7) + (offset * 13));
         Random r = new Random(seed);
         
         int id = r.nextInt();
         
         do {
            
            id = r.nextInt();
            
         } while (section_table.hasSection(id));
         
         return section_table.newSection(id);
         
      }
   
      /**
       * Add section to this tile. The section start at a edge and end at a edge.
       * 
       * @param section The section
       * @param start_edge Section start at this edge
       * @param end_edge Section end at this edge
       */
      public void addSection(
         Section section, int start_edge, int end_edge) {
       
         // FIXME: Check for previous existing section with same id.
         int id = section.id();
         section_table.putSection(section);
         
         if (offset >= space) expand(10);
         
         data.sections[offset + section_id] = id;
         
         // Set flags
         start_edge &= ALL_EDGES;
         end_edge &= ALL_EDGES;
         int flag = start_edge | end_edge;
         // Mark start and end edge for section
         data.section_flags[offset + section_flag] |= flag;
         // Mark crossed edges for this tile
         data.flags |= flag;
         // Toggle parity
         data.flags = 
            (((start_edge ^ end_edge) & TOP_EDGE) == TOP_EDGE)? 
            data.flags ^ TOP_PARITY: data.flags;
       
      }
   
      /**
       * Number of section crossing this tile.
       * 
       * @return Number sections crossing this tile
       */
      public int numOfSections() {
         
         return (int) offset / record;
         
      }
   
      /**
       * An section iterator over all sections crossing this tile.
       */
      public Tile.SectionIterator sectionIterator() {
         
         int count = numOfSections();
         int[] group = new int[count];
         
         for (int i = 0; i < count; i++) {
            
            group[i] = data.sections[(record * count) + section_id];
            
         }
       
         return (Tile.SectionIterator) new SectionIterator(section_table, group);
       
      }
      
      /** 
       * Expand the amount of available space in tile data.
       * 
       * @param amount Number of new records
       */
      private void expand(int amount) {
         
         space += amount * record;
         data.sections = Arrays.copyOf(data.sections, space);
         data.section_flags = Arrays.copyOf(data.section_flags, space);
         
      }
      
      private class SectionIterator implements Iterator<Section> {
         
         private SectionTable table;
         private int[] sections;
         private int space;
         private int offset;
               
         /**
          * Create a new section iterator for this tile. 
          * 
          * @param sections Array with sections
          */
         public SectionIterator(SectionTable table, int[] sections) {
            
            this.table = table;
            this.sections = sections;
            this.space = sections.length;
            this.offset = 0;
            
         }
         
         /**
          * Returns true if this iterator has more sections.
          * 
          * @return <code> true </code> if this iterator has more sections.
          */
         @Override
         public boolean hasNext() {
            
            return (offset < space)? true: false;
            
         }
         
         /**
          * Returns the next section in this iterator.
          * 
          * @return Next section in this iterator. 
          */
         @Override
         public Section next() {
            
            // FIXME: No more items issue
            if (!hasNext()) {
               
               return null;
               
            }
               
            Section s = table.getSection(sections[offset]);
            
            offset++;
               
            return s;
               
         }

         /** 
          * Not implemented.
          */
         @Override
         public void remove() { }
   
      }
   
   }
   
   class TileData {
      
      /** 
       * Status flags for this tile
       */
      public int flags;
   
      /**
       * Location of this tile in the grid.
       */
      public int location;
      
      /**
       * Number of sections crossing this tile
       */
      public int section_count;
      
      /**
       * Status flags for each section
       */
      public int[] section_flags;
      
      /**
       * Array of sections crossing  this tile.
       */
      public int[] sections;
      
   }
   
}
   
