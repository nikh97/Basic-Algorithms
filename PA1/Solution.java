import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.Scanner;

public class Solution {

    public static void main(String[] args) throws IOException{

        BufferedReader in = null;
        
        try{

            in = new BufferedReader(new InputStreamReader(sSystem.in));

        } catch(Exception ex){

            System.err.printf("Input file could not be read.\n");
            System.exit(1);
        }

        TwoThreeTree database = new TwoThreeTree();

        String line = in.readLine();

        long database_size = Long.parseLong(line);

        for(int i = 0; i < database_size; i++){

            line = in.readLine();

            String[] entry = line.split(" ");

            entry[0] = entry[0].trim();
            entry[1] = entry[1].trim();

            twothree.insert(entry[0], Integer.parseInt(entry[1]), database);
        } 

        line = in.readLine();

        long num_of_queries = Long.parseLong(line);
        
        for(int i = 0; i < num_of_queries; i++){

            line = in.readLine();

            String[] query = line.split(" ");

            query[0] = query[0].trim();
            query[1] = query[1].trim();

            if(query[0].compareTo(query[1]) <= 0)
                twothree.printRange(query[0], query[1], database);
            else
                twothree.printRange(query[1], query[0], database);
        }

    }
}

class twothree {

   static void insert(String key, int value, TwoThreeTree tree) {
   // insert a key value pair into tree (overwrite existsing value
   // if key is already present)

      int h = tree.height;

      if (h == -1) {
          LeafNode newLeaf = new LeafNode();
          newLeaf.guide = key;
          newLeaf.value = value;
          tree.root = newLeaf; 
          tree.height = 0;
      }
      else {
         WorkSpace ws = doInsert(key, value, tree.root, h);

         if (ws != null && ws.newNode != null) {
         // create a new root

            InternalNode newRoot = new InternalNode();
            if (ws.offset == 0) {
               newRoot.child0 = ws.newNode; 
               newRoot.child1 = tree.root;
            }
            else {
               newRoot.child0 = tree.root; 
               newRoot.child1 = ws.newNode;
            }
            resetGuide(newRoot);
            tree.root = newRoot;
            tree.height = h+1;
         }
      }
   }

   static WorkSpace doInsert(String key, int value, Node p, int h) {
   // auxiliary recursive routine for insert

      if (h == 0) {
         // we're at the leaf level, so compare and 
         // either update value or insert new leaf

         LeafNode leaf = (LeafNode) p; //downcast
         int cmp = key.compareTo(leaf.guide);

         if (cmp == 0) {
            leaf.value = value; 
            return null;
         }

         // create new leaf node and insert into tree
         LeafNode newLeaf = new LeafNode();
         newLeaf.guide = key; 
         newLeaf.value = value;

         int offset = (cmp < 0) ? 0 : 1;
         // offset == 0 => newLeaf inserted as left sibling
         // offset == 1 => newLeaf inserted as right sibling

         WorkSpace ws = new WorkSpace();
         ws.newNode = newLeaf;
         ws.offset = offset;
         ws.scratch = new Node[4];

         return ws;
      }
      else {
         InternalNode q = (InternalNode) p; // downcast
         int pos;
         WorkSpace ws;

         if (key.compareTo(q.child0.guide) <= 0) {
            pos = 0; 
            ws = doInsert(key, value, q.child0, h-1);
         }
         else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
            pos = 1;
            ws = doInsert(key, value, q.child1, h-1);
         }
         else {
            pos = 2; 
            ws = doInsert(key, value, q.child2, h-1);
         }

         if (ws != null) {
            if (ws.newNode != null) {
               // make ws.newNode child # pos + ws.offset of q

               int sz = copyOutChildren(q, ws.scratch);
               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
               if (sz == 2) {
                  ws.newNode = null;
                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
               }
               else {
                  ws.newNode = new InternalNode();
                  ws.offset = 1;
                  resetChildren(q, ws.scratch, 0, 2);
                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
               }
            }
            else if (ws.guideChanged) {
               ws.guideChanged = resetGuide(q);
            }
         }

         return ws;
      }
   }


   static int copyOutChildren(InternalNode q, Node[] x) {
   // copy children of q into x, and return # of children

      int sz = 2;
      x[0] = q.child0; x[1] = q.child1;
      if (q.child2 != null) {
         x[2] = q.child2; 
         sz = 3;
      }
      return sz;
   }

   static void insertNode(Node[] x, Node p, int sz, int pos) {
   // insert p in x[0..sz) at position pos,
   // moving existing extries to the right

      for (int i = sz; i > pos; i--)
         x[i] = x[i-1];

      x[pos] = p;
   }

   static boolean resetGuide(InternalNode q) {
   // reset q.guide, and return true if it changes.

      String oldGuide = q.guide;
      if (q.child2 != null)
         q.guide = q.child2.guide;
      else
         q.guide = q.child1.guide;

      return q.guide != oldGuide;
   }


   static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
   // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
   // also resets guide, and returns the result of that

      q.child0 = x[pos]; 
      q.child1 = x[pos+1];

      if (sz == 3) 
         q.child2 = x[pos+2];
      else
         q.child2 = null;

      return resetGuide(q);
   }
   
   static void printRange(Node node, String low, String high, int height){
        
        if(height > 0){

            InternalNode n = (InternalNode) node;

            if(low.compareTo(n.child0.guide) <= 0)
                printRange(n.child0, low, high, height - 1);

            if(low.compareTo(n.child1.guide) <= 0 || n.child2 == null)
                printRange(n.child1, low, high, height - 1);

            if(n.child2 != null)
                printRange(n.child2, low, high, height - 1);

        } else{

            LeafNode n = (LeafNode) node;
            
            if(low.compareTo(n.guide) <= 0 && n.guide.compareTo(high) <= 0){

                System.out.println(n.guide + " " + n.value);
            }   
            else
                return;
        }
    }

    static void tracePath(Node n, String key, int height, ArrayList<Node> path){

        path.add(n);

        if(height > 0){

            InternalNode n1 = (InternalNode) n;

            if(key.compareTo(n1.child0.guide) <= 0)
                tracePath(n1.child0, key, height - 1, path);

            else if(key.compareTo(n1.child1.guide) <= 0 || n1.child2 == null)
                tracePath(n1.child1, key, height - 1, path);
            else
                tracePath(n1.child2, key, height - 1, path);
        } else{

            return;
        }
    }

    static void printRange(String low, String high, TwoThreeTree tree){

        ArrayList<Node> pathlow = new ArrayList<Node>();
        ArrayList<Node> pathhigh = new ArrayList<Node>();

        tracePath(tree.root, low, tree.height, pathlow);
        tracePath(tree.root, high, tree.height, pathhigh);

        int counter = 0;
        int h = tree.height;

        while(counter < pathlow.size() && pathlow.get(counter) == pathhigh.get(counter) ){

            counter++;
            h--;
        }

        printRange(pathlow.get(counter - 1), low, high, h + 1);

    }

}


class Node {
   String guide;
   // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
   Node child0, child1, child2;
   // child0 and child1 are always non-null
   // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
   // guide points to the key

   int value;
}

class TwoThreeTree {
   Node root;
   int height;

   TwoThreeTree() {
      root = null;
      height = -1;
   }
}

class WorkSpace {
// this class is used to hold return values for the recursive doInsert
// routine

   Node newNode;
   int offset;
   boolean guideChanged;
   Node[] scratch;
}
