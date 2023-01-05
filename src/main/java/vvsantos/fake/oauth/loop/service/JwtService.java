package vvsantos.fake.oauth.loop.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

public class JwtService {

    private Algorithm algorithm;

    public JwtService(RsKeyService rsKeyService) {
        try {
            rsKeyService.init();
            algorithm = Algorithm.RSA256(null, (RSAPrivateKey) rsKeyService.getKeyPair().getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFakeToken() {
        return JWT.create()
                .withClaim("cid", "vvsantos")
                .withClaim("scope", "SCOPE1:GET SCOPE1:POST SCOPE1:PUT SCOPE2:GET SCOPE2:POST SCOPE2:PUT")
                .withIssuer("test")
                .withExpiresAt(Date.from(Instant.now().plusMillis(5000)))
                .withIssuedAt(Date.from(Instant.now()))
                .sign(algorithm);
    }


}
