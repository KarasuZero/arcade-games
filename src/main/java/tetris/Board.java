import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    private static final int WIDTH = 10, HEIGHT = 20;
    private int[][] board;
    private Piece currentPiece;
    private int[] pos;

    public Board() {
        this.board = new int[HEIGHT][WIDTH];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Board{");
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                sb.append(valueAt(i, j));
            }
            if (i != HEIGHT - 1) {
                sb.append(",");
            } else {
                sb.append("}");
            }
        }
        return sb.toString();
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                sb.append(valueAt(i, j));
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int valueAt(int i, int j) {
        if (i < pos[0] || i >= pos[0] + currentPiece.size()
                || j < pos[1] || j >= pos[1] + currentPiece.size()) {
            return board[i][j];
        } else {
            int si = i - pos[0], sj = j - pos[1];
            int v = currentPiece.signature()[si][sj];
            return v != 0 ? v : board[i][j];
        }
    }

    public void getNewPiece() {
        this.currentPiece = new Piece();
        this.pos = new int[]{0, 3};
    }

    public void getNewPiece(Piece.Type type) {
        this.currentPiece = new Piece(type);
        this.pos = new int[]{0, 3};
    }

    public void tick() {
        try {
            this.move(0);
        } catch (IllegalArgumentException ex) {
            set();
        }
    }

    public void rotate(int direction) {
        int delta = direction == 0 ? 2 : Integer.compare(direction, 0);
        currentPiece.changeOrientation(delta);
        try {
            collisionCheck();
        } catch (IllegalArgumentException ex) {
            currentPiece.changeOrientation(-delta);
            throw ex;
        }
    }

    // Direction of 0 moves down, positive moves right, negative moves left
    public void move(int direction) {
        int[] step = new int[] {direction == 0 ? 1 : 0, Integer.compare(direction, 0)};

        // Move according to calculated step
        pos[0] += step[0];
        pos[1] += step[1];

        // Check for collisions
        try {
            collisionCheck();
        } catch (IllegalArgumentException ex) {
            pos[0] -= step[0];
            pos[1] -= step[1];
            throw ex;
        }
    }

    // Move until obstacle reached, with 0 -> down, positive -> right, and negative -> left.
    // Returns the amount of times it moved, which may be 0.
    public int fullMove(int direction) {
        int i = 0;
        while (true) {
            try {
                this.move(direction);
            } catch (IllegalArgumentException ex) {
                return i;
            }
            i += 1;
        }
    }

    public void set() {
        this.fullMove(0);

        int[][] sig = currentPiece.signature();

        for (int i = 0; i < sig.length; i++) {
            int ip = i + pos[0];

            for (int j = 0; j < sig.length; j++) {
                int jp = j + pos[1];

                if (sig[i][j] > 0) {
                    board[ip][jp] = sig[i][j];
                }
            }
        }

        clearLines();
        getNewPiece();
    }

    private void collisionCheck() {
        int[][] sig = currentPiece.signature();

        for (int i = 0; i < sig.length; i++) {
            int ip = i + pos[0];

            for (int j = 0; j < sig.length; j++) {
                int jp = j + pos[1];

                if (sig[i][j] > 0) {
                    if (ip >= HEIGHT || ip < 0 || jp >= WIDTH || jp < 0) {
                        throw new IllegalArgumentException("Move results in piece leaving board");
                    }
                    if (board[ip][jp] > 0) {
                        throw new IllegalArgumentException("Move results in a collision");
                    }
                }
            }
        }
    }

    private ArrayList<Integer> clearCheck() {
        ArrayList<Integer> toClear = new ArrayList<>(4);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] == 0) break;
                if (j == WIDTH - 1) toClear.add(i);
            }
        }
        return toClear;
    }

    // Checks for filled lines and removes them, returns the amount of lines cleared.
    private int clearLines() {
        ArrayList<Integer> toClear = clearCheck();

        if (toClear.size() == 0) return 0;

        for (int i : toClear) {
            Arrays.fill(board[i], 0);
        }

        int diff = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            if (toClear.contains(i)) {
                int k = i;
                do {
                    diff++;
                    toClear.remove((Integer) k);
                    k--;
                } while (toClear.contains(k));
            }
            if (diff == 0 || i - diff < 0) continue;

            System.out.println(i + " clearing with diff " + diff);
            System.arraycopy(board[i - diff], 0, board[i], 0, WIDTH);
        }

        System.out.println(toClear.size());
        return toClear.size();
    }
}
