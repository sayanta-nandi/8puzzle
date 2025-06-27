package com.example.puzzle_solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class PuzzleSolverApplication {
	public static void main(String[] args) {
		SpringApplication.run(PuzzleSolverApplication.class, args);
	}

	static class FormData {
		public int[][] name;
	}

	public class Solver {

		private int noOfmoves;
		private boolean isSolve = true;
		private Stack<Board> moves;

		// find a solution to the initial board (using the A* algorithm)
		public Solver(Board initial) {
			if (initial == null)
				throw new IllegalArgumentException("null input");
			Board twin = initial.twin();
			PriorityQueue<Node> pq = new PriorityQueue<>();
			PriorityQueue<Node> pqi = new PriorityQueue<>();
			pq.add(new Node(initial, 0, null));
			pqi.add(new Node(twin, 0, null));
			while (!pq.isEmpty()) {
				Node n = pq.remove();
				Node ni = pqi.remove();
				Board cur = n.current;
				Node parNode = n.parents;
				Board par = parNode == null ? null : parNode.current;
				Board curi = ni.current;
				Node pariNode = ni.parents;
				Board pari = pariNode == null ? null : pariNode.current;
				int mov = n.moves;
				if (cur.isGoal()) {
					noOfmoves = mov;
					moves = new Stack<>();
					for (int i = mov - 1; i >= 0; i--) {
						moves.push(n.current);
						n = n.parents;
					}
					break;
				}
				if (curi.isGoal()) {
					isSolve = false;
					break;
				}
				Iterable<Board> it = cur.neighbors();
				Iterable<Board> iti = curi.neighbors();

				for (Board nei : it) {
					if (!nei.equals(par)) {
						pq.add(new Node(nei, mov + 1, n));
					}
				}
				for (Board nei : iti) {
					if (!nei.equals(pari)) {
						pqi.add(new Node(nei, mov + 1, ni));
					}
				}
			}
		}

		private class Node implements Comparable<Node> {
			private Board current;
			private int moves;
			private Node parents;
			private int priyorites;

			public Node(Board cur, int moves, Node par) {
				this.current = cur;
				this.moves = moves;
				this.parents = par;
				this.priyorites = moves + cur.manhattan();
			}

			public int compareTo(Node that) {
				return this.priyorites - that.priyorites;
			}
		}

		// is the initial board solvable? (see below)
		public boolean isSolvable() {
			return isSolve;
		}

		// min number of moves to solve initial board; -1 if unsolvable
		public int moves() {
			if (!isSolve)
				return -1;
			return noOfmoves;
		}

		// sequence of boards in a shortest solution; null if unsolvable
		public Stack<Board> solution() {
			if (!isSolve)
				return null;
			return moves;
		}
	}

	public class Board {
		private final int n;
		private final int m;
		private int[][] tiles;
		private int hamming;
		private int manhattan;
		private int zeroi;
		private int zeroj;

		// create a board from an n-by-n array of tiles,
		// where tiles[row][col] = tile at (row, col)
		public Board(int[][] tiles) {
			n = tiles.length;
			m = tiles[0].length;
			this.tiles = new int[n][m];
			for (int i = 0; i < n; i++) {
				this.tiles[i] = tiles[i].clone();
			}
			this.hamming = 0;
			this.manhattan = 0;
			int k = 1;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					int cur = tiles[i][j];
					if (cur != k++ && cur != 0) {
						this.hamming++;
						int ii = (cur - 1) / m;
						int jj = (cur - 1) - m * ii;
						this.manhattan += Math.abs(i - ii) + Math.abs(j - jj);
					} else if (cur == 0) {
						this.zeroi = i;
						this.zeroj = j;
					}
				}
			}
		}

		// string representation of this board
		public String toString() {
			StringBuilder s = new StringBuilder(Integer.toString(n) + "\n");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					s.append(Integer.toString(tiles[i][j])).append(" ");
				}
				s.append("\n");
			}
			return s.toString();
		}

		public int[][] getArr() {
			return tiles;
		}

		// board dimension n
		public int dimension() {
			return n;
		}

		// number of tiles out of place
		public int hamming() {
			return this.hamming;
		}

		// sum of Manhattan distances between tiles and goal
		public int manhattan() {
			return this.manhattan;
		}

		// is this board the goal board?
		public boolean isGoal() {
			return this.hamming == 0;
		}

		// does this board equal y?
		public boolean equals(Object y) {
			if (y == null)
				return false;
			if (!y.getClass().getName().equals("Board")) {
				return false;
			}
			Board that = (Board) y;
			int[][] thatTile = that.tiles;
			if (thatTile.length != tiles.length)
				return false;
			if (thatTile[0].length != tiles[0].length)
				return false;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (tiles[i][j] != thatTile[i][j])
						return false;
				}
			}
			return true;
		}

		// all neighboring boards
		public Iterable<Board> neighbors() {
			Stack<Board> st = new Stack<>();
			int[][] neiArray = new int[n][n];
			for (int i = 0; i < n; i++) {
				neiArray[i] = tiles[i].clone();
			}
			// left
			if (zeroi > 0) {
				neiArray[zeroi][zeroj] = neiArray[zeroi - 1][zeroj];
				neiArray[zeroi - 1][zeroj] = 0;
				st.push(new Board(neiArray));
				neiArray[zeroi - 1][zeroj] = neiArray[zeroi][zeroj];
			}
			// right
			if (zeroi < tiles.length - 1) {
				neiArray[zeroi][zeroj] = neiArray[zeroi + 1][zeroj];
				neiArray[zeroi + 1][zeroj] = 0;
				st.push(new Board(neiArray));
				neiArray[zeroi + 1][zeroj] = neiArray[zeroi][zeroj];
			}
			// up
			if (zeroj > 0) {
				neiArray[zeroi][zeroj] = neiArray[zeroi][zeroj - 1];
				neiArray[zeroi][zeroj - 1] = 0;
				st.push(new Board(neiArray));
				neiArray[zeroi][zeroj - 1] = neiArray[zeroi][zeroj];
			}
			// down
			if (zeroj < tiles.length - 1) {
				neiArray[zeroi][zeroj] = neiArray[zeroi][zeroj + 1];
				neiArray[zeroi][zeroj + 1] = 0;
				st.push(new Board(neiArray));
				neiArray[zeroi][zeroj + 1] = neiArray[zeroi][zeroj];
			}
			return st;
		}

		// a board that is obtained by exchanging any pair of tiles
		public Board twin() {
			int i1 = 0, j1 = 0;
			int i2, j2;
			if (i1 == zeroi && j1 == zeroj) {
				i1 = 1;
				i2 = 1;
				j2 = 1;
			} else if (i1 + 1 == zeroi && j1 == zeroj) {
				j2 = 1;
				i2 = 0;
			} else {
				i2 = 1;
				j2 = 0;
			}
			int[][] temptiles = new int[n][n];
			for (int i = 0; i < n; i++) {
				temptiles[i] = tiles[i].clone();
			}
			int temp = temptiles[i1][j1];
			temptiles[i1][j1] = temptiles[i2][j2];
			temptiles[i2][j2] = temp;
			return new Board(temptiles);
		}
	}

	public class UserResponse {
		private String status;
		private boolean isSolvable;
		public List<int[][]> moves;

		public UserResponse(String status, boolean isSolvable, List<int[][]> moves) {
			this.status = status;
			this.isSolvable = isSolvable;
			this.moves = moves;
		}

		// Getters & Setters (required for JSON serialization)
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public boolean getIsSolvable() {
			return isSolvable;
		}

		public void setIsSolvable(boolean isSolvable) {
			this.isSolvable = isSolvable;
		}

		public List<int[][]> getMoves() {
			return moves;
		}

		public void setMoves(List<int[][]> moves) {
			this.moves = moves;
		}
	}

	@PostMapping("/path")
	public UserResponse postMethodName(@RequestBody FormData entity) {
		Board board = new Board(entity.name);
		Solver solver = new Solver(board);
		List<int[][]> st = new ArrayList<>();

		if (solver.isSolve) {
			Stack<Board> s = solver.solution();
			while (!s.isEmpty()) {
				st.add(s.pop().getArr());
			}
			return new UserResponse("success", true, st);
		}

		return new UserResponse("success", false, st);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
}
