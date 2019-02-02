public abstract class MyProducer implements Runnable {

    protected String fileName; // имя файла для чтения
    protected int blockSize;    // размер блока в мегабайтах
    protected int blockNumber; // номер блока данных
    protected BlockStore store; // хранилище блоков данных

    private MyProducer() {}


    // сделать основным
    protected MyProducer(String fileName, BlockStore store, int block_size) {
        this.fileName = fileName;
        this.blockSize = block_size;
        this.store = store;
        this.blockNumber = 0;
        //openFile();
    }

    protected MyProducer(String fileName, BlockStore store) {
        this.fileName = fileName;
        this.blockSize = 1;
        this.store = store;
        this.blockNumber = 0;
        //openFile();
    }

    public abstract void openFile();
    public abstract void closeFile();
    public abstract int readBlock();
}
