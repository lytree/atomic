package top.lytree.sftp;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.IOException;
import java.io.InputStream;

public class CustomNameFile extends InMemorySourceFile {
    private final String fileName;


    private final InputStream inputStream;


    public CustomNameFile(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public long getLength() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
