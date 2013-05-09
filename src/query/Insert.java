package query;

import global.Minibase;
import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import parser.AST_Insert;
import relop.Schema;
import relop.Tuple;

/**
 * Execution plan for inserting tuples.
 */
class Insert implements Plan {

	String fileName;
	Object[] values;

	/**
	 * Optimizes the plan, given the parsed query.
	 * 
	 * @throws QueryException
	 *             if table doesn't exists or values are invalid
	 */
	public Insert(AST_Insert tree) throws QueryException
	{
		fileName = tree.getFileName();
		values = tree.getValues();
	} // public Insert(AST_Insert tree) throws QueryException

	/**
	 * Executes the plan and prints applicable output.
	 */
	public void execute()
	{
		// insert values into table
		HeapFile f = new HeapFile(fileName);
		Schema schema = Minibase.SystemCatalog.getSchema(fileName);
		Tuple tuple = new Tuple(schema, values);
		RID rid = f.insertRecord(tuple.getData());

		// update indexes of this table
		IndexDesc[] indexes = Minibase.SystemCatalog.getIndexes(fileName);
		for (IndexDesc ind : indexes)
		{
			String indexName = ind.indexName;
			String colName = ind.columnName;
			HashIndex index = new HashIndex(indexName);
			SearchKey key = new SearchKey(tuple.getField(colName));
			index.insertEntry(key, rid);
		}

		// print the output message
		System.out.println("1 row affected.");

	} // public void execute()
} // class Insert implements Plan
