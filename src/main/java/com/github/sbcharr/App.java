package com.github.sbcharr;

import java.util.ArrayList;
import java.util.List;


class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class GraphNode {
    int val;
    List<GraphNode> neighbors;

    public GraphNode(int val) {
        this.val = val;
        this.neighbors = new ArrayList<>();
    }
}

public class App {
    private int totalPaths;
    private final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public int countPaths(int[][] grid) {
        this.totalPaths = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        // If start or end is blocked, no paths
        if (grid[0][0] != 0 || grid[rows - 1][cols - 1] != 0) {
            return 0;
        }

        dfs(0, 0, rows, cols, grid);
        return totalPaths;
    }

    private void dfs(int r, int c, int rows, int cols, int[][] grid) {
        if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 1 || grid[r][c] == 2) {
            return;
        }
        // Reached bottom-right?
        if (r == rows - 1 && c == cols - 1) {
            totalPaths++;
            return;
        }

        grid[r][c] = 2;
        for (int[] d : DIRS) { // visit all 4 directions
            dfs(r + d[0], c + d[1], rows, cols, grid);
        }
        grid[r][c] = 0;
    }


    public static void main(String[] args) {
//        TreeNode node = new TreeNode(1);
//        node.left = new TreeNode(5);
//        node.right = new TreeNode(1);
//        node.right.right = new TreeNode(6);
//        App app = new App();
//        System.out.println(app.sumNumbers(node));
        int[][] grid = {
                {0, 0, 0, 0},
                {1, 1, 0, 0},
                {0, 0, 0, 1},
                {0, 1, 0, 0}
        };
        App solver = new App();
        System.out.println("Total unique paths: " + solver.countPaths(grid));
        // Expected output: 2
    }
}
