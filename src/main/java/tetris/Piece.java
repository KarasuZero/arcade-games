import java.util.List;
import java.util.Random;

public class Piece {
    public enum Type {
        LINE {
            @Override
            public int[][] piece() {return new int[][]{{0,0,0,0},{1,1,1,1},{0,0,0,0},{0,0,0,0}};}
            @Override
            public int size() {return 4;}
        },
        L {
            @Override
            public int[][] piece() {return new int[][]{{0,0,3},{3,3,3},{0,0,0}};}
            @Override
            public int size() {return 3;}
        },
        LFLIP {
            @Override
            public int[][] piece() {return new int[][]{{2,0,0},{2,2,2},{0,0,0}};}
            @Override
            public int size() {return 3;}
        },
        SQUARE {
            @Override
            public int[][] piece() {return new int[][]{{4,4},{4,4}};}
            @Override
            public int size() {return 2;}
            @Override
            public int[][] signature(int orientation) {return this.piece();} // Special case: Square cannot rotate
        },
        S {
            @Override
            public int[][] piece() {return new int[][]{{0,5,5},{5,5,0},{0,0,0}};}
            @Override
            public int size() {return 3;}
        },
        Z {
            @Override
            public int[][] piece() {return new int[][]{{7,7,0},{0,7,7},{0,0,0}};}
            @Override
            public int size() {return 3;}
        },
        T {
            @Override
            public int[][] piece() {return new int[][]{{0,6,0},{6,6,6},{0,0,0}};}
            @Override
            public int size() {return 3;}
        };

        public abstract int[][] piece();
        public abstract int size();

        public int[][] signature(int orientation) {return rotate(this.piece(), orientation);}
        public int[][] signature() {return this.piece();}

        private int[][] rotate(int[][] piece, int value) {
            value = Math.floorMod(value, 4);
            if (value == 0) return piece;
            else {
                int[][] rot = new int[size()][size()];
                int n = size() - 1;

                for (int i = 0; i <= n; i++) {
                    for (int j = 0; j <= n; j++) {
                        switch (value) {
                            case 1 -> rot[i][j] = piece[n - j][i];
                            case 2 -> rot[i][j] = piece[n - i][n - j];
                            case 3 -> rot[i][j] = piece[j][n - i];
                        }
                    }
                }

                return rot;
            }
        }
    }

    private static final List<Type> TYPES = List.of(Type.values());
    private static final int TYPECOUNT = TYPES.size();
    private static final Random RANDOM = new Random();

    private final Type type;
    private int orientation;

    public Piece() {
        this.type = TYPES.get(RANDOM.nextInt(TYPECOUNT));
        this.orientation = 0;
    }

    public Piece(Type type) {
        this.type = type;
    }

    public Type piece() {
        return this.type;
    }

    public int[][] signature() {
        return this.type.signature(this.orientation);
    }

    public int size() {
        return this.type.size();
    }

    public void changeOrientation(int delta) {
        this.orientation += delta;
    }
}
