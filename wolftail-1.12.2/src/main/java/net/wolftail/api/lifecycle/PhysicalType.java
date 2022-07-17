package net.wolftail.api.lifecycle;

import javax.annotation.Nonnull;

import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Constants;

/**
 * It's obvious Minecraft has two kind of programs, {@link #INTEGRATED_CLIENT} and
 * {@link #DEDICATED_SERVER}.
 */
public enum PhysicalType {
	
	INTEGRATED_CLIENT,
	DEDICATED_SERVER;
	
	public boolean is() {
		return CURRENT_TYPE == this;
	}
	
	/**
	 * Ensure {@code is()} return true.
	 * 
	 * @throws IllegalStateException	when the current physical type is not
	 * 		equals to {@code this}
	 * 
	 * @see #is()
	 */
	public void ensure() {
		if(CURRENT_TYPE != this)
			throw new IllegalStateException("Not in " + this);
	}
	
	@Nonnull
	public static PhysicalType currentType() {
		return CURRENT_TYPE;
	}
	
	private static final PhysicalType CURRENT_TYPE;
	
	static {
		CURRENT_TYPE = Constants.SIDE_CLIENT.equals(MixinService.getService().getSideName()) ? INTEGRATED_CLIENT : DEDICATED_SERVER;
	}
}
