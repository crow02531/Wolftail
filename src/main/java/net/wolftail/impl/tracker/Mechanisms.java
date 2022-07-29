package net.wolftail.impl.tracker;

import java.util.ArrayList;
import java.util.List;

public final class Mechanisms {
	
	private static final List<Runnable> MECHANISMS = new ArrayList<>();
	
	private Mechanisms() {
	}
	
	public static void add(Runnable r) {
		synchronized (MECHANISMS) {
			if (MECHANISMS.stream().allMatch(e -> e != r))
				MECHANISMS.add(r);
		}
	}
	
	public static void run() {
		List<Runnable> l = MECHANISMS;
		
		synchronized (l) {
			for (int i = l.size(); i-- != 0;)
				l.get(i).run();
		}
	}
}
