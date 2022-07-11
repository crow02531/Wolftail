package net.wolftail.api.lifecycle;

import javax.annotation.Nonnull;

/**
 * Represent the state of a {@link GameSection}.
 * 
 * @see GameSection
 */
public enum SectionState {
	
	BEFORE {
		
		@Override
		public SectionState advance() {
			return ACTIVE;
		}
	},
	
	ACTIVE {
		
		@Override
		public SectionState advance() {
			return IDLE;
		}
	},
	
	IDLE {
		
		@Override
		public SectionState advance() {
			return ACTIVE;
		}
	};
	
	@Nonnull
	public abstract SectionState advance();
}
