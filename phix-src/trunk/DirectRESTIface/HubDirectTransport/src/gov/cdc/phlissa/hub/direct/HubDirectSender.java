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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * HubDirectSender supports the wrapping of a plain text "payload" message and
 * sending it over a NHIN Direct REST connection for delivery to a specified
 * Direct address. The wrapped message, also referred to as the Direct message,
 * consists of headers required by the RFC822 format and the payload message.
 * The MimeMessage class from JavaMail is used to construct the Direct message.
 * The Direct message is then written from the MimeMessage object into a String,
 * using the US-ASCII character set. This String is then sent over SSL to a
 * specified Direct REST instance, after authenticating as a specified Direct
 * user with a specified password. The Direct REST instance is responsible for
 * connecting to the correct remote Direct REST instance and sending the message
 * to the remote Direct REST instance according to the requirements of the
 * Direct REST process, for delivering the message to its intended recipient.
 * 
 * @author Michael Eidson, SAIC
 * @version $Id: //PHLISSA_HUB/DirectRESTIface/HubDirectTransport/src/gov/cdc/phlissa/hub/direct/HubDirectSender.java#5 $
 */
public class HubDirectSender {

    /**
     * The default port number for the Direct REST instance.
     */
    private static final int DEFAULT_PORT = 8443;

    /**
     * The property to check for the <tt>fromAddress</tt> value to use if not
     * provided as an argument to the constructor.
     */
    private static final String FROM_ADDRESS_PROPERTY =
        "gov.cdc.phlissa.hub.direct.fromAddress";

    /**
     * The property to check for the <tt>fromPassword</tt> value to use if not
     * provided as an argument to the constructor.
     */
    private static final String FROM_PASSWORD_PROPERTY =
        "gov.cdc.phlissa.hub.direct.fromPassword";

    /**
     * The property to check for the <tt>host</tt> value to use if not provided
     * as an argument to the constructor.
     */
    private static final String HOST_PROPERTY =
        "gov.cdc.phlissa.hub.direct.host";

    /**
     * The property to check for the <tt>port</tt> value to use if 0 is provided
     * as an argument to the constructor.
     */
    private static final String PORT_PROPERTY =
        "gov.cdc.phlissa.hub.direct.port";

    /**
     * The property to check for the <tt>trustStorePassword</tt> value to use if
     * not provided as an argument to the constructor.
     */
    private static final String TRUSTSTORE_PASSWORD_PROPERTY =
        "javax.net.ssl.trustStorePassword";

    /**
     * The property to check for the <tt>trustStore</tt> value to use if not
     * provided as an argument to the constructor.
     */
    private static final String TRUSTSTORE_PROPERTY =
        "javax.net.ssl.trustStore";

    /**
     * An object in which to store the details of connecting to the Direct REST
     * instance.
     */
    private HubDirectSenderDetails details = new HubDirectSenderDetails();

    /**
     * The domain name or IP address of the user account from which Direct
     * messages are being sent.
     */
    private String fromDomain = null;

	/**
	 * The password for the user sending a Direct message.
	 */
	private String fromPassword = null;

    /**
     * The name of the user from which Direct messages are being sent.
     */
    private String fromUser = null;

    /**
     * The domain name or IP address of the host on which is running the
     * Direct REST instance that will send the Direct messages.
     */
    private String host = null;

    /**
     * The port on <tt>host</tt> to which to connect to the Direct REST
     * instance running on <tt>host</tt>.
     * 
     * @see #host
     */
    private int port = DEFAULT_PORT;

    /**
     * The Session to use for JavaMail.
     */
    private Session session =
        Session.getDefaultInstance(new Properties());

    /**
     * <p>Default constructor.</p>
     * 
     * <p>Equivalent to calling the constructor that requires arguments,
     * but passing <tt>null</tt> for all String arguments and 0 for
     * <tt>port</tt>.</p>
     * 
     * @see #HubDirectSender(String, int, String, String, String, String)
     */
    public HubDirectSender() {

        this(null, 0, null, null, null, null);
    }

