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
		private final boolean solvable;
		private final List<Board> solutionPath = new ArrayList<>();

		private static class Node implements Comparable<Node> {
			Board board;
			Node prev;
			int moves;
			int priority;

			Node(Board b, Node p) {
				board = b;
				prev = p;
				moves = (p == null) ? 0 : p.moves + 1;
				priority = board.manhattan() + moves;
			}

			public int compareTo(Node other) {
				return Integer.compare(this.priority, other.priority);
			}
		}

		public Solver(Board initial) {
			if (initial == null)
				throw new IllegalArgumentException("Board is null");

			PriorityQueue<Node> pq = new PriorityQueue<>();
			PriorityQueue<Node> twinPQ = new PriorityQueue<>();

			pq.add(new Node(initial, null));
			twinPQ.add(new Node(initial.twin(), null));

			while (true) {
				if (step(pq)) {
					solvable = true;
					return;
				}
				if (step(twinPQ)) {
					solvable = false;
					solutionPath.clear();
					return;
				}
			}
		}

		private boolean step(PriorityQueue<Node> pq) {
			Node current = pq.poll();
			if (current.board.isGoal()) {
				for (Node n = current; n != null; n = n.prev)
					solutionPath.add(0, n.board);
				return true;
			}
			for (Board neighbor : current.board.neighbors()) {
				if (current.prev == null || !neighbor.equals(current.prev.board)) {
					pq.add(new Node(neighbor, current));
				}
			}
			return false;
		}

		public boolean isSolvable() {
			return solvable;
		}

		public int moves() {
			return solvable ? solutionPath.size() - 1 : -1;
		}

		public List<Board> solution() {
			return solvable ? solutionPath : null;
		}

	}

	public class Board {
		private final int[][] tiles;
		private final int n;
		private final int hamming;
		private final int manhattan;
		private final int blankRow;
		private final int blankCol;

		public Board(int[][] blocks) {
			n = blocks.length;
			tiles = new int[n][n];
			int h = 0, m = 0, br = -1, bc = -1;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					tiles[i][j] = blocks[i][j];
					int val = tiles[i][j];
					if (val == 0) {
						br = i;
						bc = j;
					} else {
						int expectedRow = (val - 1) / n;
						int expectedCol = (val - 1) % n;
						if (val != i * n + j + 1)
							h++;
						m += Math.abs(i - expectedRow) + Math.abs(j - expectedCol);
					}
				}
			}
			hamming = h;
			manhattan = m;
			blankRow = br;
			blankCol = bc;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(n).append("\n");
			for (int[] row : tiles) {
				for (int tile : row) {
					sb.append(String.format("%2d ", tile));
				}
				sb.append("\n");
			}
			return sb.toString();
		}

		public int[][] toArray() {
			return this.tiles;
		}

		public int dimension() {
			return n;
		}

		public int hamming() {
			return hamming;
		}

		public int manhattan() {
			return manhattan;
		}

		public boolean isGoal() {
			return manhattan == 0;
		}

		public boolean equals(Object y) {
			if (this == y)
				return true;
			if (y == null || getClass() != y.getClass())
				return false;
			Board that = (Board) y;
			return Arrays.deepEquals(this.tiles, that.tiles);
		}

		public Iterable<Board> neighbors() {
			List<Board> neighbors = new ArrayList<>();
			int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
			for (int[] d : directions) {
				int newRow = blankRow + d[0], newCol = blankCol + d[1];
				if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
					int[][] copy = copyTiles();
					copy[blankRow][blankCol] = copy[newRow][newCol];
					copy[newRow][newCol] = 0;
					neighbors.add(new Board(copy));
				}
			}
			return neighbors;
		}

		public Board twin() {
			int[][] copy = copyTiles();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					if (copy[i][j] != 0 && copy[i][j + 1] != 0) {
						int tmp = copy[i][j];
						copy[i][j] = copy[i][j + 1];
						copy[i][j + 1] = tmp;
						return new Board(copy);
					}
				}
			}
			return null;
		}

		private int[][] copyTiles() {
			int[][] copy = new int[n][n];
			for (int i = 0; i < n; i++)
				copy[i] = Arrays.copyOf(tiles[i], n);
			return copy;
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

		if (solver.isSolvable()) {
			for (Board move : solver.solution()) {
				st.add(move.toArray());
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
