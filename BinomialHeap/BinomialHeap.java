/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 */

public class BinomialHeap 
{
	public int size;
	public HeapNode last;
	public HeapNode min;
	private int numOfTrees;
	
	public BinomialHeap() {
		this.size = 0;
		this.last = null;
		this.min = null;
		this.numOfTrees = 0;
	}
	
	public int getNumOfTrees() {
		return this.numOfTrees;
	}
	public int getSize() {
		return this.size;
	}
	public HeapNode getLast() {
		return this.last;
	}
	public HeapNode getMin() {
		return this.min;
	}
	
	public void setNumOfTrees(int numOfTrees) {
		this.numOfTrees = numOfTrees;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setMin(HeapNode min) {
		this.min = min;
	}
	public void setLast(HeapNode last) {
		this.last = last;
	}
	
	/**
     * private HeapNode link(HeapNode x, HeapNode y) 
     * @pre : y.key > x.key
     * Links the tree with the root x and another tree with the root y 
     * Returns HeapNode - the root of the linked tree
     * 
     * Complexity - O(1)  
     */
    private HeapNode link(HeapNode heapNode1, HeapNode heapNode2) {	
    	HeapNode x = heapNode1;
    	HeapNode y = heapNode2;
    	//if y.key is smaller than x.key change names for x and y
    	if (x.getItem().getKey() > y.getItem().getKey()) {
    		HeapNode tmp = x;
    		x = y;
    		y = tmp;
    	}
    	
    	//link both trees like we learned in class
    	y.setParent(x);
    	
    	if (x.getChild() == null) {
    		y.setNext(y);
    	}
    	else {
    		y.setNext(x.getChild().getNext());
    		x.getChild().setNext(y);
    	}
    	
    	x.setChild(y);
		x.setRank(x.getRank() + 1);

    	return x;
    }

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 * Complexity - O(logn)
	 */
	public HeapItem insert(int key, String info) { 
		// Initialize a new item and node to hold the key and info
		HeapNode newNode = new HeapNode();
		HeapItem newItem = new HeapItem();
		newItem.setKey(key);
		newItem.setInfo(info);
		newItem.setNode(newNode);
		newNode.setItem(newItem);
		
		// Initialize a new heap that hold a single tree with rank 0 - the new node
		BinomialHeap newHeap = new BinomialHeap();
		newHeap.setLast(newNode);
		newHeap.setMin(newNode);
		newHeap.setNumOfTrees(1);
		newHeap.setSize(1);
			
		// To insert the new node created from new item we will meld the new binomial heap
		// to the original heap as learned in class
		this.meld(newHeap);
		
		return newItem; 
	}

	/**
	 * 
	 * Delete the minimal item
	 * Complexity - O(logn)
	 */
	public void deleteMin()
	{   
		// if there are no nodes at all
		if (this.empty()) {
			return;
		}
		// if there is only one node he is the minimal
		else if (this.getSize() == 1) {
			this.setSize(0);
			this.setNumOfTrees(0);
			this.setLast(null);
			this.setMin(null);
		}
		
		else {
			HeapNode deletedNode = this.getMin();
			int rank = deletedNode.getRank();
			int subTreeNumOfNodes = (int) Math.pow(2, rank);
			
			
			// if the deleted node is the root of the only tree in this binomial heap 
			if (deletedNode.getNext() == deletedNode) {
				this.setSize(0);
				this.setNumOfTrees(0);
				this.setLast(null);
				this.setMin(null);
			}
			else {
				HeapNode prev = findPrev(deletedNode);
				// if the minimal is also the last 
				if (this.getLast() == deletedNode) {
					this.setLast(prev);
				}
				prev.setNext(deletedNode.getNext());
				this.setSize(this.getSize() - subTreeNumOfNodes);
				HeapNode newMin = findNewMin(this.getLast());          
				this.setMin(newMin);
				this.setNumOfTrees(this.getNumOfTrees() - 1);
			}
			// if we a disconnect a tree with one node no need the meld his children
			if (subTreeNumOfNodes == 1) {
				return;
			}
			// Initializing a new binomial heap when it trees are the deleted
			// node children and as we learned in class, meld it to the original
			else {
				BinomialHeap newHeap = new BinomialHeap();
				HeapNode child = deletedNode.getChild();
				HeapNode minOfKids = findNewMin(child);
				newHeap.setLast(child);
				newHeap.setMin(minOfKids);
				newHeap.setNumOfTrees(rank);
				newHeap.setSize(subTreeNumOfNodes - 1);
				
				// disconnecting the deleted node pointer to the new heap
				deletedNode.setChild(null);
				
				// disconnecting the children from the parent (the deleted node)  
				child.setParent(null);

				// melding the new binomial heap to the original 
				this.meld(newHeap);
			}	
		}
		return; 
	}
	
	/**
	 * Return the minimal of the brothers given a certain node
	 * Complexity - O(logn)
	 */
	private HeapNode findNewMin(HeapNode node) {
		HeapNode curr = node.getNext();
		HeapNode currMin = node;
		while (curr != node) {
			if (curr.getItem().getKey() <= currMin.getItem().getKey()) {
				currMin = curr;
			}
			curr = curr.getNext();
		}
		if (currMin.getItem().getKey() == node.getItem().getKey()) {
			currMin = node;
		}
		return currMin;
	}
	
	/**
	 * Return the previous node given a pointer to a certain node 
	 * Complexity - O(logn)
	 */
	private HeapNode findPrev(HeapNode node) {
		HeapNode curr = node;
		while (curr != null && curr.getNext() != node) {
			curr = curr.getNext();
		}
		return curr;
	}
	
	/**
	 * 
	 * Return the minimal HeapItem
	 * Complexity - O(1)
	 */
	public HeapItem findMin() {
		if (empty()) {
			return null;
		}
		else {
			return getMin().getItem();
		}
	}
		
	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * Complexity - O(logn)
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{ 
		// update key value
		int newKey = item.getKey() - diff;
		item.setKey(newKey);
		
		// if this item does not point any node, there is nothing to be done in 
		// the data structure 
		if (item.getNode() == null) {
			return;
		}
		
		else {
			// the node this item points 
			HeapNode currNode = item.getNode();
			
			// Heapify-up as long as the parent is'nt null
			while (currNode.getParent() != null) {
				HeapNode parent = currNode.getParent();
				// if we need to switch between the node and parent
				// in order to maintain the heap quality, 
				// we will use swap to change the items pointers and by that
				// only changing the information the nodes hold and not the structure
				if (currNode.getItem().getKey() < parent.getItem().getKey()) {
					currNode.swap(parent);
					currNode = currNode.getParent();
				}
				// if we reached a smaller key, no need to go all the way up
				else {
					break;
				}
			}
			// if we decreased the key to be smaller then the minimal - update !
		    if (currNode.getItem().getKey() < this.getMin().getItem().getKey()) {
		    	this.setMin(currNode);
		    }
		}

		return; 
	}

	/**
	 * 
	 * Delete the item from the heap.
	 * Complexity - O(logn)
	 */
	public void delete(HeapItem item) 
	{
		if (item.getNode() == null) {
			return;
		}
		else if (item.getNode() == this.getMin()) {
			deleteMin();
			return;
		}
		else {
			// to delete any node we will decrease its' key to be the new minimal
			int currMinKey = this.getMin().getItem().getKey();
		    int smallerThanMin = currMinKey - 1;
            int diff = item.getKey() - smallerThanMin;
		    decreaseKey(item, diff);
		    
		    // then we delete minimal with the method above
		    deleteMin();   
		}
		
		return; 
	}

	/**
	 * 
	 * Meld the heap with heap2
	 * Complexity - O(logn)
	 */
	public void meld(BinomialHeap heap2)
	{
		if (this.empty() && heap2.empty()) {
			return;
		}
		else if (!this.empty() && heap2.empty()) {
			return;
		}
		else if (this.empty() && !heap2.empty()) {
			this.setSize(heap2.getSize());
			this.setNumOfTrees(heap2.getNumOfTrees());
			this.setLast(heap2.getLast());
			this.setMin(heap2.getMin());
			return;
		}
		else { // both heaps are not empty (at least one tree in each heap)
			// update this heap size field
			this.setSize(this.getSize() + heap2.getSize());
			
			// Find the maximum rank among both heaps
			int maxRank = Math.max(this.getLast().getRank(), heap2.getLast().getRank());
			
			// creating arrays to hold the trees for every heap
			// so that every tree is in a position that equals to his rank
			
			// we build the arrays in size maxRank + 1 because when adding 2 binary numbers the length 
			// can grow with only one bit 
		    HeapNode[] treeArray1 = this.arrayOfTrees(maxRank + 2);
		    HeapNode[] treeArray2 = heap2.arrayOfTrees(maxRank + 2);
		    
			// initialize a carry to hold linked trees 
		    HeapNode[] carry = new HeapNode[1];
		    
		    // we go over heap2 to merge her trees into this heap 
		    for (int i=0; i<treeArray2.length -1; i++) {
		    	// if there is no tree in heap2 with rank i --> skip to the next iteration
		    	if (treeArray2[i] == null) {
		    		continue;
		    	}
		    	// if heap2 has a tree with rank i and this heap doesn't
		    	else if (treeArray1[i] == null && treeArray2[i] != null) {
		    		treeArray1[i] = treeArray2[i];
		    	}
		    	// if both heaps have a tree with rank i - we link!
		    	else {
		    		HeapNode root1 = treeArray1[i];
		    		HeapNode root2 = treeArray2[i];
	
		    		carry[0] = link(root2,root1);
		    		treeArray1[i] = null;
		    		int j = i+1;
		    		
		    		while (carry[0] != null) {
		    			if (treeArray1[j] == null) {
		    				treeArray1[j] = carry[0];
		    				carry[0] = null;
		    			}
		    			else {
		    				HeapNode NextRootToBeLinked = treeArray1[j];
		    				HeapNode res = link(carry[0], NextRootToBeLinked);
		    				carry[0] = res;
				    		treeArray1[j] = null;	
		    			}
		    			j++;
		    		}
		    	}
		    }
		    // update next pointer for every root in this heap
		    connectRoots(treeArray1);
		    
		    // update this heap fields - last, min and number of trees
		    int newNumOfTrees = numberOfTreesInArray(treeArray1);
		    this.setNumOfTrees(newNumOfTrees);
		    
		    if (treeArray1[maxRank + 1] == null) {
		    	this.setLast(treeArray1[maxRank]);
		    }
		    else {
		    	this.setLast(treeArray1[maxRank + 1]);
		    }
		    
		    HeapNode updatedMin = findNewMin(this.getLast());
		    this.setMin(updatedMin);
		}
		return;   		
	}
	
	 /**
	 * after melding, connect the trees in the heap by iterating their roots in the tree array
	 * Complexity - O(logn)
	 */
	
	private void connectRoots(HeapNode[] treeArray) {
		int length = treeArray.length;
	    // Find the first non-null node
	    int startIndex = 0;
	    while (startIndex < length && treeArray[startIndex] == null) {
	        startIndex++;
	    }
	    if (startIndex == length) {
	        // All elements in the array are null
	        return;
	    }
	    HeapNode previous = treeArray[startIndex];
	    HeapNode current;
	    for (int i = startIndex + 1; i < length; i++) {
	        current = treeArray[i];

	        if (current != null) {
	            previous.setNext(current);
	            previous = current;
	        }
	    }
	    // Connect the last node to the first node
	    previous.setNext(treeArray[startIndex]);
	}
	
	/** 
	 * given an array of trees returns the number of trees
	 * Complexity - O(logn)  
	 */
	private int numberOfTreesInArray(HeapNode[] arrayOfTrees) {
		int num = 0;
		int len = arrayOfTrees.length;
		for (int i=0; i<len; i++) {
			if (arrayOfTrees[i] != null) {
				num++;
			}
		}
		return num;
	}
	
	/** 
	 * @pre : this.emty() == false
	 * Returns an array so that every index points on the node who is the root of
	 * the binomial tree with that rank
	 * Complexity - O(logn)  
	 */
	private HeapNode[] arrayOfTrees(int n) {
		// Initialize the array
		HeapNode[] treeArray = new HeapNode[n];
		
		// Add trees to the array
	    HeapNode currentNode = this.last;
	    do {
	        int rank = currentNode.getRank();
	        if (treeArray[rank] == null) {
	            treeArray[rank] = currentNode;
	        }
	        currentNode = currentNode.getNext();
	    } while (currentNode != this.last);
	    
		return treeArray;
	}	

	/**
	 * 
	 * Return the number of elements in the heap
	 * Complexity - O(1)  
	 */
	public int size() {
		return getSize();
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 * Complexity - O(1)  
	 */
	public boolean empty() {
		return getSize() == 0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * Complexity - O(1)
	 */
	public int numTrees() {
		return getNumOfTrees();
	}
	
	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;
		
		public HeapNode() {
			this.item = null;
			this.child = null;
			this.next = this;
			this.parent = null;
			this.rank = 0;
		}
		
		public HeapItem getItem() {
    		return this.item;
    	}
		public HeapNode getChild() {
    		return this.child;
    	}
		public HeapNode getNext() {
    		return this.next;
    	}
		public HeapNode getParent() {
    		return this.parent;
    	}
		public int getRank() {
    		return this.rank;
    	}
		
		public void setChild(HeapNode child) {
    		this.child = child;
    	}
		public void setNext(HeapNode next) {
    		this.next = next;
    	}
		public void setParent(HeapNode parent) {
    		this.parent = parent;
    	}
		public void setRank(int rank) {
    		this.rank = rank;
    	}
		public void setItem(HeapItem item) {
    		this.item = item;
    	}
		
		/**
		 * Swap the pointers of items and nodes for decreaseKey
		 * Complexity - O(1)
		 */
		private void swap(HeapNode node2) {
			HeapItem tmpItem = node2.getItem();
			this.getItem().setNode(node2);
			node2.getItem().setNode(this);
			node2.setItem(this.getItem());
			this.setItem(tmpItem);
			return;
		}	
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;
		
		public HeapItem() {
			this.node = null;
			this.key = 0;
			this.info = null;
		}
		
		public HeapNode getNode() {
    		return this.node;
    	}
		public int getKey() {
    		return this.key;
    	}
		public String getInfo() {
    		return this.info;
    	}
    	
		public void setKey(int key) {
    		this.key = key;
    	}
		public void setInfo(String info) {
    		this.info = info;
    	}
		public void setNode(HeapNode node) {
    		this.node = node;
    	}
	}

}
