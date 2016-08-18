// Gian Brazzini
// EE 3345.0U1
// Project 5: Using Dijkstra's algorithm to find the most weight effective path between airports

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class DijkstraShortestPath {

	// Uses an ArrayList to keep track of each unique Vertex
	// 		Vertices will have unique names
	// Uses a separate ArrayList to keeep track of each individual Edge
	// 		Edges will have unique vertices and weights
	private static class AdjacencyList{
		// Keeps track of vertices and edges
		ArrayList<Vertex> vertices = new ArrayList<>();
		// The edges are also in the ArrayList of the relevant vertices
		// Having a master list makes it easier to traverse when resetting as well as when checking for duplicates
		ArrayList<Edge> edges = new ArrayList<>();
		
		// Receives two Strings and an integer as parameters
		// The Strings are the unique identifiers for the vertices.
		//		New vertices are created as needed
		// The weight is used to create an edge.
		public boolean insert(String a, String d, int w){
			// Checks if a vertex of the name 'a' or 'd' already exist
			Vertex departure = isUnique(d);
			Vertex arrival = isUnique(a);

			// If the vertex doesn't already exist, create it
			if(departure==null)
				departure = newVertex(d);
			if(arrival==null)
				arrival = newVertex(a);

			// If the edge already exists then return false without creating it
			Edge e = new Edge(arrival, departure, w);
			if( edges.contains(e) )
				return false;
			
			// Creates and edge with weight w
			// Inserts pointers of the edge into the edges array 
			//		and each vertex's adjacent vertex ArrayList
			edges.add(e);
			arrival.insertAdj(e);
			departure.insertAdj(e);
			return true;
		}
		
		// Creates a new, unique vertex.
		// Returns the vertex.
		private Vertex newVertex(String n){
			vertices.add(new Vertex(n));
			return vertices.get(vertices.size()-1);
		}

		// Traverses the vertices ArrayList for a vertex of specific name
		// Returns the Vertex or null if it's not found
		private Vertex isUnique(String name){
			Vertex v;
			for(int i=0;i<vertices.size(); i++){
				v=vertices.get(i);
				if( v.equals(name) )
					return v;
			}
			return null;
		}

		// Accepts two strings as parameters
		// Returns false if vertices of those names don't exist
		// Finds shortest path from vertex a to d otherwise
		public boolean findPath(String a, String d) throws Exception{
			// Checks if the vertices of the names in the Strings exist
			Vertex arrival = isUnique(a);
			Vertex departure = isUnique(d);

			// Returns false if the vertices didn't exist
			if(arrival==null || departure==null)
				return false;
	
			// Uses the reset function of edges and vertices list
			int size = edges.size();
			for(int i=0; i<size; i++)
				edges.get(i).reset();
			size = vertices.size();
			for(int i=0; i<size; i++)
				vertices.get(i).reset();
			
			return findPath(arrival, departure);
		}
		
		// Accepts two vertices as parameters
		private boolean findPath(Vertex a, Vertex d) throws Exception{
			
			// Starting values. Departuring location's weight and pV is set to -1 and null
			//					Known is set to true.
			Vertex smallest = d;
			smallest.update(0, null);
	
			// Uses a binary heap to keep track of the vertices that have been seen but still need the adjacent ones analysed
			BinaryHeap<Vertex> newSmallest = new BinaryHeap<>();
			
			// Find single shortest path from Vertex d
			// For-loop for each individual vertex
			int numAdj, oldweight, newWeight, size = edges.size();;
			Vertex temp;
			for(int i=0; i<size; i++){
				
				// For-loop for each edge connected to the vertex
				numAdj = smallest.adjEdges.size();
				for(int j=0; j<numAdj; j++){
					// If the edge has been visited, skip it
					if( smallest.adjEdges.get(j).visited() )
						continue;
					
					// Mark the edge as visited 
					// Make 'temp' the vertex adjacent to 'smallest'
					// Save a copy of the weight of the edge in 'oldWeight' to avoid traversing multiple times
					smallest.adjEdges.get(j).visit();
					temp = smallest.getAdj(j);
					oldweight = temp.weight;
					
					// Calculate the weight from this vertex. If is is smaller, replace
					newWeight = smallest.weight + smallest.getWeight(j);
					if( temp.weight==-1 || newWeight<oldweight )
						temp.update(newWeight, smallest);
						
					// If the vertex is not known, add it to the heap to later analyse 
					if( !temp.known )
						newSmallest.insert(temp);
				}

				// Marks the vertex as known and gets the next vertex with the smallest path
				smallest.known();
				smallest = newSmallest.deleteMin();
			}

			// Print route
			StringBuilder connections = new StringBuilder();
			connections = connections.append(" -> " + a.name);
			int c=0;
			temp=a.previousVertex;
			while(true){
				if(temp.equals(d)){
					connections.insert( 0, d.name );
					break;
				}
				
				connections.insert(0, " -> " + temp.name);
				temp = temp.previousVertex;
				c++;
			}
			
			System.out.println("\nBy price:");
			System.out.println("Price:\t\t"+a.weight);
			System.out.println("Connections:\t" + c);
			System.out.println("Route:\t\t" + connections.toString());
			
			return true;
		}
		
		// Prins a list of every unique vertex
		public boolean printList(){
			if(vertices.isEmpty()){
				System.out.println("ERROR: List empty");
				return false;
			}
					
			System.out.print("CITIES:     ");
					
			Vertex v;
			for(int i=1;i<=vertices.size(); i++){
				v=vertices.get(i-1);
				System.out.print(v.name());
						
				if((i+2)%4==0 || i==2)
					System.out.println();
				else
					System.out.print("   ");
				}
					
				System.out.println();
				return true;
			}
				
		// Prints the adjacencly list in the order that the vertices are in
		@SuppressWarnings("unused")
		public boolean printAdjacenyList(){
			if(vertices.isEmpty()){
				System.out.println("ERROR: List empty");
				return false;
			}
					
			int l;
			Vertex v;
			for(int i=0;i<vertices.size(); i++){
				v=vertices.get(i);
				System.out.print(v.name()+":   ");
						
				l=v.adjEdges.size();
				for(int j=0; j<l; j++){
					System.out.print( v.getAdj(j).name() );
					System.out.format("%6d", v.getWeight(j) );
					System.out.print(";   ");
				}
				System.out.println();
			}
			return true;
		}
		
		// Prints the entire adjacency table
		@SuppressWarnings("unused")
		public void printTable(){
			System.out.println();
			System.out.println("V\tKNOWN\tdv\tPreviousVertex");
			int size = vertices.size();
			Vertex v;
			for(int i=0; i<size; i++){
				v = vertices.get(i);
				System.out.print( v.name + "\t");
				if(v.known)
					System.out.print("T\t");
				else
					System.out.print("F\t");
				System.out.print(v.weight + "\t" );
				if(v.previousVertex==null)
					System.out.print("null\t");
				else
					System.out.print(v.previousVertex.name+"\t");
				
				
				System.out.println();
			}
			System.out.println();
		}
	}
	
	// Edge class
	private static class Edge{
		// A pointer to each vertex and weight between the vertices
		Vertex v1;
		Vertex v2;
		int weight;

		// Constructor
		Edge(Vertex vX, Vertex vY, int c){
			v1=vX;
			v2=vY;
			weight=c;
		}
		
		// Used for finding the shortest path
		// Functions for checking if it's visited and for marking it visited
		// Reset returns 'visited' to the default value
		boolean visited = false;
		public boolean visited()
			{return visited;	}
		public void visit()
			{visited = true;	}
		public void reset()
			{visited = false;	}
		
		// An edge is unique if an only if it does not contain the same two vertices as another edge
		// The order of the vertices does not matter
		@Override
		public boolean equals(Object o){
			Edge e = (Edge)o;
			return ( (e.v1==this.v1&&e.v2==this.v2) || (e.v1==this.v2&&e.v2==this.v1) );
		}
	}
	
	// Vertex class. Uses name as an unique identifier
	private static class Vertex implements Comparable<Vertex>{
		// Unique identifier name, constructor with only name as parameter, and getter
		final private String name;
		Vertex(String n)
			{name=n;	}
		public String name()
			{return name;	}
		
		// Keeps track of which edges are relevant to this vertex
		// Keeping pointers of the edges in here as well as a main list makes things easier to keep track of
		ArrayList<Edge> adjEdges = new ArrayList<>();
		public boolean insertAdj(Edge e){
			return adjEdges.add(e);
		}
		// Returns the vertex to which this vertex is adjacent to from the adjEdges ArrayList at index i
		public Vertex getAdj(int i){
			Edge e = adjEdges.get(i);
			return e.v1.equals(this) ? e.v2: e.v1;
		}
		// Returns the weight of which this vertex is adjacent to from the adjEdges ArrayList at index i
		public int getWeight(int i){
			return adjEdges.get(i).weight;
		}
		
		// Used to find the shortest path
		// Weight is the distance from the first vertex
		// Previous vertex is the the vertex that comes before it
		// Default values mark if it has been visited and the adjacent edges analysed 
		// Update updates the weight and previous vertex as necessary
		private int weight = -1;
		private Vertex previousVertex = null;
		public void update(int c, Vertex v){
			weight=c;
			previousVertex=v;
		}
		// Used for knowing if this particular vertex has been solved
		private boolean known = false;
		public void known(){
			known=true;}
		// Resets the vertex to default values
		public void reset(){
			known = false;
			weight = -1;
			previousVertex = null;
		}
		
		// Used for comparing vertices to each other by weight
		public int compareTo(final Vertex v){
			return Integer.compare(this.weight, v.weight);	}
		// Checks if two vertices are equal to each other by comparing names
		// Only one vertex of each name is allowed which makes this a valid comparison
		public boolean equals(String n){
			return n.equals(this.name);	}
		
	}

	// Code used for testing the class
	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		AdjacencyList cityList = new AdjacencyList();
		
		// Insert the text file into our adjacencyList
		// Open the file, iterate through it, and parse the input 
		Scanner file = new Scanner(new FileReader("airports.txt"));
		String temp1, temp2, departure="", arrival;
		int weight;
		for(;file.hasNext();){
			temp1 = file.next();
			temp2 = file.next();
				
			if( isInt(temp2) ){
				arrival=temp1;
				weight=Integer.parseInt(temp2);
			}else{
				departure=temp1;
				arrival=temp2;
				weight=Integer.parseInt(file.next());
			}
			
			// Inserts into the adjacencly list (vertex, vertex, path weight)
			cityList.insert(arrival, departure, weight);
		}
		file.close();

		// Print the list and asks the user to input the departing and arrival location
		try {
			do{cityList.printList();}while(getCities(cityList, in) && getYesNo(in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		in.close();
		System.out.println("Goodbye.");
	}
	
	// Asks the user to input two city names and sends it to the AdjacenyList
	// function for finding the shortest path.
	static public boolean getCities(AdjacencyList list, Scanner s) throws Exception{
		final String a = "arrival";
		String inD=null, inA=null;
		String input, out = "departure";
		
		System.out.println();
		while(true){

			if( inD!=null  && inA==null)
				out = a;
			else if( inD!=null && inA!=null ){
				list.findPath(inD, inA);
				System.out.print( String.format("%s%-26s" , "\n", "Find another path (Y/N)?") );
				return true;
			}
			
			//System.out.print("Enter " + out +" airport:");
			System.out.print( String.format("%-26s" , "Enter " + out +" airport:" ) );
			input = s.next().toUpperCase();
			if( list.isUnique(input) != null ){
				if(inD==null)
					inD=input;
				else 
					inA=input;
				out = a;
				continue;
			}
				
			System.out.print("Invalid airport name: " + input);
			System.out.print( String.format("%s%-26s" , "\n", "Enter another city (Y/N)?") );
			if(!getYesNo(s))
				return false;
		}
		
		
	}
	
	// returns true if user inputs yes
	static public boolean getYesNo(Scanner in){
		String input = in.nextLine();
		
		while(input.length()==0)
			input = in.nextLine();
		
		char c = input.toLowerCase().charAt(0);
		if(c=='y')
			return true;
		return false;
	}
	
	// Checks if a string is an integer and returns true. Used for parsing the text file
	public static boolean isInt(String s) {
		try{ 
	    	Integer.parseInt(s); 
	    }catch(NumberFormatException e){ 
	        return false; 
	    }catch(NullPointerException e){
	    	return false;
	    }
	    return true;
	}
	
}