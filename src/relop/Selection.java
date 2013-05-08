package relop;

/**
 * The selection operator specifies which tuples to retain under a condition; in
 * Minibase, this condition is simply a set of independent predicates logically
 * connected by AND operators.
 */
public class Selection extends Iterator {
	Iterator iterator;
	Predicate[] predicates;
	Tuple temp;

	/**
	 * Constructs a selection, given the underlying iterator and predicates.
	 */
	public Selection(Iterator iter, Predicate... preds) {
		iterator = iter;
		predicates = preds;
	}

	/**
	 * Gives a one-line explaination of the iterator, repeats the call on any
	 * child iterators, and increases the indent depth along the way.
	 */
	public void explain(int depth) {
		System.out.println("SelectionIterator " + depth);
		iterator.explain(depth + 1);
	}

	/**
	 * Restarts the iterator, i.e. as if it were just constructed.
	 */
	public void restart() {
		iterator.restart();
	}

	/**
	 * Returns true if the iterator is open; false otherwise.
	 */
	public boolean isOpen() {
		return iterator.isOpen();
	}

	/**
	 * Closes the iterator, releasing any resources (i.e. pinned pages).
	 */
	public void close() {
		iterator.close();
	}

	/**
	 * Returns true if there are more tuples, false otherwise.
	 */
	public boolean hasNext() {
		if (temp == null) {
			while (iterator.hasNext()) {
				Tuple t = iterator.getNext();
				boolean satisfy = true;// wether or not the tuple satisfies the
										// given predicates
				for (int i = 0; i < predicates.length && satisfy; i++)
					satisfy = satisfy && predicates[i].evaluate(t);
				if (satisfy) {
					temp = t;
					break;
				}
			}
		}
		return temp != null;
	}

	/**
	 * Gets the next tuple in the iteration.
	 * 
	 * @throws IllegalStateException
	 *             if no more tuples
	 */
	public Tuple getNext() {
		if (hasNext()) {
			Tuple t = temp;
			temp = null;
			return t;
		}
		throw new IllegalStateException();
	}

} // public class Selection extends Iterator
