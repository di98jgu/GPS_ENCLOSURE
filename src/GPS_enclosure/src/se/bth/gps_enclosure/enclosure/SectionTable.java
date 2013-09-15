package se.bth.gps_enclosure.enclosure;

import android.annotation.SuppressLint;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A fence section is a part of the entire fence. All sections together form the 
 * entire fence. A valid fence form a polygon. This is not enforced and the only 
 * strict rule is that every section have a unique id. Note that a section is 
 * mutable and thus need to be returned to this table in order to store new data
 */
public class SectionTable {
   
   /**
    * The data container for section data.
    */
   private Map<Integer, SectionData> table;
   
   /**
    * Create a new section table. 
    * @param sections
    */
   public SectionTable() {
      
      this(20);
      
   }
   
   /**
    * Create a new section table. An estimated number of sections 
    * might be given.
    * 
    * @param sections
    */
   @SuppressLint("UseSparseArrays")
   public SectionTable(int sections) {
      
      this.table = new HashMap<Integer, SectionData>(sections);
      
   }
   
   /**
    * Create new section with given id.
    * 
    * @param id Id for this section
    * @return New section
    */
   public Section newSection(int id) {
      
      return new SectionEntry(id, null);
     
   }

   /**
    * Get section for given id. If no section exist null is returned.
    * 
    * @param id Unique id for section
    * @return Section with given id
    */
   public Section getSection(int id) {
      
      if (!hasSection(id)) return null;
         
      SectionData data = table.get(Integer.valueOf(id));
      Section section = (Section) new SectionEntry(id, data);
      
      return section;
      
   }

   /**
    * Put new section data to this section table. The old section, if 
    * it exist, is returned.
    * 
    * @param section New section
    * @return Old section if such exist.
    */
   public Section putSection(Section section) {
      
      if (section == null) return null;
         
      SectionData data = section.toMap(table);
      
      if (data != null) {
         
         return new SectionEntry(section.id(), data);
         
      }
      
      return null;
      
   }

   /**
    * Return <code> true </code> if section with given id already
    * exist.
    * 
    * @param id Section id
    * @return <code> true </code> if section exist
    */
   public boolean hasSection(int id) {
      
      return table.containsKey(Integer.valueOf(id));
      
   }

   private class SectionEntry implements Section {
      
      private SectionData data;
      
      /**
       * Fence entry id.
       */
      private int id;
      
      /**
       * Total available space for records
       */
      private int space;
      
      /**
       * Current record in section
       */
      private int offset;
      
      /**
       * Length of every record in section
       */
      private int record = 2;
      
      /**
       * Record item x
       */
      private int x = 0;
      
      /**
       * Record item y
       */
      private int y = 1;
      
      /**
       * Section entry manager for fence data.
       * 
       * @param id Unique id of this entry
       * @param fence_data Fence data
       */
      public SectionEntry(int id, SectionData section_data) {
         
         if (section_data == null) {
            
            section_data = new SectionData();
            section_data.section = new long[record * 10];
            section_data.records = 0;
            
         }
         
         this.id = id;
         this.data = section_data;
         
         space = section_data.section.length;
         offset = section_data.records * record;
         
      }
      
      /**
       * Put this section data to map. The id of this section is used
       * as key. Old section data if such exist is returned.
       * 
       * @param map Map to store section data
       * @return Old section data 
       */
      public SectionData toMap(Map<Integer, SectionData> map) {
         
         Integer key = Integer.valueOf(id);
         
         return map.put(key, data);
         
      }
      
      /**
       * Unique id for this entry, can't be changed.
       * 
       * @return id of this entry
       */
      public int id() {
         
         return id;
         
      }
      
      /**
       * The first vertex for this section.
       * 
       * @return long[] First vertex of this section
       */
      public long[] firstVertex() {
         
         long[] v = new long[record];
         
         v[x] = data.section[x];
         v[y] = data.section[y];
         
         return v;
         
      }
      
      /**
       * Last vertex for this section.
       * 
       * @return long[] Last vertex of this section
       */
      public long[] lastVertex() {
         
         // FIXME: No records 
         long[] v = new long[record];
         
         if (data.records > 0) {
            
            v[x] = data.section[(offset - record) + x];
            v[y] = data.section[(offset - record) + y];
            
         }
         
         return v;
         
      }
      
