package utils;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author fmi110
 * @Description: rsa 对称加密工具类 , 加密解密代码参考支付宝 sdk 的 AlipaySignature类
 * 提供了签名,验证签名等方法
 * @Date 2018/2/24 11:48
 */
public class RSAUtils {

    public static final String SIGN_TYPE_RSA = "RSA";

    /**
     * sha256WithRsa 算法请求类型
     */
    public static final String SIGN_TYPE_RSA2 = "RSA2";

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";


    /**
     * 公钥加密
     *
     * @param content   待加密内容
     * @param publicKey 公钥
     * @param charset   字符集，如UTF-8, GBK, GB2312
     * @return 密文内容
     * @throws RuntimeException
     */
    public static String rsaEncrypt(String content, String publicKey,
                                    String charset) {
        try {
            PublicKey pubKey = getPublicKeyFromX509(SIGN_TYPE_RSA,
                                                    new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes()
                    : content.getBytes(charset);
            int                   inputLen = data.length;
            ByteArrayOutputStream out      = new ByteArrayOutputStream();
            int                   offSet   = 0;
            byte[]                cache;
            int                   i        = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();

            return StringUtils.isEmpty(charset) ? new String(encryptedData)
                    : new String(encryptedData, charset);
        } catch (Exception e) {
            throw new RuntimeException("EncryptContent = " + content + ",charset = " + charset,
                                       e);
        }
    }

    /**
     * 私钥解密
     *
     * @param content    待解密内容
     * @param privateKey 私钥
     * @param charset    字符集，如UTF-8, GBK, GB2312
     * @return 明文内容
     * @throws RuntimeException
     */
    public static String rsaDecrypt(String content, String privateKey,
                                    String charset) {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                                                       new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData = StringUtils.isEmpty(charset)
                    ? Base64.decodeBase64(content.getBytes())
                    : Base64.decodeBase64(content.getBytes(charset));
            int                   inputLen = encryptedData.length;
            ByteArrayOutputStream out      = new ByteArrayOutputStream();
            int                   offSet   = 0;
            byte[]                cache;
            int                   i        = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return StringUtils.isEmpty(charset) ? new String(decryptedData)
                    : new String(decryptedData, charset);
        } catch (Exception e) {
            throw new RuntimeException("EncodeContent = " + content + ",charset = " + charset, e);
        }
    }

