package com.https.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.nanohttpd.protocols.http.sockets.SecureServerSocketFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 (partially 1.1) server in Java
 * <p/>
 * <p/>
 * NanoHTTPD 
 *   -- version 1.25, Copyright &copy; 2001,2005-2012 
 *      Jarno Elonen (elonen@iki.fi, http://iki.fi/elonen/) and 
 *      Copyright &copy; 2010 Konstantinos Togias (info@ktogias.gr, http://ktogias.gr)
 * <p/>
 *   -- version 6 and above, Copyright &copy; 2012-
 *      Uplifted to Java6 by Paul Hawke (paul.hawke@gmail.com) and Micah Hainline.
 * <p/>
 * <p/>
 * <b>Features + limitations: </b>
 * <ul>
 * <p/>
 * <li>Only one Java file</li>
 * <li>Java 5 compatible</li>
 * <li>Released as open source, Modified BSD licence</li>
 * <li>No fixed config files, logging, authorization etc. (Implement yourself if you need them.)</li>
 * <li>Supports parameter parsing of GET and POST methods (+ rudimentary PUT support in 1.25)</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Supports file upload (since version 1.2, 2010)</li>
 * <li>Supports partial content (streaming)</li>
 * <li>Supports ETags</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Default code serves files and shows all HTTP parameters and headers</li>
 * <li>File server supports directory listing, index.html and index.htm</li>
 * <li>File server supports partial content (streaming)</li>
 * <li>File server supports ETags</li>
 * <li>File server does the 301 redirection trick for directories without '/'</li>
 * <li>File server supports simple skipping for files (continue download)</li>
 * <li>File server serves also very long files without memory overhead</li>
 * <li>Contains a built-in list of most common mime types</li>
 * <li>All header names are converted lowercase so they don't vary between browsers/clients</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/>
 * <b>How to use: </b>
 * <ul>
 * <p/>
 * <li>Subclass and implement serve() and embed to your own program</li>
 * <p/>
 * </ul>
 * <p/>
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
public abstract class NanoHTTPD {
    /*
     * Pseudo-Parameter to use to store the actual query string in the parameters map for later re-processing.
     */
    public static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    /**
     * Common mime types for dynamic content
     */
    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_APK = "application/vnd.android.package-archive";
    public static final String MIME_IMAGE= "image/png";
    public static final String MINE_M3U8 = "application/vnd.apple.mpegurl";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    private final String hostname;
    private final int myPort;
    private ServerSocket myServerSocket;
    private Thread myThread;
    private TempFileManagerFactory tempFileManagerFactory;
    private AsyncRunner asyncRunner;
//    private playbackService mPlaybackService;
    private SharedPreferences prefMgr;
    private Context mContext;
    /**
     * Constructs an HTTP server on given port.
     */
    
    public NanoHTTPD(Context c, int port) {
        this(c, null, port);
    }

    public NanoHTTPD(Context c, String hostname, int port) {
    	mContext = c;
    	prefMgr = PreferenceManager.getDefaultSharedPreferences(mContext); 
//    	this.mPlaybackService = playbackService.getInstance();
        this.hostname = hostname;
        this.myPort = port;
        this.tempFileManagerFactory = new DefaultTempFileManagerFactory();
        this.asyncRunner = new DefaultAsyncRunner();
    }

    /**
     * Starts the server
     * <p/>
     * Throws an IOException if the socket is already in use
     */
    public void start() throws IOException {
        //myServerSocket = new ServerSocket();
        //myServerSocket.bind((hostname != null) ? new InetSocketAddress(hostname, myPort) : new InetSocketAddress(myPort));
    	try{
    		myServerSocket = new ServerSocket(myPort,100);
    	//	Log.e("", "A1 ServerSocket creat"+myServerSocket.getLocalPort());
    	}
    	catch (Exception e)
    	{
    		myServerSocket = new ServerSocket(0,100);
//    		mPlaybackService.remote_port = myServerSocket.getLocalPort();
    		//myPort = myServerSocket.getLocalPort();
    	//	Log.e("", "A2 ServerSocket creat"+myServerSocket.getLocalPort());
//    		Editor ed = prefMgr.edit();
//			ed.putInt("remote_port",mPlaybackService.remote_port);
//			ed.commit();
    	}
//    	mPlaybackService.http_port = myServerSocket.getLocalPort();
//    	Editor ed1 = prefMgr.edit();
//		ed1.putInt("game_http_port",mPlaybackService.http_port);
//		ed1.commit();
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                    	Socket accept = myServerSocket.accept();
                    	
                        try
                    	{
                        	accept.setSoTimeout(30*60000);
                    	}
                    	catch (SocketException se)
        				{
        					//se.printStackTrace();
        				}
                        final Socket finalAccept = accept;
                        InputStream inputStream = finalAccept.getInputStream();
                        OutputStream outputStream = finalAccept.getOutputStream();
                        TempFileManager tempFileManager = tempFileManagerFactory.create();
                        final HTTPSession session = new HTTPSession(tempFileManager, inputStream, outputStream);
                        asyncRunner.exec(new Runnable() {
                            @Override
                            public void run() {
                                session.run();
                                if (finalAccept != null) {
                                    try {
                                        finalAccept.close();
                                    } catch (IOException ignored) {
                                    	ignored.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                    catch(SocketException e2)
                    {
                    	
                    	e2.printStackTrace();
                        break;
                    }
                    catch (IOException e) {
                    	e.printStackTrace();
                        break;
                    }
                } while (!myServerSocket.isClosed());
            }
        });
        //myThread.setDaemon(true);
        myThread.setName("AllCast Main Listener");
        myThread.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        try {
            myServerSocket.close();
        //    Log.e("", "A2 ServerSocket close");
            myThread.join();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setTempFileManagerFactory(TempFileManagerFactory tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }

    public void setAsyncRunner(AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }

    /**
     * Override this to customize the server.
     * <p/>
     * <p/>
     * (By default, this delegates to serveFile() and allows directory listing.)
     *
     * @param uri    Percent-decoded URI without parameters, for example "/index.cgi"
     * @param method "GET", "POST" etc.
     * @param parms  Parsed, percent decoded parameters from URI and, in case of POST, data.
     * @param header Header entries, percent decoded
     * @return HTTP response, see class Response for details
     */
    public abstract Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms,
                                   Map<String, String> files);

    /**
     * Decodes the percent encoding scheme. <br/>
     * For example: "an+example%20string" -> "an example string"
     */
    protected String decodePercent(String str) throws InterruptedException {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '+':
                        sb.append(' ');
                        break;
                    case '%':
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new InterruptedException();
        }
    }

    protected Map<String, List<String>> decodeParameters(Map<String, String> parms) {
        return this.decodeParameters(parms.get(QUERY_STRING_PARAMETER));
    }

    protected Map<String, List<String>> decodeParameters(String queryString) {
        Map<String, List<String>> parms = new HashMap<String, List<String>>();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                try {
                    String propertyName = (sep >= 0) ? decodePercent(e.substring(0, sep)).trim() : decodePercent(e).trim();
                    if (!parms.containsKey(propertyName)) {
                        parms.put(propertyName, new ArrayList<String>());
                    }
                    String propertyValue = (sep >= 0) ? decodePercent(e.substring(sep + 1)) : null;
                    if (propertyValue != null) {
                        parms.get(propertyName).add(propertyValue);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return parms;
    }

    public enum Method {
        GET, PUT, POST, DELETE;

        static Method lookup(String method) {
            for (Method m : Method.values()) {
                if (m.toString().equalsIgnoreCase(method)) {
                    return m;
                }
            }
            return null;
        }
    }

    public interface AsyncRunner {
        void exec(Runnable code);
    }

    public interface TempFileManagerFactory {
        TempFileManager create();
    }

    public interface TempFileManager {
        TempFile createTempFile() throws Exception;

        void clear();
    }

    public interface TempFile {
        OutputStream open() throws Exception;

        void delete() throws Exception;

        String getName();
    }

    /**
     * HTTP response. Return one of these from serve().
     */
    public static class Response {
        /**
         * HTTP status code after processing, e.g. "200 OK", HTTP_OK
         */
        public Status status;
        /**
         * MIME type of content, e.g. "text/html"
         */
        public String mimeType;
        /**
         * Data of the response, may be null.
         */
        public InputStream data;
        /**
         * Headers for the HTTP response. Use addHeader() to add lines.
         */
        public Map<String, String> header = new HashMap<String, String>();

        public Map<String,String> req;
        
        /**
         * Default constructor: response = HTTP_OK, mime = MIME_HTML and your supplied message
         */
        public Response(String msg) {
            this(Status.OK, MIME_HTML, msg);
        }
        
        /**
         * Default constructor: response = HTTP_OK, mime = MIME_HTML and your supplied message
         */
        public Response(Status status, String mimeType) {
        	this.status = status;
            this.mimeType = mimeType;
            this.data = null;
        }
        
        /**
         * Basic constructor.
         */
        public Response(Status status, String mimeType, InputStream data) {
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
        }
        
        /**
         * Advance constructor.
         */
        public Response(Status status, String mimeType, InputStream data, Map<String, String>  h) {
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
            this.req = h;
        }
        /**
         * Convenience method that makes an InputStream out of given text.
         */
        public Response(Status status, String mimeType, String txt) {
            this.status = status;
            this.mimeType = mimeType;
            try {
                this.data = new ByteArrayInputStream(txt.getBytes("UTF-8"));
            } catch (java.io.UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }

        public static void error(OutputStream outputStream, Status error, String message) {
            new Response(error, MIME_PLAINTEXT, message).send(outputStream);
        }

        /**
         * Adds given line to the header.
         */
        public void addHeader(String name, String value) {
            header.put(name, value);
        }

        /**
         * Sends given response to the socket.
         */
        private void send(OutputStream outputStream) {
            String mime = mimeType;
            SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                if (status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                PrintWriter pw = new PrintWriter(outputStream);
                pw.print("HTTP/1.1 " + status.getDescription() + " \r\n");

              
                if (header == null || header.get("Date") == null) {
                    pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");
                }
                
                boolean hasRange = false;
                int start = 0;
                if (header != null) {
                    for (String key : header.keySet()) {
                        String value = header.get(key);      
                        pw.print(key + ": " + value + "\r\n");
                    }
                }
                
                if (req != null)
                {
                	for (String key : req.keySet()) 
                	{
                        String value = req.get(key);      
	                	if (key.toLowerCase().equals("range"))
	                    {
	                    	String r = value;
	                    	int s =r.indexOf("-");
	                    	if (s > 0)
	                    	{
	                    		r = r.substring(0, s);
	                    		try
	                    		{
	                    			start = Integer.valueOf(r);
	                    		}
	                    		catch (NumberFormatException nfe)
	                    		{
	                    			start = 0;
	                    		}
	                    	}
	                    	break;
	                    }
                	}
                }
                if (mime != null) {
                    pw.print("Content-Type: " + mime + "\r\n");
                    if (data != null)
                    {
                    	pw.print("Content-Length: " + (data.available() - start ) + "\r\n");
                    	pw.print("Accept-Ranges: bytes\r\n");
                    }                    
                    if (start > 0 && data != null)
                    {
                    	pw.print("Content-Ranges: bytes" + start + "-" + data.available() +"/"+ data.available() + "\r\n");
                    }
                }
                
                pw.print("\r\n");
                pw.flush();

                if (data != null) {
                	if (start > 0)
                		data.skip(start);
                    int pending = data.available(); // This is to support partial sends, see serveFile()
                    int BUFFER_SIZE = 16 * 1024;
                    byte[] buff = new byte[BUFFER_SIZE];
                    while (pending > 0) {
                        int read = data.read(buff, 0, ((pending > BUFFER_SIZE) ? BUFFER_SIZE : pending));
                        if (read <= 0) {
                            break;
                        }
                        outputStream.write(buff, 0, read);
                        pending -= read;
                    }
                }
                outputStream.flush();
                outputStream.close();
                if (data != null)
                    data.close();
            } catch (IOException ioe) {
                // Couldn't write? No can do.
            }
        }

        /**
         * Some HTTP response status codes
         */
        public enum Status {
            OK(200, "OK"), CREATED(201, "Created"), NO_CONTENT(204, "No Content"), PARTIAL_CONTENT(206, "Partial Content"), REDIRECT(301,
                    "Moved Permanently"), NOT_MODIFIED(304, "Not Modified"), BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401,
                    "Unauthorized"), FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not Found"), RANGE_NOT_SATISFIABLE(416,
                    "Requested Range Not Satisfiable"), INTERNAL_ERROR(500, "Internal Server Error");
            private int requestStatus;
            private String descr;

            Status(int requestStatus, String descr) {
                this.requestStatus = requestStatus;
                this.descr = descr;
            }

            public int getRequestStatus() {
                return this.requestStatus;
            }

            public String getDescription() {
                return "" + this.requestStatus + " " + descr;
            }
        }
    }

    public static class DefaultTempFile implements TempFile {
        private File file;
        private OutputStream fstream;

        public DefaultTempFile(String tempdir) throws IOException {
            file = File.createTempFile("NanoHTTPD-", "", new File(tempdir));
            fstream = new FileOutputStream(file);
        }

        @Override
        public OutputStream open() throws Exception {
            return fstream;
        }

        @Override
        public void delete() throws Exception {
            file.delete();
        }

        @Override
        public String getName() {
            return file.getAbsolutePath();
        }
    }

    /**
     * Handles one session, i.e. parses the HTTP request and returns the response.
     */
    protected class HTTPSession implements Runnable {
        public static final int BUFSIZE = 8192;
        private final TempFileManager tempFileManager;
        private InputStream inputStream;
        private OutputStream outputStream;

        public HTTPSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
            this.tempFileManager = tempFileManager;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            try {
                if (inputStream == null) {
                    return;
                }

                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Apache's default header limit is 8KB.
                // Do NOT assume that a single read will get the entire header at once!
                byte[] buf = new byte[BUFSIZE];
                int splitbyte = 0;
                int rlen = 0;
                {
                    int read = inputStream.read(buf, 0, BUFSIZE);
                    while (read > 0) {
                        rlen += read;
                        splitbyte = findHeaderEnd(buf, rlen);
                        if (splitbyte > 0)
                            break;
                        read = inputStream.read(buf, rlen, BUFSIZE - rlen);
                    }
                }

                // Create a BufferedReader for parsing the header.
                BufferedReader hin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, rlen)));
                Map<String, String> pre = new HashMap<String, String>();
                Map<String, String> parms = new HashMap<String, String>();
                Map<String, String> header = new HashMap<String, String>();
                Map<String, String> files = new HashMap<String, String>();

                // Decode the header into parms and header java properties
                decodeHeader(hin, pre, parms, header);
                Method method = Method.lookup(pre.get("method"));
                if (method == null) {
                    Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
                    throw new InterruptedException();
                }
                String uri = pre.get("uri");
                long size = extractContentLength(header);

                // Write the part of body already read to ByteArrayOutputStream f
                RandomAccessFile f = getTmpBucket();
                if (splitbyte < rlen) {
                    f.write(buf, splitbyte, rlen - splitbyte);
                }

                // While Firefox sends on the first read all the data fitting
                // our buffer, Chrome and Opera send only the headers even if
                // there is data for the body. We do some magic here to find
                // out whether we have already consumed part of body, if we
                // have reached the end of the data to be sent or we should
                // expect the first byte of the body at the next read.
                if (splitbyte < rlen) {
                    size -= rlen - splitbyte + 1;
                } else if (splitbyte == 0 || size == 0x7FFFFFFFFFFFFFFFl) {
                    size = 0;
                }

                // Now read all the body and write it to f
                buf = new byte[512];
                while (rlen >= 0 && size > 0) {
                    rlen = inputStream.read(buf, 0, 512);
                    size -= rlen;
                    if (rlen > 0) {
                        f.write(buf, 0, rlen);
                    }
                }

                // Get the raw body as a byte []
                ByteBuffer fbuf = f.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
                f.seek(0);

                // Create a BufferedReader for easily reading it as string.
                InputStream bin = new FileInputStream(f.getFD());
                BufferedReader in = new BufferedReader(new InputStreamReader(bin));
                //MiniLog.i("httpd", ""+method);
                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (Method.POST.equals(method)) {
                    String contentType = "";
                    String contentTypeHeader = header.get("content-type");

                    StringTokenizer st = null;
                    if (contentTypeHeader != null) {
                        st = new StringTokenizer(contentTypeHeader, ",; ");
                        if (st.hasMoreTokens()) {
                            contentType = st.nextToken();
                        }
                    }

                    if ("multipart/form-data".equalsIgnoreCase(contentType)) {
                        // Handle multipart/form-data
                        if (!st.hasMoreTokens()) {
                            Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                            throw new InterruptedException();
                        }

                        String boundaryStartString = "boundary=";
                        int boundaryContentStart = contentTypeHeader.indexOf(boundaryStartString) + boundaryStartString.length();
                        String boundary = contentTypeHeader.substring(boundaryContentStart, contentTypeHeader.length());
                        if (boundary.startsWith("\"") && boundary.startsWith("\"")) {
                            boundary = boundary.substring(1, boundary.length() - 1);
                        }

                        decodeMultipartData(boundary, fbuf, in, parms, files);
                    } else {
                        // Handle application/x-www-form-urlencoded
                        String postLine = "";
                        char pbuf[] = new char[512];
                        int read = in.read(pbuf);
                        while (read >= 0 && !postLine.endsWith("\r\n")) {
                            postLine += String.valueOf(pbuf, 0, read);
                            read = in.read(pbuf);
                            	Log.i("httpd", postLine);
                        }
                        postLine = postLine.trim();
                        decodeParms(postLine, parms);
                    }
                }

                if (Method.PUT.equals(method))
                    files.put("content", saveTmpFile(fbuf, 0, fbuf.limit()));

                // Ok, now do the serve()
                Response r = serve(uri, method, header, parms, files);
                if (r == null) {
                    Response.error(outputStream, Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                    throw new InterruptedException();
                } else {
                    r.send(outputStream);
                }

                in.close();
                inputStream.close();
            } catch (IOException ioe) {
                try {
                    Response.error(outputStream, Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                    throw new InterruptedException();
                } catch (Throwable ignored) {
                }
            } catch (InterruptedException ie) {
                // Thrown by sendError, ignore and exit the thread.
            } finally {
                tempFileManager.clear();
            }
        }

        private long extractContentLength(Map<String, String> header) {
            long size = 0x7FFFFFFFFFFFFFFFl;
            String contentLength = header.get("content-length");
            if (contentLength != null) {
                try {
                    size = Integer.parseInt(contentLength);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
            return size;
        }

        /**
         * Decodes the sent headers and loads the data into Key/value pairs
         */
        private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, String> parms, Map<String, String> header)
                throws InterruptedException {
            try {
                // Read the request line
                String inLine = in.readLine();
                if (inLine == null) {
                    return;
                }

                StringTokenizer st = new StringTokenizer(inLine);
                if (!st.hasMoreTokens()) {
                    Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                    throw new InterruptedException();
                }

                pre.put("method", st.nextToken());

                if (!st.hasMoreTokens()) {
                    Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                    throw new InterruptedException();
                }

                String uri = st.nextToken();

                // Decode parameters from the URI
                int qmi = uri.indexOf('?');
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms);
                    uri = decodePercent(uri.substring(0, qmi));
                } else {
                    uri = decodePercent(uri);
                }

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names lowercase since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                    String line = in.readLine();
                    while (line != null && line.trim().length() > 0) {
                        int p = line.indexOf(':');
                        if (p >= 0)
                            header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                        line = in.readLine();
                    }
                }

                pre.put("uri", uri);
            } catch (IOException ioe) {
                Response.error(outputStream, Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                throw new InterruptedException();
            }
        }

        /**
         * Decodes the Multipart Body data and put it into Key/Value pairs.
         */
        private void decodeMultipartData(String boundary, ByteBuffer fbuf, BufferedReader in, Map<String, String> parms,
                                         Map<String, String> files) throws InterruptedException {
            try {
                int[] bpositions = getBoundaryPositions(fbuf, boundary.getBytes());
                int boundarycount = 1;
                String mpline = in.readLine();
                while (mpline != null) {
                    if (!mpline.contains(boundary)) {
                        Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                        throw new InterruptedException();
                    }
                    boundarycount++;
                    Map<String, String> item = new HashMap<String, String>();
                    mpline = in.readLine();
                    while (mpline != null && mpline.trim().length() > 0) {
                        int p = mpline.indexOf(':');
                        if (p != -1) {
                            item.put(mpline.substring(0, p).trim().toLowerCase(), mpline.substring(p + 1).trim());
                        }
                        mpline = in.readLine();
                    }
                    if (mpline != null) {
                        String contentDisposition = item.get("content-disposition");
                        if (contentDisposition == null) {
                            Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                            throw new InterruptedException();
                        }
                        StringTokenizer st = new StringTokenizer(contentDisposition, "; ");
                        Map<String, String> disposition = new HashMap<String, String>();
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            int p = token.indexOf('=');
                            if (p != -1) {
                                disposition.put(token.substring(0, p).trim().toLowerCase(), token.substring(p + 1).trim());
                            }
                        }
                        String pname = disposition.get("name");
                        pname = pname.substring(1, pname.length() - 1);

                        String value = "";
                        if (item.get("content-type") == null) {
                            while (mpline != null && !mpline.contains(boundary)) {
                                mpline = in.readLine();
                                if (mpline != null) {
                                    int d = mpline.indexOf(boundary);
                                    if (d == -1) {
                                        value += mpline;
                                    } else {
                                        value += mpline.substring(0, d - 2);
                                    }
                                }
                            }
                        } else {
                            if (boundarycount > bpositions.length) {
                                Response.error(outputStream, Response.Status.INTERNAL_ERROR, "Error processing request");
                                throw new InterruptedException();
                            }
                            int offset = stripMultipartHeaders(fbuf, bpositions[boundarycount - 2]);
                            String path = saveTmpFile(fbuf, offset, bpositions[boundarycount - 1] - offset - 4);
                            files.put(pname, path);
                            value = disposition.get("filename");
                            value = value.substring(1, value.length() - 1);
                            do {
                                mpline = in.readLine();
                            } while (mpline != null && !mpline.contains(boundary));
                        }
                        parms.put(pname, value);
                    }
                }
            } catch (IOException ioe) {
                Response.error(outputStream, Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                throw new InterruptedException();
            }
        }

        /**
         * Find byte index separating header from body. It must be the last byte of the first two sequential new lines.
         */
        private int findHeaderEnd(final byte[] buf, int rlen) {
            int splitbyte = 0;
            while (splitbyte + 3 < rlen) {
                if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                    return splitbyte + 4;
                }
                splitbyte++;
            }
            return 0;
        }

        /**
         * Find the byte positions where multipart boundaries start.
         */
        public int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
            int matchcount = 0;
            int matchbyte = -1;
            List<Integer> matchbytes = new ArrayList<Integer>();
            for (int i=0; i<b.limit(); i++) {
                if (b.get(i) == boundary[matchcount]) {
                    if (matchcount == 0)
                        matchbyte = i;
                    matchcount++;
                    if (matchcount == boundary.length) {
                        matchbytes.add(matchbyte);
                        matchcount = 0;
                        matchbyte = -1;
                    }
                } else {
                    i -= matchcount;
                    matchcount = 0;
                    matchbyte = -1;
                }
            }
            int[] ret = new int[matchbytes.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = matchbytes.get(i);
            }
            return ret;
        }

        /**
         * Retrieves the content of a sent file and saves it to a temporary file. The full path to the saved file is returned.
         */
        private String saveTmpFile(ByteBuffer  b, int offset, int len) {
            String path = "";
            if (len > 0) {
                try {
                    TempFile tempFile = tempFileManager.createTempFile();
                    ByteBuffer src = b.duplicate();
                    FileChannel dest = new FileOutputStream(tempFile.getName()).getChannel();
                    src.position(offset).limit(offset + len);
                    dest.write(src.slice());
                    path = tempFile.getName();
                } catch (Exception e) { // Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
            return path;
        }

        private RandomAccessFile getTmpBucket() throws IOException {
            try {
                TempFile tempFile = tempFileManager.createTempFile();
                return new RandomAccessFile(tempFile.getName(), "rw");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            return null;
        }

        /**
         * It returns the offset separating multipart file headers from the file's data.
         */
        private int stripMultipartHeaders(ByteBuffer b, int offset) {
            int i;
            for (i=offset; i<b.limit(); i++) {
                if (b.get(i) == '\r' && b.get(++i) == '\n' && b.get(++i) == '\r' && b.get(++i) == '\n') {
                    break;
                }
            }
            return i + 1;
        }

        /**
         * Decodes parameters in percent-encoded URI-format ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and
         * adds them to given Map. NOTE: this doesn't support multiple identical keys due to the simplicity of Map.
         */
        private void decodeParms(String parms, Map<String, String> p) throws InterruptedException {
            if (parms == null) {
                p.put(QUERY_STRING_PARAMETER, "");
                return;
            }

            p.put(QUERY_STRING_PARAMETER, parms);
            StringTokenizer st = new StringTokenizer(parms, "&");
            try {
                while (st.hasMoreTokens()) {
                    String e = st.nextToken();
                    int sep = e.indexOf('=');
                    if (sep >= 0) {
                        p.put(decodePercent(e.substring(0, sep)).trim(),
                                decodePercent(e.substring(sep + 1)));
                    } else {
                        p.put(decodePercent(e).trim(), "");
                    }
                }
            } catch (InterruptedException e) {
                Response.error(outputStream, Response.Status.BAD_REQUEST, "BAD REQUEST: Bad percent-encoding.");
            }
        }
    }

    private class DefaultTempFileManagerFactory implements TempFileManagerFactory {
        @Override
        public TempFileManager create() {
            return new DefaultTempFileManager();
        }
    }

    public static class DefaultTempFileManager implements TempFileManager {
        private final String tmpdir;
        private final List<TempFile> tempFiles;

        public DefaultTempFileManager() {
            tmpdir = System.getProperty("java.io.tmpdir");
//        	tmpdir = playbackService.getInstance().packagedir;//mainConst.mCachePath;
            tempFiles = new ArrayList<TempFile>();
        }

        @Override
        public TempFile createTempFile() throws Exception {
            DefaultTempFile tempFile = new DefaultTempFile(tmpdir);
            tempFiles.add(tempFile);
            return tempFile;
        }

        @Override
        public void clear() {
            for (TempFile file : tempFiles) {
                try {
                    file.delete();
                } catch (Exception ignored) {
                }
            }
            tempFiles.clear();
        }
    }

    private class DefaultAsyncRunner implements AsyncRunner {
        private long requestCount;
        @Override
        public void exec(Runnable code) {
            ++requestCount;
            Thread t = new Thread(code);
            t.setDaemon(true);
            t.setName("NanoHttpd Request Processor (#" + requestCount + ")");
            t.start();
        }
    }
    public void makeSecure(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
        new SecureServerSocketFactory(sslServerSocketFactory, sslProtocols);
    }
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManager[] keyManagers) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedKeyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and a
     * loaded KeyManagerFactory. These objects must properly loaded/initialized
     * by the caller.
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManagerFactory loadedKeyFactory) throws IOException {
        try {
            return makeSSLSocketFactory(loadedKeyStore, loadedKeyFactory.getKeyManagers());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
