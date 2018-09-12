public class Block {
    private byte[] arr=null;
    private int blockSize;
    private int dataSize;
    private int number;

    public Block() {
        blockSize=0;
        dataSize=0;
    }

    public Block(int block_size) {
        arr = new byte[1024*1024*block_size];
        blockSize = block_size;
        dataSize = 0;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void putBytes(byte[] data, int count) {

            arr = data;
            blockSize = data.length/1024/1024;
            dataSize = count;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public byte[] getBytes() {
        return arr;
    }
}
