package graph;



/**
 * This class provide the head and size of each adjacency list
 * @author chenkaikuang
 *
 */
public class List {
	/* private instance variables */
	public ListNode head;
	public int size;
	
	/**
	 * Constructor method for List class
	 */
	public List(int v) {
		head = new ListNode(v);
		size = 1;
	}
	
	/**
	 * Constructor method for List class, with no params
	 */
	public List() {
		head = null;
		size = 0;
	}
	
	/**
	 * Add v to the tail of the list
	 * @param v the int being added
	 */
	public void addNode(int v) {
		if(head == null) {
			head = new ListNode(v);
			size ++;
			return;
		}
		
		ListNode next = head.next;
		if(next == null) {
			head.setNext(new ListNode(v));
		} else {
			ListNode p = next;
			while(p.next != null) {
				p = p.next;
			}
			
			p.setNext(new ListNode(v));
		}
		size ++;
	}
	
	/**
	 * Determine if v is one of the linked nodes
	 * @return true means v is in the linked nodes
	 */
	public boolean isLinked(int v) {
		
		if(head == null) return false;
		
		ListNode next = head.next;
		if(next == null) {
			return false;
		} else {
			ListNode p = next;
			while(p != null) {
				if(p.value == v) return true;
				p = p.next;
			}
		}
		return false;
	}
	
	/**
	 * Check if v is in this list(including the head)
	 */
	public boolean isInList(int v) {
		if(head == null) return false;
		if(v == head.value) return true;
		if(isLinked(v)) return true;
		return false;
	}
}
