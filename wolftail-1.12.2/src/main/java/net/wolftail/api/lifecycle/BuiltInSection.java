package net.wolftail.api.lifecycle;

import net.wolftail.impl.SharedImpls;

public final class BuiltInSection extends GameSection {
	
	public static final BuiltInSection GAME_PREPARING	= new BuiltInSection("PREPARING", null);
	public static final BuiltInSection GAME_PREPARED	= new BuiltInSection("PREPARED");
	public static final BuiltInSection GAME_LOADING		= new BuiltInSection("LOADING");
	public static final BuiltInSection GAME_LOADED		= new BuiltInSection("LOADED");
	public static final BuiltInSection GAME_WANDERING	= new BuiltInSection("WANDERING");
	public static final BuiltInSection GAME_PLAYING		= new BuiltInSection("PLAYING");
	
	static {
		SharedImpls.Holder1.token_preparing = GAME_PREPARING.token;
		SharedImpls.Holder1.token_prepared = GAME_PREPARED.token;
		SharedImpls.Holder1.token_loading = GAME_LOADING.token;
		SharedImpls.Holder1.token_loaded = GAME_LOADED.token;
		SharedImpls.Holder1.token_wandering = GAME_WANDERING.token;
		SharedImpls.Holder1.token_playing = GAME_PLAYING.token;
	}
	
	private final String name;
	
	private BuiltInSection(String name) {
		this.name = name;
	}
	
	private BuiltInSection(String name, Void unused) {
		super(SectionState.ACTIVE);
		
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
