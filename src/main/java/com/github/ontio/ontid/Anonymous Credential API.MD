# Ontology Anonymous Credential Java SDK

## Role

owner: credential owner, who hold some credentials and create presentation;

issuer: credential issuer, who issue credential;

verifier: credential verifier, who verify credential and presentation.

#### VerifiableCredential
[code](./anonymous/impl/FormatCredential.java)

Refer: https://www.w3.org/TR/vc-data-model/#basic-concepts

VerifiableCredential.issuer: issuer ontId.

#### Proof

[code](./anonymous/impl/AnonymousProof.java)

Refer: https://www.w3.org/TR/vc-data-model/#proofs-signatures

#### VerifiablePresentation

[code](./anonymous/impl/FormatPresentation.java)
Refer: https://www.w3.org/TR/vc-data-model/#presentations-0

VerifiablePresentation.holder: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId.

### SDK

The OntId2 class is Ontology Anonymous Credential protocol SDK class.

#### OntId2

[code](./OntId2.java)

It's ONTID2 SDK class, all Ontology Anonymous Credential function should entry from here.

OntId2.OntId: ontId contract.

## API
There are many interfaces to use Ontology Anonymous Credential protocol.
### Constructor
1. **public OntId2(String ontId, Account signer, Library library, OntId ontIdContract)**

	Generate OntId2 object.

    If `ontId` not empty and `signer` not null, there will query `OntIdPubKey` from ontology chain and use it as self signer.
    
    The library is the anonymous credential library.
    
2. **public FormatCredential createCredential(String issuerId, String holder, Pointer issuer, Request request, String[] context, String verificationMethod, ProofPurpose purpose, Date expiration)**
    comment:
    * Create FormatCredential object.
    * Check expiration.
    * Generate proof.
    param: 
    * issuerId: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId;
    * holder: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId;
    * issuer: the issuer object generate by library.
    
3. **FormatCredential createCredential(Credential credential, String issuerId, String holder, String[] context, String verificationMethod, ProofPurpose purpose, Date expiration, String credentialSubject)**
    * Update FormatCredential object.
    * Check expiration.
    * Generate proof.
    
4. **public FormatPresentation createPresentation(PresentationParam presentationParam, String issuerId, String holder, String[] context, String verificationMethod, ProofPurpose purpose)**
5. **public FormatPresentation createPresentation(Presentation presentation, String issuerId, String holder, String[] context, String verificationMethod, ProofPurpose purpose)**
6. 



