package net.sf.microlog.appender;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.sf.microlog.Level;
import net.sf.microlog.util.PropertiesGetter;

/**
 * A class that logs to a file. The class uses the FileConnection API from
 * JSR-75.
 * <p>
 * The file name can be passed with the property
 * <code>microlog.appender.FileAppender.filename</code>.
 * 
 * npearman - Modified for Sony Ericsson filesystem
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @author Karsten Ohme
 * @since 0.1
 */
public class FileAppender extends AbstractAppender {

	public static final String FILENAME_STRING = "microlog.appender.FileAppender.filename";
	public static final String DEFAULT_FILENAME = "microlog.txt";
	public static final String DEFAULT_EMULATOR_ROOT = "/root1";

	private static final int BUFFER_SIZE = 256;
	private static final String FILE_PROTOCOL = "file://";

	private final String lineSeparator = "\r\n";

	private String fileSeparator = "/";
	private String rootDir = "/e:/";
	private String fileName = DEFAULT_FILENAME;
	private FileConnection fileConnection;
	private OutputStream outputStream;

	/**
	 * Create a FileAppender.
	 */
	public FileAppender() {
		super();
		String separator = System.getProperty("file.separator");
		if (separator != null) {
			this.fileSeparator = separator;
		}
	}

	/**
	 * Get the separator that is used when creating the URI for the logfile.
	 * 
	 * @return the fileSeparator
	 */
	public String getFileSeparator() {
		return fileSeparator;
	}

	/**
	 * Set the separator that is used for creating the URI for the logfile.
	 * 
	 * Note that changing this after the logfile has been opened has no effect.
	 * 
	 * @param fileSeparator
	 *            the fileSeparator to set
	 */
	public void setFileSeparator(String fileSeparator) {
		this.fileSeparator = fileSeparator;
	}

	/**
	 * Set the filename of the logfile.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the filename of the logfile.
	 * 
	 * Note that changing this after the logfile has been opened has no effect.
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the size of the log.
	 * 
	 * @return the size of the log.
	 */
	public long getLogSize() {

		long logSize = SIZE_UNDEFINED;

		if (fileConnection != null) {
			try {
				logSize = fileConnection.fileSize();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return logSize;
	}

	/**
	 * Configure the FileAppender.
	 * <p>
	 * The file name can be passed with the property
	 * <code>microlog.appender.FileAppender.filename</code>.
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	public synchronized void configure(PropertiesGetter properties) {
		// Set the record store name from Properties
		fileName = properties.getString(FILENAME_STRING);
		if (fileName == null) {
			fileName = DEFAULT_FILENAME;
		}
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#openLog()
	 */
	public synchronized void openLog() {

		if (logOpen) {
			System.out.println("MicroLog already opened");
			return;
		}
		if (fileConnection == null) {
			try {
				long fileSize = 0;
				String connectionString = getConnectionString();
				System.out.println("Creating log " + connectionString);

				// DO NOT use Connector.open(<url>, <mode>)
				fileConnection = (FileConnection) Connector
						.open(connectionString);
				System.out.println("Opened FileConnection");
				if (!fileConnection.exists()) {
					System.out.println("Creating file - doesn't exist");
					// create file
					fileConnection.create();
				} else {
					fileSize = fileConnection.fileSize();
					System.out.println("File exists, file size: " + fileSize);
				}

				// open stream at end of file
				outputStream = fileConnection.openOutputStream(fileSize);
				logOpen = true;
				System.out.println("Finished opening FileAppender");
			} catch (Throwable e) {
				System.out.println("Unable to open log: " + e.getMessage());
				e.printStackTrace();
				// close streams on error
				closeLog();
			}
		}

	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#doLog(net.sf.microlog.Level,
	 *      java.lang.Object, java.lang.Throwable)
	 */
	public void doLog(Level level, Object message, Throwable t) {
		if (logOpen && formatter != null) {
			synchronized (fileConnection) {
				String logString = formatter.format(level, message, t);
				try {
					byte[] stringData = logString.getBytes();
					outputStream.write(stringData);
					outputStream.write(lineSeparator.getBytes());
					outputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#clearLog()
	 */
	public synchronized void clearLog() {
		if (fileConnection != null && fileConnection.isOpen()) {
			try {
				fileConnection.truncate(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#closeLog()
	 */
	public synchronized void closeLog() {
		logOpen = false;

		if (outputStream != null) {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (Exception ex) {
			}
		}
		if (fileConnection != null && fileConnection.isOpen()) {
			try {
				fileConnection.close();
			} catch (IOException e) {
			}
		}

		while (fileConnection != null && fileConnection.isOpen()) {
			// wait a while for file to close
			System.out.println("Waiting 2 secs for log file to close..");
			try {
				Thread.sleep(2000);
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Get the connection string to use for opening the FileConnection.
	 * 
	 * @return the connection String.
	 */
	private synchronized String getConnectionString() {
		StringBuffer stringBuffer = new StringBuffer(BUFFER_SIZE);

		stringBuffer.append(FILE_PROTOCOL);
		stringBuffer.append(rootDir);
		stringBuffer.append(fileName);

		return stringBuffer.toString();
	}

}
