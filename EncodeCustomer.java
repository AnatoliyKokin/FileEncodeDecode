import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EncodeCustomer extends MyCostumer {

    static ZipOutputStream zout;
    volatile boolean fileOpened;

    public EncodeCustomer(String fileName, BlockStore store) {
        super(fileName, store);
    }

    @Override
    public void openFile() {
        if (!fileOpened) {

            try {
                zout = new ZipOutputStream(new FileOutputStream(fileName));
                fileOpened =true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeFile() {
        try {
            zout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeBlock() {
        int i=0;

        try {

            synchronized (EncodeCustomer.class) {
                Block blk=store.get();
                i = blk.getNumber();
                ZipEntry z = new ZipEntry(Long.toString(i));
                zout.putNextEntry(z);
                zout.write(blk.getBytes(), 0, blk.getDataSize());
                zout.closeEntry();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Записан " + ( i) + " блок потоком "+Thread.currentThread().getName());
        System.out.println("В буфере " + (store.blockCount()) + " блоков");
    }

    @Override
    public void run() {
        int i=0;
        openFile();

        while (true) {

            if (i>10) break;

            //synchronized (EncodeCustomer.class) {
                if (store.blockCount() > 0) {
                    writeBlock();

                    i = 0;
                } else {

                    try {
                        //wait();
                        Thread.sleep(100);
                        System.out.println("Поток записи вынужденно спит");
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            //}
        }

        closeFile();
        System.out.println("Поток завершен "+Thread.currentThread().getName());

    }

}
