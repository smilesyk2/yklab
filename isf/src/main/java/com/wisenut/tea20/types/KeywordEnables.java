package com.wisenut.tea20.types;

public enum KeywordEnables {
	NOT_APPLY {
		@Override
		public String toString() {
			return "NOT_APPLY";
		}
	},
	Y {
		@Override
		public String toString() {
			return "1";
		}
	},
	N {
		@Override
		public String toString() {
			return "0";
		}
	}
}
