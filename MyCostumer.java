import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public abstract class MyCostumer implements Runnable {

    protected String fileName;
    protected BlockStore store;


    protected MyCostumer() {}

    protected MyCostumer(String fileName, BlockStore store) {
        this.fileName = fileName;
        this.store = store;
        //openFile();
    }

    public abstract void openFile();
    public abstract void closeFile();
    public abstract void writeBlock();
}
