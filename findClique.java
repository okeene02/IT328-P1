import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A program for solving a large number of clique problems
 * from a file. Converts each clique problem to a vertex cover
 * problem, which is then solved and the solution is converted
 * to a clique solution.
 */
public class findClique {
    public static void main(String[] args) throws FileNotFoundException {
        // Check args
        if (args.length != 1) {
            System.out.println("Usage: findClique [filename]");
            System.exit(0);
        }

        // Input file
        String filename = args[0];

        // Create array of graphs (represented by adjacency matrix)
        CliqueProblem[] graphArray = readGraphFile(filename);

        // Print header
        System.out.printf("* Max Cliques in %s (reduced to K-Vertex Cover) *\n", filename);
        System.out.println("(|V|,|E|) (size, ms used) Cliques");

        // Iterate through each graph
        for(int cliqueNum = 0; cliqueNum < graphArray.length; cliqueNum++){
            // Results variables
            CliqueProblem cliqueProblem = graphArray[cliqueNum];
            
            long start_time = System.currentTimeMillis();
            
            VertexCoverProblem vertexCoverProblem = VertexCoverProblem.createFromCliqueProblem(cliqueProblem);
            int[] cover = vertexCoverProblem.findMinVertexCover();
            int[] clique = CliqueProblem.createCliqueFromCover(cover);
            
            int cliqueSize = 0;
            for (int i = 0; i < cover.length; i++) {
                if (clique[i] == 1)
                    cliqueSize++;
            }

            // Calculate time spent
            long total_time = System.currentTimeMillis() - start_time;

            // Print information
            System.out.printf("G%d (%2d, %d) (size = %d ms=%d) %s\n", 
                cliqueNum + 1, 
                cliqueProblem.graph.vertexCount, 
                cliqueProblem.graph.edgeCount, 
                cliqueSize,
                total_time, 
                cliqueToString(clique));
        }
    }

    /**
     * Converts a clique into a string representation.
     * Each clique represents a mask indicating which vertices
     * are included. The string versions lists out the vertices included.
     * So [0, 0, 0, 1, 1] is represented "{3, 4}"
     * @param clique
     * The clique to convert
     * @return
     * The string representation of the clique
     */
    public static String cliqueToString(int[] clique) {
        ArrayList<Integer> vertices = new ArrayList<Integer>();

        for (int i = 0; i < clique.length; i++) {
            if (clique[i] == 1)
                vertices.add(i);
        }

        String ret = "{";
        for (int i = 0; i < vertices.size() - 1; i++) {
            ret += vertices.get(i) + ",";
        }

        ret += vertices.get(vertices.size() - 1);

        return ret + "}";
    }

    /**
     * Creates an array of CliqueProblems from the contents of 
     * a file of graphs
     * @param filename
     * The file to read from
     * @return
     * An array of CliqueProblem's read from the file
     * @throws FileNotFoundException
     */
    public static CliqueProblem[] readGraphFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filename));

        // List of graphs. Total number of lines is unknown, so use dynamic array.
        ArrayList<CliqueProblem> graphList = new ArrayList<CliqueProblem>();

        // Iterate over file lines and add each graph to the list
        while (in.hasNextLine()) {
            int size = in.nextInt();

            if(size == 0) break;
            CliqueProblem cliqueProblem = new CliqueProblem();
            cliqueProblem.graph = new Graph(size, in);
            graphList.add(cliqueProblem);
        }

        in.close();

        // Convert to static array
        return graphList.toArray(new CliqueProblem[0]);
    }
}

