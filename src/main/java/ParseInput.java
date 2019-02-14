import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ParseInput {

    static int processCount;
    static int processId[];
    static int rootProcess;
    static int adjacencyMatrix[][];

    public static void readFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Log.write("ParseInput", "Input File not found");
        } else {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (count == 0)
                        processCount = Integer.parseInt(line);
                    else if (count == 1) {
                        processId = new int[processCount];
                        String ids[] = line.split(" ");
                        for (int i = 0; i < ids.length; i++)
                            processId[i] = Integer.parseInt(ids[i]);
                    } else if (count == 2)
                        rootProcess = Integer.parseInt(line);
                    else {
                        if (count == 3)
                            adjacencyMatrix = new int[processCount][processCount];
                        String ids[] = line.split(" ");
                        for (int i = 0; i < ids.length; i++)
                            adjacencyMatrix[count - 3][i] = Integer.parseInt(ids[i]);
                    }
                    count += 1;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
