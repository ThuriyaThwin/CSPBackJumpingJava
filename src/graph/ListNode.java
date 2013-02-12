package graph;


/**
 * This class implements list data structure
 * @author chenkaikuang
 *
 */
public class ListNode {
	
	/* private instance variables */
	public int value;
	public ListNode next;
	
	/**
	 * Constructor method for ListNode class
	 * @param value 
	 * @param next 
	 */
	public ListNode(int value, ListNode next) {
		this.value = value;
		this.next = next;
	}
	
	/**
	 * Constructor method for ListNode class
	 * @param value
	 */
	public ListNode(int value) {
		this.value = value;
		this.next = null;
	}
	
	
	/**
	 * Set the next ListNode
	 */
	public void setNext(ListNode next) {
		this.next = next;
	}

}
