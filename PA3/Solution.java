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

        HashHeap database = new HashHeap();

        String line = in.readLine();

        long database_size = Long.parseLong(line);

        for(int i = 0; i < database_size; i++){

        	line = in.readLine();

            String[] entry = line.split(" ");

            entry[0] = entry[0].trim();
            entry[1] = entry[1].trim();

            database.insert(entry[0], Long.parseLong(entry[1]));
        }

        line = in.readLine();

        long num_of_queries = Long.parseLong(line);

        for(int i = 0; i < num_of_queries; i++){

        	line = in.readLine();

            String[] entry = line.split(" ");

            entry[0] = entry[0].trim();
            entry[1] = entry[1].trim();

            if(entry[0].equals("1")){
            	entry[2] = entry[2].trim();
             	database.update(entry[1], Long.parseLong(entry[2]));
            }else
            	System.out.println(database.threshold(Long.parseLong(entry[1])));
        }

	}
}

class HashHeap{

	int size;
	Hashtable <String, Data> hash;
	Heap heap;


	HashHeap(){

		size =  0;
		hash = new Hashtable(1000);
		heap = new Heap();
	}

	

	boolean insert(String name, long score){

		Data added = new Data();
		added.name = name;
		added.score = score;
		added.pos = size;

		heap.insert(added);
		hash.put(added.name, added);

		size++;
		
		return true;
	}

	boolean update(String name, long k){
		
		Data updated = hash.get(name);

		if(updated == null) return false;

		updated.score += k;

		heap.moveDown(updated.pos);

		return true;
	}

	int threshold(long cutoff){

		while(heap.get(0).score < cutoff){

			Data removed = heap.remove();
			hash.remove(removed.name);

			size--;

		}

		return size;

	}

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

		heap.get(pos1).pos = pos1;
		heap.get(pos2).pos = pos2;
	}

	Data get(int pos){

		return heap.get(pos);
	}

}

class Data{

	String name;
	long score;
	int pos;

	long compareTo(Data d2){

		return  this.score - d2.score;
	}

}