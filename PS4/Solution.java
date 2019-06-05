import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.Scanner;

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

        for(int i = 1; i <= num_of_queries; i++){

        	line = in.readLine().split(" ");

            database.graph.get(Integer.parseInt(line[0]) - 1).add(Integer.parseInt(line[1]));
            database.degrees[Integer.parseInt(line[1]) - 1] += 1;

        }

        ArrayList<Integer> sorted = database.LexTopSort();

        
        if(sorted.size() < database_size)
        	System.out.print(-1);
        
        else{

	    	for(int i = 0; i<sorted.size(); i++){

	    		System.out.print(sorted.get(i) + " ");
	    	}
    	}

	}
}

class Graph{

	int v;
	ArrayList<ArrayList<Integer>> graph;
	int[] degrees;

	Graph(int v){

		this.v = v;
		graph = new ArrayList<ArrayList<Integer>>(v);
		for(int i = 0; i<v; i++) graph.add(new ArrayList<Integer>());
		degrees = new int[v];
	}

	ArrayList<Integer> LexTopSort(){

		Heap zeroVertices = new Heap();

		for(int i = 0; i < degrees.length; i++){

			if(degrees[i] == 0)
				zeroVertices.append(i + 1);
		}

		ArrayList<Integer> orderedVertices = new ArrayList<Integer>();

		while (zeroVertices.size() != 0){

			int nextVertex = zeroVertices.pop();
			orderedVertices.add(nextVertex);
			for(int i = 0; i < graph.get(nextVertex - 1).size(); i++){

				degrees[graph.get(nextVertex - 1).get(i) - 1] -= 1;
				if(degrees[graph.get(nextVertex - 1).get(i) - 1] == 0){

					zeroVertices.append(graph.get(nextVertex - 1).get(i));
				}
			}

		}

		return orderedVertices;

	}


	 
}

class Heap{

	ArrayList<Integer> heap;

	Heap(){

		heap = new ArrayList<Integer>(1000);
	}

	int parent(int pos){return (pos-1)/2; }
	int left(int pos){return (2*pos) + 1; }
	int right(int pos){return (2*pos) + 2; }
	boolean hasLeft(int pos){ return left(pos) < heap.size();}
	boolean hasRight(int pos){ return right(pos) < heap.size();}

	boolean append(int data){

		heap.add(data);

		moveUp(heap.size() - 1);

		return true;

	}

	Integer pop(){

		if(heap.size() == 0) return null;
		int returned = heap.get(0);
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
				if(heap.get(right) < heap.get(left))
					min = right;
			}

			if(heap.get(min) >= heap.get(pos)) break;
			swap(pos, min);
			pos = min;
		}

	}

	void moveUp(int pos){

		while(pos > 0){

			int parent = parent(pos);
			if(heap.get(pos) >= heap.get(parent)) break;

			swap(pos, parent);
			pos = parent;
		}

	}

	void swap(int pos1, int pos2){

		int temp = heap.get(pos1);
		heap.set(pos1, heap.get(pos2));
		heap.set(pos2, temp);
		
	}

	int size(){

		return heap.size();
	}

	int get(int pos){

		return heap.get(pos);
	}

}