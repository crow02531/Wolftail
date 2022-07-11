package net.wolftail.util.tracker;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.SharedImpls;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public final class ContentTracker {
	
	private final MinecraftServer server;
	
	private ContentTracker(MinecraftServer arg0) {
		this.server = arg0;
	}
	
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		order.type().subscribe(this.server, order, subscriber);
	}
	
	public void unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		order.type().unsubscribe(this.server, order, subscriber);
	}
	
	@Nonnull
	public static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		ExtensionsMinecraftServer ext = SharedImpls.as(server);
		ContentTracker r = ext.wolftail_getContentTracker();
		
		if(r == null) {
			synchronized(LOCK) {
				r = ext.wolftail_getContentTracker();
				
				if(r == null)
					ext.wolftail_setContentTracker(r = new ContentTracker(server));
			}
		}
		
		return r;
	}
	
	private static final Object LOCK = new Object();
}
