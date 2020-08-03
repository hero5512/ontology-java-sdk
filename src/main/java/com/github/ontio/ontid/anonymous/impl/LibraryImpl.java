package com.github.ontio.ontid.anonymous.impl;

import com.github.ontio.ontid.anonymous.Library;
import com.github.ontio.sdk.exception.SDKException;
import com.sun.jna.Pointer;

public class LibraryImpl implements Library {

    public static final int ISSUER_PUBLIC_SIZE = 928;
    public static final int ACC_VALUE_SIZE = 96;
    public static final int ACC_WITNESS_SIZE = 96 * 3 + 48 + 8;
    public static final int MASTER_SECRET_SIZE = 32;
    public static final int NONCE_SIZE = 16;

    private Library library;

    public LibraryImpl() {
        library = Library.INSTANCE;
    }

    @Override
    public Pointer new_issuer() {
        return library.new_issuer();
    }

    @Override
    public String serialize_issuer(Pointer issuer) throws Exception {
        if (issuer == null) {
            throw new SDKException("issuer is null");
        }
        return library.serialize_issuer(issuer);
    }

    @Override
    public Pointer deserialize_issuer(String issuer) throws Exception {
        if (issuer == null) {
            throw new SDKException("issuer is null");
        }
        return library.deserialize_issuer(issuer);
    }

    @Override
    public void free_issuer(Pointer issuer) throws Exception {
        if (issuer == null) {
            throw new SDKException("issuer is null");
        }
        library.free_issuer(issuer);
    }

    @Override
    public void publish_issuer(byte[] issuerPublic, Pointer issuer) throws Exception {
        if (issuerPublic == null || issuerPublic.length != ISSUER_PUBLIC_SIZE) {
            throw new SDKException("issuerPublic's length is invalid");
        }
        if (library == null) {
            throw new SDKException("library has not initialized");
        }
        library.publish_issuer(issuerPublic, issuer);
    }

    @Override
    public void get_accumulator_value(byte[] value, Pointer issuer) throws Exception {
        if (value == null || value.length != ACC_VALUE_SIZE) {
            throw new SDKException("value's length is invalid");
        }
        library.get_accumulator_value(value, issuer);
    }

    @Override
    public String get_accumulator_info(Pointer issuer) throws Exception {
        if (issuer == null) {
            throw new SDKException("issuer is null");
        }
        return library.get_accumulator_info(issuer);
    }

    @Override
    public void free_str(String s) {
        if (s == null) {
            return;
        }
        library.free_str(s);
    }

    @Override
    public void new_master_secret(byte[] masterSecret) throws Exception {
        if (masterSecret == null || masterSecret.length != MASTER_SECRET_SIZE) {
            throw new SDKException("masterSecret's length is invalid");
        }
        if (library == null) {
            throw new SDKException("library has not initialized");
        }
        library.new_master_secret(masterSecret);
    }

    @Override
    public String make_issue_request(byte[] masterSecret, String attributes, byte[] nonce, byte[] issuerPublic) throws Exception {
        if (masterSecret == null || attributes == null || nonce == null || issuerPublic == null
                || masterSecret.length != MASTER_SECRET_SIZE || nonce.length != NONCE_SIZE
                || issuerPublic.length != ISSUER_PUBLIC_SIZE) {
            throw new SDKException("issuer is null");
        }
        return library.make_issue_request(masterSecret, attributes, nonce, issuerPublic);
    }

    @Override
    public String issue_credential(Pointer issuer, String request) throws Exception {
        if (issuer == null || request == null) {
            throw new SDKException("issuer or request is null");
        }
        return library.issue_credential(issuer, request);
    }

    @Override
    public int revoke_credential(Pointer issuer, Long i) throws Exception {
        if (issuer == null || i == null) {
            throw new SDKException("issuer or request is null");
        }
        return library.revoke_credential(issuer, i);
    }

    @Override
    public int extract_witness(byte[] witness, String credential) throws Exception {
        if (witness == null || witness.length != ACC_WITNESS_SIZE || credential == null) {
            throw new SDKException("witness or credential is invalid");
        }
        return library.extract_witness(witness, credential);
    }

    @Override
    public int update_witness(byte[] witness, byte[] new_witness, Pointer issuer, Long i, byte isAdd) throws Exception {
        if (witness == null || issuer == null || i == null || (isAdd != 0 && isAdd != 1) || witness.length != ACC_WITNESS_SIZE) {
            throw new SDKException("invalid parameter");
        }
        return library.update_witness(witness, new_witness, issuer, i, isAdd);
    }

    @Override
    public String update_credential_witness(String credential, byte[] issuer_public, byte[] witness, byte[] accumulator) throws Exception {
        if (credential == null || witness == null || accumulator == null || witness.length != ACC_WITNESS_SIZE || accumulator.length != ACC_VALUE_SIZE) {
            throw new SDKException("invalid parameter");
        }
        return library.update_credential_witness(credential, issuer_public, witness, accumulator);
    }

    public String present(String credential, byte[] masterSecret, String attributes, byte[] issuerPublic, byte[] accumulator, byte[] nonce) throws Exception {
        if (credential == null || masterSecret == null || attributes == null || issuerPublic == null || accumulator == null
                || nonce == null || masterSecret.length != MASTER_SECRET_SIZE || issuerPublic.length != ISSUER_PUBLIC_SIZE
                || accumulator.length != ACC_VALUE_SIZE || nonce.length != NONCE_SIZE) {
            throw new SDKException("invalid parameter");
        }
        return library.present(credential, masterSecret, attributes, issuerPublic, accumulator, nonce);
    }

    public int verify(String presentation, byte[] issuerPublic, byte[] accumulator, byte[] nonce) throws Exception {
        if (presentation == null || issuerPublic == null || accumulator == null || nonce == null
                || issuerPublic.length != ISSUER_PUBLIC_SIZE || accumulator.length != ACC_VALUE_SIZE || nonce.length != NONCE_SIZE) {
            throw new SDKException("invalid parameter");
        }
        return library.verify(presentation, issuerPublic, accumulator, nonce);
    }
}
