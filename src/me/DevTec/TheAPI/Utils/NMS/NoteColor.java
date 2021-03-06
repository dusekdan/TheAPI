package me.DevTec.TheAPI.Utils.NMS;

public class NoteColor {
	private int note;

	public NoteColor(int note) {
		if (note < 0) {
			throw new IllegalArgumentException("The note value is lower than 0");
		}
		if (note > 24) {
			throw new IllegalArgumentException("The note value is higher than 24");
		}
		this.note = note;
	}

	public float getValueX() {
		return note / 24F;
	}

	public float getValueY() {
		return 0;
	}

	public float getValueZ() {
		return 0;
	}
}
