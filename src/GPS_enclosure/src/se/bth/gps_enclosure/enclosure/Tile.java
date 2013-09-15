package se.bth.gps_enclosure.enclosure;

import java.util.Iterator;
import java.util.Map;

import se.bth.gps_enclosure.enclosure.TileTable.TileData;

/**
 * A tile in the tile table. 
 * 
 * @author Jim Gunnarsson
 */

public interface Tile {
   
   /**
    * Flag marking all edges of this tile
    */
   public static final int ALL_EDGES = 15;
   
   /**
    * Flag clearing all edges of this tile
    */
   public static final int NO_EDGES = 0;
   
   /**
    * Flag marking right edge of this tile
    */
   public static final int RIGHT_EDGE = 1;
   
   /**
    * Flag marking top edge of this tile
    */
   public static final int TOP_EDGE = 2;
   
   /**
    * Flag marking left edge of this tile
    */
   public static final int LEFT_EDGE = 4;
   
   /**
    * Flag marking bottom edge of this tile
    */
   public static final int BOTTOM_EDGE = 8;
   
   /** 
    * Flag marking even and odd number of top edge crossings
    */
   public static final int TOP_PARITY = 16;
   
   /**
    * Flag marking top right corner of this tile
    */
   public static final int TR_CORNER = 32;
   
   /**
    * Flag marking top left corner of this tile
    */
   public static final int TL_CORNER = 64;
   
   /**
    * Flag marking bottom left corner of this tile
    */
   public static final int BL_CORNER = 128;
   
   /**
    * Flag marking bottom right corner of this tile
    */
   public static int BR_CORNER = 256;
   
   /**
    * Flag marking all corners of this tile
    */
   public static final int ALL_CORNERS = 511 - 31;

	/** 
	* Minimize internal data records. 
	*/
	public void trim();
	
	/**
    * Put this tile data to map. The location of this tile is used
    * as key. Old tile data if such exist is returned. This is
    * not useful outside TileData so don't try to use this
    * method.
    * 
    * @param map Map to store tile data
    * @return Old tile data 
    */
   public TileData toMap(Map<Integer, TileData> map);
   
   /** 
    * Return true if selected corner is inside.
    * 
    * @param Selected corner
    * @return <code>true</code> if corner is inside the fence
    */
   public boolean isInside(int corner);
	
	/** 
	* Get which type this tile is. Valid types is found in
	* TileTable. 
	* 
	* @return Type of tile
	*/
	public int getType();
	
	/** 
	* Get grid location of this tile.
	* 
	* @return Location of this tile
	*/
	public int getLocation();
	
	/**
	* Create a new section for this tile.
	*/
	public Section newSection();
	
	/**
	* Add section to this tile. The section start at a edge and end at a edge.
	* 
	* @param section The section
	* @param start_edge Section start at this edge
	* @param end_edge Section end at this edge
	*/
	public void addSection(
	 Section section, int start_edge, int end_edge);
	
	/**
	* Number of section crossing this tile.
	* 
	* @return Number sections crossing this tile
	*/
	public int numOfSections();
	
	/**
	* An section iterator over all sections crossing this tile.
	*/
	public SectionIterator sectionIterator();
	
	public interface SectionIterator extends Iterator<Section> {}
	
}
