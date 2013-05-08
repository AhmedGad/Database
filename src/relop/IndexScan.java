package relop;

import global.SearchKey;
import heap.HeapFile;
import index.BucketScan;
import index.HashIndex;

/**
 * Wrapper for bucket scan, an index access method.
 */
public class IndexScan extends Iterator {
	
	private HeapFile file;
	private HashIndex index;
	private BucketScan bucketScan;

	/**
	 * Constructs an index scan, given the hash index and schema.
	 */
	public IndexScan(Schema schema, HashIndex index, HeapFile file)
	{
		this.file = file;
		this.index = index;
		setSchema(schema);
		bucketScan = index.openScan();
	}

	/**
	 * Gives a one-line explaination of the iterator, repeats the call on any
	 * child iterators, and increases the indent depth along the way.
	 */
	public void explain(int depth)
	{
		System.out.println("IndexScan : "+index.toString());
	}

	/**
	 * Restarts the iterator, i.e. as if it were just constructed.
	 */
	public void restart()
	{
		bucketScan.close();
		bucketScan = index.openScan();
	}

	/**
	 * Returns true if the iterator is open; false otherwise.
	 */
	public boolean isOpen()
	{
		return index != null;
	}

	/**
	 * Closes the iterator, releasing any resources (i.e. pinned pages).
	 */
	public void close()
	{
		bucketScan.close();
		file = null;
		index = null;
	}

	/**
	 * Returns true if there are more tuples, false otherwise.
	 */
	public boolean hasNext()
	{
		return bucketScan.hasNext();
	}

	/**
	 * Gets the next tuple in the iteration.
	 * 
	 * @throws IllegalStateException
	 *             if no more tuples
	 */
	public Tuple getNext()
	{
		if(bucketScan.hasNext())
		{
			return new Tuple(getSchema(), file.selectRecord(bucketScan.getNext()));
		}else
			throw new IllegalStateException("No more tuples");
	}

	/**
	 * Gets the key of the last tuple returned.
	 */
	public SearchKey getLastKey()
	{
		return bucketScan.getLastKey();
	}

	/**
	 * Returns the hash value for the bucket containing the next tuple, or
	 * maximum number of buckets if none.
	 */
	public int getNextHash()
	{
		return bucketScan.getNextHash();
	}

} // public class IndexScan extends Iterator
