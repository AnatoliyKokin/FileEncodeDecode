public class DecodeThreadPool extends MyThreadPool {
    protected DecodeThreadPool(String fileNameIn, String fileNameOut, int blockSize) {
        super(fileNameIn, fileNameOut, blockSize);
    }

    @Override
    protected MyProducer createProducer() {
        return new DecodeProducer(fileNameIn, store, blockSize);
    }

    @Override
    protected MyCostumer createConsumer() {
        return new DecodeCustomer(fileNameOut,store);
    }

    @Override
    protected void createThreads() {
       super.createThreads();
    }
}
