import java.io.IOException;
import java.io.RandomAccessFile;

public class EncodeProducer extends MyProducer {


    private RandomAccessFile fis;
    private static volatile int readBlocks;
    private long fileLen;


    public EncodeProducer(String fileName, BlockStore store, int block_size) {
        super(fileName, store, block_size);
    }

    @Override
    public void openFile() {
        try {
            fis = new RandomAccessFile(fileName, "r");
            fileLen = fis.length();
            //fis.seek(readBlocks * 1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeFile() {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int readBlock() {
        Block blk = new Block();
        int blockLen;
        if (fileLen>=1024*1024*blockSize) blockLen = 1024*1024*blockSize;
        else blockLen = (int)fileLen;
        //System.out.println("Поток номер "+threadNumber+" Размер блока "+blockLen );

        byte[] tmpBytes = new byte[blockLen];
        int count=0;
        try {
            count = fis.read(tmpBytes);
            blk.putBytes(tmpBytes,count);
            blk.setNumber(blockNumber);
            //blockNumber++;
            store.put(blk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void run() {

        System.out.println("Открыт поток для чтения "+Thread.currentThread().getName());

        openFile();

        long position=0;

        while (true) {
                synchronized (EncodeProducer.class) {
                    position = readBlocks * 1024 * 1024 * blockSize;
                    if (position >= fileLen) break;
                    blockNumber = readBlocks;
                    readBlocks++;
                }
                try {
                    fis.seek(position);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int kol = readBlock();
                System.out.println("Прочитан " + blockNumber + " блок потоком номер "+Thread.currentThread().getName());
                //if (kol < 1024 * 1024 * blockSize) break;
                if (kol<=0) break;

            System.out.println("В буфере" + (store.blockCount()) + " блоков");

        }

        closeFile();
        System.out.println("Поток завершен "+Thread.currentThread().getName());
    }


}
