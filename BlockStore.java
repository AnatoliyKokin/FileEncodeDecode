import java.util.LinkedList;

public class BlockStore {
    private LinkedList<Block> blocks = new LinkedList<>();

    public BlockStore() {
        //???

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
        while (blocks.size()<1) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Block blk = blocks.pollFirst();
        notify();
        return blk;
    }

    public int blockCount() {
        return blocks.size();
    }
}
