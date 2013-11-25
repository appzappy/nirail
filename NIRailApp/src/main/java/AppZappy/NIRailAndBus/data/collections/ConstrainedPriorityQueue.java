package AppZappy.NIRailAndBus.data.collections;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Priority Queue that has a limited size. 
 * Items added beyond the maximum size cause the largest item to be discarded
 * @author Kurru
 *
 * @param <T> The type of object to be kept within the PriorityQueue
 */
public class ConstrainedPriorityQueue<T> extends PriorityQueue<T>
{
	private static final long serialVersionUID = 204011824281243892L;
	
	private int maxItems = 0;
	/**
	 * Get the maximum number of items in this queue
	 * @return Maximum number of items in the queue
	 */
	public int getMaxSize()
	{
		return maxItems;
	}
	
	/**
	 * ConstrainedPriorityQueue used for selecting the smallest {maxSize} elements
	 * @param maxSize The maximum number of items to be stored in this priority queue
	 * @param comparator The comparator used to compare the stored items
	 */
	public ConstrainedPriorityQueue(int maxSize, Comparator<T> comparator)
	{
		super(maxSize, comparator);
		this.maxItems = maxSize;
	}
	
	

	/**
	 * Add an item to the queue
	 * @param value The item to add to the queue
	 * @return Always true
	 */
	public boolean add(T value)
	{
		final T peek = this.peek();
		final int items = this.size();
		if (items < maxItems)
		{
			super.add(value);
		}
		else if (this.comparator().compare(peek, value) < 0)
		{
			this.poll();
			super.add(value);
		}
		return true;
	};
}
