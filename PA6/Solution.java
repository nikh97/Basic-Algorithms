import java.io.*;
import java.util.*;

public class Solution{

	public static void main(String[] args) throws IOException{

		BufferedReader in = null;
        
        try{

            in = new BufferedReader(new InputStreamReader(System.in));

        } catch(Exception ex){

            System.err.printf("Input file could not be read.\n");
            System.exit(1);
        }

        String[] line = in.readLine().split(" ");

        int database_size = Integer.parseInt(line[0]);
        int num_of_queries = Integer.parseInt(line[1]);

        Graph database = new Graph(database_size);

        for(int i = 0; i < num_of_queries; i++){

        	line = in.readLine().split(" ");

        	Edge e = new Edge();

        	e.u = Integer.parseInt(line[0]) - 1;
        	e.v = Integer.parseInt(line[1]) - 1;
        	if(line[2].equals("1")) e.color = "blue";
        	else e.color = "red";

        	database.edges.add(e);

        }

        int path = Graph.redBlue(database, 0, database_size - 1);
        if(path == Integer.MAX_VALUE) System.out.println(-1);
        else System.out.println(path);
    
	}
}

class Graph{

	int n;
	ArrayList<ArrayList<Edge>> graph;
	ArrayList<Edge> edges;

	Graph(int n){

		this.n = n;
		graph = new ArrayList<ArrayList<Edge>>(n);
		for(int i = 0; i<n; i++) graph.add(new ArrayList<Edge>());
		edges = new ArrayList<Edge>();
	}


	static int redBlue(Graph g, int source, int sink){

		Graph doubleg = new Graph(2 * g.n);

		for(int i = 0; i < g.edges.size(); i++){

			Edge e = g.edges.get(i);

			int newWeight = 1;

			if(e.color.equals("blue")){

				newWeight = 0;
				doubleg.addEdge(e.u, e.v + g.graph.size(), newWeight);
			}

			doubleg.addEdge(e.u, e.v, newWeight);
			doubleg.addEdge(e.u + g.graph.size(), e.v + g.graph.size(), newWeight);
		}

		int[] distance = Djikstra(doubleg, source);
		return distance[sink + g.n];
	}

	void addEdge(int first, int second, int newWeight){

		Edge edge = new Edge();
		edge.u = first;
		edge.v = second;
		edge.weight = newWeight;


		graph.get(first).add(edge);


	}


	static int[] Djikstra(Graph g, int source){

		int[] d = new int[g.n];

		for(int i = 0; i < g.n; i++){

			d[i] = Integer.MAX_VALUE;
		}

		d[source] = 0;

		Data data = new Data();
		data.v = source;
		data.distance = d[source];

		Heap Q = new Heap();
		Q.insert(data);

		Data u;

		while(Q.heap.size() != 0){

			u = Q.remove();

			for(int i = 0; i < g.graph.get(u.v).size(); i++){

				if(d[u.v] + g.graph.get(u.v).get(i).weight < d[g.graph.get(u.v).get(i).v]){


					if(d[g.graph.get(u.v).get(i).v] == Integer.MAX_VALUE){
						Data added = new Data();
						added.v = g.graph.get(u.v).get(i).v;
						added.distance = d[u.v] + g.graph.get(u.v).get(i).weight;
						Q.insert(added);
					} 

					d[g.graph.get(u.v).get(i).v] = d[u.v] + g.graph.get(u.v).get(i).weight;

				}
			}
		}
		
		return d;
	}
	 
}

class Edge{

	int u;
	int v;
	int weight;
	String color;
}

class Heap{

	ArrayList<Data> heap;

	Heap(){

		heap = new ArrayList<Data>(1000);
	}

	int parent(int pos){return (pos-1)/2; }
	int left(int pos){return (2*pos) + 1; }
	int right(int pos){return (2*pos) + 2; }
	boolean hasLeft(int pos){ return left(pos) < heap.size();}
	boolean hasRight(int pos){ return right(pos) < heap.size();}

	boolean insert(Data data){

		heap.add(data);

		moveUp(heap.size() - 1);

		return true;

	}

	Data remove(){

		if(heap.size() == 0) return null;
		Data returned = heap.get(0);
		swap(0, heap.size() - 1);
		heap.remove(heap.size() - 1);

		moveDown(0);

		return returned;

	}

	void moveDown(int pos){

		while(hasLeft(pos)){
			int left = left(pos);
			int min = left;

			if(hasRight(pos)){
				int right = right(pos);
				if(heap.get(right).compareTo(heap.get(left)) < 0)
					min = right;
			}

			if(heap.get(min).compareTo(heap.get(pos)) >= 0) break;
			swap(pos, min);
			pos = min;
		}

	}

	void moveUp(int pos){

		while(pos > 0){

			int parent = parent(pos);
			if(heap.get(pos).compareTo(heap.get(parent)) >= 0) break;

			swap(pos, parent);
			pos = parent;
		}

	}

	void swap(int pos1, int pos2){

		Data temp = heap.get(pos1);
		heap.set(pos1, heap.get(pos2));
		heap.set(pos2, temp);
	}

	Data get(int pos){

		return heap.get(pos);
	}

}

class Data{

	int v;
	int distance;

	long compareTo(Data d2){

		return  this.distance - d2.distance;
	}

}