package models;

public class Pattern_Comment {
	private int id;
	private int idComment;
	private int idPattern;
	private float proximity;
	private boolean isLinked;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdComment() {
		return idComment;
	}
	public void setIdComment(int idComment) {
		this.idComment = idComment;
	}
	public int getIdPattern() {
		return idPattern;
	}
	public void setIdPattern(int idPattern) {
		this.idPattern = idPattern;
	}
	public float getProximity() {
		return proximity;
	}
	public void setProximity(float proximity) {
		this.proximity = proximity;
	}
	public boolean isLinked() {
		return isLinked;
	}
	public void setLinked(boolean isLinked) {
		this.isLinked = isLinked;
	}	
	
}
