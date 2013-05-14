package relop;

import java.util.Arrays;

/**
 * The simplest of all join algorithms: nested loops (see textbook, 3rd edition,
 * section 14.4.1, page 454).
 */
public class SimpleJoin extends Iterator {

	Iterator left, right;
	Predicate[] preds;
	Tuple next, currLeft;
	Predicate[][] cnf;

	/**
	 * Constructs a join, given the left and right iterators and join predicates
	 * (relative to the combined schema).
	 */
	public SimpleJoin(Iterator left, Iterator right, Predicate... preds) {
		this.right = right;
		this.left = left;
		this.preds = preds;
		cnf = new Predicate[preds.length][1];
		for (int i = 0; i < preds.length; i++) {
			cnf[i][0] = preds[i];
		}
		setSchema(Schema.join(left.getSchema(), right.getSchema()));
		calcNext();
	}

	public SimpleJoin(Iterator left, Iterator right, Predicate[][] cnf_preds) {
		this.right = right;
		this.left = left;
		cnf = cnf_preds;
		setSchema(Schema.join(left.getSchema(), right.getSchema()));
		calcNext();
	}

	/**
	 * Gives a one-line explaination of the iterator, repeats the call on any
	 * child iterators, and increases the indent depth along the way.
	 */
	public void explain(int depth) {
		System.out.println("SimpleJoin : " + Arrays.toString(preds));
		left.explain(depth + 1);
		right.explain(depth + 1);
	}

	/**
	 * Restarts the iterator, i.e. as if it were just constructed.
	 */
	public void restart() {
		left.restart();
		right.restart();
		currLeft = null;
		calcNext();
	}

	/**
	 * Returns true if the iterator is open; false otherwise.
	 */
	public boolean isOpen() {
		return left.isOpen() && right.isOpen();
	}

	/**
	 * Closes the iterator, releasing any resources (i.e. pinned pages).
	 */
	public void close() {
		left.close();
		right.close();
	}

	/**
	 * Returns true if there are more tuples, false otherwise.
	 */
	public boolean hasNext() {
		return next != null;
	}

	/**
	 * Gets the next tuple in the iteration.
	 * 
	 * @throws IllegalStateException
	 *             if no more tuples
	 */
	public Tuple getNext() {
		if (next == null || !isOpen())
			throw new IllegalStateException("No more tuples");
		Tuple ret = next;
		calcNext();
		return ret;
	}

	private void calcNext() {
		Tuple candidate = null;
		if (currLeft == null && left.hasNext())
			currLeft = left.getNext();
		while (currLeft != null && candidate == null) {
			if (!right.hasNext()) // restart iterator if we reached the end
			{
				if (left.hasNext())
					currLeft = left.getNext();
				else {
					currLeft = null;
					break;
				}
				right.restart();
			}
			while (right.hasNext()) {
				// merge right and left tuples
				candidate = Tuple.join(currLeft, right.getNext(), getSchema());
				boolean valid = true;
				// CNF predicate evaluator
				for (Predicate[] clause : cnf) {
					boolean cval = false;
					for (Predicate j : clause)
						cval |= j.evaluate(candidate);
					valid &= cval;
				}
				// check this candidate with each merge condition (Predicate)
				// for (Predicate pred : preds) {
				// // check this predicate
				// valid = pred.evaluate(candidate);
				// if (!valid)
				// break; // invalid candidate
				// }
				if (!valid)
					candidate = null; // invalid candidate
				else {
					// valid candidate
					next = candidate;
					return;
				}
			}
		}
		next = candidate;
	}

} // public class SimpleJoin extends Iterator
