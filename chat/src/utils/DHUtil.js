// 引入crypto
const crypto = window.require("crypto");
const CryptoJS = window.require("crypto-js");
const primeLength = 512; // 素数p长度
const G = 2;  // 素数G

export default class DH{
  constructor(p, g){
    let dh;
    if(p){
      dh = crypto.createDiffieHellman(p, "hex", g, "hex");
    } else{
      dh = crypto.createDiffieHellman(primeLength, G);
    }
    this.publicKey = dh.generateKeys().toString("hex");
    this.privateKey = dh.getPrivateKey().toString("hex");
    this.p = dh.getPrime().toString("hex");
    this.g = dh.getGenerator().toString("hex");
    this.generateSecret = (key, inEnc, outEnc) => dh.computeSecret(key, inEnc ? inEnc : "hex", outEnc ? outEnc : "hex");
    this.setPrivateKey = (key) => {
      dh.setPrivateKey(key, 'hex');
      this.privateKey = dh.getPrivateKey().toString("hex");
    };
    this.setPublicKey = (key) => {
      dh.setPublicKey(key, 'hex');
      this.publicKey = dh.getPublicKey().toString("hex");
    }
  }
}

const aes_config = {
  iv: "1012132405963708",
}
/**
 * 加密数据
 * @param {String} data 
 */
 export function encryption(key, data){
  return CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse(key), {
    iv: CryptoJS.enc.Utf8.parse(aes_config.iv),
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  }).toString()
}
/**
 * 解密数据
 * @param {*} key 
 * @param {*} data 
 */
export function decryption(key, data){
  let decrypted = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(key), {
    iv: CryptoJS.enc.Utf8.parse(aes_config.iv),
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  });
  return decrypted.toString(CryptoJS.enc.Utf8);
}