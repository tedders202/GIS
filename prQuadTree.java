import java.util.ArrayList;

//On my honor:
//
//- I have not discussed the Java language code in my program with
//anyone other than my instructor or the teaching assistants
//assigned to this course.
//
//- I have not used Java language code obtained from another student,
//or any other unauthorized source, including the Internet, either
//modified or unmodified.
//
//- If any Java language code or documentation used in my program
//was obtained from another source, such as a text book or course
//notes, that has been clearly noted with a proper citation in
//the comments of my program.
//
//- I have not designed this program in such a way as to defeat or
//interfere with the normal operation of the grading code.
//
//<Jae Won Shin>
//<jaewons 9061-34703>
/**
 * The coordinate index using quadtree implementations.
 * @author Jae Won Shin
 *
 * @param <T>
 */
public class prQuadTree<T extends Compare2D<? super T>> {

	prQuadNode root;
	long xMin, xMax, yMin, yMax;
	// Total number of data objects within the tree. Number of leaves
	// can be smaller than number of data objects since each leaf can
	// hold BUCKETSIZE objects.
	int numOfDataObj;
	public static int BUCKETSIZE = 4;

	// Initialize quadtree to empty state.
	public prQuadTree(long xMin, long xMax, long yMin, long yMax) {
		root = null;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		numOfDataObj = 0;
	}

	// Pre: elem != null
	// Post: If elem lies within the tree's region, and elem is not already
	// present in the tree, elem has been inserted into the tree.
	// Return true iff elem is inserted into the tree.
	public boolean insert(T elem) {
		if (elem != null && elem.inBox(xMin, xMax, yMin, yMax)) {
			int prevNum = numOfDataObj;
			root = insertHelper(root, elem, xMin, xMax, yMin, yMax);
			return prevNum != numOfDataObj;
		}
		return false;
	}

	// Pre: elem != null
	// Returns reference to an element x within the tree such that elem.equals(x)
	// is true, provided such a matching element occurs within the tree; returns
	// null otherwise.
	public T find(T Elem) {
		// In case Elem is null or Elem is outside of box.
		if (Elem != null && Elem.inBox(xMin, xMax, yMin, yMax)) {
			return find1Helper(root, Elem, xMin, xMax, yMin, yMax);
		}
		return null;
	}

	// Pre: xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is in
	// the tree and x lies at coordinates within the defined rectangular region,
	// including the boundary of the region.
	public ArrayList<T> find(long xLo, long xHi, long yLo, long yHi) {
		// Creates an empty list
		ArrayList<T> list = new ArrayList<>(numOfDataObj);
		if (root != null) {
			find2Helper(list, root, xLo, xHi, yLo, yHi);
		}
		return list;
	}

	/**
	 * Private helper function for the insert() method. Uses recursion Breaks up
	 * into cases for whether the prQuadNode is a leaf or internal or null. (1) If
	 * null, just automatically add.
	 * 
	 * (2) Else if a leaf, then check if it is same element and if not then turn the
	 * leaf into an internal and put the the old and new leaf in their respective
	 * quadrants. If they are in the same quadrant then repeat the second half of
	 * the first sentence until they are in different quadrants.
	 * 
	 * (3) Else if an internal, then check what quadrant the element would go and
	 * check (1), (2), and (3) again.
	 * 
	 * @param node The node it is currently on
	 * @param elem The element that is being added
	 * @param xLo  The smallest x in the current quadrant
	 * @param xHi  The largest x in the current quadrant
	 * @param yLo  The smallest y in the current quadrant
	 * @param yHi  The largest y in the current quadrant
	 * @return The new subtree for modifying the quadtree
	 */
	@SuppressWarnings("unchecked")
	private prQuadNode insertHelper(prQuadNode node, T elem, double xLo, double xHi, double yLo, double yHi) {
		// Creates a leaf node and adds the element if the node is null
		if (node == null) {
			prQuadLeaf<T> leaf = new prQuadLeaf<T>();
			leaf.Elements.add(elem);
			numOfDataObj++;
			return leaf;
		} // Checks cases when the node is a leaf
		else if (node.getClass() == prQuadLeaf.class) {
			prQuadLeaf<T> leaf = (prQuadLeaf<T>) node;
			// Checks if the element is already in the list
			for (int i = 0; i < leaf.Elements.size(); i++) {
				if (leaf.Elements.get(i).equals(elem)) {
					return node;
				}
			}
			// If the elements array in the leaf node is not full
			if (leaf.Elements.size() < BUCKETSIZE) {
				leaf.Elements.add(elem);
				numOfDataObj++;
				return node;
			}
			prQuadInternal internal = new prQuadInternal();
			// Only happens when Elements array is full.
			// Goes through each element in the Elements array, figures out which
			// quadrant node it it supposed to be in, and calls insertHelper with
			// new quadrant coordinates and internal node children.
			double centerX = (xHi + xLo) / 2;
			double centerY = (yHi + yLo) / 2;
			
			// Grabs all the elements in the full leaf node and splits them
			for (int j = 0; j < BUCKETSIZE; j++) {
				Direction dir = leaf.Elements.get(j).inQuadrant(xLo, xHi, yLo, yHi);

				if (dir == Direction.NE) {
					internal.NE = insertHelper(internal.NE, leaf.Elements.get(j), centerX, xHi, centerY, yHi);
				}
				if (dir == Direction.NW) {
					internal.NW = insertHelper(internal.NW, leaf.Elements.get(j), centerX, xHi, centerY, yHi);;
				}
				if (dir == Direction.SW) {
					internal.SW = insertHelper(internal.SW, leaf.Elements.get(j), centerX, xHi, centerY, yHi);;
				}
				if (dir == Direction.SE) {
					internal.SE = insertHelper(internal.SE, leaf.Elements.get(j), centerX, xHi, centerY, yHi);;
				}
			}
			// Adds the element after all the other elements from the full leaf were split
			Direction dir2 = elem.inQuadrant(xLo, xHi, yLo, yHi);
			if (dir2 == Direction.NE) {
				internal.NE = insertHelper(internal.NE, elem, centerX, xHi, centerY, yHi);
			}
			if (dir2 == Direction.NW) {
				internal.NW = insertHelper(internal.NW, elem, xLo, centerX, centerY, yHi);
			}
			if (dir2 == Direction.SW) {
				internal.SW = insertHelper(internal.SW, elem, xLo, centerX, yLo, centerY);
			}
			if (dir2 == Direction.SE) {
				internal.SE = insertHelper(internal.SE, elem, centerX, xHi, yLo, centerY);
			}
			node = internal;
			return node;
		} // If the node is an internal node, then calls insertHelper on each of the
			// quadrant node with new quadrant coordinates.
		else if (node.getClass() == prQuadInternal.class) {
			prQuadInternal internal = (prQuadInternal) node;
			Direction dir = elem.inQuadrant(xLo, xHi, yLo, yHi);

			double centerX = (xHi + xLo) / 2;
			double centerY = (yHi + yLo) / 2;

			if (dir == Direction.NE) {
				internal.NE = insertHelper(internal.NE, elem, centerX, xHi, centerY, yHi);
			}
			if (dir == Direction.NW) {
				internal.NW = insertHelper(internal.NW, elem, xLo, centerX, centerY, yHi);
			}
			if (dir == Direction.SW) {
				internal.SW = insertHelper(internal.SW, elem, xLo, centerX, yLo, centerY);
			}
			if (dir == Direction.SE) {
				internal.SE = insertHelper(internal.SE, elem, centerX, xHi, yLo, centerY);
			}
		}
		return node;
	}

