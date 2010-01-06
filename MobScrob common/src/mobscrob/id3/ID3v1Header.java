/**
 * 
 */
package mobscrob.id3;

import java.io.IOException;

/**
 * @author Neill
 * 
 */
public class ID3v1Header implements ID3Header {

	public static final int HEADER_LEN = 3;
	public static final int ID3v1_TAG_LENGTH = 128;

	private byte[] raw;

	public ID3v1Header(byte[] raw) {
		this.raw = raw;
	}

	public long bodyLength() {
		return ID3v1_TAG_LENGTH - 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#hasExtendedHeader()
	 */
	public boolean hasExtendedHeader() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#hasUnchronization()
	 */
	public boolean hasUnchronization() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#majorVersion()
	 */
	public int majorVersion() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#parse()
	 */
	public void parse() throws IOException {
		// check that header contains T A G

		if (raw[0] != 'T' || raw[1] != 'A' || raw[2] != 'G') {
			throw new IOException("Invalid ID3v1 tag header");
		}
	}

}
