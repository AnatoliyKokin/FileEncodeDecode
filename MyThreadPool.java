public abstract class MyThreadPool {

    protected String fileNameIn; // имя файла для чтения
    protected String fileNameOut; // имя файла для записи
    protected int blockSize; // размер блока данных в мегабайтах
    protected BlockStore store;

    private MyThreadPool() {}

    protected MyThreadPool(String fileNameIn, String fileNameOut, int blockSize) {
        this.fileNameIn = fileNameIn;
        this.fileNameOut = fileNameOut;
        this.blockSize = blockSize;
        store = new BlockStore();
    }

    protected abstract MyProducer createProducer();
    protected abstract MyCostumer createConsumer();
    protected  void createThreads() {

        MyProducer prod1=createProducer();
        MyCostumer cons1=createConsumer();
        Thread z11 = new Thread(prod1);
        Thread z22 = new Thread(cons1);

        z11.start();
        z22.start();

        try {

            z11.join();
            z22.join();


        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
