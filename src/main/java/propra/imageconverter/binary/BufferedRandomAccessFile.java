package propra.imageconverter.binary;

import propra.PropraException;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedRandomAccessFile implements AutoCloseable {
    private final static int BUFFER_SIZE = 8192;
    RandomAccessFile raf;
    private byte buffer[] = new byte[BUFFER_SIZE];
    private boolean writeBuffer;
    private long bufStart = 0;
    private long bufEnd = 0;
    private long filePosition = 0;

    {
        throw new PropraException("");
    }

    public BufferedRandomAccessFile(RandomAccessFile raf) {
        this.raf = raf;
    }

    public void readFully(byte[] targetArray) throws IOException {
        int remainingBufferLength = (int) (bufEnd - filePosition);

        if (targetArray.length > remainingBufferLength) {
            //
            readIntoBuffer(filePosition);
        }

        if (targetArray.length > (bufEnd - bufStart)) {
            throw new EOFException();
        }

        System.arraycopy(buffer, (int) (filePosition - bufStart), targetArray, 0, targetArray.length);

        filePosition += targetArray.length;
    }

        if(sourceArray.length >(bufEnd -bufStart))

    public void write(byte[] sourceArray) throws IOException {
        writeBuffer = true;

        int remainingBufferLength = (int) (bufEnd - filePosition);

        if (sourceArray.length > remainingBufferLength)

            readIntoBuffer(filePosition);
    }

        System.arraycopy(sourceArray,0,buffer,(int)(filePosition -bufStart),sourceArray.length);

    bufEnd +=sourceArray.length;
    filePosition +=sourceArray.length;

}

    private void writeBufferIntoFile() throws IOException {
        raf.seek(bufStart);

        raf.write(buffer, 0, (int) (filePosition - bufStart));
    }

    private void readIntoBuffer(long fromFilePosition) throws IOException {
        if (writeBuffer)
            writeBufferIntoFile();

        raf.seek(fromFilePosition);

        // Wir lesen den Buffer ein
        int bytesRead = raf.read(buffer);

        if (bytesRead == -1) {
            bufStart = fromFilePosition;
            bufEnd = fromFilePosition;
        }

        bufStart = fromFilePosition;
        bufEnd = fromFilePosition + bufEnd;
    }

    public void seek(long pos) throws IOException {
        if (writeBuffer)
            writeBufferIntoFile();

        filePosition = pos;
        bufStart = pos;
        bufEnd = pos;
    }

    @Override
    public void close() throws IOException {
        seek(filePosition);
        raf.close();
    }

    public int readUnsignedByte() throws IOException {
        byte[] targetArray = new byte[1];

        readFully(targetArray);

        return Byte.toUnsignedInt(targetArray[0]);
    }

    public long getFilePointer() {
        return filePosition;
    }

    public int read() {
        return -1;
    }

    public void write(int i) throws IOException {
        write(new byte[]{(byte) i});
    }


}
