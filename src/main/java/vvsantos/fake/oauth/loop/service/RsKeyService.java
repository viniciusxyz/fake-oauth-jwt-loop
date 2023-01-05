package vvsantos.fake.oauth.loop.service;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.*;

public class RsKeyService {
    private static final String RSA_PRIVATE_KEY_TYPE = "RSA PRIVATE KEY";
    private static final String RSA_PUBLIC_KEY_TYPE = "RSA PUBLIC KEY";
    private final String publicPath = null;
    private KeyFactory keyFactory;
    private PemObject privatePem;
    private PemObject publicPem;

    public void init() throws Exception {
        try {
            keyFactory = KeyFactory.getInstance("RSA");

            var location = System.getenv("CERT_LOCATION") == null ? "src/main/resources/key1.pem" : System.getenv("CERT_LOCATION");

            privatePem = getPemObject(location);

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public KeyPair getKeyPair() throws IOException, InvalidKeySpecException {
        final PrivateKeyInfo pkinfo = getPrivateKeyInfo();

        final ASN1Sequence seq = ASN1Sequence.getInstance(pkinfo.parsePrivateKey());

        if (privatePem.getType().equals(RSA_PRIVATE_KEY_TYPE)) {
            if (seq.size() != 9 && seq.size() != 10) {
                throw new RuntimeException("invalid: RSA ASN1 sequence");
            }
        }

        final org.bouncycastle.asn1.pkcs.RSAPrivateKey privateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey
                .getInstance(seq);
        final RSAPrivateCrtKeySpec privateSpec = new RSAPrivateCrtKeySpec(privateKey.getModulus(),
                privateKey.getPublicExponent(), privateKey.getPrivateExponent(), privateKey.getPrime1(),
                privateKey.getPrime2(), privateKey.getExponent1(), privateKey.getExponent2(),
                privateKey.getCoefficient());

        PublicKey publicKey;

        if (publicPem == null) {
            publicKey = getPublicKey(privateKey.getModulus(), privateKey.getPublicExponent());
        } else {
            publicKey = getPublicKey(publicPem);
        }

        return new KeyPair(publicKey, keyFactory.generatePrivate(privateSpec));
    }

    private PrivateKeyInfo getPrivateKeyInfo() throws IOException {
        if (privatePem.getType().equals(RSA_PRIVATE_KEY_TYPE)) {
            try (final ASN1InputStream asn1Stream = new ASN1InputStream(new ByteArrayInputStream(privatePem.getContent()))) {
                final AlgorithmIdentifier rsa = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
                return new PrivateKeyInfo(rsa, asn1Stream.readObject());
            }
        }

        return PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(privatePem.getContent()));
    }

    private PublicKey getPublicKey(PemObject publicPem) throws InvalidKeySpecException {
        KeySpec spec = null;

        if (publicPem.getType().equals(RSA_PUBLIC_KEY_TYPE)) {
            final ASN1Sequence seq = ASN1Sequence.getInstance(publicPem.getContent());
            final RSAPublicKey key = RSAPublicKey.getInstance(seq);

            spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
        } else {
            spec = new X509EncodedKeySpec(publicPem.getContent());
        }

        return getPublicKey(spec);
    }

    private PublicKey getPublicKey(BigInteger modulo, BigInteger expoentePublico) throws InvalidKeySpecException {
        final RSAPublicKeySpec spec = new RSAPublicKeySpec(modulo, expoentePublico);

        return getPublicKey(spec);
    }

    private PublicKey getPublicKey(KeySpec spec) throws InvalidKeySpecException {
        return keyFactory.generatePublic(spec);
    }

    private PemObject getPemObject(String path) throws IOException {
        try (final PemReader pemReader = new PemReader(new FileReader(path))) {
            return pemReader.readPemObject();
        }
    }
}
