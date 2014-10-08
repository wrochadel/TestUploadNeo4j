import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.kernel.impl.util.FileUtils;
import java.io.File;
import java.io.IOException;



public class TestUpload
{

	/**
	 * @param args
	 */
	private static enum RelTypes implements RelationshipType
	{
		KNOWS
	}
	
	private static void clearDb(String DB_PATH)
	{
		try
		{
			FileUtils.deleteRecursively(new File (DB_PATH));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		GraphDatabaseService graphDb;
        Node firstNode;
        Node secondNode;
        Relationship relationship;
        
        String DB_PATH = "/home/apoorv/Downloads/neo4j-community-2.1.4/data/Test.db";
        clearDb(DB_PATH);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
        
        try(Transaction tx = graphDb.beginTx())
        {
        	Label p1 = DynamicLabel.label("Person");
        	firstNode = graphDb.createNode(p1);
        	firstNode.setProperty("FirstName", "John");
        	firstNode.setProperty("LastName","Hamilton");
        	
        	secondNode = graphDb.createNode(p1);
        	secondNode.setProperty("FirstName", "Raj");
        	secondNode.setProperty("LastName", "Reddy");
        	secondNode.setProperty("Age",61);
        	
        	relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
        	relationship.setProperty("since",1970);
        	       	
        	tx.success();
        	System.out.println("WoohOO");
        	graphDb.shutdown();
        }
	}
}