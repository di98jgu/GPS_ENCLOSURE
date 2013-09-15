package se.bth.gps_enclosure.enclosure;

import java.util.Iterator;
import java.util.Map;

import se.bth.gps_enclosure.enclosure.SectionTable.SectionData;

/**
 * A section in a segment 
 * 
 * @author Jim Gunnarsson
 */
public interface Section {
   
   /**
    * Put section data to map. The id of this section is used
    * as key. Old section data if such exist is returned. This is
    * not useful outside SectionTable so don't try to use this
    * method.
    * 
    * @param map Map to store section data
    * @return Old section data 
    */
   public SectionData toMap(Map<Integer, SectionData> map);
      
   /**
    * Unique id for this entry, can't be changed.
    * 
    * @return id of this entry
    */
   public int id();
   
   /**
    * The first vertex for this section.
    * 
    * @return long[] First vertex of this section
    */
   public long[] firstVertex();
   
   /**
    * Last vertex for this section.
    * 
    * @return long[] Last vertex of this section
    */
   public long[] lastVertex();
   
   /**
    * Get a vertex at a given index starting at 0.  
    * 
    * @param index Index of segment
    * @return Vertex at given index
    */
   public long[] vertexAt(int index);
   
   /**
    * Add a new vertex to the end of this section. Thus given
    * vertex will be the last vertex of this section.
    * 
    * @param vertex New vertex 
    */
   public void addVertex(long[] vertex);
   
   /**
    * Number of vertices in this section.
    * 
    * @return Number of vertices
    */
   public int numOfVertices();
   
   /**
    * Return <code> true </code> if the two vertices is 
    * the same.
    * 
    * @param a First vertex
    * @param b Second vertex
    * @return <code> true </code> if same
    */
   public boolean sameVertex(long[] a, long[] b);
   
   /**
    * Create a SegmentIterator for this section. This iterator 
    * will create a Segment for each pair of vertices starting at
    * 0.
    * 
    * @param scale Scale factor for Segment
    * @return A SegmentIterator for this section
    */
   public SegmentIterator segmentIterator(int scale);
   
   public interface SegmentIterator extends Iterator<Segment> {}
   
}
