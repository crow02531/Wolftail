package net.wolftail.impl.util.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.RandomAccess;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.longs.AbstractLong2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * A simple, brute-force implementation of a map based on two parallel backing
 * arrays. Perfect for storing a relatively small number of things.
 * 
 * @see it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap Long2ObjectArrayMap
 */
public class SmallLong2ObjectMap<V> extends AbstractLong2ObjectMap<V> implements Cloneable, RandomAccess {
	
	private static final long serialVersionUID = 1L;
	
	private transient long[] key;
	private transient Object[] value;
	
	private int size;
	
	public SmallLong2ObjectMap() {
		this.key = LongArrays.EMPTY_ARRAY;
		this.value = ObjectArrays.EMPTY_ARRAY;
	}
	
	public SmallLong2ObjectMap(final int capacity) {
		this.key = new long[capacity];
		this.value = new Object[capacity];
	}
	
	public SmallLong2ObjectMap(final long key0, final V val0) {
		this.key = new long[] { key0 };
		this.value = new Object[] { val0 };
		
		this.size = 1;
	}
	
	private int findKey(final long k) {
		long[] key = this.key;
		
		for(int i = this.size; i-- != 0;)
			if(key[i] == k) return i;
		
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(long key) {
		int index = this.findKey(key);
		
		return index < 0 ? this.defRetValue : (V) this.value[index];
	}
	
	public long getKey(int index) {
		return this.key[Preconditions.checkElementIndex(index, this.size)];
	}
	
	@SuppressWarnings("unchecked")
	public V getVal(int index) {
		return (V) this.value[Preconditions.checkElementIndex(index, this.size)];
	}
	
	@SuppressWarnings("unchecked")
	public V setVal(int index, V newVal) {
		V oldVal = (V) this.value[Preconditions.checkElementIndex(index, this.size)];
		this.value[index] = newVal;
		
		return oldVal;
	}
	
	@SuppressWarnings("unchecked")
	public V rem(int index) {
		final Object[] value = this.value;
		final V oldValue = (V) value[Preconditions.checkElementIndex(index, this.size)];
		final int tail = this.size - index - 1;
		
		System.arraycopy(this.key, index + 1, this.key, index, tail);
		System.arraycopy(value, index + 1, value, index, tail);
		
		value[--this.size] = null;
		
		return oldValue;
	}
	
	@Override
	public int size() {
		return this.size;
	}
	
	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}
	
	@Override
	public void clear() {
		Arrays.fill(this.value, 0, this.size, null);
		
		this.size = 0;
	}
	
	@Override
	public boolean containsKey(final long k) {
		return this.findKey(k) >= 0;
	}
	
	@Override
	public boolean containsValue(Object v) {
		Object[] value = this.value;
		
		for(int i = this.size; i-- != 0;)
			if(Objects.equal(value[i], v)) return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V put(long k, V v) {
		final int oldIndex = this.findKey(k);
		
		if(oldIndex >= 0) {
			final V oldValue = (V) this.value[oldIndex];
			this.value[oldIndex] = v;
			
			return oldValue;
		}
		
		if(this.size == this.key.length)
			this.expand();
		
		this.key[this.size] = k;
		this.value[this.size++] = v;
		
		return this.defRetValue;
	}
	
	private void expand() {
		final int oldLength = this.key.length;
		final int newLength = oldLength == 0 ? 2 : oldLength * 2;
		
		final long[] newKey = new long[newLength];
		final Object[] newValue = new Object[newLength];
		
		System.arraycopy(this.key, 0, newKey, 0, oldLength);
		System.arraycopy(this.value, 0, newValue, 0, oldLength);
		
		this.key = newKey;
		this.value = newValue;
	}
	
	@Override
	public V remove(final long k) {
		final int oldIndex = this.findKey(k);
		
		return oldIndex < 0 ? this.defRetValue : this.rem(oldIndex);
	}
	//TODO SmallLong2ObjectMap: entrySet, keySet, values
	@Override
	public ObjectSet<Entry<V>> long2ObjectEntrySet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public LongSet keySet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ObjectCollection<V> values() {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SmallLong2ObjectMap<V> clone() {
		SmallLong2ObjectMap<V> c;
		
		try {
			c = (SmallLong2ObjectMap<V>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		
		c.key = this.key.clone();
		c.value = this.value.clone();
		
		return c;
	}
	
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		
		for(int i = 0; i < this.size; i++) {
			s.writeLong(this.key[i]);
			s.writeObject(this.value[i]);
		}
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		
		this.key = new long[this.size];
		this.value = new Object[this.size];
		
		for(int i = 0; i < this.size; i++) {
			this.key[i] = s.readLong();
			this.value[i] = s.readObject();
		}
	}
}
