/**
// * Author: Matt Hotovy
 * Date: 4/19/2019
 * 
 * This class is used to create a linkedList 
 */
package project.sort;

import java.util.Iterator;

import project.lib.Invoice;

public class LinkedList<T> implements Iterable<Invoice> {

	// Define the state of my list
	private Comparator<Invoice> comparator;
	private Node<Invoice> head = null;
	private Node<Invoice> tail = null;
	private int size = 0; 

	//Constructor method
	public LinkedList() {
		this.comparator = null;
		this.head = null;
		this.tail = null;
		this.size = 0;
	}

	public Comparator<Invoice> getComparator() {
		return this.comparator;
	}
	
//--------------------------------------------------------------------	

	/**
	 * This method sets to comparator for the method
	 * @param comparator2
	 */
	public void setComparator(Comparator<Invoice> comparator2) {
		this.comparator = comparator2;
	}
	
//------------------------------------------------------------------------	
	
	/**
	 * This method returns the size of the list
	 * @return size
	 */
	public int getSize() {
		return size;
	}
	
//-------------------------------------------------------------------------	

	/**
	 * find the size if you do not already have the size stored.
	 * @return size of the list
	 */
	public int size() {
		Node<Invoice> current = head;
		int count = 0;
		//while not at the end of the list increase counter.
		while (current != null) {
			current = current.getNext();
			count++;
		}
		return count;
	}
	
//---------------------------------------------------------------------------------	

	//Checks if the list is empty or not
	public boolean isEmpty() {
		return (head == null);
	}	

//---------------------------------------------------------------------------------
	
	/**
	 * Method to take an invoice and comparator to insert the invoice to the list by the comparator
	 * @param invoice
	 * @param comparator
	 */
	public void insertSorted(Invoice invoice, Comparator<Invoice> comparator) {
		LinkedList<Invoice> sortedList = new LinkedList<Invoice>();
		sortedList.setComparator(comparator);
		Comparator<Invoice> listComparator = sortedList.getComparator();

		// insert at the beginning
		if (head == null || (listComparator.compare(invoice, (Invoice) head.getInvoice())) < 0) {
			// either the list is empty or the new Invoice comes before the current head
			addInvoiceToHead(invoice);
			return;
		}

		// insert in the middle
		Node<Invoice> current = head;
		int counter = 0;
		//while the current is still in the list continue comparing
		while (current != null) {
			counter++;
			Invoice currentInvoice = current.getInvoice();
			//"if" executes if the given invoice comes before the current Invoice.
			if ((listComparator.compare(invoice, currentInvoice)) < 0) {
				insertInvoiceAtIndex(invoice, counter-1);
				return;
			}
			current = current.getNext();
		}

		// Insert to the end;
		addInvoiceToTail(invoice);
		return;
	}
	
//-------------------------------------------------------------------------------------	

	/**
	 * This method adds the given {@link invoice} instance to the beginning of the
	 * list.
	 * @param invoice
	 */
	public void addInvoiceToHead(Invoice invoice) {
		if (invoice == null) {
			throw new IllegalArgumentException("This LinkedList impelmentation does not allow null Invoices");
		}
		Node<Invoice> newHead = new Node<Invoice>(invoice);
		if (this.tail == null) {
			this.head = newHead;
			this.tail = newHead;
			size++;
		} else {
			newHead.setNext(this.head);
			this.head.setPrevious(newHead);
			this.head = newHead;
			size++;
		}
	}

//-------------------------------------------------------------------------------

	/**
	 * Returns the element at the head of this list, but does not remove it.
	 * @return Invoice from the Head of the list.
	 */
	public Invoice getInvoiceFromHead() {
		if (this.getSize() == 0) {
			throw new IllegalStateException("Cannot retrieve from an empty list");
		} else {
			return this.head.getInvoice();
		}
	}

//--------------------------------------------------------------------------------

	/**
	 * This method removes an invoice from the head of the List
	 * @return the removed Invoice.
	 */
	public Invoice removeInvoiceFromHead() {
		Invoice item = null;
		if (this.getSize() == 0) {
			throw new IllegalStateException("Cannot remove from an empty list");
		} else if (this.getSize() == 1) {
			//if the list only has one Invoice, remove it.
			item = this.head.getInvoice();
			this.head = null;
			this.tail = null;
			size--;
		} else {
			//Remove from the head.
			item = this.head.getInvoice();
			this.head = this.head.getNext();
			this.head.setPrevious(null);
			size--;
		}
		return item;
	}

//--------------------------------------------------------------------------------	

	/**
	 * This method adds the given {@link invoice} instance to the end of the list.
	 * @param Invoice
	 */
	public void addInvoiceToTail(Invoice invoice) {
		if (invoice == null) {
			throw new IllegalArgumentException("This LinkedList impelmentation does not allow null elements");
		}
		
		Node<Invoice> newTail = new Node<Invoice>(invoice);
		if (this.tail == null) {
			this.tail = newTail;
			this.head = newTail;
			size++;
		} else {
			//set the current tails node to the new node
			newTail.setPrevious(this.tail);
			this.tail.setNext(newTail);
			this.tail = newTail;
			size++;
		}
	}

//---------------------------------------------------------------------------------

