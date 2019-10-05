package models;

public class Pattern {
	private int id;
	private String pattern;
	private int level; //Usado quando for implementar os sinonimos. Talvez seja desnecessario e use apenas o campo idOriginal
	private int idOriginal;//Usado quando for implementar os sinonimos
	private int length;
	private float score;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
	
	
}
