import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DecodeCustomer extends MyCostumer {

    private FileOutputStream zout;
    private static File file;
    private static volatile boolean fileOpened;
    private static int writeBlocks;


    protected DecodeCustomer(String fileName, BlockStore store) {
        super(fileName, store);
    }


    @Override
    public void openFile() {
        try {
            file = new File(fileName);
            if (!fileOpened) {
                if (file.exists()) file.delete();
            }
            zout = new FileOutputStream(fileName, true);
            fileOpened = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeFile() {
        try {
            //store.isReady = true;
            zout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeBlock() {

        synchronized (DecodeCustomer.class) {
            Block blk=store.get();
            if (blk==null) {store.isReady=true; return;}
            int i = blk.getNumber();
            System.out.println("writing " + i + " block in thread "+Thread.currentThread().getName());
            try {
                //count = getBytes();

                //zout.write(blk.getBytes(), 0, blk.getDataSize());
                zout.write(blk.getBytes());
                zout.flush();
                writeBlocks = i;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Записан " + (i) + " блок");
        }
        System.out.println("В буфере " + (store.blockCount()) + " блоков");

    }

    @Override
    public void run() {

        openFile();

        while (!store.isReady || store.blockCount()>0) {

                writeBlock();

        }

        closeFile();
        System.out.println("Поток закрыт "+Thread.currentThread().getName());

    }

}
