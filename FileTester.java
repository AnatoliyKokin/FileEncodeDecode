

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class FileTester {

    static ZipOutputStream zout=null;

    public static  void main(String args[]) {

        if (args.length<2) return;

        System.out.println("processors = "+ Runtime.getRuntime().availableProcessors());
        System.out.println("processors = "+ Runtime.getRuntime().maxMemory());

        Date date = new Date();

        try {

            zout = new ZipOutputStream(new FileOutputStream(args[1]));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        BlockStore storage = new BlockStore();

        MyFileReader mfr1 = new MyFileReader(args[0],0,10,400,storage);
        MyFileWriter mfr2 = new MyFileWriter(args[1],storage);

        Thread z1 = new Thread(mfr1);
        Thread z2 = new Thread(mfr2);
        //ExecutorService executor = Executors.newCachedThreadPool();
        //executor.execute(mfr1);
        //executor.execute(mfr2);

        //executor.shutdown();


        //while (!executor.isShutdown());
        z1.start();
        z2.start();
        //z3.start();
        //z4.start();
        try {
            //z1.start();
            z1.join();
            //z2.start();
            z2.join();
            //z3.start();
            //z3.join();
            //z4.start();
            //z4.join();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*try {
            zout.close();
        }
        catch (IOException r) {
            r.printStackTrace();
        }*/
        Date date2 = new Date();
        System.out.println((date2.getTime() - date.getTime())+" ms");
    }

    public static class MyFileReader implements Runnable {


        private String fileName;
        private int offset;
        private int blockSize;
        private int blockCount;
        private int blockNumber;
        private BlockStore store;
        RandomAccessFile fis = null;

        public MyFileReader(String fileName, int offset, int block_size, int block_count, BlockStore store) {
            this.fileName = fileName;
            this.offset = offset;
            this.blockCount = block_count;
            this.blockSize = block_size;
            this.store = store;
            blockNumber = offset;
            openFile();
        }


        public MyFileReader (String fileName, BlockStore store) {
            this.fileName = fileName;
            this.store = store;
        }

        private void openFile() {
            try {
                fis = new RandomAccessFile(fileName, "r");
                fis.seek(offset * 1024*1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int readBlock() {

            Block blk = new Block();
            byte[] tmpBytes = new byte[1024*1024*blockSize];
            int count=0;
            try {
                count = fis.read(tmpBytes);
                blk.putBytes(tmpBytes,count);
                blk.setNumber(blockNumber);
                blockNumber++;
                store.put(blk);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }



        @Override
        public void run() {
            //ZipEntry z=null;
            int i=0;
            System.out.println("Открыт поток для чтения " + (offset + i));


            while (blockCount>0) {
                System.out.println("Прочитан " + (offset + i) + " блок");
                int kol=readBlock();
                //offset++;????
                if (kol<1024*1024*blockSize) break;

                /*while (store.blockCount()*blockSize>100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                System.out.println("В буфере" + (store.blockCount()) + " блоков");
                blockCount--;
                i++;
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Запись блока "+(offset+i)+" Завершена");
        }

        /*private long  getBytes() {
            long result=0;
            RandomAccessFile fis = null;
            try {
                fis = new RandomAccessFile(fileName, "r");
                fis.seek(offset * 1024*1024);
                result = fis.read(arr);
                fis.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }*/

        /*private void putEntryBytes(ZipOutputStream stream, int count) {
            //RandomAccessFile fis = null;
            try {
                //fis = new RandomAccessFile(fileName, "r");
                //fis.seek(offset * 1024*1024);
                //byte[] arr = new byte[1024*1024];
                //fis.read(arr);
                stream.write(arr,0,count);
            }
            catch (IOException e) {
                e.printStackTrace();
            }


        }*/
    }

    public static class MyFileWriter implements Runnable {


        private String fileName;
       // private int blockSize;
       // private int blockCount;
        private BlockStore store;
        ZipOutputStream zout = null;

        public MyFileWriter(String fileName, BlockStore store) {
            this.fileName = fileName;
            this.store = store;
            openFile();
        }

        private void openFile() {
            try {
                zout = new ZipOutputStream(new FileOutputStream(fileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void writeBlock() {
            //int i=0;
            Block blk=store.get();
            int i = blk.getNumber();
            try {
                //count = getBytes();
                ZipEntry z = new ZipEntry(Long.toString(i));
                zout.putNextEntry(z);
                zout.write(blk.getBytes(),0,blk.getDataSize());
                zout.closeEntry();
                }
             catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Записан " + ( i) + " блок");
            System.out.println("В буфере " + (store.blockCount()) + " блоков");
        }



        @Override
        public void run() {

            int i=0;


            while (true) {

                if (i>10) break;


                if (store.blockCount()>0) {
                    writeBlock();

                    i=0;
                }
                else {

                    try {
                        //wait();
                        Thread.sleep(100);
                        System.out.println("Поток записи вынужденно спит");
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                zout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}