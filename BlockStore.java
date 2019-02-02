import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockStore {
    public volatile boolean isReady;
    private ArrayDeque<Block> blocks = new ArrayDeque<>(10);


    public BlockStore() {
        isReady = false;
    }

    public synchronized void put(Block block) {
        if (blocks.size()>0)
        while(blocks.size()*blocks.getFirst().getBlockSize()>100){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        blocks.addLast(block);
        notify();
    }

    public synchronized Block get() {
        while (blocks.size()<1 && isReady==false) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Block blk = blocks.pollFirst();
        if (blocks.size()<3)
        notify();
        return blk;
    }

    public synchronized int blockCount() {
        return blocks.size();
    }
}
