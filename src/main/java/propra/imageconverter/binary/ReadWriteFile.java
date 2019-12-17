package propra.imageconverter.binary;

import propra.PropraException;

import java.io.*;
import java.nio.channels.Channels;

/**
 * Wir wollen die Möglichkeit haben, eine geöffnete Datei
 * an mehreren Stellen lesen zu können und innerhalb der Datei
 * auch hin- und herspringen können (Random Access).
 * <p>
 * Die Klasse ReadWriteFile gibt uns diese Möglichkeit. Sie ermöglicht
 * es uns für eine geöffnete Datei an beliebigen Stellen (gebufferte)
 * Eingabe- und Ausgabestreams zu öffnen. Es kann gleichzeitig immer nur
 * ein Eingabe- oder Ausgabestream geöffnet sein.
 * <p>
 * Ein Ausgabestream kann nur geöffnet werden, wenn das zugrundeliegende
 * RandomAccessFile im Modus ReadWrite geöffnet wurde.
 */
public final class ReadWriteFile implements AutoCloseable {
    private final RandomAccessFile randomAccessFile;

    /**
     * Aktueller Ausgabestream, falls geöffnet, sonst null.
     */
    private BufferedOutputStream lastOutputStream = null;

    /**
     * Aktueller Eingabestream, falls geöffnet, sonst null.
     */
    private InputStream lastInputStream = null;

    private ReadWriteFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    /**
     * Erstellt eine Instanz von ReadWriteFile.
     */
    public static ReadWriteFile createReadWriteFile(RandomAccessFile randomAccessFile) {
        return new ReadWriteFile(randomAccessFile);
    }

    /**
     * Erstellt eine Instanz von ReadWriteFile, bei der sichergestellt wird,
     * dass die Inhalte der Datei zuvor gelöscht werden.
     */
    public static ReadWriteFile overwriteReadWriteFile(RandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.setLength(0);

        return new ReadWriteFile(randomAccessFile);
    }

    /**
     * Wirft eine Exception, fallls ein anderer Stream
     * für diese Datei bereits geöffnet ist.
     */
    private void throwIfStreamOpened() {
        if (lastInputStream != null || lastOutputStream != null)
            throw new PropraException("Es kann zurzeit kein neuer Stream geöffnet werden, da bereits ein Stream für diese Datei geöffnet wurde.");
    }

    /**
     * Gibt einen Eingabestream von der Position [filePosition] in der Datei zurück
     * <p>
     * Der Benutzer muss diesen Eingabestream mittels releaseInputStream
     * wieder freigeben. Der EingabeStream darf durch den Benutzer nicht
     * geschlossen werden.
     */
    public InputStream inputStream(long filePosition) throws IOException {
        throwIfStreamOpened();

        // Zuerst an diese Stelle der Datei wechseln
        randomAccessFile.seek(filePosition);

        // Dann öffnen wir den Eingabestream an dieser Position
        lastInputStream = new BufferedInputStream(
                Channels.newInputStream(
                        randomAccessFile.getChannel()
                )
        );

        return lastInputStream;
    }

    /**
     * Gibt den Eingabestream wieder frei.
     */
    public void releaseInputStream() {
        lastInputStream = null;

        // BufferedInputStream darf nicht geschlossen werden,
        // da sonst der zugrundeliegende FileChannel geschlossen würde.
    }

    /**
     * Gibt einen Ausgabestream von der Position [filePosition] in der Datei zurück
     * <p>
     * Der Benutzer muss diesen Eingabestream mittels releaseInputStream
     * wieder freigeben. Der Ausgabestream darf durch den Benutzer nicht
     * geschlossen werden.
     * <p>
     * Nur unterstützt, wenn die Datei für Schreiben geöffnet ist.
     */
    public BufferedOutputStream outputStream(long filePosition) throws IOException {
        throwIfStreamOpened();

        // Zuerst an diese Stelle der Datei wechseln
        randomAccessFile.seek(filePosition);

        // Dann öffnen wir den Ausgabestream an dieser Position
        lastOutputStream = new BufferedOutputStream(
                Channels.newOutputStream(
                        randomAccessFile.getChannel()
                )
        );

        return lastOutputStream;
    }

    /**
     * Gibt den Ausgabestream wieder frei.
     */
    public void releaseOutputStream() throws IOException {
        // Noch nicht gespeicherte Daten des
        // Ausgabestreams schreiben
        lastOutputStream.flush();
        lastOutputStream = null;

        // Der Stream darf nicht geschlossen werden,
        // da sonst der zugrundeliegende FileChannel geschlossen würde.
    }

    /**
     * Schließt die zugrundeliegende Datei.
     */
    @Override
    public void close() throws IOException {
        // Noch nicht gespeicherte Daten des
        // Ausgabestreams schreiben
        if (lastOutputStream != null) {
            lastOutputStream.flush();
        }

        randomAccessFile.close();
    }

}
