import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.Scanner;

public class Solution2 {

    public static void main(String[] args) throws IOException{

        BufferedReader in = null;
        
        try{

            in = new BufferedReader(new InputStreamReader(System.in));

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

            if(entry[0].equals("1"))
              twothree.insert(entry[1], Integer.parseInt(entry[2]), database);

            else if(entry[0].equals("2")){

              entry[2].trim();
              entry[3].trim();

              if(entry[1].compareTo(entry[2]) <= 0)
                twothree.updateValue(entry[1], entry[2], database.height, database.root, Integer.parseInt(entry[3]));
              else
                twothree.updateValue(entry[2], entry[1], database.height, database.root, Integer.parseInt(entry[3]));
            } 

            else{
              int counter = 0;
              twothree.searchValue(database.root, entry[1], database.height, counter);
            }
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

         q.child0.value += p.value;
         q.child1.value += p.value;
         if(q.child2 != null)
          q.child2.value += p.value;
         p.value = 0;

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
   
   static void updateValue(String low, String high, int height, Node root, int k){

    if(root == null){
      System.out.println(-1);
      return;
    }

    Node nextNode1;
    Node nextNode2;

    if(height > 0){

      InternalNode node = (InternalNode) root;

      if(low.compareTo(node.child0.guide) <= 0)
        nextNode1 = node.child0;

      else if(low.compareTo(node.child1.guide) <= 0 || node.child2 == null)
        nextNode1 = node.child1;

      else
        nextNode1 = node.child2;


      if(high.compareTo(node.child0.guide) <= 0)
        nextNode2 = node.child0;

      else if(high.compareTo(node.child1.guide) <= 0 || node.child2 == null)
        nextNode2 = node.child1;

      else
        nextNode2 = node.child2;


      if(nextNode1 == nextNode2)
        updateValue(low, high, height - 1, nextNode1, k);

      else if(nextNode1 == node.child0 && node.child2 != null && nextNode2 == node.child2){
        node.child1.value += k;
        updateValueLeft(low, high, height - 1, nextNode1, k);
        updateValueRight(low, high, height - 1, nextNode2, k);
        return;
      }

      else{

        updateValueLeft(low, high, height - 1, nextNode1, k);
        updateValueRight(low, high, height - 1, nextNode2, k);
        return;
      }

    } else{

      LeafNode n = (LeafNode) root;
            
      if(low.compareTo(n.guide) <= 0 && n.guide.compareTo(high) <= 0){

        n.value += k;
      }   
      else
          return;

    }

   }

   static void updateValueLeft(String low, String high, int height, Node node, int k){

    if(height > 0){

      InternalNode n = (InternalNode) node;

      if(low.compareTo(n.child0.guide) <= 0){
        n.child1.value += k;
        if(n.child2 != null)
          n.child2.value += k;

        updateValueLeft(low, high, height - 1, n.child0, k);
      }

      else if(low.compareTo(n.child1.guide) <= 0 || n.child2 == null){

        if(n.child2 != null)
          n.child2.value += k;

        updateValueLeft(low, high, height - 1, n.child1, k);
        
      }

      else{

        updateValueLeft(low, high, height - 1, n.child2, k);

      }
    } else{

      LeafNode n = (LeafNode) node;
            
      if(low.compareTo(n.guide) <= 0 && n.guide.compareTo(high) <= 0)
        n.value += k;   
      else
          return;
    }

   }

   static void updateValueRight(String low, String high, int height, Node node, int k){

    if(height > 0){

      InternalNode n = (InternalNode) node;

      if(high.compareTo(n.child0.guide) <= 0){
        updateValueRight(low, high, height - 1, n.child0, k);
      }

      else if(high.compareTo(n.child1.guide) <= 0 || n.child2 == null){

        n.child0.value += k;
        updateValueRight(low, high, height - 1, n.child1, k);
        
      }

      else{
        n.child0.value += k;
        n.child1.value += k;
        updateValueRight(low, high, height - 1, n.child2, k);

      }
    } else{

      LeafNode n = (LeafNode) node;
            
      if(low.compareTo(n.guide) <= 0 && n.guide.compareTo(high) <= 0){
        n.value += k;
      }   
      else
          return;
    }

   }

   static void searchValue(Node node, String key, int height, int counter){

    counter += node.value;

    if(height > 0){

      InternalNode n = (InternalNode) node;

      if(key.compareTo(n.child0.guide) <= 0)
        searchValue(n.child0, key, height - 1, counter);

      else if(key.compareTo(n.child1.guide) <= 0 || n.child2 == null)
        searchValue(n.child1, key, height - 1, counter);

      else
        searchValue(n.child2, key, height - 1, counter);

    } else{
      
      LeafNode n = (LeafNode) node;
      
      if(key.equals(n.guide))
        System.out.println(counter);
      else
        System.out.println(-1);
    }

   }

}


class Node {
   String guide;
   int value;

   Node(){
    value = 0;
   }
   // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
   Node child0, child1, child2;
   // child0 and child1 are always non-null
   // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
   // guide points to the key
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
