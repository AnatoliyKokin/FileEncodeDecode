public class EncodeThreadPool extends MyThreadPool {


    protected EncodeThreadPool(String fileNameIn, String fileNameOut, int blockSize) {
        super(fileNameIn, fileNameOut, blockSize);
    }

    @Override
    protected MyProducer createProducer() {
        return new EncodeProducer(fileNameIn,store, blockSize);
    }

    @Override
    protected MyCostumer createConsumer() {
        return new EncodeCustomer(fileNameOut,store);
    }

    @Override
    protected void createThreads() {
        super.createThreads();
    }
}
