/*
 * Copyright (c) 2005, 2016, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.security.pkcs11;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.*;
import javax.crypto.spec.*;

import sun.security.internal.spec.TlsMasterSecretParameterSpec;

import static sun.security.pkcs11.TemplateManager.*;
import sun.security.pkcs11.wrapper.*;
import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * KeyGenerator for the SSL/TLS master secret.
 *
 * @author  Andreas Sterbenz
 * @since   1.6
 */
public final class P11TlsMasterSecretGenerator extends KeyGeneratorSpi {

    private final static String MSG = "TlsMasterSecretGenerator must be "
        + "initialized using a TlsMasterSecretParameterSpec";

    // token instance
    private final Token token;

    // algorithm name
    private final String algorithm;

    // mechanism id
    private long mechanism;

    @SuppressWarnings("deprecation")
    private TlsMasterSecretParameterSpec spec;
    private P11Key p11Key;

    CK_VERSION ckVersion;

    // whether SSLv3 is supported
    private final boolean supportSSLv3;

    P11TlsMasterSecretGenerator(Token token, String algorithm, long mechanism)
            throws PKCS11Exception {
        super();
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;

        // Given the current lookup order specified in SunPKCS11.java, if
        // CKM_SSL3_MASTER_KEY_DERIVE is not used to construct this object,
        // it means that this mech is disabled or unsupported.
        supportSSLv3 = (mechanism == CKM_SSL3_MASTER_KEY_DERIVE);
    }

    protected void engineInit(SecureRandom random) {
        throw new InvalidParameterException(MSG);
    }

    @SuppressWarnings("deprecation")
    protected void engineInit(AlgorithmParameterSpec params,
            SecureRandom random) throws InvalidAlgorithmParameterException {
        if (params instanceof TlsMasterSecretParameterSpec == false) {
            throw new InvalidAlgorithmParameterException(MSG);
        }

        TlsMasterSecretParameterSpec spec = (TlsMasterSecretParameterSpec)params;
        int version = (spec.getMajorVersion() << 8) | spec.getMinorVersion();
        if ((version == 0x0300 && !supportSSLv3) || (version < 0x0300) ||
            (version > 0x0302)) {
             throw new InvalidAlgorithmParameterException
                    ("Only" + (supportSSLv3? " SSL 3.0,": "") +
                     " TLS 1.0, and TLS 1.1 are supported (0x" +
                     Integer.toHexString(version) + ")");
        }

        SecretKey key = spec.getPremasterSecret();
        // algorithm should be either TlsRsaPremasterSecret or TlsPremasterSecret,
        // but we omit the check
        try {
            p11Key = P11SecretKeyFactory.convertKey(token, key, null);
        } catch (InvalidKeyException e) {
            throw new InvalidAlgorithmParameterException("init() failed", e);
        }
        this.spec = spec;
        if (p11Key.getAlgorithm().equals("TlsRsaPremasterSecret")) {
            mechanism = (version == 0x0300) ? CKM_SSL3_MASTER_KEY_DERIVE
                                             : CKM_TLS_MASTER_KEY_DERIVE;
            ckVersion = new CK_VERSION(0, 0);
        } else {
            // Note: we use DH for all non-RSA premaster secrets. That includes
            // Kerberos. That should not be a problem because master secret
            // calculation is always a straightforward application of the
            // TLS PRF (or the SSL equivalent).
            // The only thing special about RSA master secret calculation is
            // that it extracts the version numbers from the premaster secret.
            mechanism = (version == 0x0300) ? CKM_SSL3_MASTER_KEY_DERIVE_DH
                                             : CKM_TLS_MASTER_KEY_DERIVE_DH;
            ckVersion = null;
        }
    }

    protected void engineInit(int keysize, SecureRandom random) {
        throw new InvalidParameterException(MSG);
    }

    protected SecretKey engineGenerateKey() {
        if (spec == null) {
            throw new IllegalStateException
                ("TlsMasterSecretGenerator must be initialized");
        }
        byte[] clientRandom = spec.getClientRandom();
        byte[] serverRandom = spec.getServerRandom();
        CK_SSL3_RANDOM_DATA random =
                new CK_SSL3_RANDOM_DATA(clientRandom, serverRandom);
        CK_SSL3_MASTER_KEY_DERIVE_PARAMS params =
                new CK_SSL3_MASTER_KEY_DERIVE_PARAMS(random, ckVersion);

        Session session = null;
        try {
            session = token.getObjSession();
            CK_ATTRIBUTE[] attributes = token.getAttributes(O_GENERATE,
                CKO_SECRET_KEY, CKK_GENERIC_SECRET, new CK_ATTRIBUTE[0]);
            long keyID = token.p11.C_DeriveKey(session.id(),
                new CK_MECHANISM(mechanism, params), p11Key.keyID, attributes);
            int major, minor;
            if (params.pVersion == null) {
                major = -1;
                minor = -1;
            } else {
                major = params.pVersion.major;
                minor = params.pVersion.minor;
            }
            SecretKey key = P11Key.masterSecretKey(session, keyID,
                "TlsMasterSecret", 48 << 3, attributes, major, minor);
            return key;
        } catch (Exception e) {
            throw new ProviderException("Could not generate key", e);
        } finally {
            token.releaseSession(session);
        }
    }
}
