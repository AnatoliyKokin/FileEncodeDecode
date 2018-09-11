

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

        MyZipper z1 = new MyZipper(args[0],0,10,400);
        //MyZipper z2 = new MyZipper(args[0],1000,10,100);
        //MyZipper z3 = new MyZipper(args[0],2000,10,100);
        //MyZipper z4 = new MyZipper(args[0],3000,10,100);

        z1.start();
        //z2.start();
        //z3.start();
        //z4.start();
        try {
            //z1.start();
            z1.join();
            //z2.start();
            //z2.join();
            //z3.start();
            //z3.join();
            //z4.start();
            //z4.join();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            zout.close();
        }
        catch (IOException r) {
            r.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println((date2.getTime() - date.getTime())+" ms");
    }

    public static class MyZipper extends Thread {


        private String fileName;
        private long offset;
        private int blockSize;
        private int blockCount;
        private byte[] arr;

        public MyZipper(String fileName, long offset, int block_size, int block_count) {
            this.fileName = fileName;
            this.offset = offset;
            this.blockCount = block_count;
            this.blockSize = block_size;
            arr = new byte[blockSize*1024*1024];
        }

        @Override
        public void run() {
            super.run();
            //ZipEntry z=null;
            int count =0;
            int i=0;
            System.out.println("Открыт поток для записи " + (offset + 1) + " блока");

            RandomAccessFile fis = null;
            try {
                fis = new RandomAccessFile(fileName, "r");
                fis.seek(offset * 1024*1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (blockCount>0) {

                try {
                        //count = getBytes();
                    ZipEntry z = new ZipEntry(Long.toString(offset+i));
                    count = fis.read(arr);
                    if (count==0) break;
                    synchronized (zout) {
                            zout.putNextEntry(z);
                            putEntryBytes(zout,count);
                            zout.closeEntry();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                }

                if (count<arr.length) break;
                //offset++;????
                blockCount--;
                i++;
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Запись блока "+offset+1+" Завершена");
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

        private void putEntryBytes(ZipOutputStream stream, int count) {
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


        }
    }

}