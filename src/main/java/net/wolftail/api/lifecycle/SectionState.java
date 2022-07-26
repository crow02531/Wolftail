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
		public SectionState getNext() {
			return ACTIVE;
		}
	},
	
	ACTIVE {
		
		@Override
		public SectionState getNext() {
			return IDLE;
		}
	},
	
	IDLE {
		
		@Override
		public SectionState getNext() {
			return ACTIVE;
		}
	};
	
	@Nonnull
	public abstract SectionState getNext();
}
