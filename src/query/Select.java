package query;

import global.Minibase;
import global.SortKey;
import heap.HeapFile;

import java.util.LinkedList;
import java.util.Queue;

import parser.AST_Select;
import relop.FileScan;
import relop.Iterator;
import relop.Predicate;
import relop.Projection;
import relop.Schema;
import relop.SimpleJoin;

/**
 * Execution plan for selecting tuples.
 */
class Select implements Plan {
	
	String[] cols , tables;
	SortKey[] orders;
	Predicate[][] preds;
	/**
	 * Optimizes the plan, given the parsed query.
	 * 
	 * @throws QueryException
	 *             if validation fails
	 */
	public Select(AST_Select tree) throws QueryException
	{
		cols = tree.getColumns();
		orders = tree.getOrders();
		preds = tree.getPredicates();
		tables = tree.getTables();
	} // public Select(AST_Select tree) throws QueryException

	/**
	 * Executes the plan and prints applicable output.
	 */
	public void execute()
	{
//		FileScan[] scans = new FileScan[tables.length];
		
		// opening tables scans
		Queue<Iterator> queue = new LinkedList<Iterator>();
		for(int i = 0; i < tables.length;i++)
		{
			Schema schema = Minibase.SystemCatalog.getSchema(tables[i]);
			HeapFile file = new HeapFile(tables[i]);
			queue.add(new FileScan(schema, file));
		}
		// join all tables (super join)
		while(queue.size() > 1)
		{
			Iterator left = queue.poll();
			Iterator right = queue.poll();
			queue.add(new SimpleJoin(left, right, new Predicate[0]));
		}
		// select from tables
//		for(int i = 0 ; i < )
//		Selection select = new Selection(iter, preds);
		
		// projection
		Iterator selected = queue.poll();
		
		// get columns indexes in selected table
		Schema selectedSchema = selected.getSchema();
		Integer[] fields = new Integer[cols.length];
		for(int i = 0; i  < cols.length;i++)
			fields[i] = selectedSchema.fieldNumber(cols[i]);
		
		Projection output = new Projection(selected, fields);
		int count = 0;
		while(output.hasNext())
		{
			output.getNext().print();
			count++;
		}
		
		// print the output message
		System.out.println(count+" rows affected.");

	} // public void execute()

} // class Select implements Plan
