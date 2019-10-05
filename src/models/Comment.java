package models;

public class Comment {
	private int id;
	private String comment;
	private String steamed_comment;
	private int idmethod;
	private int idclass;
	private boolean hasflag;
	private String postagged;
	private String path;
	private int idproject;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSteamed_comment() {
		return steamed_comment;
	}
	public void setSteamed_comment(String steamed_comment) {
		this.steamed_comment = steamed_comment;
	}
	public int getIdmethod() {
		return idmethod;
	}
	public void setIdmethod(int idmethod) {
		this.idmethod = idmethod;
	}
	public int getIdclass() {
		return idclass;
	}
	public void setIdclass(int idclass) {
		this.idclass = idclass;
	}
	public boolean isHasflag() {
		return hasflag;
	}
	public void setHasflag(boolean hasflag) {
		this.hasflag = hasflag;
	}
	public String getPostagged() {
		return postagged;
	}
	public void setPostagged(String postagged) {
		this.postagged = postagged;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getIdproject() {
		return idproject;
	}
	public void setIdproject(int idproject) {
		this.idproject = idproject;
	}
	
	
}
