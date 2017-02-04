package com.cs371group2.admin;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;

/**
 * Object for verifying a string firebase token and breaking it down into a FirebaseToken
 *
 * Created on 2017-02-04.
 */
public class FirebaseTokenVerifier {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FirebaseTokenVerifier.class.getName());

    private static final String PUBLIC_KEYS_URI = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    private static final String EMAIL_CLAIM = "email";

    private static final String EMAIL_VERIFIED_CLAIM = "email_verified";

    /**
     * Verifies a firebase token in the form of a string and returns a FirebaseToken
     * object representing the token's information
     *
     * @param token The unverified token
     *
     * @return A FirebaseToken object representing the verified token
     *
     * @throws GeneralSecurityException If the token was null, invalid, or had issues verified throw this
     * @throws IOException If there was an issue loading the key as a JSON object
     */
    public FirebaseToken verify(String token) throws GeneralSecurityException, IOException {
        if (token == null || token.isEmpty()) {
            LOGGER.log(Level.WARNING, "Token receieved was null or empty");
            throw new SignatureException("Token was null or empty");
        }
        // get public keys
        JsonObject publicKeys = getPublicKeysJson();

        // get json object as map
        // loop map of keys finding one that verifies
        for (Map.Entry<String, JsonElement> entry : publicKeys.entrySet()) {
            try {
                // get public key
                LOGGER.info(entry.getKey());
                PublicKey publicKey = getPublicKey(entry);

                // validate claim set
                Jws<Claims> jws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
                String uuid = jws.getBody().getSubject();
                String email = (String)jws.getBody().get(EMAIL_CLAIM);
                boolean verifiedEmail = (boolean)jws.getBody().get(EMAIL_VERIFIED_CLAIM);
                return new FirebaseToken(uuid, email, verifiedEmail);
            } catch (SignatureException e) {
                // If the key doesn't match the next key should be tried
            }
        }

        LOGGER.log(Level.WARNING, "Token was not found in map of keys");
        throw new SignatureException("Did not find token in map of keys");
    }

    /**
     * Returns a PublicKey based on the given map entry
     *
     * @param entry The map entry to find the public key for
     *
     * @return The public key of the entry
     * @throws GeneralSecurityException
     */
    private PublicKey getPublicKey(Map.Entry<String, JsonElement> entry) throws GeneralSecurityException, IOException {
        String publicKeyPem = entry.getValue().getAsString()
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();

        LOGGER.info(publicKeyPem);

        // generate x509 cert
        InputStream inputStream = new ByteArrayInputStream(entry.getValue().getAsString().getBytes("UTF-8"));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(inputStream);

        return cert.getPublicKey();
    }

    /**
     * Gets all the public keys as a Json object
     *
     * @return The public keys as a Json object
     * @throws IOException If parsing fails, throws this exception
     */
    private JsonObject getPublicKeysJson() throws IOException {
        // get public keys
        URI uri = URI.create(PUBLIC_KEYS_URI);
        GenericUrl url = new GenericUrl(uri);
        HttpTransport http = new NetHttpTransport();
        HttpResponse response = http.createRequestFactory().buildGetRequest(url).execute();

        // store json from request
        String json = response.parseAsString();
        // disconnect
        response.disconnect();

        // parse json to object
        return new JsonParser().parse(json).getAsJsonObject();
    }
}