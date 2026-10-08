package com.noemod.autofarm;

public enum FarmingMode {
	OFF("NONAKTIF", "§c"),
	AUTOFARM("AUTOFARM AKTIF", "§a"),
	BONEMEAL("AUTO BONE MEAL AKTIF", "§b");

	private final String displayName;
	private final String colorCode;

	FarmingMode(String displayName, String colorCode) {
		this.displayName = displayName;
		this.colorCode = colorCode;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getColorCode() {
		return colorCode;
	}

	public String getFormattedText() {
		return colorCode + "[Noe-Mod] Mode: " + displayName;
	}
}