    /**
     * Constructor with arguments.
     * 
     * @param host The host name or IP address of the machine which hosts
     * the Direct REST instance. If not set (is <tt>null</tt> or only consists
     * of whitespace), defaults to the value of the Java system property
     * <tt>gov.cdc.phlissa.hub.direct.host</tt>. If this value also is
     * not set, throws an <tt>IllegalArgumentException</tt>.
     * @param port The port on <tt>host</tt> on which the Direct REST
     * instance is listening. If 0 is passed for this argument, it is set
     * to the value of the Java system property
     * <tt>gov.cdc.phlissa.hub.direct.port</tt>. If this value is also 0 or
     * is <tt>null</tt>, the argument is set to the value of the class constant
     * DEFAULT_PORT.
     * @param fromAddress The Direct email-style address of the user sending
     * the Direct message. If not set, defaults to the value of the Java
     * system property <tt>gov.cdc.phlissa.hub.direct.fromAddress</tt>. If this
     * value also is not set, throws an <tt>IllegalArgumentException</tt>.
     * If the value specified for <tt>fromAddress</tt> is not of the form
     * <em>user</em><tt>@</tt><em>domain</em>, throws an
     * <tt>IllegalArgumentException</tt>.
     * @param fromPassword The Direct REST password for the user named in
     * <tt>fromAddress</tt>. If not set, defaults to the value of the
     * Java system property <tt>gov.cdc.phlissa.hub.direct.fromPassword</tt>.
     * If this value also is not set, throws an
     * <tt>IllegalArgumentException</tt>.
     * @param trustStore The complete file path to the Java keystore file
     * which contains the certificates to be trusted as signing authorities
     * for the certificates used for the SSL connection to the Direct
     * REST instance. If not set, defaults to the value of the Java
     * system property <tt>javax.net.ssl.trustStore</tt>. If this value also
     * is not set, throws an <tt>IllegalArgumentException</tt>. If this
     * parameter <em>is</em> set, its value will be used to replace the
     * current value of the Java system property
     * <tt>javax.net.ssl.trustStore</tt>.
     * @param trustStorePassword The password for <tt>trustStore</tt>. If
     * not set, defaults to the value of the Java system property
     * <tt>javax.net.ssl.trustStorePassword</tt>. If this value also is
     * not set, throws an <tt>IllegalArgumentException</tt>. If this
     * parameter <em>is</em> set, its value will be used to replace the
     * current value of the Java system property
     * <tt>javax.net.ssl.trustStorePassword</tt>.
     * 
     * @throws IllegalArgumentException if any argument other than
     * <tt>port</tt> is not set and does not have a corresponding Java
     * system property set.
     * 
     * @see #DEFAULT_PORT
     */
    public HubDirectSender(
            String host,
            int port,
            String fromAddress,
            String fromPassword,
            String trustStore,
            String trustStorePassword) {

        if ((host == null) || (host.trim().equals(""))) {

            host = System.getProperty(HOST_PROPERTY);

            if ((host == null) || (host.trim().equals(""))) {

                throw new IllegalArgumentException(
                        "host argument is not set and system property "
                        + HOST_PROPERTY + " is not set.");
            }
        }
        this.host = host;

        if (port == 0) {

            port = Integer.getInteger(PORT_PROPERTY, DEFAULT_PORT);

            if (port == 0) {

                port = DEFAULT_PORT;
            }
        }
        this.port = port;

        if ((fromAddress == null) || (fromAddress.trim().equals(""))) {

            fromAddress = System.getProperty(FROM_ADDRESS_PROPERTY);

            if ((fromAddress == null) || (fromAddress.trim().equals(""))) {

                throw new IllegalArgumentException(
                        "fromAddress argument is not set and system property "
                        + FROM_ADDRESS_PROPERTY + " is not set.");
            }
        }

        StringTokenizer st = new StringTokenizer(fromAddress, "@");

        if (st.hasMoreTokens()) this.fromUser = st.nextToken();

        if (st.hasMoreTokens()) this.fromDomain = st.nextToken();

        if ((this.fromUser == null) ||
                (this.fromUser.trim().equals("")) ||
                (this.fromDomain == null) ||
                (this.fromDomain.trim().equals(""))) {

            throw new IllegalArgumentException(
                    "Invalid value for fromAddress; must be of the form "
                    + "user@domain.");
        }

        if ((fromPassword == null) || (fromPassword.trim().equals(""))) {

            fromPassword = System.getProperty(FROM_PASSWORD_PROPERTY);

            if ((fromPassword == null) || (fromPassword.trim().equals(""))) {

                throw new IllegalArgumentException(
                        "fromPassword argument is not set and system property "
                        + FROM_PASSWORD_PROPERTY + " is not set.");
            }
        }

        this.fromPassword = fromPassword;

        // Must ensure the trustStore property is set for use by SSL.
        if ((trustStore != null) && !trustStore.trim().equals("")) {

            System.setProperty(TRUSTSTORE_PROPERTY, trustStore);

        } else {

            trustStore = System.getProperty(TRUSTSTORE_PROPERTY);

            if ((trustStore == null) || (trustStore.trim().equals(""))) {

                throw new IllegalArgumentException(
                        "trustStore argument is not set and system property "
                        + TRUSTSTORE_PROPERTY + " is not set.");
            }
        }

        // Must ensure the trustStorePassword property is set for use by SSL.
        if ((trustStorePassword != null) && !trustStorePassword.trim().equals("")) {

            System.setProperty(TRUSTSTORE_PASSWORD_PROPERTY, trustStorePassword);

        } else {

            trustStorePassword = System.getProperty(TRUSTSTORE_PASSWORD_PROPERTY);

            if ((trustStorePassword == null) || (trustStorePassword.trim().equals(""))) {

                throw new IllegalArgumentException(
                        "trustStorePassword argument is not set and system property "
                        + TRUSTSTORE_PASSWORD_PROPERTY + " is not set.");
            }
        }
    }

