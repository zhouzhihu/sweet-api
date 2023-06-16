import JSEncrypt from 'jsencrypt'

/**
 * 加密字符串
 * @param publicKey 公钥
 * @param str 要加密的字符串
 */
export function encryptStr(publicKey, str) {
    const key = getPublicKey(publicKey)
    const crypt = new JSEncrypt()
    crypt.setKey(key)
    const encrypted = crypt.encrypt(str)
    return encodeURIComponent(encrypted)
}

/**
 * 获取公钥
 * @param key 公钥 pem
 */
function getPublicKey(key) {
    return `-----BEGIN PUBLIC KEY-----\n${key}\n-----END PUBLIC KEY-----`
}
