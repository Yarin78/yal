package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Not tested yet

/**
 * Finds the lexicographically first euler cycle in an undirected graph.
 */
public class Euler {
	private final int n; // Number of nodes
	private int[] degree;
	private int[][] edgeCount; // Adjacency edge matrix

	public Euler(int n) {
		this.n = n;
	}

	public void addEdge(int u, int v) {
		degree[u]++;
		degree[v]++;
		edgeCount[u][v]++;
		edgeCount[v][u]++;
	}

	public List<Integer> findCycle()
	{
		List<Integer> result = new ArrayList<Integer>();
		int noOdd = 0, firstOdd = -1, firstNonZero = -1;
		for (int i = 0; i < n; i++) {
			if (degree[i] > 0 && firstNonZero < 0) {
				firstNonZero = i;
			}
			if (degree[i] % 2 == 1) {
				noOdd++;
				if (firstOdd < 0) {
					firstOdd = i;
				}
			}
		}
		if (noOdd > 2) {
			return null;
		}
		if (firstNonZero < 0) {
			return result;
		}
		if (noOdd > 0) {
			search(firstOdd, result);
		} else {
			search(firstNonZero, result);
		}

		Collections.reverse(result);
		return result;
	}

	private void search(int cur, List<Integer> result) {
		for (int i = 0; i < n; i++) {
			while (edgeCount[cur][i] > 0) {
				edgeCount[cur][i]--;
				edgeCount[i][cur]--;
				search(i, result);
			}
		}
		result.add(cur);
	}
}
