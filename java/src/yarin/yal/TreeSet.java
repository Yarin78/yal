package yarin.yal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

// TODO: C# TreeSet implementation converted to Java. Not tested.
public class TreeSet<T> {
	private Comparator<T> comparator;
	private int count;
	private Node root;
	private int version;

	public TreeSet() {
	}

	public TreeSet(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	public Comparator<T> getComparator() {
		return this.comparator;
	}

	private void setRoot(Node value) {
		this.root = value;
		if (value != null) {
			this.root.setParent(null);
		}
	}

	private int compare(T k1, T k2) {
		if (this.comparator == null) {
			return ((Comparable<T>)k1).compareTo(k2);
		}
		return this.comparator.compare(k1, k2);
	}

	public void add(T item) {
		if (root == null) {
			setRoot(new Node(item, null, false));
			count = 1;
		}
		else {
			Node r = this.root;
			Node node = null;
			Node grandParent = null;
			Node greatGrandParent = null;
			int num = 0;
			while (r != null) {
				num = compare(item, r.getItem());
				if (num == 0) {
					this.root.setRed(false);
					throw new IllegalArgumentException("AddingDuplicate");
				}
				if (is4Node(r)) {
					split4Node(r);
					if (isRed(node))
						node = insertionBalance(r, node, grandParent, greatGrandParent);
				}
				greatGrandParent = grandParent;
				grandParent = node;
				node = r;
				r = (num < 0) ? r.getLeft() : r.getRight();
			}
			Node current = new Node(item, node);
			if (num > 0)
				node.setRight(current);
			else
				node.setLeft(current);
			if (node.isRed())
				node = insertionBalance(current, node, grandParent, greatGrandParent);
			this.root.setRed(false);
			count++;
			version++;
		}
	}

	public void clear() {
		setRoot(null);
		count = 0;
		version++;
	}

	public boolean contains(T item) {
		return findNode(item) != null;
	}

	public Iterator getIterator() {
		return new TreeSetIterator();
	}

	public boolean remove(T item) {
		if (root == null) {
			return false;
		}
		Node r = root;
		Node parent = null;
		Node node3 = null;
		Node match = null;
		Node parentOfMatch = null;
		boolean flag = false;
		while (r != null) {
			if (is2Node(r)) {
				if (parent == null)	{
					r.setRed(true);
				}
				else {
					Node sibling = getSibling(r, parent);
					if (sibling.isRed()) {
						if (parent.getRight() == sibling) {
							rotateLeft(parent);
						}
						else {
							rotateRight(parent);
						}
						parent.setRed(true);
						sibling.setRed(false);
						replaceChildOfNodeOrRoot(node3, parent, sibling);
						node3 = sibling;
						if (parent == match) {
							parentOfMatch = sibling;
						}
						sibling = (parent.getLeft() == r) ? parent.getRight() : parent.getLeft();
					}
					if (is2Node(sibling)) {
						merge2Nodes(parent, r, sibling);
					}
					else {
						TreeRotation rotation = rotationNeeded(parent, r, sibling);
						Node newChild = null;
						switch (rotation) {
							case LeftRotation:
								sibling.getRight().setRed(false);
								newChild = rotateLeft(parent);
								break;

							case RightRotation:
								sibling.getLeft().setRed(false);
								newChild = rotateRight(parent);
								break;

							case RightLeftRotation:
								newChild = rotateRightLeft(parent);
								break;

							case LeftRightRotation:
								newChild = rotateLeftRight(parent);
								break;
						}
						newChild.setRed(parent.isRed());
						parent.setRed(false);
						r.setRed(true);
						replaceChildOfNodeOrRoot(node3, parent, newChild);
						if (parent == match) {
							parentOfMatch = newChild;
						}
						node3 = newChild;
					}
				}
			}
			int num = flag ? -1 : compare(item, r.getItem());
			if (num == 0) {
				flag = true;
				match = r;
				parentOfMatch = parent;
			}
			node3 = parent;
			parent = r;
			if (num < 0) {
				r = r.getLeft();
			}
			else {
				r = r.getRight();
			}
		}
		if (match != null) {
			replaceNode(match, parentOfMatch, parent, node3);
			count--;
		}
		if (this.root != null) {
			this.root.setRed(false);
		}
		version++;
		return flag;
	}

	public int size() {
		return this.count;
	}

	public T getItem(int index) {
		Node node = findNodeByIndex(index);
		if (node == null)
			return null;
		return node.getItem();
	}

	public int getIndex(T item) {
		Node node = findNode(item);
		if (node == null)
			return -1;
		return findIndexByNode(node);
	}

	public Node findNodeByIndex(int index) {
		if (root == null)
			return null;
		if (index < 0 || index >= root.getCount())
			return null;

		Node node = root;
		while (true) {
			int leftCount = node.getLeft() == null ? 0 : node.getLeft().getCount();
			if (index < leftCount)
				node = node.getLeft();
			else if (index == leftCount)
				return node;
			else {
				index -= leftCount + 1;
				node = node.getRight();
			}
		}
	}

	public int findIndexByNode(Node node) {
		Node current = node;
		while (current.getParent() != null) {
			current = current.getParent();
		}
		int skipped = 0;
		while (true) {
			int dif = compare(node.getItem(), current.getItem());
			int leftCount = current.getLeft() == null ? 0 : current.getLeft().getCount();
			if (dif == 0)
				return skipped + leftCount;
			if (dif < 0)
				current = current.getLeft();
			else if (dif > 0) {
				skipped += 1 + leftCount;
				current = current.getRight();
			}
		}
	}

	/// <summary>
	/// Finds the first node in the set where they key is greater than or equal to <paramref name="item"/>.
	/// </summary>
	/// <param name="item">The key to find.</param>
	/// <returns>A node in the tree, or null if all nodes in the tree has a key less than the one specified.</returns>
	public Node findStartNode(T item) {
		int num;
		Node start = null;
		for (Node node = root; node != null; node = (num <= 0) ? node.getLeft() : node.getRight()) {
			num = compare(item, node.getItem());
			if (num <= 0) {
				start = node;
			}
		}
		return start;
	}

	/// <summary>
	/// Finds the last node in the set where they key is less than or equal to <paramref name="item"/>.
	/// </summary>
	/// <param name="item">The key to find.</param>
	/// <returns>A node in the tree, or null if all nodes in the tree has a key greater than the one specified.</returns>
	public Node findEndNode(T item) {
		int num;
		Node stop = null;
		for (Node node = root; node != null; node = (num < 0) ? node.getLeft() : node.getRight())	{
			num = compare(item, node.getItem());
			if (num >= 0) {
				stop = node;
			}
		}
		return stop;
	}

	public Node findNode(T item) {
		int num;
		for (Node node = root; node != null; node = (num < 0) ? node.getLeft() : node.getRight()) {
			num = compare(item, node.getItem());
			if (num == 0) {
				return node;
			}
		}
		return null;
	}

	private Node getSibling(Node node, Node parent) {
		return parent.getLeft() == node ? parent.getRight() : parent.getLeft();
	}

	public boolean inOrderTreeWalk(TreeWalkAction action) {
		if (root != null) {
			Stack<Node> stack = new Stack<Node>(); //2*((int) Math.log(Count + 1)));
			Node r = root;
			while (r != null) {
				stack.push(r);
				r = r.getLeft();
			}
			while (stack.size() != 0) {
				r = stack.pop();
				if (!action.act(r)) {
					return false;
				}
				for (Node node2 = r.getRight(); node2 != null; node2 = node2.getLeft()) {
					stack.push(node2);
				}
			}
		}
		return true;
	}

	private Node insertionBalance(Node current, Node parent, Node grandParent, Node greatGrandParent) {
		Node node;
		boolean flag = grandParent.getRight() == parent;
		boolean flag2 = parent.getRight() == current;
		if (flag == flag2) {
			node = flag2 ? rotateLeft(grandParent) : rotateRight(grandParent);
		}
		else {
			node = flag2 ? rotateLeftRight(grandParent) : rotateRightLeft(grandParent);
			parent = greatGrandParent;
		}
		grandParent.setRed(true);
		node.setRed(false);
		replaceChildOfNodeOrRoot(greatGrandParent, grandParent, node);

		return parent;
	}

	private boolean is2Node(Node node) {
		return (isBlack(node) && isNullOrBlack(node.getLeft())) && isNullOrBlack(node.getRight());
	}

	private boolean is4Node(Node node) {
		return isRed(node.getLeft()) && isRed(node.getRight());
	}

	private boolean isBlack(Node node) {
		return (node != null) && !node.isRed();
	}

	private boolean isNullOrBlack(Node node) {
		if (node != null)
			return !node.isRed();
		return true;
	}

	private boolean isRed(Node node) {
		return (node != null) && node.isRed();
	}

	private void merge2Nodes(Node parent, Node child1, Node child2) {
		parent.setRed(false);
		child1.setRed(true);
		child2.setRed(true);
	}

	private void replaceChildOfNodeOrRoot(Node parent, Node child, Node newChild) {
		if (parent != null) {
			if (parent.getLeft() == child) {
				parent.setLeft(newChild);
			}
			else {
				parent.setRight(newChild);
			}
		}
		else {
			setRoot(newChild);
		}
	}

	private void replaceNode(Node match, Node parentOfMatch, Node succesor, Node parentOfSuccesor) {
		if (succesor == match) {
			succesor = match.getLeft();
		}
		else {
			if (succesor.getRight() != null) {
				succesor.getRight().setRed(false);
			}
			if (parentOfSuccesor != match) {
				parentOfSuccesor.setLeft(succesor.getRight());
				succesor.setRight(match.getRight());
			}
			succesor.setLeft(match.getLeft());
		}
		if (succesor != null) {
			succesor.setRed(match.isRed());
		}
		replaceChildOfNodeOrRoot(parentOfMatch, match, succesor);
	}

	private Node rotateLeft(Node node) {
		Node right = node.getRight();
		node.setRight(right.getLeft());
		right.setLeft(node);
		return right;
	}

	private Node rotateLeftRight(Node node) {
		Node left = node.getLeft();
		Node right = left.getRight();
		node.setLeft(right.getRight());
		right.setRight(node);
		left.setRight(right.getLeft());
		right.setLeft(left);
		return right;
	}

	private Node rotateRight(Node node) {
		Node left = node.getLeft();
		node.setLeft(left.getRight());
		left.setRight(node);
		return left;
	}

	private Node rotateRightLeft(Node node) {
		Node right = node.getRight();
		Node left = right.getLeft();
		node.setRight(left.getLeft());
		left.setLeft(node);
		right.setLeft(left.getRight());
		left.setRight(right);
		return left;
	}

	private TreeRotation rotationNeeded(Node parent, Node current, Node sibling) {
		if (isRed(sibling.getLeft())) {
			if (parent.getLeft() == current)
				return TreeRotation.RightLeftRotation;
			return TreeRotation.RightRotation;
		}
		if (parent.getLeft() == current)
			return TreeRotation.LeftRotation;
		return TreeRotation.LeftRightRotation;
	}

	private void split4Node(Node node) {
		node.setRed(true);
		node.getLeft().setRed(false);
		node.getRight().setRed(false);
	}

	void updateVersion() {
		version++;
	}

	public class TreeSetIterator implements Iterator {
		private final int version;
		private final Stack<Node> stack;
		private Node current;
//		private Node dummyNode = new Node(null, null);

		TreeSetIterator() {
			version = TreeSet.this.version;
			stack = new Stack<Node>(); //2*((int) Math.log((set.Count + 1))));
			current = null;
			intialize();
		}

		private void intialize() {
			current = null;
			for (Node node = TreeSet.this.root; node != null; node = node.getLeft()) {
				stack.push(node);
			}
		}

/*
		public boolean MoveNext() {
			if (version != TreeSet.this.version)
				throw new IllegalStateException();
			if (stack.size() == 0) {
				current = null;
				return false;
			}
			current = stack.pop();
			for (Node node = current.getRight(); node != null; node = node.getLeft()) {
				stack.push(node);
			}
			return true;
		}

		public T getCurrent() {
			if (current != null)
				return current.getItem();
			return null;
		}

		boolean getNotStartedOrEnded() {
			return current == null;
		}

		void Reset() {
			if (version != TreeSet.this.version) {
				throw new IllegalStateException();
			}
			stack.clear();
			Intialize();
		}

*/
		@Override
		public boolean hasNext() {
			if (version != TreeSet.this.version)
				throw new IllegalStateException();
			return stack.size() > 0;
		}

		@Override
		public Object next() {
			if (version != TreeSet.this.version)
				throw new IllegalStateException();
			if (stack.size() == 0) {
				current = null;
				return null;
			}
			current = stack.pop();
			for (Node node = current.getRight(); node != null; node = node.getLeft()) {
				stack.push(node);
			}
			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public class Node {
		public Node(T item, Node parent) {
			setItem(item);
			setRed(true);
			setCount(1);
			this.parent = parent;
		}

		public Node(T item, Node parent, boolean isRed) {
			setItem(item);
			setRed(isRed);
			setCount(1);
			this.parent = parent;
		}

		public void updateCount() {
			int curCount = getCount();
			setCount(1 + (getLeft() == null ? 0 : getLeft().getCount()) + (getRight() == null ? 0 : getRight().getCount()));
			if (curCount != getCount() && getParent() != null) {
				getParent().updateCount();
			}
		}

		private Node left, right, parent;
		private boolean isRed;
		private T item;
		private int count;

		public Node getParent() {
			return parent;
		}

		void setParent(Node parent) {
			this.parent = parent;
		}

		public boolean isRed() {
			return isRed;
		}

		public void setRed(boolean red) {
			isRed = red;
		}

		public T getItem() {
			return item;
		}

		public void setItem(T item) {
			this.item = item;
		}

		public Node getLeft() {
			return left;
		}

		public void setLeft(Node value) {
			left = value;
			if (left != null)
				left.setParent(this);
			updateCount();
		}

		public Node getRight() {
			return right;
		}

		public void setRight(Node value) {
			right = value;
			if (right != null)
				right.setParent(this);
			updateCount();
		}

		public int getCount() {
			return count;
		}

		private void setCount(int count) {
			this.count = count;
		}

		public void Validate() {
			int count = 1;
			if (left != null) {
				left.Validate();
				count += left.getCount();
				if (left.getParent() != this)
					throw new RuntimeException();
			}
			if (right != null) {
				right.Validate();
				count += right.getCount();
				if (right.getParent() != this)
					throw new RuntimeException();
			}

			if (count != getCount())
				throw new RuntimeException();
		}

		@Override
		public String toString() {
			String ls = left == null ? "" : left.toString();
			String rs = right == null ? "" : right.toString();
			return this.item + " (cnt = " + getCount() + ")  L=(" + ls + ") R=(" + rs + ")";
		}

		private boolean isLeftChild() {
			return parent != null && parent.getLeft() == this;
		}

		private boolean isRightChild() {
			return parent != null && parent.getRight() == this;
		}

		public Node next() {
			Node current;
			if (right != null) {
				current = right;
				while (current.left != null) {
					current = current.left;
				}
				return current;
			}
			current = this;
			while (current.isRightChild()) {
				current = current.getParent();
			}
			if (current.isLeftChild())
			{
				return current.getParent();
			}
			return null;
		}

		public Node previous() {
			Node current;
			if (left != null) {
				current = left;
				while (current.getRight() != null) {
					current = current.getRight();
				}
				return current;
			}
			current = this;
			while (current.isLeftChild()) {
				current = current.getParent();
			}
			if (current.isRightChild()) {
				return current.getParent();
			}
			return null;
		}
	}

	public void validate() {
		if (root == null)
			return;
		root.Validate();
	}

	@Override
	public String toString() {
		if (root == null)
			return "";
		return root.toString();
	}

	enum TreeRotation {
		Unused, // = 0
		LeftRotation, // = 1,
		RightRotation, // = 2
		RightLeftRotation, // = 3,
		LeftRightRotation, // = 4,
	}

	public interface TreeWalkAction {
		boolean act(TreeSet.Node node);
	}

}