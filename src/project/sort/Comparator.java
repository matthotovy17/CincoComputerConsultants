/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is the superclass comparator for 
 * the specific subclass comparators
 */
package project.sort;

public abstract class Comparator<T> {
	//Abstract method for subclasses to compare to invoices
	public abstract int compare(T element1, T element2);
}
