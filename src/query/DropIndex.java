package query;

import index.HashIndex;
import global.Minibase;
import parser.AST_DropIndex;

/**
 * Execution plan for dropping indexes.
 */
class DropIndex implements Plan {
	/** Name of the index to create. */
	protected String fileName;

	/**
	 * Optimizes the plan, given the parsed query.
	 * 
	 * @throws QueryException
	 *             if index doesn't exist
	 */
	String indexName;

	public DropIndex(AST_DropIndex tree) throws QueryException {
		fileName = tree.getFileName();
		QueryCheck.indexExists(fileName);
	} // public DropIndex(AST_DropIndex tree) throws QueryException

	/**
	 * Executes the plan and prints applicable output.
	 */
	public void execute() {
		new HashIndex(fileName).deleteFile();
		Minibase.SystemCatalog.dropIndex(fileName);

		// print the output message
		System.out.println("Index dropped");
	} // public void execute()

} // class DropIndex implements Plan
