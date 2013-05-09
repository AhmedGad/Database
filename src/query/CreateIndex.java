package query;

import heap.HeapFile;
import index.HashIndex;
import global.Minibase;
import global.SearchKey;
import parser.AST_CreateIndex;
import relop.FileScan;
import relop.Schema;
import relop.Tuple;

/**
 * Execution plan for creating indexes.
 */
class CreateIndex implements Plan {

	/** Name of the index to create. */
	protected String fileName;

	/** Name of the table to index. */
	protected String ixTable;

	/** Name of the column to index. */
	protected String ixColumn;
	protected Schema schema;

	/**
	 * Optimizes the plan, given the parsed query.
	 * 
	 * @throws QueryException
	 *             if index already exists or table/column invalid
	 */
	public CreateIndex(AST_CreateIndex tree) throws QueryException {
		fileName = tree.getFileName();
		ixTable = tree.getIxTable();
		ixColumn = tree.getIxColumn();
		QueryCheck.tableExists(ixTable);
		QueryCheck.fileNotExists(fileName);
		schema = Minibase.SystemCatalog.getSchema(ixTable);
		QueryCheck.columnExists(schema, ixColumn);
		// TODO check wether there exists an already created index
	} // public CreateIndex(AST_CreateIndex tree) throws QueryException

	/**
	 * Executes the plan and prints applicable output.
	 */
	public void execute() {
		HashIndex hashidx = new HashIndex(fileName);
		FileScan fsc = new FileScan(schema, new HeapFile(ixTable));
		int fieldNo = schema.fieldNumber(ixColumn);
		while (fsc.hasNext()) {
			Tuple tuple = fsc.getNext();
			hashidx.insertEntry(new SearchKey(tuple.getField(fieldNo)),
					fsc.getLastRID());
		}
		fsc.close();
		Minibase.SystemCatalog.createIndex(fileName, ixTable, ixColumn);
		System.out.println("Index " + fileName + " Created on table " + ixTable
				+ " Column" + ixColumn);

	} // public void execute()
} // class CreateIndex implements Plan