    /**
     * @return any error message produced by the last call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * This return value will be the empty string if no error message was
     * produced. This return value is not reliable if this method is called
     * simultaneously with a call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}.
     */
    public String getErrorMessage() {

        return details.errorMessage;
    }

    /**
     * @return the Direct address from which messages will be sent with this
     * HubDirectSender object.
     */
    public String getFromAddress() {

        return fromUser + "@" + fromDomain;
    }

    /**
     * @return the <tt>host</tt> on which resides the Direct REST instance to
     * which this HubDirectSender object will connect and send the Direct
     * message for forwarding to the intended recipient.
     */
    public String getHost() {

        return host;
    }

    /**
     * @return the value of the <tt>Location</tt> header received from the
     * remote Direct instance during the last call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}. This
     * value will be the empty string if no <tt>Location</tt> header was
     * received. This value is unreliable if {@link #hasError()} returns
     * <tt>true</tt> or if this method is called simultaneously with a call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * 
     * @see #hasError()
     */
    public String getLocation() {

        return details.location;
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
     * @return the <tt>port</tt> on which the Direct REST instance is listening.
     */
    public int getPort() {

        return port;
    }

    /**
     * @return the response received from the Direct instance on the last call
     * to {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * This method will not return <tt>null</tt>; if an empty response is
     * received from the Direct instance, then this method returns the empty
     * string. This value does not include headers. This value is not reliable
     * if {@link #hasError()} returns <tt>true</tt>. This value is not
     * reliable if this method is called simultaneously with a call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * 
     * @see #hasError()
     */
    public String getResponse() {

        return details.response;
    }

    /**
     * @return the password for the trustStore used for SSL.
     */
    public String getTrustStorePassword() {

        return System.getProperty("javax.net.ssl.trustStorePassword");
    }

    /**
     * @return the path to the trustStore used for SSL.
     */
    public String getTrustStorePath() {

        return System.getProperty("javax.net.ssl.trustStore");
    }

    /**
     * @return <tt>true</tt> if the last call to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}
     * produced an error condition; else returns <tt>false</tt>. The return
     * value of this method is unreliable if called simultaneously with a call
     * to {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * If this method returns <tt>true</tt>, the return values of
     * {@link #getLocation()} and {@link #getResponse()} are unreliable.
     */
    public boolean hasError() {

        return details.hasError;
    }

    /**
     * This synchronized method calls
     * {@link #sendDirectMessage(String, String, String, boolean)} with a last
     * argument of <tt>false</tt>, and returns a copy of the details about the
     * completion of the call. No other call to this method and no
     * <em>subsequent</em> calls to 
     * {@link #sendDirectMessage(String, String, String, boolean)} or
     * {@link #sendDirectMessage(String, String, String, String, boolean)}
     * will corrupt the fields of the object returned by this method. In an
     * environment where multiple threads may call methods on the same
     * HubDirectSender object, it is necessary to handle synchronization so
     * that no two calls to
     * {@link #sendDirectMessage(String, String, String, String, boolean)}
     * occur simultaneously on the same object, unless details of the results
     * are ignored. Using {@link #sendDirectMessage(String, String, String)}
     * to the exclusion of
     * {@link #sendDirectMessage(String, String, String, boolean)} and
     * {@link #sendDirectMessage(String, String, String, String, boolean)}
     * will suffice to handle synchronization when it is needed. This method
     * will not throw any exceptions; if any error occurs, the details object
     * returned by this method will reflect it.
     * 
     * @param toAddress The Direct address to which to send the message.
     * @param subject The subject of the message.
     * @param message The message to send.
     * 
     * @return The details regarding the completion of the call to
     * {@link #sendDirectMessage(String, String, String, boolean)}.
     * This object will not be reused by this method or by subsequent calls to
     * {@link #sendDirectMessage(String, String, String, boolean)} or
     * {@link #sendDirectMessage(String, String, String, String, boolean)}.
     * 
     * @see #sendDirectMessage(String, String, String, boolean)
     * @see #sendDirectMessage(String, String, String, String, boolean)
     */
    public synchronized HubDirectSenderDetails sendDirectMessage(
            String toAddress,
            String subject,
            String message) {

        try {

        	sendDirectMessage(toAddress, subject, message, false);

        } catch (Throwable e) {

			// ignore
		}

        HubDirectSenderDetails detailsCopy =
            new HubDirectSenderDetails(details);

        return detailsCopy;
    }

    /**
     * <p>Connect to the Direct REST instance and authenticate the user
     * configured in the constructor for this class; send a message via the
     * Direct REST instance to the specified <tt>toAddress</tt>, with the
     * specified subject. Equivalent to calling
     * {@link #sendDirectMessage(String, String, String, String, boolean)},
     * where the first two arguments are taken from the <tt>toAddress</tt>,
     * with the first argument coming before the @ in the address and the
     * second argument coming after the @ in the address.</p>
     * 
     * <p>This method is not synchronized.</p>
     * 
     * @param toAddress The Direct address to which to send the message.
     * @param subject The subject of the message.
     * @param message The message to send.
     * @param throwException An indicator of whether to throw exceptions.
     * 
     * @see #sendDirectMessage(String, String, String)
     * @see #sendDirectMessage(String, String, String, String, boolean)
     */
    public void sendDirectMessage(
            String toAddress,
            String subject,
            String message,
            boolean throwException) throws Throwable {

        StringTokenizer st = new StringTokenizer(toAddress, "@");

        String toUser = null;

        String toDomain = null;

        if (st.hasMoreTokens()) {

            toUser = st.nextToken();
        }
        if (st.hasMoreTokens()) {

            toDomain = st.nextToken();
        }
        sendDirectMessage(toUser, toDomain, subject, message, throwException);
    }

    /**
     * <p>Connect to the Direct REST instance and authenticate the user;
     * send the specified message with the specified subject via the Direct
     * REST instance to the Direct address
     * <tt>toUser</tt>@<tt>toDomain</tt>. After this method is called, and
     * before it is called again on the same object, the occurrence of an error
     * may be discovered by calling {@link #hasError()}, any error message
     * may be obtained by calling {@link #getErrorMessage()}, any response (not
     * including headers) received from the Direct REST instance may be obtained
     * by calling {@link #getResponse()}, and the location header of the
     * response may be obtained by calling {@link #getLocation()}.</p>
     * 
     * <p>This method is not synchronized, so care should be taken to call
     * this method on any given HubDirectSender object only after making any
     * calls to {@link #hasError()}, {@link #getErrorMessage()},
     * {@link #getLocation()}, and {@link #getResponse()} to retrieve any
     * details desired regarding the results of the previous call to this
     * method on the same object. In a multi-threaded situation, if the details
     * of the completion of this method are important, the use of
     * {@link #sendDirectMessage(String, String, String)} is recommended.</p>
     * 
     * @param toUser The user part of the Direct address,
     * <em>user</em><tt>@</tt><em>domain</em>.
     * @param toDomain The domain part of the Direct address,
     * <em>user</em><tt>@</tt><em>domain</em>.
     * @param subject The subject for the message.
     * @param message The message to send via Direct REST.
     * @param throwException Pass <tt>true</tt> if the method is to throw an
     * exception when an error occurs; pass <tt>false</tt> if no exception is
     * desired to be thrown when an error occurs. The {@link #hasError()} and
     * {@link #getErrorMessage()} methods may still be called even if this
     * parameter is <tt>true</tt> and an exception is thrown.
     * 
     * @see #getErrorMessage()
     * @see #getLocation()
     * @see #getResponse()
     * @see #hasError()
     * @see #sendDirectMessage(String, String, String)
     * @see #sendDirectMessage(String, String, String, boolean)
     * @see #HubDirectSender(String, int, String, String, String, String)
     */
    public void sendDirectMessage(
            String toUser,
            String toDomain,
            String subject,
            String message,
            boolean throwException) throws Throwable {

        details.response = "";

        details.location = "";

        details.hasError = false;

        details.errorMessage = "";

        OutputStreamWriter connOut = null;

        BufferedReader rdr = null;

        try {

            Authenticator.setDefault(new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(
                    		fromUser, fromPassword.toCharArray());
                }
            });

            String wrappedMessage =
                wrapMessage(toUser + "@" + toDomain, subject, message);

            String urlSpec = "https://" + host + ":" + port + "/nhin/v1/"
            + toDomain + "/" + toUser + "/messages";

            URL url = new URL(urlSpec);

            HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "message/rfc822");

