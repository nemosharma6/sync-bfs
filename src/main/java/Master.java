import java.util.ArrayList;
import java.util.List;

public class Master {

    public static void main(String args[]) {
        Log.init(args[1]);
        ParseInput.readFile(args[0]);
        List<Node> nodes = new ArrayList<Node>();

        ArrayList<Link> linkList = new ArrayList<>();
        for (int i = 0; i < ParseInput.processCount; i++) {
            for (int j = 0; j < ParseInput.processCount; j++) {
                if (ParseInput.adjacencyMatrix[i][j] == 1 && j > i)
                    linkList.add(new Link(ParseInput.processId[i], ParseInput.processId[j]));
            }
        }

        for (int i = 0; i < ParseInput.processId.length; i++) {
            ArrayList<Link> links = new ArrayList<>();
            for (Link e : linkList) {
                if (e.getNode1() == ParseInput.processId[i] || e.getNode2() == ParseInput.processId[i])
                    links.add(e);
            }
            nodes.add(new Node(ParseInput.processId[i], links));
        }

        boolean terminate = false;

        try {
            while (!terminate) {
                for (Node node : nodes)
                    node.start();

                for (Node node : nodes)
                    node.join();

                terminate = true;
                for (Node node : nodes)
                    terminate = terminate && node.isTerminate();
            }
        } catch (Exception e) {
            Log.write(Master.class.getName(), e.getMessage());
        }

        for (Node node : nodes) {
            Log.write(Master.class.getName(), "Node : " + node.getId() + ", Parent : " + node.parent() +
                    ", Children : " + node.getChildren());
            System.out.println(Master.class.getName() + " : " + node.getId() + " Parent: " + node.parent() +
                    ", Children: {" + node.getChildren() + "}");
        }
    }
}
