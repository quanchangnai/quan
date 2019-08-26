package quan.test.findpath;

import java.util.*;


public class PathFinder {

    private MapData mapData;

    private List<Node> openList = new ArrayList<>();
    private Set<Node> closeList = new HashSet<>();

    private Point startPoint;
    private Point endPoint;

    public PathFinder(MapData mapData, Point startPoint, Point endPoint) {
        this.mapData = mapData;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public List<Point> find() {

        Node startNode = new Node(startPoint);
        openList.add(startNode);

        boolean findSuccess = false;
        while (!openList.isEmpty()) {
            Node currentNode = openList.get(0);
            if (currentNode.getPoint().equals(endPoint)) {
                findSuccess = true;
                break;
            }
            openList.remove(0);
            closeList.add(currentNode);

            int currentX = currentNode.getPoint().getX();
            int currentY = currentNode.getPoint().getY();
            Node adjacentNode;
            if (currentX < mapData.length() - 1) {
                adjacentNode = new Node(new Point(currentX + 1, currentY));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_STRAIGHT);
            }
            if (currentX > 0) {
                adjacentNode = new Node(new Point(currentX - 1, currentY));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_STRAIGHT);
            }
            if (currentY < mapData.width() - 1) {
                adjacentNode = new Node(new Point(currentX, currentY + 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_STRAIGHT);
            }
            if (currentY > 0) {
                adjacentNode = new Node(new Point(currentX, currentY - 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_STRAIGHT);
            }
            if (currentX < mapData.length() - 1 && currentY > 0) {
                adjacentNode = new Node(new Point(currentX + 1, currentY - 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_DIAGONAL);
            }
            if (currentX > 0 && currentY > 0) {
                adjacentNode = new Node(new Point(currentX - 1, currentY - 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_DIAGONAL);
            }
            if (currentX > 0 && currentY < mapData.width() - 1) {
                adjacentNode = new Node(new Point(currentX - 1, currentY + 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_DIAGONAL);
            }
            if (currentX < mapData.length() - 1 && currentY < mapData.width() - 1) {
                adjacentNode = new Node(new Point(currentX + 1, currentY + 1));
                checkAdjacentNode(currentNode, adjacentNode, MapData.COST_DIAGONAL);
            }
        }

        List<Point> path = new ArrayList<>();
        if (findSuccess) {
            path = buildPath();
            smoothPath(path);
        }
        openList.clear();
        closeList.clear();
        return path;
    }

    private void checkAdjacentNode(Node currentNode, Node adjacentNode, int cost) {
        if (closeList.contains(adjacentNode)) {
            return;
        }

        if (mapData.indexOf(adjacentNode.getPoint().getX(), adjacentNode.getPoint().getY()) == MapData.OBSTACLE) {
            //障碍
            closeList.add(adjacentNode);
            return;
        }

        int index = openList.indexOf(adjacentNode);
        if (index >= 0) {
            adjacentNode = openList.get(index);
            if (currentNode.getF() + cost < adjacentNode.getF()) {
                adjacentNode.setParent(currentNode);
                calcF(adjacentNode, cost);
            }
        } else {
            adjacentNode.setParent(currentNode);
            calcF(adjacentNode, cost);
            openList.add(adjacentNode);
            Collections.sort(openList);
        }
    }

    private void calcF(Node node, int cost) {
        calcG(node, cost);
        calcH(node);
        node.setF(node.getG() + node.getH());
    }

    private void calcG(Node node, int cost) {
        if (node.getParent() != null) {
            node.setG(node.getParent().getG() + cost);
        }
    }

    private void calcH(Node node) {
        node.setH((Math.abs(endPoint.getX() - node.getPoint().getX()) + Math.abs(endPoint.getY() - node.getPoint().getY())) * MapData.COST_STRAIGHT);
    }


    private List<Point> buildPath() {
        List<Point> path = new ArrayList<>();
        Node node = openList.get(0);
        while (node != null) {
            path.add(0, node.getPoint());
            node = node.getParent();
        }
        return path;
    }

    //路径平滑
    private void smoothPath(List<Point> path) {
        for (int i = 0; i < path.size() - 2; i++) {
            Point point1 = path.get(i);
            for (int j = i + 2; j < path.size(); j++) {
                Point point2 = path.get(j);
                smoothPathX(point1, point2, path);
                smoothPathY(point1, point2, path);
            }
        }
    }

    private void smoothPathX(Point point1, Point point2, List<Point> path) {
        if (point1.getX() != point2.getX()) {
            return;
        }
        int point1Index = path.indexOf(point1);
        int point2Index = path.indexOf(point2);

        boolean noBlock = true;
        int beginY = Math.min(point1.getY(), point2.getY());
        int endY = Math.max(point1.getY(), point2.getY());
        for (int tmpY = beginY + 1; tmpY < endY; tmpY++) {
            if (mapData.indexOf(point1.getX(), tmpY) == MapData.OBSTACLE) {
                //有障碍
                noBlock = false;
            }
        }
        if (noBlock) {
            for (int index = point1Index + 1; index < point2Index; index++) {
                path.get(index).setX(point1.getX());
            }
        }
    }

    private void smoothPathY(Point point1, Point point2, List<Point> path) {
        if (point1.getY() != point2.getY()) {
            return;
        }
        int point1Index = path.indexOf(point1);
        int point2Index = path.indexOf(point2);

        boolean noBlock = true;
        int beginX = Math.min(point1.getX(), point2.getX());
        int endX = Math.max(point1.getY(), point2.getY());
        for (int tmpX = beginX + 1; tmpX < endX; tmpX++) {
            if (mapData.indexOf(tmpX, point1.getY()) == MapData.OBSTACLE) {
                //有障碍
                noBlock = false;
            }
        }
        if (noBlock) {
            for (int index = point1Index + 1; index < point2Index; index++) {
                path.get(index).setY(point1.getY());
            }
        }
    }


    private static class Node implements Comparable<Node> {
        private Point point;
        private Node parent;
        private int f;
        private int g;
        private int h;

        Node(Point point) {
            this.setPoint(point);
        }

        public int compareTo(Node other) {
            return getF() - other.getF();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return getPoint() != null ? getPoint().equals(node.getPoint()) : node.getPoint() == null;
        }

        @Override
        public int hashCode() {
            return getPoint() != null ? getPoint().hashCode() : 0;
        }

        Point getPoint() {
            return point;
        }

        void setPoint(Point point) {
            this.point = point;
        }

        Node getParent() {
            return parent;
        }

        void setParent(Node parent) {
            this.parent = parent;
        }

        int getF() {
            return f;
        }

        void setF(int f) {
            this.f = f;
        }

        int getG() {
            return g;
        }

        void setG(int g) {
            this.g = g;
        }

        int getH() {
            return h;
        }

        void setH(int h) {
            this.h = h;
        }

    }

}