      /**
       * Get a vertex at a given index starting at 0.  
       * 
       * @param index Index of segment
       * @return Vertex at given index
       */
      public long[] vertexAt(int index) {
         
         int p = record * index;
         long[] v = new long[record];
         
         // FIXME: Exception for out of index
         if ((p >= 0) && (p < offset)) {
            
            v[x] = data.section[p + x];
            v[y] = data.section[p + y];
            
         }
         
         return v;
         
      }
      
      /**
       * Add a new vertex to the end of this section. Thus given
       * vertex will be the last vertex of this section. A vertex
       * identical to previous vertex, e.g. a segment of zero length,
       * is ignored.
       * 
       * @param vertex New vertex 
       */
      public void addVertex(long[] vertex) {
         
         if (offset >= space) expand(10);
         
         if (!sameVertex(lastVertex(), vertex)) {
            
            data.section[offset + x] = vertex[x];
            data.section[offset + y] = vertex[x];
            
            offset += record;
            
         }
         
         return;
         
      }
      
      /**
       * Number of vertices in this section.
       * 
       * @return Number of vertices
       */
      public int numOfVertices() {
         
         return (int) offset / record;
         
      }
      
      /**
      * Return <code> true </code> if the two vertices is 
      * the same.
      * 
      * @param a First vertex
      * @param b Second vertex
      * @return <code> true </code> if same
      */
      public boolean sameVertex(long[] a, long[] b) {
         
         if ((a[x] == b[x]) && (a[y] == b[y])) return true;
         
         return false;
         
      }
      
      /** 
       * Expand the amount of available space in data section.
       * 
       * @param amount Number of new records
       */
      private void expand(int amount) {
         
         space += amount * record;
         data.section = Arrays.copyOf(data.section, space);
         
      }
      
      /**
       * Create a SegmentIterator for this section. This iterator 
       * will create a Segment for each pair of vertices starting at
       * 0.
       *        * 
       * @param scale Scale factor for Segment
       * @return A SegmentIterator for this section
       */
      public Section.SegmentIterator segmentIterator(int scale) {
         
         long[] section_data = Arrays.copyOf(data.section, space);;
         
         return (Section.SegmentIterator) new SegmentIterator(section_data, scale);
         
      }

      private class SegmentIterator implements Iterator<Segment> {
         
         /**
          * The section with vertices
          */
         private long[] vertices;
   
         /**
          * Total length of section
          */
         private int space;
         
         /**
          * Current record in section
          */
         private int offset;
         
         /**
          * Length of every record in section
          */
         private int record = 2;
         
         /**
          * Record item x
          */
         private int x = 0;
         
         /**
          * Record item y
          */
         private int y = 1;
         
         /** 
          * Scale factor for segment
          */
         private int scale;
         
         
         /**
          * Create a segment iterator for this section.
          * 
          * @param Array with segment records
          */
         public SegmentIterator(long[] vertices, int scale) {
            
            this.vertices = vertices;
            this.scale = scale;
            
            this.space = vertices.length;
            this.offset = 0;
            
            
         }
         
         /**
          * Returns true if this iterator has more segments.
          * 
          * @return <code> true </code> if this iterator has more segments.
          */
         @Override
         public boolean hasNext() {
            
            if ((offset - record) < space) {
               
               return true;
               
            }
            
            return false;
            
         }
         
         /**
          * Returns the next segment in this iterator.
          * 
          * @return Next segment in this iterator. 
          */
         @Override
         public Segment next() {
            
            long[] a = new long[record];
            long[] b = new long[record];
            
            a[x] = vertices[x];
            a[y] = vertices[y];
            b[x] = vertices[record + x];
            b[y] = vertices[record + y];
            
            Segment s = new Segment(a, b, scale);
            
            offset += record;
            
            return s;
            
         }
         
         /**
          * Not implemented
          */
         @Override
         public void remove() { }
         
      }
      
   }

  class SectionData {
     
    /**
     * Number of records in this section
     */
    public int records;

    /**
     * Section of fence
     */
    public long[] section;

  }

}
