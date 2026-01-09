# Keycloak Setup for OAuth2/JWT

After starting docker-compose:

1. Open http://localhost:8888/admin
   - Username: `admin`
   - Password: `admin`

2. Create realm `aifa`:
   - Realm settings → General → Create realm
   - Name: `aifa`

3. Create client `aifa-web`:
   - Realm settings → Clients → Create client
   - Name: `aifa-web`
   - Client type: OpenID Connect
   - Valid Redirect URIs: `http://localhost:5173/*`
   - Client Authentication: OFF
   - Save

4. Create test user:
   - Users → Create user
   - Username: `testuser`
   - Email: `test@example.com`
   - Set password (not temporary)

5. Get token (for testing):
   ```bash
   curl -X POST http://localhost:8888/realms/aifa/protocol/openid-connect/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_id=aifa-web&username=testuser&password=PASSWORD&grant_type=password"
   ```

6. Use JWT in requests:
   ```bash
   curl http://localhost:8080/api/finance/api/transactions \
     -H "Authorization: Bearer <token>"
   ```

Gateway + finance-service now require valid JWT tokens from the Keycloak issuer.
