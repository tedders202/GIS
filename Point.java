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
 * The data objects that goes in the prQuadTree
 * @author Jae Won Shin
 *
 */
public class Point implements Compare2D<Point> {

	private long xcoord;
	private long ycoord;
	private ArrayList<Long> locations;
	// Locations length
	private int maxSize;

	/**
	 * Constructor with no parameters (Most likely never used)
	 */
	public Point() {
		xcoord = 0;
		ycoord = 0;
		maxSize = 10;
		locations = new ArrayList<Long>(maxSize);
	}
	
	/**
	 * Constructor with x, y (will be used in some cases)
	 * @param x
	 * @param y
	 */
	public Point(long x, long y) {
		xcoord = x;
		ycoord = y;
		maxSize = 10;
		locations = new ArrayList<Long>(maxSize);
	}

	/**
	 * Constructor with x, y, and offset (will be used in most cases)
	 * @param x
	 * @param y
	 * @param offset
	 */
	public Point(long x, long y, long offset) {
		xcoord = x;
		ycoord = y;
		maxSize = 10;
		locations = new ArrayList<Long>(maxSize);
		locations.add(offset);
	}

	// For the following methods, let P designate the Point object on which
	// the method is invoked (e.g., P.getX()).

	// Reporter methods for the coordinates of P.
	public long getX() {
		return xcoord;
	}

	public long getY() {
		return ycoord;
	}
	
	/**
	 * Return list of file offsets.
	 */
	public ArrayList<Long> locations() {
		return locations;
	}
	
	/**
	 * Append a file offset to the existing list.
	 */
	public boolean addLocation(Long offset) {
		if (offset != null)
		{
			// Checks if locations array is full and increases if it is full
			if (locations.size() == maxSize)
			{
				locations.ensureCapacity(maxSize + 10);
				maxSize = maxSize + 10;
			}
			locations.add(offset);
			return true;
		}
		return false;
	}

	// Determines which quadrant of the region centered at P the point (X, Y),
	// consistent with the relevant diagram in the project specification;
	// returns NODQUADRANT if P and (X, Y) are the same point.
	public Direction directionFrom(long X, long Y) {
		// Returns NorthEast iff X and Y are positive of P, or if X is positive while Y
		// is on the x-axis.
		if ((X > getX() && Y > getY()) || (X > getX() && Y == getY())) {
			return Direction.NE;
		}
		// Returns NorthWest iff X is negative and Y is positive of P, or if X is on the
		// y-axis while Y is positive.
		else if ((X < getX() && Y > getY()) || (X == getX() && Y > getY())) {
			return Direction.NW;
		}
		// Returns SouthWest iff X and Y are negative of P, or if X is negative while Y
		// is on the x-axis
		else if ((X < getX() && Y < getY()) || (X < getX() && Y == getY())) {
			return Direction.SW;
		}
		// Returns SouthEast iff X is positive and Y is negative of P, or if X is on the
		// y-axis and Y is negative.
		else if ((X > getX() && Y < getY()) || (X == getX() && Y < getY())) {
			return Direction.SE;
		}
		// Only other case is when X and Y are equal to P
		return Direction.NOQUADRANT;
	}

	// Determines which quadrant of the specified region P lies in,
	// consistent with the relevant diagram in the project specification;
	// returns NOQUADRANT if P does not lie in the region.
	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi) {
		// Returns NOQUADRANT if P is out of bounds.
		if (getX() < xLo || getX() > xHi || getY() < yLo || getY() > yHi )
		{
			return Direction.NOQUADRANT;
		}
		
		double centerX = (xHi + xLo) / 2;
		double centerY = (yHi + yLo) / 2;

		// Returns NorthEast iff P is positive of the center, or if X is positive while Y
		// is on the x-axis, or if P is the center.
		if ((getX() > centerX && getY() > centerY) || (getX() > centerX && getY() == centerY) 
				|| (getX() == centerX && getY() == centerY)) {
			return Direction.NE;
		}
		// Returns NorthWest iff P.getX() is negative and P.getY() is positive, or if X is on the
		// y-axis while Y is positive.
		else if ((getX() < centerX && getY() > centerY) || (getX() == centerX && getY() > centerY)) {
			return Direction.NW;
		}
		// Returns SouthWest iff P is negative of the center, or if X is negative while Y
		// is on the x-axis
		else if ((getX() < centerX && getY() < centerY) || (getX() < centerX && getY() == centerY)) {
			return Direction.SW;
		}
		// Returns SouthEast iff P.getX() is positive and P.getY() is negative, or if X is on the
		// y-axis and Y is negative. Only other cases are
		// ((getX() > centerX && getY() < centerY) || (getX() == centerX && getY() < centerY))
		else {
			return Direction.SE;
		}
	}

	// Returns true iff P lies in the specified region.
	public boolean inBox(double xLo, double xHi, double yLo, double yHi) {

		return inQuadrant(xLo, xHi, yLo, yHi) != Direction.NOQUADRANT;
	}

	// Returns a String representation of P.
	public String toString() {

		return new String("(" + xcoord + ", " + ycoord + ")" + " "
		+ locations.toString());
	}

	// Returns true iff P and o specify the same point.
	public boolean equals(Object o) {
		if (o == null)
		{
			return false;
		}
		if (this.getClass() == o.getClass())
		{
			Point obj = (Point) o;
			return this.directionFrom(obj.getX(), obj.getY()) == Direction.NOQUADRANT;
		}
		return false;
	}
}
