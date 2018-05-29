package ager.common;

import java.awt.Font;

public class AgeFont extends Font {
	private static final long serialVersionUID = -8660560397982613435L;

	public AgeFont() {
		super("微软雅黑", Font.PLAIN, 12);
	}
	
	public Font getBoldFont() {
		return this.deriveFont(BOLD);
	}
	
}