	/**
	 * Returns the Invoice at the tail of this list, but does not remove it.
	 * @return Invoice at the tail of the list
	 */
	public Invoice getInvoiceFromTail() {
		if (this.getSize() == 0) {
			throw new IllegalStateException("Cannot retrieve from an empty list");
		} else {
			return this.tail.getInvoice();
		}
	}

//----------------------------------------------------------------------------------	

	/**
	 * This method removes an invoice from the tail of the List
	 * @return the removed Invoice.
	 */
	public Invoice removeInvoiceFromTail() {
		Invoice item = null;
		if (this.getSize() == 0) {
			throw new IllegalStateException("Cannot remove from an empty list");
		} else if (this.getSize() == 1) {
			//if the list only has one Invoice, remove it.
			item = this.tail.getInvoice();
			this.head = null;
			this.tail = null;
			size--;
		} else {
			//Remove from the tail.
			item = this.tail.getInvoice();
			this.tail = this.tail.getPrevious();
			this.tail.setNext(null);
			size--;
		}
		return item;
	}

//---------------------------------------------------------------------------------	

	/**
	 * This method inserts the given Invoice at the given position.
	 * @param invoice
	 * @param index at which to be inserted
	 */
	public void insertInvoiceAtIndex(Invoice invoice, int index) {
		if (index < 0 || index > this.getSize()) {
			throw new IllegalArgumentException("Index " + index + " is out of bounds");
		}

		Node<Invoice> newNode = new Node<Invoice>(invoice);
		if (this.head == null) {
			//List is empty
			this.head = newNode;
			this.size++;
			return;
		} else if (index == 0) {
			//add to the head of the list.
			newNode.setNext(this.head);
			this.head = newNode;
			this.size++;
			return;
		}
		//adds invoice at given index
		Node<Invoice> previous = this.getNode(index - 1);
		newNode.setNext(previous.getNext());
		previous.setNext(newNode);
		this.size++;
		return;
	}

//-----------------------------------------------------------------------------	

	/**
	 * This method gets the the node at the given index
	 * @param index
	 * @return LinkedList node
	 */
	private Node<Invoice> getNode(int index) {
		if (index < 0 || index > this.getSize()) {
			throw new IllegalArgumentException("Index " + index + " is out of bounds");
		}

		Node<Invoice> current = this.head;
		//loops until at the given index and it returns it.
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}
		return current;
	}

//-------------------------------------------------------------------------------

	/**
	 * Returns the {@link Invoice} element stored at the given
	 * <code>index</code>.
	 * @param index
	 * @return Invoice at that index
	 */
	public Invoice getInvoice(int index) {
		return this.getNode(index).getInvoice();
	}

//---------------------------------------------------------------------------

	/**
	 * This function clears out the contents of the list, making it an empty list.
	 */
	public void clear() {
		if (this.head == null) {
			throw new NullPointerException("The list is already empty!");
		} else {
			this.head = null;
			this.size = 0;
		}
	}

//---------------------------------------------------------------------------

	/**
	 * This method removes the {@link Invoice} from the given <code>index</code>,
	 * indices start at 0. Implicitly, the remaining elements' indices are reduced.
	 * @param index
	 */
	public void remove(int index) {
		if (index < 0 || index >= this.getSize()) {
			throw new IllegalArgumentException("Index " + index + " is out of bounds");
		}

		Node<Invoice> current = this.getNode(index);
		if (index == 0) {
			if (this.getSize() == 1) {
				this.clear();
			} else {
				//set the previous node to the currents next node.
				Node<Invoice> previous = this.getNode(index);
				current = previous.getNext();
				previous.setNext(current.getNext());
				size--;
			}
		} else {
			//set the previous node to the currents next node.
			Node<Invoice> previous = this.getNode(index);
			current = previous.getNext();
			previous.setNext(current.getNext());
			size--;
		}
		return;
	}

//-----------------------------------------------------------------------------

	/**
	 * creates a string from the linkedList
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Node<Invoice> current = head;
		
		//loops through and adds the current invoice to the string builder.
		while (current != null) {
			Invoice invoice = current.getInvoice();
			sb.append(invoice.toString() + "\n");
			current = current.getNext();
		}
		
		sb.append("]");
		return sb.toString();
	}

//------------------------------------------------------------------------------

	/**
	 * Prints this list to the standard output.
	 */
	public void print() {
		// Here we can call the toString method but we need to give it a linkedList.
		String elements = this.toString();
		System.out.println(elements);
	}

//-------------------------------------------------------------------------------------	

	/**
	 * Iterator method for the Iterable interface and the linkedList
	 */
	@Override
	public Iterator<Invoice> iterator() {
		return new Iterator<Invoice>() {
			Node<Invoice> current = head;

			@Override
			public boolean hasNext() {
				if (current == null) {
					return false;
				} else {
					return true;
				}
			}

			@Override
			public Invoice next() {
				Invoice element = current.getInvoice();
				current = current.getNext();
				return element;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("not implemented");
			}
		};
	}

}
