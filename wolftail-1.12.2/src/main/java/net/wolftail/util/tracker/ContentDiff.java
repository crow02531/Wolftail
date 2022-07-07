package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.network.PacketBuffer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;

@SideWith(section = GameSection.GAME_PLAYING)
public final class ContentDiff {
	
	static {
		SharedImpls.H2.content_diff_factory = ContentDiff::new;
	}
	
	private final SubscribeOrder order;
	
	private ContentDiff(SubscribeOrder arg0) {
		this.order = arg0;
	}
	
	@Nonnull
	public SubscribeOrder order() {
		return this.order;
	}
	
	public void writeTo(@Nonnull PacketBuffer buf) {
		
	}
	
	@Nonnull
	public static ContentDiff readFrom(@Nonnull PacketBuffer buf) {
		return null;
	}
}
