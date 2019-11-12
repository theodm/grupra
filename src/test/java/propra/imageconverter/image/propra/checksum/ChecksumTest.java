package propra.imageconverter.image.propra.checksum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import propra.imageconverter.image.propra.Checksum;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChecksumTest {
	/**
	 * Nimm einen Test vor, für eine Eingabe, die als String vorliegt.
	 */
	private long calcForString(String str) throws IOException {
		byte[] test = str.getBytes();

        return calcForByteArray(test);
	}

	/**
	 * Nimm einen Test vor, für eine Eingabe, die als Byte-Array vorliegt.
	 */
	private long calcForByteArray(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
			return Checksum.calcStreamingChecksum(bytes.length, bis::read);
        }
	}

	@Test
	@DisplayName("Die Prüfsummen für die Beispielfälle werden korrekt generiert.")
	void checksumTest() throws IOException {
		assertEquals(0x00750076, calcForString("t"));
		assertEquals(0x00DC0152, calcForString("te"));
		assertEquals(0x015202A4, calcForString("tes"));
		assertEquals(0x01CA046E, calcForString("test"));
		assertEquals(0x3C56F024, calcForString(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."));
		assertEquals(0x07AEE0D6, calcForString(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));

		assertEquals(0x00000001, calcForByteArray(new byte[] {}));
		assertEquals(0x00010002, calcForByteArray(new byte[] { 0 }));
		assertEquals(0x00020003, calcForByteArray(new byte[] { 1 }));
		assertEquals(0x00040006, calcForByteArray(new byte[] { 0, 1 }));
		assertEquals(0x00040007, calcForByteArray(new byte[] { 1, 0 }));
		assertEquals(0x01820283, calcForByteArray(new byte[] { (byte) 255, (byte) 128 }));

	}
}