package propra.imageconverter.image.propra;

/**
 * Die Klasse enthält Informationen, die sowohl
 * vom PropraReader, als auch vom PropraWriter verwendet und benötigt werden.
 *
 * Das Propra-Format ist unter https://moodle-wrm.fernuni-hagen.de/mod/page/view.php?id=40779
 * dokumentiert.
 */
final class PropraFileFormat {
    /**
     * Der statische Beginn einer Propa-Datei.
     */
    final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();

    /**
     * Der Offset vom Beginn einer Propra-Datei
     * bis zum Beginn der Prüfsumme
     */
    final static long OFFSET_CHECKSUM = MAGIC_HEADER.length + 2 + 2 + 1 + 1 + 8;

    /**
     * Der Offset vom Beginn einer Propra-Datei
     * bis zu dem Beginn des Datensegments
     */
    final static long OFFSET_DATA = MAGIC_HEADER.length + 2 + 2 + 1 + 1 + 8 + 4;
}
