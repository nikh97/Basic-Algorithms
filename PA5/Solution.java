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

            database.graph.get(Integer.parseInt(line[0]) - 1).add(Integer.parseInt(line[1]) - 1);

        }

        database.DFS();

        for(int i = database.cycle.size() - 1; i >= 0; i--)
        	System.out.print(database.cycle.get(i) + " ");

	}
}

class Graph{

	int v;
	ArrayList<ArrayList<Integer>> graph;
	String[] color;
	int[] p;
	boolean foundcycle = false;
	ArrayList<Integer> cycle = new ArrayList<Integer>();

	Graph(int v){

		this.v = v;
		graph = new ArrayList<ArrayList<Integer>>(v);
		for(int i = 0; i<v; i++) graph.add(new ArrayList<Integer>());
		color = new String[v];
		p = new int[v];
	}

	void DFS(){

	 	for(int i = 0; i < v; i++){

	 		color[i] = "white";

	 	}

	 	for(int i = 0; i < v; i++){

	 		if(foundcycle) break;

	 		if(color[i].equals("white")) RecDFS(i);
	 	}

	 	if(!foundcycle) System.out.print("0");

	}

	void RecDFS(int u){

		color[u] = "gray";
		for(int i = 0; i < graph.get(u).size(); i++){

			if(foundcycle) break;

			if (color[graph.get(u).get(i)].equals("white")){
				p[graph.get(u).get(i)] = u;
				RecDFS(graph.get(u).get(i));
			}

			if (color[graph.get(u).get(i)].equals("gray")){

				foundcycle = true;
				System.out.println("1");
				printCycle(u, graph.get(u).get(i));
			}
		}

		color[u] = "black";
	}

	void printCycle(int u, int i){

		do{

			cycle.add(u + 1);
			u = p[u];

		}while(u != i);

		cycle.add(i + 1);
	}

	 
}
