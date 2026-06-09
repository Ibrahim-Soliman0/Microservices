### Auth Service API Endpoints


`http://localhost:8081`


| Category               | HTTP Method | Endpoint Path            | Purpose / Description                                                                                                 |
|:-----------------------|:------------|:-------------------------|:----------------------------------------------------------------------------------------------------------------------|
| **Frontend / Browser** | `GET`       | `/login`                 | Triggers the Google OAuth2 login page.                                                                                |
| **Frontend / Browser** | `GET`       | `/api/auth/me`           | Returns the logged-in user's profile data and JWT.                                                                    |
| **Frontend / Browser** | `POST`      | `/api/auth/logout`       | Invalidates the HTTP session and logs the user out.                                                                   |
| **Internal Services**  | `GET`       | `/api/auth/validate`     | Validates the session. Other services (e.g., Gateway) call this with the `JSESSIONID` cookie to verify authorization. |
| **Internal Services**  | `GET`       | `/api/auth/user/{email}` | Retrieves a specific user's database record using their email.                                                        |
| **Monitoring**         | `GET`       | `/api/auth/health`       | Custom health check returning service status (UP).                                                                    |
| **Database**           | `GET`       | `/h2-console`            | Web GUI for the in-memory database (`jdbc:h2:mem:authdb`).                                                            |

### External Service Requirements

| External Service         | Required Configuration                           | Description                                                                       |
|:-------------------------|:-------------------------------------------------|:----------------------------------------------------------------------------------|
| **Google Cloud Console** | `http://localhost:8081/login/oauth2/code/google` | Must be added as an "Authorized redirect URI" in your Google credentials.         |
| **Eureka Registry**      | `http://localhost:8761/eureka/`                  | The discovery server URL this service registers with on startup.                  |
| **Token Generator**      | `http://localhost:8082/api/tokens/generate`      | The internal POST endpoint where this app sends the profile to get the final JWT. |

**Note:** I deleted the secret key and Id you can put them in application.properties or in terminal

```bash
    # 1. Inject the secrets into your terminal session
export GOOGLE_CLIENT_ID="your-real-id-from-google"
export GOOGLE_CLIENT_SECRET="your-real-secret-from-google"

# 2. Run the application
mvn clean spring-boot:run
```

**to run using docker**
```bash
    mvn clean package -DskipTests
    docker build -t auth-service .
    docker run -p 8081:8081 auth-service
```
