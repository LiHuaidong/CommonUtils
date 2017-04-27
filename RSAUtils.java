import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtils {
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
		RSAPrivateKey priKey = null;
		try {
			Base64 base64Decoder = new Base64();
			byte[] buffer = base64Decoder.decode(privateKeyStr);

			RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
			RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			priKey = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivKeySpec);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return priKey;
	}
	
	public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
		RSAPublicKey pubKey = null;
		try {
			Base64 base64Decoder = new Base64();
			byte[] buffer = base64Decoder.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return pubKey;
	}
	
	public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
		byte[] output = null;
		if (publicKey != null) {
			try {
				Cipher cipher = null;
				cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				output = cipher.doFinal(plainTextData);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return output;
	}
  
    /** 
     * 解密过程 
     * @param privateKey 私钥 
     * @param cipherData 密文数据 
     * @return 明文 
     * @throws Exception 解密过程中的异常信息 
     */  
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
		byte[] result = null;
		if (privateKey != null) {
			try {
				Cipher cipher = null;
				cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				result = cipher.doFinal(cipherData);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String decryptString(RSAPrivateKey privateKey, String encrypttext) {
		String result = null;
		if (privateKey != null) {
			try {
				byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
	            byte[] data = decrypt(privateKey, en_data);
	            if(data != null) {
	            	result = new String(data, "UTF-8");
	            }
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
        
        return result;
    }

	public static void main(String[] args) throws Exception {
		String decryptString = "35d9000065ab7578bbe2478a3cbcf8b79a4d06a3ff06ad307d1510d7b1e9950ed6af3685397a3c3d9ae1ebe339b16cb3a31d0273cc04c5fb458a80c3a76666c847c3df4e5ce22d4e4e286a5f12e3f1e78e2e6198bda1e4c676cb993eb67564479ef5fb3a8376b85cb0d1fdd5776d2930b6176f3bcff63bcb9d332aee171ffcbe";
		RSAPrivateKey privateKey = loadPrivateKey("MIICXQIBAAKBgQCv5bSTDKsBefhf1UOdmo6UeRjt+W+2tgxoghF5FwYgA0CDFL+OF6l1ls2nym6f5R5O0joT7v7pZIetCxgHWhtrRoiDMrVJJzNr1tq4UFnn8zYDHSAqOtNmcSeMymq+s94DhA+t5ZKwLWcctRSJbGhsVYx3PM/CeSy2r3lf+a9HxwIDAQABAoGBAIBhfodpUpK0TCTeCgjI2tHhBOIEEZZgjvPkIw6nUFAjL328sgbqnEN3+shBhrZrStCqvbr4z9LXDfOtyi7rhGB0Oc4cNmNMuJ5RGRdhh7bK7dRnwCc+pofo6kiriBM7VgrBR5gq+TDrblB0cWqDuB7jLk99DHj7YUOfiigFK/H5AkEA5CuYy/m3DKNQQj+fdHuuyz6AQ9B7i4aYBYDM2tPDIH13I3HNCVlhoauHaNevwossbyKn59ulsH3QnP6aFItYkwJBAMVZ7suPky3NutyLbIykgC8OCoMvQ/6fXjAKTxtkOz2GZZnM621kyAQLHSRzs+DFm1YpOslU/ktsvOPT6YQT2H0CQFG4C5GCbjzogOY5QzLE2WWRjP68vILMQcY2yyfUkB4i0XiwWW7vxDfS5zx1g0156wAiuWAhGsaLSRvxU93nymECQQCr3FWID9Ar4y0HQqOVFuxKDgA5nb+ozUBItbGOOQsd2RxBg6LB8TH902/dfqT26R8NOXpoOgv5wwf7n1+MFXxBAkATqNJyXghPS0qlVstnzbrLK4m0P+dkyux8hIFiB6aJEUgoa+0Ukb6Ml1ufNzAjRr31fGgbOvKupXB/mJV9wD4V");
		
		System.out.println(decryptString(privateKey, decryptString));
	}

}
