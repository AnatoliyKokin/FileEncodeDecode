import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DecodeProducer extends MyProducer {

    private static ZipFile zf;
    private static Enumeration e;
    public static volatile boolean fileClosed;

    public DecodeProducer(String fileName, BlockStore store, int block_size) {
        super(fileName, store, block_size);
        openFile();

    }


    @Override
    public void openFile() {

        if (zf==null) {
                try {
                    zf = new ZipFile(fileName);
                    e = zf.entries();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }

    }

    @Override
    public void closeFile() {
        try {
            fileClosed = true;
            zf.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int readBlock() {
        ZipEntry entry = (ZipEntry) e.nextElement();
        byte[] tmpBytes = new byte[(int)entry.getSize()];
        Block blk = new Block();

        int count=0;
        int tcount = 0;

        try {
            InputStream is = zf.getInputStream(entry);
            do {
                count = is.read(tmpBytes, tcount, tmpBytes.length-tcount);
                if (count<0) break;
                tcount+=count;
            }
            while (count>0);
            if (tcount==0) return 0;

            System.out.println("read "+tcount+" bytes from entry "+entry.getName() );
            blk.putBytes(tmpBytes,tcount);
            blockNumber = Integer.valueOf(entry.getName());
            blk.setNumber(blockNumber);
            //blockNumber++;
            store.put(blk);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //int tcount=0;

        return tcount;
    }

    @Override
    public void run() {

        System.out.println("Открыт поток для чтения "+Thread.currentThread().getName());

        while (!store.isReady ) {

            synchronized (DecodeProducer.class) {
                if (fileClosed) break;
                if (!e.hasMoreElements()) {store.isReady=true; break;}
                int kol = readBlock();
                if (kol<=0) break;
                System.out.println("Прочитан " + (blockNumber) + " блок потоком "+Thread.currentThread().getName());
            }

            System.out.println("В буфере" + (store.blockCount()) + " блоков");
        }


        closeFile();
        System.out.println("Поток завершен "+Thread.currentThread().getName());
    }


}
