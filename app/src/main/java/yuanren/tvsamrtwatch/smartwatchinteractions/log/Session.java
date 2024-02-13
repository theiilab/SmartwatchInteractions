package yuanren.tvsamrtwatch.smartwatchinteractions.log;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class Session extends Application {
    private final static String TAG = "Metrics";
    public static int SESSION_3_NUM_BLOCK = 3;

    public int pid = 0;
    public String method = "";
    public int id = 0;
    public int dataSet = 0;
    private int index = 0;
    private int[] randoms;
    public List<Block> blocks = new ArrayList<>();

    private String[] session1_targetMovies = {
            "The King's Man",
            "Jumanji",
            "The Devil Wears Prada",
            "Venom",
            "Harry Potter and the Prisoner of Azkaban",
            "Insomnia",
            "Mama Mia",
            "Sherlock Holmes",
            "Flipped",
            "Inception",
            "Space Jam",
            "Death on the Nile"};

    private String[] session1_targetMovies2 = {
            "Red Notice",
            "Uncharted",
            "The Wolf of Wall Street",
            "Iron man",
            "Fantastic Beasts and Where to Find Them",
            "Fall",
            "Lala Land",
            "The Da Vinci Code",
            "Crazy Rich Asians",
            "The Adam Project",
            "Million Dollar Baby",
            "Pain Hustler"};



    private String[] session2_targetMovies = {
            "The King's Man",
            "Jumanji",
            "The Devil Wears Prada",
            "Venom",
            "Harry Potter and the Prisoner of Azkaban",
            "Insomnia",
            "Mama Mia",
            "Sherlock Holmes"};

    private String[] session2_targetMovies2 = {
            "Red Notice",
            "Uncharted",
            "The Wolf of Wall Street",
            "Iron man",
            "Fantastic Beasts and Where to Find Them",
            "Fall",
            "Lala Land",
            "The Da Vinci Code"};

    public void init(int pid, int sid, String method, int dataSet) {
        this.pid = pid;
        this.id = sid;
        this.method = method;
        this.dataSet = dataSet;

        generateBlocks(0);
    }

    public void init(int pid, int sid, String method, int dataSet, int block) {
        this.pid = pid;
        this.id = sid;
        this.method = method;
        this.dataSet = dataSet;

        generateBlocks(block - 1);
    }

    private void generateBlocks(int index) {
        switch (id) {
            case 1:
                for (int i = index; i < session1_targetMovies.length; ++i) {
                    String targetMovie = dataSet == 0 ? session1_targetMovies[i] : session1_targetMovies2[i];
                    Block block = new Block(this, pid, id, method, dataSet, i + 1, targetMovie);
                    blocks.add(block);
                }
                break;
            case 2:
                for (int i = index; i < session2_targetMovies.length; ++i) {
                    String targetMovie = dataSet == 0 ? session2_targetMovies[i] : session2_targetMovies2[i];
                    Block block = new Block(this, pid, id, method, dataSet, i + 1, targetMovie);
                    blocks.add(block);
                }
                break;
            default:
                for (int i = 0; i < 3; ++i) {
                    Block block = new Block(this, pid, id, method, dataSet, i + 1, "");
                    blocks.add(block);
                }
                break;
        }
    }

    public Block nextBlock() {
        switch (id) {
            case 1:
                index = Math.min(index + 1, session1_targetMovies.length - 1);
                break;
            case 2:
                index = Math.min(index + 1, session2_targetMovies.length - 1);
                break;
            default:
                index = Math.min(index + 1, 2); // max block is 3
                break;
        }
        return blocks.get(index);
    }

    public Block getCurrentBlock() {
        return blocks.get(index);
    }
}
