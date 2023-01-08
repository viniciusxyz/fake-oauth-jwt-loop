package vvsantos.fake.oauth.loop.service;

import io.fusionauth.jwt.rsa.RSASigner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class JwtService {

    private static final String KEY_PKCS1_PATH = "src/main/resources/key1.pem";
    private RSASigner signer;

    public JwtService() {
        try {
            signer = RSASigner.newSHA256Signer(new String(Files.readAllBytes(Paths.get(KEY_PKCS1_PATH))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFakeToken() {
        io.fusionauth.jwt.domain.JWT jwt = new io.fusionauth.jwt.domain.JWT()
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .setIssuer("test")
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(60))
                .addClaim("cid", "vvsantos")
                .addClaim("scope", "SCOPE1:GET SCOPE1:POST SCOPE1:PUT SCOPE2:GET SCOPE2:POST SCOPE2:PUT");

        return io.fusionauth.jwt.domain.JWT.getEncoder().encode(jwt, signer);
    }


}
