package com.cs371group2.admin;

import android.util.Pair;
import com.cs371group2.account.Account;
import com.cs371group2.account.AccountDao;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
 * Disclaimer: portions of this class were adapted from the source file found here. The original
 * code is available under the MIT License giving us the ability to adapt it and use it within the
 * project. The original code can be found here: https://github.com/rvep/dev_backend/blob/42dea19b894217d25b5bac9dc2a26623254ef052/src/main/java/io/abnd/rvep/security/service/impl/FirebaseAuthVerifier.java
 *
 * This class represents a firebase authenticator object for ensuring that a user has the correct
 * admission levels when accessing functionality that requires firebase authentication.
 *
 * History properties: Instances of this class are immutable from the time they are created.
 *
 * Invariance properties: This class assumes that all tokens it will be authenticating are JSON
 * web-tokens generated from firebase authentication. All other token types/formats will be invalid.
 *
 * Created on 2017-02-09.
 */
final class FirebaseAuthenticator extends Authenticator {

    /**
     * Authenticates the given firebase token and returns the account associated with it
     *
     * @param token The user's token to be authenticated
     * @return The account associated with the given token\
     * @precond token is valid and non-null
     */
    @Override
    protected Pair<Account, AccessToken> authenticateAccount(String token)
            throws UnauthorizedException {

        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Token is invalid and cannot be authenticated");
        }

        AccessToken accessToken;

        try {
            accessToken = verifyToken(token);
        } catch (IOException e) {
            throw new UnauthorizedException("Failed to verify token.");
        } catch (GeneralSecurityException e) {
            throw new UnauthorizedException("Token has timed out and is no longer valid.");
        }

        AccountDao dao = new AccountDao();
        Account account = dao.load(accessToken);

        if (account == null) {
            throw new UnauthorizedException("Account does not exist in the datastore");
        }

        LOGGER.log(Level.INFO,
                account.getId() + " has submitted a legal request. Checking permissions...");

        return new Pair<Account, AccessToken>(account, accessToken);
    }

    /**
     * PRIVATE AUTHENTICATOR HELPERS
     */

    /**
     * Private logger for the class
     */
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(FirebaseAuthenticator.class.getName());

    /**
     * The URL of secure JWT token methods to check when verifying the JWT
     */
    private static final String PUBLIC_KEYS_URI = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    /**
     * The string identifier for the email claim field of the token
     */
    private static final String EMAIL_CLAIM = "email";

    /**
     * The string identifier for the name field of the token
     */
    private static final String NAME_CLAIM = "user_id";

    /**
     * The string identifier for the email verified field of the token
     */
    private static final String EMAIL_VERIFIED_CLAIM = "email_verified";

    /**
     * Verifies a firebase token in the form of a string and returns a FirebaseToken
     * object representing the token's information
     *
     * @param token The unverified token
     * @return A FirebaseToken object representing the verified token
     * @throws GeneralSecurityException If the token was null, invalid, or had issues verified throw
     * this
     * @throws IOException If there was an issue loading the key as a JSON object
     * @precond token is non-empty and non-null
     */
    private AccessToken verifyToken(String token) throws GeneralSecurityException, IOException {
        if (token == null || token.isEmpty()) {
            LOGGER.log(Level.WARNING, "Token receieved was null or empty");
            throw new IOException("Token was null or empty");
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
                String email = (String) jws.getBody().get(EMAIL_CLAIM);
                String name = (String) jws.getBody().get(NAME_CLAIM);
                boolean verifiedEmail = (boolean) jws.getBody().get(EMAIL_VERIFIED_CLAIM);

                return new AccessToken(email, uuid, name, verifiedEmail);

            } catch (SignatureException e) {
                // If the key doesn't match the next key should be tried
            } catch (MalformedJwtException | UnsupportedJwtException e) {
                LOGGER.warning("Received malformed JWT " + token + " Cause: " + e.getMessage());
                throw new IOException("Malformed JWT recieved");
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Illegal argument during token verification " + e.getMessage());
                throw new IOException("Both name and email could not be parsed.");
            }
        }

        LOGGER.log(Level.WARNING, "Token was not found in map of keys");
        throw new IOException("Did not find token in map of keys");
    }

    /**
     * Returns a PublicKey based on the given map entry
     *
     * @param entry The map entry to find the public key for
     * @return The public key of the entry
     */
    private PublicKey getPublicKey(Map.Entry<String, JsonElement> entry)
            throws GeneralSecurityException, IOException {
        String publicKeyPem = entry.getValue().getAsString()
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();

        LOGGER.info(publicKeyPem);

        // generate x509 cert
        InputStream inputStream = new ByteArrayInputStream(
                entry.getValue().getAsString().getBytes("UTF-8"));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);

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
