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

        String s1 = in.readLine();
        String s2 = in.readLine();

		int n = s1.length();
		int m = s2.length();


		int[][] scoring_matrix = {
			{2, -1, -1, -1},
			{-1, 2, -1, -1},
			{-1, -1, 2, -1},
			{-1, -1, -1, 2},
		};


		int[][] DP = new int[m + 1][n + 1];

		for(int i = 0; i < n + 1; i++)
			DP[0][i] = -1 * i;
		for(int j = 0; j < m + 1; j++)
			DP[j][0] = -1 * j;

		maxScore(s1, s2, DP, scoring_matrix);
		
		int i = n;
		int j = m;

		StringBuilder a1 = new StringBuilder("");
		StringBuilder a2 = new StringBuilder("");

		while (i != 0 && j != 0){

			if(DP[j][i] - scoring_matrix[toInt(s2.charAt(j - 1))][toInt(s1.charAt(i - 1))] == DP[j-1][i-1]){

				a1 = a1.append(s1.charAt(i-1));
				a2 = a2.append(s2.charAt(j-1));
				i--;
				j--;
			}

			else if(DP[j][i] == DP[j][i-1] - 1){

				a1 = a1.append(s1.charAt(i-1));
				a2 = a2.append('_');
				i--;
			}

			else if(DP[j][i] == DP[j-1][i] - 1){

				a1 = a1.append('_');
				a2 = a2.append(s2.charAt(j-1));
				j--;
			}
		}

		if(i != 0){
			while(i != 0){

				a1 = a1.append(s1.charAt(i-1));
				a2 = a2.append('_');
				i--;
			}
		}

		if(j != 0){
			while(j != 0){

				a1 = a1.append('_');
				a2 = a2.append(s2.charAt(j-1));
				j--;
			}
		}

		System.out.println(DP[s2.length()][s1.length()]);
		
		for(int p = a1.length() - 1; p >= 0; p--)	
			System.out.print(a1.charAt(p));
		System.out.println();

		for(int p = a2.length() - 1; p >= 0; p--)	
			System.out.print(a2.charAt(p));
		System.out.println();



	}

	static void maxScore(String s1, String s2, int[][] DP, int[][] scoring_matrix){

		for(int j = 1; j < s2.length() + 1; j++){
			for (int i = 1; i < s1.length() + 1; i++){

				int twoL = DP[j - 1][i - 1] + scoring_matrix[toInt(s2.charAt(j - 1))][toInt(s1.charAt(i - 1))];
				int oneGap = DP[j-1][i] - 1;
				int gapOne = DP[j][i - 1] - 1;

				DP[j][i] = Math.max(twoL, Math.max(oneGap, gapOne));
			}
		}
	}

	static int toInt(char c){
			
		int i = -1;
		
		if(c == 'A')
			return 0;
		if(c == 'C')
			return 1;
		if(c == 'G')
			return 2;
		if(c == 'T')
			return 3;
		
		return i;
	}
}