    /**
     * sha256WithRsa 加签
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @
     */
    public static String rsaSign_SHA256WithRSA(String content, String privateKey,
                                               String charset) {

        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                                                       new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_SHA256RSA_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            throw new RuntimeException("RSAcontent = " + content + "; charset = " + charset, e);
        }

    }

    /**
     * sha1WithRsa 加签 , 内容使用 utf-8 编码
     *
     * @param content
     * @param privateKey
     * @return
     */
    public static String rsaSign_SHA1WithRSA(String content, String privateKey) {
        return rsaSign_SHA1WithRSA(content, privateKey, "utf-8");
    }

    /**
     * sha1WithRsa 加签
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @
     */
    public static String rsaSign_SHA1WithRSA(String content, String privateKey,
                                             String charset) {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                                                       new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (InvalidKeySpecException ie) {
            throw new RuntimeException("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私钥", ie);
        } catch (Exception e) {
            throw new RuntimeException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }

    /**
     * SHA256WithRSA 验签 , 内容使用 utf-8 编码
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean rsaCheck_SHA256WithRSA(String content, String sign, String publicKey) {
        return rsaCheck_SHA256WithRSA(content, sign, publicKey, "utf-8");
    }

    /**
     * SHA256WithRSA 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @param charset
     * @return
     */
    public static boolean rsaCheck_SHA256WithRSA(String content, String sign, String publicKey,
                                                String charset) {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                                                    new ByteArrayInputStream(publicKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_SHA256RSA_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    /**
     * SHA1WithRSA 验签 , 内容使用 utf-8 编码
     *
     * @param content
     * @param sign      签名
     * @param publicKey
     * @return
     */
    public static boolean rsaCheck_SHA1WithRSA(String content, String sign, String publicKey) {
        return rsaCheck_SHA1WithRSA(content, sign, publicKey, "utf-8");
    }

    /**
     * SHA1WithRSA 验签
     *
     * @param content
     * @param sign      签名
     * @param publicKey
     * @param charset
     * @return
     */
    public static boolean rsaCheck_SHA1WithRSA(String content, String sign, String publicKey,
                                              String charset) {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                                                    new ByteArrayInputStream(publicKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    /**
     * 获取rsa算法的私钥
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String privateKey) throws Exception {
        return getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));
    }

    /**
     * 根据私钥字符串获取私钥 , 默认算法为 rsa
     *
     * @param algorithm
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    String privateKey) throws Exception {
        return getPrivateKeyFromPKCS8(
                algorithm == null ? SIGN_TYPE_RSA : algorithm, new ByteArrayInputStream(privateKey.getBytes()));
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = readText(ins)
                .getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * 获取rsa算法的公钥
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromX509(String publicKey) throws Exception {
        return getPublicKeyFromX509(SIGN_TYPE_RSA, new ByteArrayInputStream(publicKey.getBytes()));
    }

    /**
     * 根据公钥字符串获取公钥
     *
     * @param algorithm
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromX509(String algorithm, String publicKey) throws Exception {
        return getPublicKeyFromX509(
                algorithm == null ? SIGN_TYPE_RSA : algorithm, new ByteArrayInputStream(publicKey.getBytes()));
    }

    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString()
                                  .getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    private static String readText(InputStream in) throws IOException {
        return readText(in, null, -1);
    }

    private static String readText(InputStream in, String encoding, int bufferSize)
            throws IOException {
        Reader reader = (encoding == null) ? new InputStreamReader(in) : new InputStreamReader(in,
                                                                                               encoding);
        return readText(reader, bufferSize);
    }

    private static String readText(Reader reader, int bufferSize) throws IOException {
        StringWriter writer = new StringWriter();
        io(reader, writer, bufferSize);
        return writer.toString();
    }

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static void io(Reader in, Writer out) throws IOException {
        io(in, out, -1);
    }

    private static void io(Reader in, Writer out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = DEFAULT_BUFFER_SIZE >> 1;
        }
        char[] buffer = new char[bufferSize];
        int    amount;
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }
    }

    //--------------------------以上内容抄自支付宝支付sdk签名类并做部分修改----------------------------------//

    public static String getSign_MD5withRSA(String content, String privateKey) throws Exception {
        return getSign_MD5withRSA(content, getPrivateKeyFromPKCS8(privateKey));
    }

    /**
     * 用md5生成内容摘要，再用RSA的私钥加密，进而生成数字签名,签名结果使用 base64 编码成字符串
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String getSign_MD5withRSA(String content, PrivateKey privateKey) throws Exception {
        byte[]    contentBytes = content.getBytes("utf-8");
        Signature signature    = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }

    /**
     * MD5withRSA验签
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifySign_MD5withRSA(String content, String sign, String publicKey) throws Exception {
        return verifySign_MD5withRSA( content,  sign, getPublicKeyFromX509(publicKey));
    }
    /**
     * MD5withRSA 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifySign_MD5withRSA(String content, String sign, PublicKey publicKey) throws Exception {
        byte[]    contentBytes = content.getBytes("utf-8");
        Signature signature    = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }


    /**
     * SHA1withRSA签名(用sha1生成内容摘要，再用RSA的私钥加密，进而生成数字签名)
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String getSign_SHA1withRSA(String content, PrivateKey privateKey) throws Exception {
        byte[]    contentBytes = content.getBytes("utf-8");
        Signature signature    = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }


    /**
     * SHA1withRSA验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifySign_SHA1withRSA(String content, String sign, PublicKey publicKey) throws Exception {
        byte[]    contentBytes = content.getBytes("utf-8");
        Signature signature    = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }

    //生成密钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024); //可以理解为：加密后的密文长度，实际原文要小些 越大 加密解密越慢
        KeyPair keyPair = keyGen.generateKeyPair();
        return keyPair;
    }


    public static void main(String[] args) throws Exception {

        String PUBLIC_KEY  =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+RTb5LZMc89x4I41+kovQjbDvsXgQ130uxf86k7LJcX64vZmx3xNa1iRx96jwCELukN6RAUOPUHtbvAgJdhXaqvOhhPVYYst92O1sJ0tNger5JbUIaqhvun6MTd0CLvKEmSWNzUHDwaUWxYhaEWHtI5J4ti4CKZczzIdkIGD0EwIDAQAB";
        String PRIVATE_KEY =
                "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAL5FNvktkxzz3HgjjX6Si9CNsO+xeBDXfS7F/zqTsslxfri9mbHfE1rWJHH3qPAIQu6Q3pEBQ49Qe1u8CAl2Fdqq86GE9Vhiy33Y7WwnS02B6vkltQhqqG+6foxN3QIu8oSZJY3NQcPBpRbFiFoRYe0jkni2LgIplzPMh2QgYPQTAgMBAAECgYEAgWabQ43+PjOPXll3knxiAB1NF0bQDEwxg8z+jr0CW8QHVecEjxbSl/WJZcT0LQLBWnRGGcINS3uF2dagdKbNpvMpTTlUJuLCREpJyIYadbeJnZqSgov7ZlF8hLs/v9FB/ZJJStlf8Akw/HLOVHjWXPAYXDziV9a5sY7JWfpexMECQQDxvEcFTFnqk5Vlr8PM9hruYfp8CTKXrMIKVgsBP8WQhgB5W9ksQtR+29ToUPxwur5BPPOz/d52M7UmEJKkSjezAkEAyX990YcjxmxIteDRhKnfFLmbsWVR4Nk846sNFuWZwIN2hBEmZqLJdhIvuJ0A8P8BODGwVNpdjls3bSGv9e4iIQJBANCNrAQxK/4KV8EEE/wXc4Koey9ZzBE5VasPMq1gNHWMdoo9KK9b9PKBfDz16eTj59Jm4KYv5ya5U5bEg49EDKsCQQCGbes3/avtdlKwHNRLaLVu80GfAVS16NjJn1W1P+rq2AoapAJ1mQdFIW77AKwfHFHo9qWIhsLB9bMtuLrXHU9hAkEAmDlc6e2dcfpho0nRUkKNJcqcIRnEkvmqeITUc3dOnouQpDTmF8COnVSzHTAIIYQ35AAVK6ooApcgeyD+HBrbyg==";


        String content = "fmi110";
        String a       = RSAUtils.getSign_SHA1withRSA(content, RSAUtils.getPrivateKeyFromPKCS8(null, PRIVATE_KEY));
        String b       = RSAUtils.rsaSign_SHA1WithRSA(content, PRIVATE_KEY, "utf-8");
        System.out.println(a);
        System.out.println("=============");
        System.out.println(b);

        boolean re = RSAUtils.rsaCheck_SHA1WithRSA(content, a, PUBLIC_KEY);
        System.out.println(re);
    }

}