package net.wolftail.util.client;

public final class VanillaUnit extends UIUnit {
	
	public VanillaUnit(int width, int height) {
		super(width, height, true, false);
	}
	
	@Override
	void flush0() {
		
	}
	
	@Override
	public UnitType type() {
		return UnitType.VANILLA;
	}
}
