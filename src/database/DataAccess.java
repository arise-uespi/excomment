package database;

public class DataAccess {
	public static PostgreSQLJDBC dataManager; 
	
	//DataSources
	public static ProjectDataSource projectDataSource;
	public static ClassDataSource classDataSource = new ClassDataSource();
	public static MethodDataSource methodDataSource;
	public static CommentDataSource commentDataSource;
	public static PatternDataSource patternDataSource = new PatternDataSource();
}