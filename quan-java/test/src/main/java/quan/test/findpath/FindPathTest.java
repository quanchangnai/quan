package quan.test.findpath;


import java.util.List;

public class FindPathTest {

    public static void main(String[] args) {
        int mapLength = 10;
        int mapWidth = 10;
        int[][] data = {
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 1, 0, 1, 0, 0},
                {0, 0, 1, 0, 0, 1, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}
        };

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapWidth; y++) {
                System.out.print(data[x][y] + "   ");
            }
            System.out.println();
        }

        MapData mapData = new MapData(data);
        Point startPoint = new Point(0, 0);
        Point endPoint = new Point(8, 8);


        long startTime = System.nanoTime();
        List<Point> pathPoints = new PathFinder(mapData, startPoint, endPoint).find();
        long endTime = System.nanoTime();
        System.out.println("寻路耗时0（ns）：" + (endTime - startTime));

        startTime = System.nanoTime();
        new PathFinder(mapData, startPoint, endPoint).find();
        endTime = System.nanoTime();
        System.out.println("寻路耗时1（ns）：" + (endTime - startTime));

        startTime = System.nanoTime();
        new PathFinder(mapData, startPoint, endPoint).find();
        endTime = System.nanoTime();
        System.out.println("寻路耗时2（ns）：" + (endTime - startTime));


        for (int x = 0; x < mapLength; x++) {
            for (int y = 0; y < mapWidth; y++) {
                boolean flag = false;
                for (Point point : pathPoints) {
                    if (point.getX() == x && point.getY() == y) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    System.out.print(data[x][y] + "*  ");
                } else {
                    System.out.print(data[x][y] + "   ");
                }
            }
            System.out.println();
        }
    }
}
