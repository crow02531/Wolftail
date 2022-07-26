package net.wolftail.impl.util.collect;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

/**
 * A simple linked list providing directly node handling.
 * 
 * @see java.util.LinkedList LinkedList
 */
public class LinkedObjectCollection<K> extends AbstractObjectCollection<K> {
	
	private Node head;
	
	private int size;
	
	public Node enter(K k) {
		Node n = new Node(k);
		
		Node h;
		if((h = this.head) != null)
			(h.prev = n).next = h;
		
		this.head = n;
		this.size++;
		
		this.modCount++;
		
		return n;
	}
	
	public boolean add(K k) {
		this.enter(k);
		
		return true;
	}
	
	private int modCount;
	
	@Override
	public ObjectIterator<K> iterator() {
		return new AbstractObjectIterator<K>() {
			
			Node next = head;
			Node lastNext;
			
			int expectedModCount = modCount;
			
			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Override
			public K next() {
				checkForComodification();
				if(next == null) throw0();
				
				next = (lastNext = next).next;
				
				return lastNext.item;
			}
			
			@Override
			public void remove() {
				checkForComodification();
				if(lastNext == null) throw new IllegalStateException();
				
				lastNext.unlink();
				
				lastNext = null;
				expectedModCount++;
			}
			
			void checkForComodification() {
				if(expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}
	
	@Override
	public int size() {
		return this.size;
	}
	
	@Override
	public void clear() {
		super.clear();
		
		this.modCount = 0;
	}
	
	public final class Node {
		
		private Node prev, next;
		
		private K item;
		
		private Node(K i) {
			item = i;
		}
		
		public K get() {
			return item;
		}
		
		public void set(K k) {
			item = k;
			
			modCount++;
		}
		
		public void unlink() {
			if(prev == null) {
				if(head != this) throw0();
				
				head = next;
			} else {
				if(prev.next != this) throw0();
				
				prev.next = next;
			}
			
			size--;
			
			modCount++;
		}
	}
	
	private static void throw0() {
		throw new NoSuchElementException();
	}
}
