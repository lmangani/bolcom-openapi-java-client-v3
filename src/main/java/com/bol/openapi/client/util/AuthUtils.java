package com.bol.openapi.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for applying HMAC signatures.
 */
public final class AuthUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("GMT"));
    private static final String HEADER_CONTENT_MD5 = "Content-MD5";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_DATE = "Date";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_OAI = "X-OpenAPI";
    private static final String HEADER_OAI_AUTH = "X-OpenAPI-Authorization";
    private static final String HEADER_OAI_DATE = "X-OpenAPI-Date";
    private static final String HEADER_OAI_SESSION_ID = "X-OpenAPI-Session-ID";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final Logger LOG = LoggerFactory.getLogger(AuthUtils.class);

    private AuthUtils() {
    }

    /**
     * Handles the request, by adding the required headers for an OpenAPI-RS call.
     * 
     * @param request The HTTP web request.
     * @param accessKeyId The access key ID.
     * @param secretAccessKey The secret access key.
     */
    public static void handleRequest(final HttpRequest request, final String accessKeyId, final String secretAccessKey) {
        handleRequest(request, accessKeyId, secretAccessKey, null, null, null);
    }

    /**
     * Handles the request, by adding the required headers for an OpenAPI-RS call.
     * 
     * @param request The HTTP web request.
     * @param accessKeyId The access key ID.
     * @param secretAccessKey The secret access key.
     * @param sessionId The session id.
     */
    public static void handleRequest(final HttpRequest request, final String accessKeyId, final String secretAccessKey, final String sessionId) {
        handleRequest(request, accessKeyId, secretAccessKey, sessionId, null, null);
    }

    /**
     * Handles the request, by adding the required headers for an OpenAPI-RS call.
     * 
     * @param request The HTTP web request.
     * @param accessKeyId The access key ID.
     * @param secretAccessKey The secret access key.
     * @param sessionId The session id.
     * @param httpParameters The HTTP parameters.
     */
    public static void handleRequest(final HttpRequest request, final String accessKeyId, final String secretAccessKey, final String sessionId, final List<NameValuePair> httpParameters) {
        handleRequest(request, accessKeyId, secretAccessKey, sessionId, null, httpParameters);
    }

    /**
     * Handles the request, by adding the required headers for an OpenAPI-RS call.
     * 
     * @param request The HTTP web request.
     * @param accessKeyId The access key ID.
     * @param secretAccessKey The secret access key.
     * @param sessionId The session id.
     * @param body The request body to hash.
     * @param httpParameters The HTTP parameters.
     */
    public static void handleRequest(final HttpRequest request, final String accessKeyId, final String secretAccessKey, final String sessionId, final String body, final List<NameValuePair> httpParameters) {
        // Session-ID (optional)
        if (sessionId != null) {
            request.addHeader(HEADER_OAI_SESSION_ID, sessionId);
        }

        // Content-Type
        if (request.getRequestLine() != null && request.getRequestLine().getMethod() != null && request.getRequestLine().getMethod().equalsIgnoreCase("POST")) {
            request.addHeader(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
        }

        // Accept
        request.addHeader(HEADER_ACCEPT, "application/xml");

        // Content-MD5 (optional)
        if (body != null) {
            byte[] entityAsBytes = null;

            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(body);
                entityAsBytes = baos.toByteArray();
            } catch (final IOException e) {
                LOG.error("An I/O error occurs while writing stream header", e);
            }

            if (entityAsBytes != null) {
                request.addHeader(HEADER_CONTENT_MD5, StringUtils.trim(Base64.encodeBase64String(DigestUtils.md5(entityAsBytes))));
            }
        }

        // Date
        final DateTime dt = new DateTime();
        request.addHeader(HEADER_DATE, DATE_TIME_FORMATTER.print(dt));

        // Authorization
        try {
            final String stringToSign = AuthUtils.createStringToSign(request, httpParameters);
            request.addHeader(HEADER_OAI_AUTH, accessKeyId + ":" + calculateHMAC256(stringToSign, secretAccessKey));
        } catch (final NoSuchAlgorithmException e) {
            LOG.error("The specified algorithm is not available in the default provider package or any of the other provider packages that were searched", e);
        } catch (final InvalidKeyException e) {
            LOG.error("The given key is inappropriate for initializing this MAC", e);
        } catch (final URISyntaxException e) {
            LOG.error("The given string violates RFC 2396", e);
        } catch (final UnsupportedEncodingException e) {
            LOG.error("UTF-8 encoding is not supported on this platform", e);
        }
    }

    /**
     * Creates the string to needs to be signed based on the required data embedded within the HTTP request.
     *
     * @param request The HTTP web request.
     * @param httpParameters The HTTP parameters.
     *
     * @return The string that needs to be signed for the current request.
     *
     * @throws URISyntaxException When the URI contains a syntax error.
     * @throws UnsupportedEncodingException When the given encoding is not supported.
     */
    public static String createStringToSign(final HttpRequest request, final List<NameValuePair> httpParameters) throws URISyntaxException, UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder(256);

        // HTTP method
        if (request.getRequestLine() != null && request.getRequestLine().getMethod() != null) {
            sb.append(request.getRequestLine().getMethod());
        }
        sb.append("\n");

        // Content-MD5
        if (request.getFirstHeader(HEADER_CONTENT_MD5) != null) {
            sb.append(request.getFirstHeader(HEADER_CONTENT_MD5).getValue());
        }
        sb.append("\n");

        // Content-Type
        if (request.getFirstHeader(HEADER_CONTENT_TYPE) != null) {
            sb.append(request.getFirstHeader(HEADER_CONTENT_TYPE).getValue());
        }
        sb.append("\n");

        // Date
        if (request.getFirstHeader(HEADER_OAI_DATE) != null) {
            sb.append(request.getFirstHeader(HEADER_OAI_DATE).getValue());
        } else if (request.getFirstHeader(HEADER_DATE) != null) {
            sb.append(request.getFirstHeader(HEADER_DATE).getValue());
        }
        sb.append("\n");

        // Canonicalized OpenAPI headers
        final Map<String, String> sortedHeaderMap = new TreeMap<String, String>();
        final Header[] headerNames = request.getAllHeaders();

        // Sort the OpenAPI headers alphabetically, in lower case
        for (final Header header : headerNames) {
            if (header.getName().toLowerCase(Locale.US).startsWith(HEADER_OAI.toLowerCase(Locale.US)) && !header.getName().equalsIgnoreCase(HEADER_OAI_AUTH)) {
                sortedHeaderMap.put(header.getName(), header.getValue());
            }
        }

        // Add the sorted OpenAPI headers with their value
        for (final Iterator<Entry<String, String>> it = sortedHeaderMap.entrySet().iterator(); it.hasNext();) {
            final Entry<String, String> entry = it.next();

            sb.append(entry.getKey().toLowerCase(Locale.US));
            sb.append(":");
            if (entry.getValue() != null) {
                sb.append(entry.getValue());
            }
            sb.append("\n");
        }

        // Canonicalized resource (exclude query parameters)
        if (request.getRequestLine() != null && request.getRequestLine().getUri() != null) {
            final URI uri = new URI(request.getRequestLine().getUri());
            sb.append(uri.getPath());
        } else {
            sb.append('/');
        }
        sb.append("\n");

        // Canonicalized query and post parameters
        if (httpParameters != null) {
            final Map<String, List<String>> sortedParamMap = new TreeMap<String, List<String>>();
            for (final NameValuePair nameValuePair : httpParameters) {
                if (sortedParamMap.containsKey(nameValuePair.getName())) {
                    final List<String> values = sortedParamMap.get(nameValuePair.getName());
                    values.add(nameValuePair.getValue());
                } else {
                    final List<String> values = new ArrayList<String>();
                    values.add(nameValuePair.getValue());
                    sortedParamMap.put(nameValuePair.getName(), values);
                }
            }

            for (final Iterator<Entry<String, List<String>>> it = sortedParamMap.entrySet().iterator(); it.hasNext();) {
                final Entry<String, List<String>> entry = it.next();

                sb.append('&');
                sb.append(URLDecoder.decode(entry.getKey(), "UTF-8"));
                sb.append('=');

                if (entry.getValue() != null) {
                    final List<String> values = entry.getValue();

                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i) != null) {
                            sb.append(URLDecoder.decode(values.get(i), "UTF-8"));

                            if (i < values.size() - 1) {
                                sb.append(',');
                            }
                        }
                    }
                }

                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Calculates the HMAC256 string based on the given string and secret access key.
     *
     * @param stringToSign The string to sign.
     * @param secretAccessKey The secret access key to sign the string with.
     *
     * @return The calculated HMAC256 string.
     *
     * @throws NoSuchAlgorithmException When the algorithm doesn't exist (HmacSHA256).
     * @throws InvalidKeyException When the key is invalid.
     */
    public static String calculateHMAC256(final String stringToSign, final String secretAccessKey) throws NoSuchAlgorithmException, InvalidKeyException {
        // Get an hmac_sha1 key from the raw key bytes
        final SecretKeySpec signingKey = new SecretKeySpec(secretAccessKey.getBytes(), HMAC_SHA256_ALGORITHM);

        // Get an hmac_sha256 Mac instance and initialize with the signing key
        final Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);

        // Compute the hmac on input data bytes
        final byte[] rawHmac = mac.doFinal(stringToSign.getBytes());

        // Base64-encode the hmac
        return Base64.encodeBase64String(rawHmac).trim();
    }
}
