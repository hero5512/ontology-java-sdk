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
### Constructor and Updater
