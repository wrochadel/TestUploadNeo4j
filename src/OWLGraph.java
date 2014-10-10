import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.impl.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;


public class OWLGraph
{
	private static enum RelTypes implements RelationshipType
	{
		KNOWS
	}
	
	String DB_PATH;
	public GraphDatabaseService graphDb;

	public OWLGraph(String DB_PATH)
	{
		this.DB_PATH = DB_PATH;
		clearDb();
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( this.DB_PATH );
		registerShutdownHook( this.graphDb );			
	}

	private void clearDb()
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

	private void registerShutdownHook( final GraphDatabaseService graphDb )
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
	
	
	private ExecutionEngine createExecutionEngine(String labelName, String uniqueProperty)
	{
		
		try ( Transaction tx = graphDb.beginTx() )
		{
		    graphDb.schema()
		            .constraintFor( DynamicLabel.label( labelName ) )
		            .assertPropertyIsUnique( uniqueProperty )
		            .create();
		    tx.success();
		}

		ExecutionEngine engine = new ExecutionEngine(graphDb);
		return engine;
	}
	
	
	public static void AddToGraph(OWLGraph G)
	{
		Node firstNode;
		Node secondNode;
		Relationship relationship;
		
		ExecutionEngine IDEngine = G.createExecutionEngine("Person","ID"); 
		
		//ExecutionEngine engine = new ExecutionEngine(G.graphDb);
		
		try(Transaction tx = G.graphDb.beginTx())
		{
			Label p1 = DynamicLabel.label("Person");
			firstNode = G.graphDb.createNode(p1);
			firstNode.setProperty("ID",1);
			firstNode.setProperty("FirstName", "John");
			firstNode.setProperty("LastName","Hamilton");

			secondNode = G.graphDb.createNode(p1);
			secondNode.setProperty("ID",2);
			secondNode.setProperty("FirstName", "Raj");
			secondNode.setProperty("LastName", "Reddy");
			secondNode.setProperty("Age",61);

			relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			relationship.setProperty("since",1970);

			// Get or create new Node
			ExecutionResult result;
			String queryString = "MERGE (n:Person {ID: {ID}}) RETURN n";
		    Map<String, Object> parameters = new HashMap<>();
		    parameters.put( "ID", 1 );
		    result = IDEngine.execute( queryString, parameters );
		    Iterator<Node> n_column = result.columnAs( "n" );
		   
		    Node n;
		    n = n_column.next();
		    n.setProperty("Age", 55);
		    
			tx.success();
			System.out.println("WoohOO");
		}
		G.graphDb.shutdown();
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String DB_PATH = "/home/apoorv/Downloads/neo4j-community-2.1.4/data/Test.db";    
		OWLGraph G = new OWLGraph(DB_PATH);
		//ExecutionEngine engine = new ExecutionEngine(G.graphDb);
		
		AddToGraph(G);
	}
}