	/**
	 * Using the same strategy for insertion, created cases where node is a leaf,
	 * internal, or null. Then checked where the element would be inserted in the
	 * tree and instead of inserting, just checked whether the same element existed
	 * 
	 * @param node Current node
	 * @param elem Element being searched
	 * @param xLo  The smallest x in the current quadrant
	 * @param xHi  The largest x in the current quadrant
	 * @param yLo  The smallest y in the current quadrant
	 * @param yHi  The largest y in the current quadrant
	 * @return The point that is equal to elem if it exists.
	 */
	@SuppressWarnings("unchecked")
	private T find1Helper(prQuadNode node, T elem, double xLo, double xHi, double yLo, double yHi) {
		if (node == null) {
			return null;
		} // Searches through the Elements array if it is a Leaf class.
		else if (node.getClass() == prQuadLeaf.class) {
			prQuadLeaf<T> leaf = (prQuadLeaf<T>) node;
			for (int i = 0; i < leaf.Elements.size(); i++) {
				if (elem.equals(leaf.Elements.get(i))) {
					return leaf.Elements.get(i);
				}
			}
		} // Calls find1Helper on each of the quadrant nodes if it is an internal node
		else if (node.getClass() == prQuadInternal.class) {
			prQuadInternal internal = (prQuadInternal) node;
			Direction dir = elem.inQuadrant(xLo, xHi, yLo, yHi);
			double centerX = (xHi + xLo) / 2;
			double centerY = (yHi + yLo) / 2;
			if (dir == Direction.NE) {
				return find1Helper(internal.NE, elem, centerX, xHi, centerY, yHi);
			}
			if (dir == Direction.NW) {
				return find1Helper(internal.NW, elem, xLo, centerX, centerY, yHi);
			}
			if (dir == Direction.SW) {
				return find1Helper(internal.SW, elem, xLo, centerX, yLo, centerY);
			}
			if (dir == Direction.SE) {
				return find1Helper(internal.SE, elem, centerX, xHi, yLo, centerY);
			}
		}
		return null;
	}

	/**
	 * Implementation consists of checking through the entire tree and then adding
	 * them into the ArrayList, however before adding them in each point is checked
	 * to see if they are within the region by using the P.inBox(long xLo, long xHi,
	 * long yLo, long yHi) method.
	 * 
	 * @param list The ArrayList being returned
	 * @param node The current node
	 * @param xLo  The smallest x in the quadrant
	 * @param xHi  The largest x in the quadrant
	 * @param yLo  The smallest y in the quadrant
	 * @param yHi  The largest y in the quadrant
	 */
	@SuppressWarnings("unchecked")
	private void find2Helper(ArrayList<T> list, prQuadNode node, long xLo, long xHi, long yLo, long yHi) {
		if (node.getClass() == prQuadLeaf.class) {
			prQuadLeaf<T> leaf = (prQuadLeaf<T>) node;
			// Checks the Elements array and adds all data objects that is within the
			// specified quadrant into the specified list.
			for (int i = 0; i < leaf.Elements.size(); i++) {
				if (leaf.Elements.get(i).inBox(xLo, xHi, yLo, yHi)) {
					list.add(leaf.Elements.get(i));
				}
			}
		} // Calls find2Helper on each of the quadrant nodes if it is an internal node
		else if (node.getClass() == prQuadInternal.class) {
			prQuadInternal internal = (prQuadInternal) node;
			if (internal.NE != null) {
				find2Helper(list, internal.NE, xLo, xHi, yLo, yHi);
			}
			if (internal.NW != null) {
				find2Helper(list, internal.NW, xLo, xHi, yLo, yHi);
			}
			if (internal.SE != null) {
				find2Helper(list, internal.SE, xLo, xHi, yLo, yHi);
			}
			if (internal.SW != null) {
				find2Helper(list, internal.SW, xLo, xHi, yLo, yHi);
			}
		}
	}
}
