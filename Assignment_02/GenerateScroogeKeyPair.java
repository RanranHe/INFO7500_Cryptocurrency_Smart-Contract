package hw2;

import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;


public class GenerateScroogeKeyPair {

	private static final String KEY_ALGORITHM = "ECDSA";
	private static final String PROVIDER = "BC";
	private static final String CURVE_NAME = "secp256k1";

	private ECGenParameterSpec ecGenSpec;
	private KeyPairGenerator keyGen_;
	private SecureRandom random;
	
	public static void main(String[] args) throws Exception {
		new GenerateScroogeKeyPair().run("scrooge", "123456");
	}
	
	public void run(String keyname, String password) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		
		random = SecureRandom.getInstanceStrong();
		ecGenSpec = new ECGenParameterSpec(CURVE_NAME);
		keyGen_ = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);

		keyGen_.initialize(ecGenSpec, random);
		System.out.println("Generating key pair. Please wait....");
		KeyPair kp = keyGen_.generateKeyPair();
		System.out.println("Key generation complete.");
		PrivateKey secretKey = kp.getPrivate(); //"sk" == "secret key" == "private key"

		{
			String pkFilename = keyname + "_pk.pem";
			
			StringWriter sw = new StringWriter();
			JcaPEMWriter wr = new JcaPEMWriter(sw);
			wr.writeObject(kp.getPublic());
			wr.close();
			Writer fw = new FileWriter(pkFilename);
			fw.write(sw.toString());
			fw.close();
			System.out.println("Public Key:\n" + sw.toString());
		}
		
		String skFilename = keyname + "_sk.pem";

		storeSecretKeyToEncrypted(secretKey, skFilename, password);
	}
	
	public String storeSecretKeyToEncrypted(PrivateKey sk, String filename, String password) throws Exception {
		JcaPEMWriter privWriter = new JcaPEMWriter(new FileWriter(filename));
		PEMEncryptor penc = (new JcePEMEncryptorBuilder("AES-256-CFB"))
					.build(password.toCharArray());
		privWriter.writeObject(sk, penc);
		privWriter.close();
		return null;
	}
}
