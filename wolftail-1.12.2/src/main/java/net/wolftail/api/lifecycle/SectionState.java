package net.wolftail.api.lifecycle;

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
	
	public abstract SectionState advance();
}
