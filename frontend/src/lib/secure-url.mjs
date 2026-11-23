export function validateSecureUrl(value, setting, isProduction) {
  const uri = new URL(value);
  const isLoopback = ["localhost", "127.0.0.1", "[::1]"].includes(uri.hostname);
  const allowsLocalHttp = !isProduction && isLoopback;

  if (
    uri.username ||
    uri.password ||
    uri.search ||
    uri.hash ||
    (uri.protocol !== "https:" && !(allowsLocalHttp && uri.protocol === "http:"))
  ) {
    throw new Error(`${setting} must be HTTPS, except for local loopback development.`);
  }

  return uri;
}
