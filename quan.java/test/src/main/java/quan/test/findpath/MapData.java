package quan.test.findpath;

public class MapData {

    public static final int OBSTACLE = 1;//障碍

    public static final int COST_STRAIGHT = 10;//直线移动消耗
    public static final int COST_DIAGONAL = 14;//斜线移动消耗

    private int[][] grids;

    public MapData(int[][] grids) {
        this.grids = grids;
    }

    public int length() {
        return grids.length;
    }

    public int width() {
        return grids[0].length;
    }

    public int indexOf(int x, int y) {
        return grids[x][y];
    }
}
