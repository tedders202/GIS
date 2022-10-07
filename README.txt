Author: Jae Won Shin
PID: jaewons

How my J4 project works:

GIS	Contains the main(String args[]) that handles the command prompt and also handles
	the commands given from the script file.

Worker	Contains many functions designed to make the GIS class smaller and somewhat cleaner.

BufferObject 	Objects with an long offset field and String records field. Used in BufferPool

BufferPool	Implementation of BufferPool uses an ArrayList<BufferObject>
		Whenever insert or find is called, it puts the element being searched
		or added to the front of the BufferPool (assuming the element is valid).
		Size of BufferPool is 15

Hashable	Interface that is used in the nameEntry and HashTable classes.

nameEntry	Objects with a String name field and locations field. Used in Hashtable

HashTable 	Implementation of HashTable is described in J3.

Compare2D	Interface that is used in the Point and prQuadTree classes.

Direction	Enumerated types for the different quadrants in the prQuadTree which
		are NE, NW, SE, and SW

Point		Objects that hold the geographic coordinates and offset.
		The fields are x, y, and locations. Used in prQuadTree

prQuadNode	A parent class to prQuadLeaf and prQuadInternal.

		prQuadLeaf	Holds an ArrayList of Elements that has a bucket size of 4.
		prQuadInternal	Holds four prQuadNodes, each representing the different
				quadrants
		Note: Initially were internal classes in prQuadTree, however I decided
		      to take them out because the Worker class has a printTree method
                      which requires me to move them outside the prQuadTree.
		      I have tried to do import.prQuadTree.prQuadNode but it did not work
		      so I could not think of a different alternative.

prQuadTree	Implementation of prQuadTree is described in J2.

			  Class:	Line numbers:
HashTable implementation: HashTable	1-274

nameEntry implementation: nameEntry	1-119

prQuadTree implementaion: prQuadTree	1-285

Point implementation:	  Point		1-200

BufferPool implementation: BufferPool	1-161

BufferObject implementation: BufferObject	1-119

Feature Name Index:	   GIS 		68 (initialize)
					180, 184 (inserting into table)
Location Index:		   GIS		109 (initialize)
					188, 192 (inserting into tree)
BufferPool		   GIS		65 (initialize)
			   Worker	492, 536 (search)

Reads the command line in the format...

"java GIS <database file name> <command script file name> <log file name>"

Then creates a RandomAccessFile object that reads and writes for database file,
RandomAccessFile object that only reads for script file, and
FileWriter object that only writes for log file.

Both database file and log file are empty upon creation.

Script file will hold a data file that is to be imported to the database file and a coordinate
bound for quadtree.

Import command wil...
1) start the creation of a name index in the form of a hash table,
2) start the creation of a coordinate index in the form of a quadtree,
3) and move all records from the data file to the database file.
Note:	One error I had with multiple imports was that I was not flushing the file
	before importing the new file which gave incomplete database files.

Whenever a search command like...
"what_is_at<tab><geographic coordinate>"
"what_is<tab><feature name><tab><state abbreviation>"
"what_is_in<tab><geographic coordinate><tab><half-height><tab><half-width>"
the GIS class will retrieve an offset/s from either the hash table or the quadtree.
Once the offset/s is retrieved, then it will first be searched for in the buffer pool.
If not in the buffer pool, then will use fseek(offset) to search in the database file.

Efficiency of buffer pool comes from searching through RAM rather than through Disc which
costs far more clock-cycles than searching through RAM.
The hashtable, quadtree, and buffer pool are kept in RAM while
files are kept in Disc.

The buffer pool will be an ArrayList of length 15 that holds data objects that each have
two fields...
1) long offset - which holds the offset of a record line
2) String record - the GIS record line corresponding to the offset

Objects that are most recently used (MRU) will be towards the front of the ArrayList and the
objects that are least recently used (LRU) will be towards the back of the ArrayList.
ex.	Let the buffer pool be full.
	The most recently used object will be at index 0.
	The least recently used object will be at index 14.

Warning: When doing world building for large databases, it might take over 1 or 2 minutes