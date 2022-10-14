const config = {
  localName: "localhost",
  port: 8090,
  baseURL: ``,
  timeout: 10 * 1000,
  token: "",
}
config.baseURL = `http://${config.localName}:${config.port}`;
export default config;