package extracao;

/**
 * Class to map a comment into an object
 */
public class Comment {
	public String comment;
	public int idMethod = 0;
	public int idClass = 0; 
	public String path;
	
	public Comment(String comment, String path) {
		this.comment = comment;
		this.path = path;
	}
	
	public Comment(String comment, String path, int idMethod, int idClass) {
		this.comment = comment;
		this.idClass = idClass;
		this.idMethod = idMethod;
		this.path = path;
	}
	
	@Override
	public boolean equals(Object arg0) {
		boolean isEqual= false;
		Comment aux = (Comment) arg0;
	    if (comment != null) { 
	        isEqual = comment.equals(aux.comment);
	    } 
	 
	    return isEqual;
	}	
	
	@Override
	public String toString() {
		String text = comment + " | " + path + " | " + idMethod + " | " + idClass;
		return text;
	}
}
