/*
   Copyright 2011  U.S. Centers for Disease Control and Prevention

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package gov.cdc.phlissa.hub.direct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * HubDirectReceiver supports the parsing of a plain text message received over
 * a NHIN Direct connection. These Direct messages are formatted according to RFC822,
 * with headers preceding a payload message. This class is not intended to be a
 * generic parser of all possible messages sent over NHIN Direct, but only of those
 * bearing HL7 messages with a content type of text/plain. It may be used for other
 * messages, provided they are of content type text/plain. It will not parse Direct
 * messages that are of other content types.
 * 
 * @author Michael Eidson, SAIC
 * @version $Id: //PHLISSA_HUB/DirectRESTIface/HubDirectTransport/src/gov/cdc/phlissa/hub/direct/HubDirectReceiver.java#4 $
 */
public class HubDirectReceiver {

	/**
	 * Data object to contain details regarding the completion of a call to
	 * {@link HubDirectReceiver#parse(String, boolean)}.
	 */
	public class HubDirectReceiverDetails {

		/**
		 * Indicates whether an error condition occurred.
		 */
		public boolean hasError = false;

		/**
		 * If an error has occurred, this field contains the error message.
		 */
		public String errorMessage = "";

		/**
		 * If no error has occurred, this field contains the extracted payload
		 * message.
		 */
		public String message = "";

		/**
		 * Default constructor.
		 */
		public HubDirectReceiverDetails() {

			super();
		}

		/**
		 * Copy constructor. Constructed object contains the same values as those
		 * in the passed parameter.
		 * 
		 * @param d The HubDirectReceiverDetails object to be copied. May not be
		 * <tt>null</tt>.
		 */
		public HubDirectReceiverDetails(HubDirectReceiverDetails d) {

			this.hasError = d.hasError;

			this.errorMessage = d.errorMessage;

			this.message = d.message;
		}
	}
	private Session session =
    	Session.getDefaultInstance(new Properties());

	private HubDirectReceiverDetails details =
		new HubDirectReceiverDetails();

	/**
     * Constructor.
     */
    public HubDirectReceiver() {

    	super();
    }

    /**
     * <p>A synchronized method that calls {@link #parse(String, boolean)} with the
     * same first argument and a second argument of <tt>false</tt>, returning
     * a copy of the details object built by the call to {@link #parse(String, boolean)}.
     * In a multi-threaded situation where different threads make use of the same
     * HubDirectReceiver object, this method may be used exclusive of any direct calls
     * to {@link #parse(String, boolean)} to ensure that the threads do not
     * corrupt each other's results before they can be examined. If different
     * threads use their own instances of HubDirectReceiver, there is no conflict,
     * and the non-synchronized {@link #parse(String, boolean)} method of an object
     * may be called and then the getter methods of that object called without fear
     * of the results having been corrupted by another thread.</p>
     * 
     * <p>If synchronization is such an issue that use of this method is required,
     * then the methods {@link #hasError()}, {@link #getErrorMessage()}, and
     * {@link #getMessage()} should not be used, as their return values will not be
     * reliable. The fields of the object returned by this method should be examined
     * instead.</p> 
     * 
     * @param directMessage The Direct message to be parsed.
     * 
     * @return A HubDirectReceiverDetails object containing the details of the parse.
     * This object will not be overwritten by subsequent calls to this method or to
     * {@link #parse(String, boolean)}.
     * 
     * @see #parse(String, boolean)
     * @see HubDirectReceiverDetails
     */
    public synchronized HubDirectReceiverDetails parse(String directMessage) {

    	parse(directMessage, false);

    	HubDirectReceiverDetails detailsCopy =
    		new HubDirectReceiverDetails(details);

    	return detailsCopy;
    }