            connOut = new OutputStreamWriter(conn.getOutputStream());

            connOut.write(wrappedMessage);

            connOut.close();

            connOut = null;

            String locationHdr = conn.getHeaderField("Location");

            details.location = locationHdr;

            rdr = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String data;

            while ((data = rdr.readLine()) != null) {

                details.response = details.response + data;
            }
            rdr.close();

            rdr = null;

        } catch (Throwable e) {

            details.errorMessage = e.getLocalizedMessage();

            details.hasError = true;

            if (throwException) {

            	throw e;
//                throw new RuntimeException(e);
            }

        } finally {

            if (connOut != null) {

                try {

                    connOut.close();

                } catch (IOException e) {

                    // ignore
                }
            }

            if (rdr != null) {

                try {

                    rdr.close();

                } catch (IOException e) {

                    // ignore
                }
            }
        }
    }

    /**
     * Wraps a plain text message with headers required for Direct messages.
     * Effectively adds headers that turn a plain text message into an
     * rfc822-formatted message, as required by Direct.
     * 
     * @param toAddress The Direct address to which to send the message.
     * @param subject The subject for the message.
     * @param message The message to be sent via Direct.
     * 
     * @return The wrapped message, suitable for sending via Direct; i.e.,
     * formatted as an email message in rfc822 format.
     * 
     * @throws MessagingException if any Direct address is not of the proper
     * format or any problem occurs in setting the properties of the MimeMessage
     * object used to wrap the message.
     * 
     * @throws IOException if any error occurs in writing the wrapped message
     * from the MimeMessage to a ByteArrayOutputStream, or converting the output
     * stream to a String using the "US-ASCII" character set.
     */
    public String wrapMessage(
            String toAddress, String subject, String message)
    throws MessagingException, IOException {

        String wrappedMessage = null;

        MimeMessage mimemsg = new MimeMessage(session);

        InternetAddress fromAddress =
            new InternetAddress(fromUser + "@" + fromDomain, true);

        mimemsg.setFrom(fromAddress);

        InternetAddress toInternetAddress =
            new InternetAddress(toAddress, true);

        mimemsg.setRecipient(RecipientType.TO, toInternetAddress);

        mimemsg.setSubject(subject, "US-ASCII");

        mimemsg.setSentDate(new Date());

        // Make sure that only CRLF is used at ends of lines in message.
        message = message.replaceAll("\r\n", "\n");
        message = message.replaceAll("\r", "\n");
        message = message.replaceAll("\n", "\r\n");

        mimemsg.setContent(message, "text/plain");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        mimemsg.writeTo(baos);

        wrappedMessage = baos.toString("US-ASCII");

        return wrappedMessage;
    }

    /**
     * @return <tt>true</tt> if {@link #hasError()} returns <tt>false</tt> and
     * {@link #getResponse()} would return a value of length greater than 0
     * and consisting of more than whitespace.
     */
    private boolean hasNonEmptyResponse() {

        boolean hasNonEmptyResponse = true;

        if (hasError() || getResponse().trim().equals("")) {

            hasNonEmptyResponse = false;
        }
        return hasNonEmptyResponse;
    }

    /**
     * Data object to contain details regarding the completion of a call to
     * {@link HubDirectSender#sendDirectMessage(String, String, String, String, boolean)}.
     */
    public class HubDirectSenderDetails {

        /**
         * If an error has occurred, this field contains the error message.
         */
        public String errorMessage = "";

        /**
         * Indicates whether an error condition occurred. 
         */
        public boolean hasError = false;

        /**
         * If no error has occurred, this field contains the value of the
         * <tt>Location</tt> header received from the Direct REST instance.
         */
        public String location = "";

        /**
         * If no error has occurred, this field contains the response from the
         * Direct REST instance, not including any headers.
         */
        public String response = "";

        /**
         * Default constructor.
         */
        public HubDirectSenderDetails() {

            super();
        }

        /**
         * Copy constructor. Constructed object contains the same values as
         * those in the passed parameter, <tt>originalDetails</tt>.
         * 
         * @param originalDetails The HubDirectSenderDetails object to be
         * copied. May not be <tt>null</tt>.
         */
        public HubDirectSenderDetails(HubDirectSenderDetails originalDetails) {

            this.hasError = originalDetails.hasError;

            this.errorMessage = originalDetails.errorMessage;

            this.location = originalDetails.location;

            this.response = originalDetails.response;
        }
    }

    /**
     * <p>A main method that allows the sending of a message via Direct. Reads
     * the message from a file on disk, wraps it for sending via Direct, and
     * connects to the Direct REST instance specified in the arguments. The
     * message is sent from a user at a specified Direct address to a user at
     * another specified Direct address.</p>
     * 
     * <p>This method calls #sendDirectMessage(String, String, String, boolean)
     * with a final argument of <tt>false</tt>, and prints the output of the
     * call.</p>
     * 
     * @param args The arguments and a brief usage message may be listed by
     * executing the main method with no arguments, or with a first argument
     * of <tt>--help</tt> (in which case all other arguments are ignored).
     * 
     * <pre>Options:
     * --host &lt;host&gt;
     * --from &lt;fromuser&gt;@&lt;fromdomain&gt;
     * --to &lt;touser&gt;@&lt;todomain&gt;
     * --pass &lt;password&gt;
     * --port &lt;port&gt;
     * --subject &lt;subject&gt;
     * --file &lt;filename&gt;
     * --trust &lt;trustStore&gt;
     * --trustpass &lt;trustStorePassword&gt;</pre>
     * Send Direct message from <tt>fromuser</tt>@<tt>fromdomain</tt> to
     * <tt>touser</tt>@<tt>todomain</tt>. The <tt>password</tt> is the password
     * for <tt>fromuser</tt>. The <tt>port</tt> is the port on <tt>host</tt>
     * on which the Direct REST instance is listening. The <tt>host</tt>
     * defaults to <tt>fromdomain</tt> if not specified. The <tt>subject</tt>
     * is the subject line for the message. The <tt>filename</tt> is the name
     * of the file containing the message to be sent. The <tt>trustStore</tt>
     * is the trustStore with the trusted CA certficate for SSL. The
     * <tt>trustStorePassword</tt> is the password for the <tt>trustStore</tt>.
     * If the <tt>trustStore</tt> or <tt>trustStorePassword</tt> are
     * omitted, they will be read from system properties
     * <tt>javax.net.ssl.trustStore</tt> and
     * <tt>javax.net.ssl.trustStorePassword</tt>, respectively.
     * 
     * @throws IOException if an error occurs in reading the message from
     * the file specified by <tt>filename</tt>.
     * 
     * @see #sendDirectMessage(String, String, String, boolean)
     */
    public static void main(String[] args) throws IOException {

        if ((args.length == 0) ||
        		((args.length > 0) && args[0].equalsIgnoreCase("--help"))) {

            String helpMessage = "Options:\n"
                + "  --host <host>\n"
                + "  --from <fromuser>@<fromdomain>\n"
                + "  --to <touser>@<todomain>\n"
                + "  --pass <password>\n"
                + "  --port <port>\n"
                + "  --subject <subject>\n"
                + "  --file <filename>\n"
                + "  --trust <trustStore>\n"
                + "  --trustpass <trustStorePassword>\n"
                + "\nSend Direct message from fromuser at fromdomain to "
                + "touser at todomain. The password is the fromuser's password. "
                + "The port is the port on host to which to connect to Direct. "
                + "The host defaults to the fromdomain if not specified. "
                + "The subject is the subject line for the message. "
                + "The filename is the name of the file containing the message "
                + "to be sent. The trustStore is the trustStore with the "
                + "certficate for SSL. The trustStorePassword is the password for "
                + "the trustStore. If the trustStore or trustStorePassword are "
                + "omitted, they will be read from system properties "
                + "javax.net.ssl.trustStore and javax.net.ssl.trustStorePassword, "
                + "respectively.";

            System.out.println(helpMessage);

            System.exit(0);
        }

        String host = null;
        String fromAddress = null;
        String fromDomain = null;
        String password = null;
        String toAddress = null;
        String toPort = null;
        int port = 0;
        String subject = null;
        String messageFileName = null;
        String trustStore = null;
        String trustStorePassword = null;

        for (int i=0; i<args.length-1; i+=2) {

            if (args[i].equalsIgnoreCase("--host")) {

                host = args[i+1];

            } else if (args[i].equalsIgnoreCase("--from")) {

                fromAddress = args[i+1];

                StringTokenizer st = new StringTokenizer(fromAddress, "@");

                if (st.hasMoreTokens()) {

                    st.nextToken();
                }
                if (st.hasMoreTokens()) {

                    fromDomain = st.nextToken();
                }
                if (host == null) {

                    host = fromDomain;
                }
            } else if (args[i].equalsIgnoreCase("--to")) {

                toAddress = args[i+1];

            } else if (args[i].equalsIgnoreCase("--pass")) {

                password = args[i+1];
        
            } else if (args[i].equalsIgnoreCase("--port")) {

                toPort = args[i+1];

                port = Integer.parseInt(toPort);

            } else if (args[i].equalsIgnoreCase("--subject")) {

                subject = args[i+1];

            } else if (args[i].equalsIgnoreCase("--file")) {

                messageFileName = args[i+1];

            } else if (args[i].equalsIgnoreCase("--trust")) {

                trustStore = args[i+1];

            } else if (args[i].equalsIgnoreCase("--trustpass")) {

                trustStorePassword = args[i+1];
            }
        }
        String message = readFileAsString(messageFileName);

        HubDirectSender sndr = new HubDirectSender(
                host, port, fromAddress, password, trustStore, trustStorePassword);

        try {

        	sndr.sendDirectMessage(toAddress, subject, message, false);

        } catch (Throwable e) {

        	// ignore
		}

        if (sndr.hasError()) {

            System.out.println("ERROR: " + sndr.getErrorMessage());

        } else {

            System.out.println("Location: " + sndr.getLocation());

            if (sndr.hasNonEmptyResponse()) {

                System.out.println(sndr.getResponse());
            }
        }
    }

    /**
     * Reads the contents of a file and returns those contents as a single
     * string.
     * 
     * @param fileName The name of the file from which to read.
     * 
     * @return The contents of the file as a single string.
     * 
     * @throws IOException if an error occurs while attempting to read from
     * the specified file, or if the length of the file's contents exceeds
     * Integer.MAXVALUE.
     */
    public static String readFileAsString(String fileName) throws IOException {

        DataInputStream dis =
        	new DataInputStream(new FileInputStream(fileName));

        try {

            long len = new File(fileName).length();

            if (len > Integer.MAX_VALUE) {

                throw new IOException(
                        "Length of file " + fileName + " (" + len
                        + ") exceeds maximum integer size.");
            }
            byte[] bytes = new byte[(int) len];

            dis.readFully(bytes);

            return new String(bytes);

        } finally {

            dis.close();
        }
    }
}
