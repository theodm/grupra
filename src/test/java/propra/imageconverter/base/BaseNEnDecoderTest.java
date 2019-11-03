package propra.imageconverter.base;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseNEnDecoderTest {
    static List<TestCase> testCases() {
        return List.of(
                new TestCase("f", "CO", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new TestCase("fo", "CPNG", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new TestCase("foo", "CPNMU", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new TestCase("foob", "CPNMUOG", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new TestCase("fooba", "CPNMUOJ1", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new TestCase("foobar", "CPNMUOJ1E8", "0123456789ABCDEFGHIJKLMNOPQRSTUV"),

                new TestCase("f", "01100110", "01"),
                new TestCase("fo", "0110011001101111", "01"),
                new TestCase("foo", "011001100110111101101111", "01"),
                new TestCase("foob", "01100110011011110110111101100010", "01"),
                new TestCase("fooba", "0110011001101111011011110110001001100001", "01"),
                new TestCase("foobar", "011001100110111101101111011000100110000101110010", "01"),

                new TestCase("f", "Zg", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"),
                new TestCase("fo", "Zm8", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"),
                new TestCase("foo", "Zm9v", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"),
                new TestCase("foob", "Zm9vYg", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"),
                new TestCase("fooba", "Zm9vYmE", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"),
                new TestCase("foobar", "Zm9vYmFy", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void encode(TestCase testCase) throws IOException {
        BitInputStream bis = new BitInputStream(
                new ByteArrayInputStream(
                        testCase.input.getBytes()
                )
        );

        StringWriter os = new StringWriter();

        BaseNEnDecoder.encode(
                bis,
                os,
                testCase.alphabet
        );

        String encodeResult = os.toString();

        assertEquals(testCase.expected, encodeResult);

        // Rückwärtstest
        BufferedReader encodedBis
                = new BufferedReader(new StringReader(encodeResult));

        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        BitOutputStream decodedOS = new BitOutputStream(decodedBytes);

        BaseNEnDecoder.decode(encodedBis, decodedOS, testCase.alphabet);

        String decodeResult = decodedBytes.toString();

        assertEquals(testCase.input, decodeResult);
    }

    static class TestCase {
        public final String input;
        public final String expected;
        public final String alphabet;

        public TestCase(String input, String expected, String alphabet) {
            this.input = input;
            this.expected = expected;
            this.alphabet = alphabet;
        }

        @Override
        public String toString() {
            return "TestCase{" +
                    "input='" + input + '\'' +
                    ", expected='" + expected + '\'' +
                    ", alphabet='" + alphabet + '\'' +
                    '}';
        }
    }
}