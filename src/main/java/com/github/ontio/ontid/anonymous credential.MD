# Anonymous Credential

Ontology anonymous certificate is a special digital certificate. Like a general certificate, the anonymous certificate reflects the issuer’s knowledge of certain information about the certificate holder. The certificate holder can then present the certificate to others, and the latter can verify the authenticity of the certificate through a certain algorithm. The content of the entire certificate will be exposed when the ordinary certificate is presented. However, in many scenarios, the holder does not need to provide all the information to the verifier. The anonymous certificate allows the holder to choose to hide part of the information when presenting the certificate, so as to achieve better privacy. The following are the characteristics of the anonymous certificate.

> 1 . Selective presentation
>
> Anonymous certificates make the BBS + signature algorithm a certificate signature. The feature of the algorithm allows the certificate holder to selectively hide part of the certificate information and only disclose the required information when presenting the certificate. According to the signature of the certificate, the holder constructs a proof, which can verify the authenticity of the information presented.
> 
> 2 . Range proof on values 
> 
> When the certificate holder presents the certificate information, he can choose to hide the value of the numerical attribute. The verifier can only prove that the numerical attribute is in a certain range. For example, the age is order than 18 or the salary is in a certain range, etc. By using ontology anonymous credential protocol, the credential owner can achieve selectively disclosure attributes he wants without disclosing any other information. 
>  
> 3 . Validity of credential 
> 
> The issuer of the certificate uses the accumulator to maintain the validity of the certificate. The issuer assigns an ID to each issued certificate and adds the ID to the accumulator. When the issuer needs to revoke a certain certificate, the ID is deleted from the accumulator. The issuer will post the latest value of the accumulator on the chain. When the certificate holder presents the certificate, he must also provide proof of the certificate ID in the accumulator. Then the verifier can verify the valididate of the presentation.  

## Details of the protocol
In order to issue anonymous credential, the issuer needs to hold a BBS+ signature key, a random seed, an accumulator.  The BBS+ signature key is used to generate certificate signatures. The random seed is used to generate the generators used in Bulletproofs. the accumulator is maintained to record the validity of the certificate. When a new certificate is issued by the issuer, a certificate ID is assigned to it, the ID is added to the accumulator, and the corresponding witness is generated. Due to the algorithm characteristics of the accumulator, after its value is updated, the witnesses corresponding to the previously accumulated value need to be updated accordingly. Therefore, the issuer also needs to manage the update of witness so that the certificate holder can check the latest status at any time. 
### Issue credential
1. The user sends an initialization request to the issuer to issue a certificate.
2. A random challenge is sent to the user.
3. The user generate master secret key. The user computes the commitment of the master private key by using public information of the issuer. The user generate the proof of the master secret key by using the Challenge.
4. The user sends the information to be included in the certificate together with the commitment and proof of the master private key to the issuer.
5. After the user information is passed, the issuer assigns ID to the certificate, signs the ID and certificate information, returns the ID and signature to the user, and adds the ID to the accumulator to update the value of the accumulator.
6. After the user verifies the signature, the relevant data is saved.

### Present credential
1. The holder queries the certificate issuer for the latest accumulator proof of the certificate ID.
2. The holder selectively chooses information to show. Then the holder generates the range proof and constructs the proof of presentation.
3. The holder will send the information together with the proof to the verifier.

### Verify Presentation
1. The verifier obtains the latest value of the issuer's authentication key and accumulator from blockchain network.
2. The verifier verifies the certificate presentation.







