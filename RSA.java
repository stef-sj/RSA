import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class RSA {

	// Not safe, but easy to code
	BigInteger n;
	BigInteger e;
	BigInteger d;

	public static void main(String[] args) throws NoSuchAlgorithmException {

		// For week 1
		//encryptDecrypt();

		// For week 2 signature/verification
		signVerify();

		// For week 2 part 2 - timing in bits/time
		durationOfHash();
		
		// For week 2 part 3 - duration of keyGen(2000)
		durationOfKeyGen();


	}

	public static void encryptDecrypt() {
		RSA rsa = new RSA();
		rsa.KeyGen(500);
		BigInteger c = rsa.Encrypt("hej med dig ");
		System.out.println("The cipher text is: " + c);
		String decryptedText = rsa.Decrypt(c);
		System.out.println("Text decrypted to: " + decryptedText);
	}

	public static void signVerify() throws NoSuchAlgorithmException {
		RSA rsa = new RSA();
		rsa.KeyGen(500);
		String m = "Chocolate Fudge";
		BigInteger s = rsa.generateSignature(m);
		if(rsa.verifySignature(s, m))
			System.out.println("Signature verified.");
		else
			System.out.println("Verification returned false");
	}

	private static void durationOfHash() throws NoSuchAlgorithmException {
		RSA rsa = new RSA();

		StringBuilder sb = new StringBuilder(10000);
		for (int i=0; i<10000; i++) {
			sb.append('a');
		}
		String m = sb.toString();
		rsa.timeHash(m);
	}
	
	private static void durationOfKeyGen() {
		RSA rsa = new RSA();
		long startTime = System.currentTimeMillis();

		rsa.KeyGen(2000);
		
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Duration of keyGen(2000): " + duration + " ms.");
	}



	public void KeyGen(int k) {

		BigInteger p = BigInteger.probablePrime(k, new Random());
		BigInteger q = BigInteger.probablePrime(k, new Random());

		boolean gcd = true;
		while (gcd) {

			BigInteger i = BigInteger.valueOf(3).gcd(p.subtract(BigInteger.ONE));
			BigInteger j = BigInteger.valueOf(3).gcd(q.subtract(BigInteger.ONE));
			if (i.equals(j) && i.equals(BigInteger.ONE)) {
				gcd = false; 
			}
			else {
				p = BigInteger.probablePrime(k, new Random());
				q = BigInteger.probablePrime(k, new Random());
			}
		}

		n = p.multiply(q);

		e = BigInteger.valueOf(3);
		d = e.modPow(BigInteger.ONE.negate(), p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

	}

	public BigInteger Encrypt(String m) {
		byte[] byteArray = m.getBytes();
		BigInteger newM = new BigInteger(byteArray);
		BigInteger c = newM.modPow(e, n);
		return c;
	}

	private String Decrypt(BigInteger c) {
		BigInteger decrypted = c.modPow(d, n);
		byte[] decryptedAsBytes = decrypted.toByteArray();
		String decryptedAsText = new String(decryptedAsBytes);
		return decryptedAsText;
	}

	public BigInteger generateSignature(String m) throws NoSuchAlgorithmException {

		// Hashing signature
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(m.getBytes());
		byte[] byteArray = md.digest(); 
		BigInteger hashedM = new BigInteger(byteArray);

		// BELOW, SIGNATURE
		BigInteger s = hashedM.modPow(d, n);

		return s;
	}

	public boolean verifySignature(BigInteger s, String m) throws NoSuchAlgorithmException {

		// Hashing original message
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(m.getBytes());
		byte[] hashedMArray = md.digest();
		BigInteger hashedMBigInteger = new BigInteger(hashedMArray);

		//Decrypting s
		BigInteger decryptedSignature = s.modPow(e, n);

		if(hashedMBigInteger.equals(decryptedSignature))
			return true;

		return false;

	}

	// For timing
	public static BigInteger timeHash(String m) throws NoSuchAlgorithmException {

		long startTime = System.currentTimeMillis();

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(m.getBytes());
		byte[] byteArray = md.digest(); 
		BigInteger hashedM = new BigInteger(byteArray);

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Duration of hash: " + duration + " ms.");

		return null;
	}
}
