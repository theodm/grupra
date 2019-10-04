package propra.imageconverter.image;

import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

// https://stackoverflow.com/questions/1094703/java-file-input-with-rewind-reset-capability/18665678#18665678
public class MarkableFileInputStream extends FilterOutputStream {
    private FileChannel myFileChannel;
    private long mark = -1;

    public MarkableFileInputStream(FileOutputStream fis) {
        super(fis);
        myFileChannel = fis.getChannel();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            mark = myFileChannel.position();
        } catch (IOException ex) {
            mark = -1;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (mark == -1) {
            throw new IOException("not marked");
        }
        myFileChannel.position(mark);
    }
}