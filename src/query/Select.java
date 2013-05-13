package query;

import global.Minibase;
import global.SortKey;
import heap.HeapFile;

import java.util.ArrayList;

import parser.AST_Select;
import relop.FileScan;
import relop.Iterator;
import relop.Predicate;
import relop.Schema;
import relop.Selection;
import relop.Tuple;

/**
 * Execution plan for selecting tuples.
 */
class Select implements Plan {

	String[] cols, tables;
	SortKey[] orders;
	Predicate[][] preds;
	Iterator[] tablesIters;

	/**
	 * Optimizes the plan, given the parsed query.
	 * 
	 * @throws QueryException
	 *             if validation fails
	 */
	public Select(AST_Select tree) throws QueryException {
		cols = tree.getColumns();
		orders = tree.getOrders();
		preds = tree.getPredicates();
		tables = tree.getTables();
		tablesIters = new Iterator[tables.length];

		// initialize iterators
		for (int i = 0; i < tablesIters.length; i++) {
			tablesIters[i] = new FileScan(
					Minibase.SystemCatalog.getSchema(tables[i]), new HeapFile(
							tables[i]));
		}

	} // public Select(AST_Select tree) throws QueryException

	public void push() {

		for (int i = 0; i < preds.length; i++) {
			for (int j = 0; j < tables.length; j++) {
				Schema s = Minibase.SystemCatalog.getSchema(tables[i]);
				boolean match = true;
				for (int k = 0; k < preds[i].length && match; k++)
					match &= preds[i][k].validate(s);
				if (match) {
					// put selection p(table[i]) in the iterators array instead
					// of table[i]
				}
			}
		}
	}

	/**
	 * Executes the plan and prints applicable output.
	 */
	public void execute() {
		@SuppressWarnings("unchecked")
		ArrayList<Predicate[]>[] CNF_s = new ArrayList[tables.length];

		for (int t = 0; t < tables.length; t++) {
			CNF_s[t] = new ArrayList<Predicate[]>();

			for (int i = 0; i < preds.length; i++) {
				boolean ok = true;
				for (int j = 0; j < preds[i].length; j++) {
					ok &= preds[i][j].validate(global.Minibase.SystemCatalog
							.getSchema(tables[t]));
					if (!ok)
						break;
				}
				if (ok)
					CNF_s[t].add(preds[i]);
			}

		}

		for (int t = 0; t < tables.length; t++)
			if (CNF_s[t].size() > 0) {

				Predicate[][] cur_pred = new Predicate[CNF_s[t].size()][];

				for (int i = 0; i < cur_pred.length; i++)
					cur_pred[i] = CNF_s[t].get(i);

				tablesIters[t] = new Selection(tablesIters[t], cur_pred);
			}

	} // public void execute()

	// /**
	// * Executes the plan and prints applicable output.
	// */
	// public void execute() {
	// // FileScan[] scans = new FileScan[tables.length];
	//
	// // opening tables scans
	// Queue<Iterator> queue = new LinkedList<Iterator>();
	// for (int i = 0; i < tables.length; i++) {
	// Schema schema = Minibase.SystemCatalog.getSchema(tables[i]);
	// HeapFile file = new HeapFile(tables[i]);
	// queue.add(new FileScan(schema, file));
	// }
	// // join all tables (super join)
	// while (queue.size() > 1) {
	// Iterator left = queue.poll();
	// Iterator right = queue.poll();
	// queue.add(new SimpleJoin(left, right, new Predicate[0]));
	// }
	// Iterator selected = queue.poll();
	//
	// // select from tables
	// for (int i = 0; i < preds.length; i++) {
	// Iterator[] ors = new Iterator[preds[i].length];
	// for (int j = 0; j < preds[i].length; j++) {
	// ors[j] = new Selection(selected, preds[i][j]);
	// // System.out.println(preds[i][j].toString());
	// }
	// // System.out.println("----------------");
	// selected = new Union(ors, i);
	// }
	// // projection
	//
	// // get columns indexes in selected table
	// Schema selectedSchema = selected.getSchema();
	// Integer[] fields = new Integer[cols.length];
	// for (int i = 0; i < cols.length; i++)
	// fields[i] = selectedSchema.fieldNumber(cols[i]);
	//
	// Projection output = new Projection(selected, fields);
	// int count = 0;
	// while (output.hasNext()) {
	// output.getNext().print();
	// count++;
	// }
	// // output.explain(0);
	// output.close();
	//
	// // print the output message
	// System.out.println(count + " rows affected.");
	//
	// } // public void execute()

	class Union extends Iterator {
		Iterator[] itrs;
		int next;

		public Union(Iterator[] itrs, int t) {
			setSchema(itrs[0].getSchema());
			this.itrs = itrs;
			next = 0;
		}

		@Override
		public void explain(int depth) {
			System.out.println("Union : " + depth);
			for (int i = 0; i < itrs.length; i++)
				itrs[i].explain(depth + 1);
		}

		@Override
		public void restart() {
			if (next == itrs.length)
				next--;
			while (next >= 0)
				itrs[next--].restart();
			next = 0;
		}

		@Override
		public boolean isOpen() {
			return itrs[0].isOpen();
		}

		@Override
		public void close() {
			for (int i = 0; i < itrs.length; i++)
				itrs[i].close();
		}

		@Override
		public boolean hasNext() {
			return next < itrs.length && itrs[next].hasNext();
		}

		@Override
		public Tuple getNext() {
			Tuple ret = itrs[next].getNext();
			if (!itrs[next].hasNext()) {
				itrs[next].restart();
				next++;
			}
			return ret;
		}
	}
} // class Select implements Plan
