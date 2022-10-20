package net.wolftail.api;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

// TODO implement this idea
@Immutable
public final class Introduction {
	
	private final String name;
	private final String desc;
	
	private final String pics;
	
	public Introduction(@Nonnull String n, @Nonnull String d) {
		this(n, d, null);
	}
	
	public Introduction(@Nonnull String n, @Nonnull String d, String p) {
		this.name = n;
		this.desc = d;
		
		this.pics = p == null ? "wolftail:textures/misc/utype/missing.json" : p;
	}
	
	/**
	 * The display name of the uniplayer type.
	 * 
	 * @return the localization key of the display name
	 */
	@Nonnull
	public String name() {
		return this.name;
	}
	
	/**
	 * The description of the uniplayer type.
	 * 
	 * @return the localization key of the description
	 */
	@Nonnull
	public String description() {
		return this.desc;
	}
	
	/**
	 * @return a string that could be parsed as resource location, must ends with
	 *         '.json'
	 */
	@Nonnull
	public String pictures() {
		return this.pics;
	}
	
	@Override
	public int hashCode() {
		return (this.desc.hashCode() + this.pics.hashCode() * 31) * 31 + this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Introduction))
			return false;
		
		Introduction o0 = (Introduction) o;
		
		return this.name.equals(o0.name) && this.desc.equals(o0.desc) && this.pics.equals(o0.pics);
	}
}
