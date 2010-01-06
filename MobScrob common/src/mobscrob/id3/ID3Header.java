package mobscrob.id3;

import java.io.IOException;

/**
 * Interface for an ID3 header
 * 
 * @author Neill
 * 
 */
public interface ID3Header {

	/**
	 * Parses the raw header data
	 * 
	 * @throws IOException
	 */
	public abstract void parse() throws IOException;

	/**
	 * Returns the body length held in the header
	 * 
	 * @return
	 */
	public abstract long bodyLength();

	/**
	 * Returns the major version number held in the header
	 * 
	 * @return
	 */
	public abstract int majorVersion();

	public abstract boolean hasUnchronization();

	public abstract boolean hasExtendedHeader();

}