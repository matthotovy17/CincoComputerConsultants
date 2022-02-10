/**
 * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is for the node objects in the LinkedList 
 */
package project.sort;

public class Node<Invoice> {

	private Node<Invoice> next;
	private Node<Invoice> previous;
	private final Invoice invoice;

	// Constructor
	public Node(Invoice invoice) {
		this.invoice = invoice;
	}

	public Node() {
		this.invoice = null;
		this.next = null;
	}

	public Invoice getInvoice() {
		return this.invoice;
	}

	public Node<Invoice> getNext() {
		return this.next;
	}

	// Sets the node to the next node
	public void setNext(Node<Invoice> next) {
		this.next = next;
	}

	// gets the previous node in the list
	public Node<Invoice> getPrevious() {
		return this.previous;
	}

	// Sets the previous node the current node
	public void setPrevious(Node<Invoice> previous) {
		this.previous = previous;
	}

	// returns boolean if the current node has a next node.
	public boolean hasNext() {
		return this.next != null;
	}

	@Override
	public String toString() {
		return this.invoice.toString();
	}

}
