package net.wolftail.internal.util.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.shorts.AbstractShortIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

/**
 * A simple, brute-force implementation of a short set based on a backing array.
 * Perfect for storing a relatively small number of things.
 * 
 * <p>
 * Notice that a short set will never have its size exceed 65536.
 * </p>
 * 
 * @see it.unimi.dsi.fastutil.shorts.ShortArraySet ShortArraySet
 */
public class SmallShortSet extends AbstractShortSet implements Cloneable, Serializable, RandomAccess {
	
	private static final long serialVersionUID = 1L;
	
	private final int limit; // <= 65536
	
	private int size; // <= limit
	private transient short[] array;
	
	public SmallShortSet() {
		this.limit = 0;
		
		this.array = ShortArrays.EMPTY_ARRAY;
	}
	
	public SmallShortSet(int limit) {
		this(limit, Math.min(limit, 16));
	}
	
	public SmallShortSet(int capacity, int limit) {
		if ((limit = this.limit = Math.min(limit, 65536)) < 0)
			throw new IllegalArgumentException("limit");
		
		if (capacity < 0)
			throw new IllegalArgumentException("capacity");
		
		this.array = new short[Math.min(capacity, limit)];
	}
	
	private int find(final short o) {
		short[] a = this.array;
		
		for (int i = this.size; i-- != 0;)
			if (a[i] == o)
				return i;
			
		return -1;
	}
	
	@Override
	public boolean contains(final short k) {
		return this.find(k) >= 0;
	}
	
	@Override
	public boolean rem(final short k) {
		final int pos = this.find(k);
		if (pos < 0)
			return false;
		
		System.arraycopy(this.array, pos + 1, this.array, pos, this.size-- - pos - 1);
		
		this.modCount++;
		
		return true;
	}
	
	@Override
	public boolean add(final short k) {
		final int pos = find(k);
		if (pos >= 0)
			return false;
		
		if (this.size == this.array.length)
			if (!this.tryExpand())
				throw new IllegalStateException("Full");
			
		this.array[this.size++] = k;
		
		this.modCount++;
		
		return true;
	}
	
	private boolean tryExpand() {
		final int oldLength = this.array.length;
		final int newLength = Math.min(oldLength == 0 ? 2 : oldLength * 2, this.limit);
		
		if (newLength == oldLength)
			return false;
		
		final short[] b = new short[newLength];
		
		System.arraycopy(this.array, 0, b, 0, oldLength);
		this.array = b;
		
		return true;
	}
	
	public short get(int index) {
		return this.array[Preconditions.checkElementIndex(index, this.size)];
	}
	
	@Override
	public void clear() {
		this.size = 0;
		
		this.modCount = 0;
	}
	
	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}
	
	public boolean isFull() {
		return this.size == this.limit;
	}
	
	private transient int modCount;
	
	@Override
	public ShortIterator iterator() {
		return new AbstractShortIterator() {
			
			int next;
			
			int expectedModCount = modCount;
			
			@Override
			public boolean hasNext() {
				return next < size;
			}
			
			@Override
			public short nextShort() {
				checkForComodification();
				if (!hasNext())
					throw new NoSuchElementException();
				
				return array[next++];
			}
			
			@Override
			public void remove() {
				checkForComodification();
				
				final int tail = size-- - next--;
				
				System.arraycopy(array, next + 1, array, next, tail);
			}
			
			@Override
			public int skip(int n) {
				checkForComodification();
				
				next += (n = Math.min(n, size - next));
				
				return n - 1;
			}
			
			void checkForComodification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}
	
	@Override
	public int size() {
		return this.size;
	}
	
	public int limit() {
		return this.limit;
	}
	
	@Override
	public SmallShortSet clone() {
		SmallShortSet c;
		
		try {
			c = (SmallShortSet) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		
		c.array = this.array.clone();
		c.modCount = 0;
		
		return c;
	}
	
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		
		for (int i = 0; i < this.size; i++)
			s.writeShort(this.array[i]);
	}
	
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		
		if (!(0 <= this.size && this.size <= this.limit && this.limit <= 65536))
			throw new IOException();
		
		this.array = new short[this.size];
		
		for (int i = 0; i < this.size; i++)
			this.array[i] = s.readShort();
	}
}
