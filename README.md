Simple Java wrapper around the Hetzner Robot API. Based on the [Java-OVH wrapper](https://github.com/ovh/java-ovh) for easy implementation.

```java
import nl.sbdeveloper.hetznerapi.HetznerApi;

public class HetznerApiTest {
    public void testCall() {
        String username = "xxxxxxxx";
        String password = "xxxxxxxx";
        
        HetznerApi api = new HetznerApi(username, password);
        try {
            api.get("/me");
        } catch (HetznerApiException e) {
            e.printStackTrace();
        }
    }
}
```

# API Documentation

Check out https://robot.your-server.de/doc/webservice/en.html for the documentation about this API.