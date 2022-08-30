package net.wolftail.internal.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.wolftail.api.lifecycle.SectionState;

public final class SectionHandler {
	
	public static final SectionHandler HANDLER_PREPARING = new SectionHandler(SectionState.ACTIVE);
	public static final SectionHandler HANDLER_PREPARED = new SectionHandler();
	public static final SectionHandler HANDLER_LOADING = new SectionHandler();
	public static final SectionHandler HANDLER_LOADED = new SectionHandler();
	public static final SectionHandler HANDLER_WANDERING = new SectionHandler();
	public static final SectionHandler HANDLER_PLAYING = new SectionHandler();
	
	private final ReadWriteLock lock;
	
	private SectionState state;
	
	private SectionHandler() {
		this(SectionState.BEFORE);
	}
	
	private SectionHandler(SectionState init) {
		this.lock = new ReentrantReadWriteLock();
		
		this.state = init;
	}
	
	private void doLock() {
		this.lock.writeLock().lock();
	}
	
	private void doAdvance() {
		this.state = this.state.getNext();
	}
	
	private void doUnlock() {
		this.lock.writeLock().unlock();
	}
	
	// caller are responsible of checking lock state
	public SectionState getState() {
		return this.state;
	}
	
	public Lock getLock() {
		return this.lock.readLock();
	}
	
	private static final Logger logger = LogManager.getLogger("Wolftail/Lifecycle");
	
	public static Thread dedicatedServerRegularThread;
	
	public static void finish_preparing() {
		SectionHandler preparing = HANDLER_PREPARING;
		SectionHandler prepared = HANDLER_PREPARED;
		SectionHandler loading = HANDLER_LOADING;
		
		preparing.doLock();
		prepared.doLock();
		loading.doLock();
		
		preparing.doAdvance();
		prepared.doAdvance();
		loading.doAdvance();
		
		logger.info("Section PREPARING end and PREPARED, LOADING start");
		
		preparing.doUnlock();
		prepared.doUnlock();
		loading.doUnlock();
	}
	
	public static void finish_loading(boolean isServer) {
		SectionHandler loading = HANDLER_LOADING;
		SectionHandler loaded = HANDLER_LOADED;
		SectionHandler wandering = HANDLER_WANDERING;
		
		loading.doLock();
		loaded.doLock();
		wandering.doLock();
		
		loading.doAdvance();
		loaded.doAdvance();
		wandering.doAdvance();
		
		logger.info("Section LOADING end and LOADED, WANDERING start");
		
		loading.doUnlock();
		loaded.doUnlock();
		wandering.doUnlock();
		
		if (isServer) {
			SectionHandler playing = HANDLER_PLAYING;
			
			wandering.doLock();
			playing.doLock();
			
			wandering.doAdvance();
			playing.doAdvance();
			
			logger.info("Section WANDERING end and PLAYING start");
			
			wandering.doUnlock();
			playing.doUnlock();
		}
	}
	
	public static void on_client_playing_change() {
		SectionHandler wandering = HANDLER_WANDERING;
		SectionHandler playing = HANDLER_PLAYING;
		
		wandering.doLock();
		playing.doLock();
		
		wandering.doAdvance();
		playing.doAdvance();
		
		if (wandering.state == SectionState.ACTIVE)
			logger.info("Section PLAYING end and WANDERING start");
		else
			logger.info("Section WANDERING end and PLAYING start");
		
		wandering.doUnlock();
		playing.doUnlock();
	}
}