    /**
     * <p>Parses a Direct message to strip off the headers. After this method is
     * called, and before it is called again on the same HubDirectReceiver object,
     * the payload message following the headers may be obtained by calling
     * the method {@link #getMessage()}, while any error message may be obtained by
     * calling the method {@link #getErrorMessage()}, and the fact of whether
     * an error occurred may be known by calling {@link #hasError()}.</p>
     * 
     * <p>This method is not synchronized. In a multi-threaded situation,
     * different threads may each use their own instance of HubDirectReceiver
     * without corrupting each other's results. If different threads try to make
     * use of the same HubDirectReceiver object, however, they run the risk of
     * corrupting each other's results; to avoid this, synchronization may be
     * enforced by only making calls to {@link #parse(String)} and examining the
     * details object returned by that call, rather than calling the getter methods.
     * 
     * @param directMessage The Direct message to be parsed. The payload message
     * in the Direct message must be a plain-text message. That is, the
     * <tt>Content-type</tt> header of the Direct message must specify
     * <tt>text/plain</tt>. It is also assumed that the message uses the system's
     * default character set.
     * @param throwException A value of <tt>true</tt> indicates that a RuntimeException
     * exception should be thrown if an error occurs. A value of <tt>false</tt>
     * indicates that this method is not to throw any exceptions, but only indicate
     * error conditions through the getter methods.
     * 
     * @see #getMessage()
     * @see #getErrorMessage()
     * @see #hasError()
     * @see #parse(String)
     * @see HubDirectReceiverDetails
     */
    public void parse(String directMessage, boolean throwException) {

    	RuntimeException re = null;

		details.message = "";

		details.errorMessage = "";

		details.hasError = false;

		try {

			InputStream in = new ByteArrayInputStream(directMessage.getBytes());

			MimeMessage mimemsg = new MimeMessage(session, in);

			if (mimemsg.isMimeType("text/plain")) {

				details.message = mimemsg.getContent().toString();

			} else {

				details.errorMessage = "Unsupported content type: "
					+ mimemsg.getContentType();

				details.hasError = true;

				if (throwException) {

					re = new RuntimeException(details.errorMessage);

					throw re;
				}
			}
		} catch (Throwable e) {

			details.errorMessage = e.getLocalizedMessage();

			details.hasError = true;

			if (throwException) {

				re = new RuntimeException(e);

				throw re;
			}
		}
    }

    /**
     * @return <tt>true</tt> if the last call to {@link #parse(String, boolean)}
     * caused an error condition; else returns <tt>false</tt>. This return value
     * is unreliable if this method is called simultaneously with a call to
     * {@link #parse(String, boolean)}. If synchronization is an issue, consider using
     * {@link #parse(String)} instead and examining the <tt>hasError</tt> field of
     * the returned object.
     */
    public boolean hasError() {

    	return details.hasError;
    }

    /**
     * @return the error message resulting from the error condition, if any, that
     * occurred during the last call to {@link #parse(String, boolean)}. This return
     * value is unreliable if this method is called simultaneously with a call to
     * {@link #parse(String, boolean)}. If synchronization is an issue, consider using
     * {@link #parse(String)} instead and examining the <tt>errorMessage</tt> field of
     * the returned object.
     */
    public String getErrorMessage() {

    	return details.errorMessage;
    }

	/**
	 * Return a name suitable for logging purposes. Since this class may be
	 * used by external processes, such as Mirth, for example, this method is
	 * provided for external processes to call to retrieve a name to use in
	 * log messages concerning this class.
	 * 
	 * @return a name by which this class may be identified, suitable for
	 * use in log messages in external processes.
	 */
	public String getLogName() {

		return getClass().getName();
	}

    /**
     * @return the payload message extracted from the Direct message during the last
     * call to {@link #parse(String, boolean)}. This return value is unreliable if
     * this method is called simultaneously with a call to {@link #parse(String, boolean)}. If
     * synchronization is an issue, consider using {@link #parse(String)} instead
     * and examining the <tt>message</tt> field of the returned object.
     */
    public String getMessage() {

    	return details.message;
    }

    /**
     * A main method for reading a Direct message from a file and extracting
     * the payload message, which must be a plain-text message (e.g., the
     * Direct message's <tt>Content-type</tt> header must specify
     * <tt>text/plain</tt>). This method prints the payload message if no error
     * occurs, else it prints the error message.
     * 
	 * @param args The first and only argument is the path to the file containing
	 * the Direct message in RFC822 format. If no arguments are passed, or if the
	 * first argument is <tt>--help</tt>, then a usage message is printed and the
	 * method exits.
	 * 
     * @throws IOException if an error occurs while reading the file.
	 */
	public static void main(String[] args) throws IOException {

		if ((args == null) ||
				(args.length == 0) ||
				(args[0].equalsIgnoreCase("--help"))) {

			System.out.println("The first and only argument is the path "
					+ "to the file containing the Direct message to be parsed.");

			System.exit(0);
		}
		String directMessage = HubDirectSender.readFileAsString(args[0]);

		HubDirectReceiver receiver = new HubDirectReceiver();

		receiver.parse(directMessage);

		if (receiver.hasError()) {

			System.out.println("ERROR: " + receiver.getErrorMessage());

			System.exit(1);

		} else {

			System.out.println(receiver.getMessage());

			System.exit(0);
		}
	}
}
