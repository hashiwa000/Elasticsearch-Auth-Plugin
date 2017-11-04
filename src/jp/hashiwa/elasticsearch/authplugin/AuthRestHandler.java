package jp.hashiwa.elasticsearch.authplugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.rest.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AuthRestHandler implements RestHandler {
  private final Logger logger = Loggers.getLogger(AuthRestHandler.class);
  private final RestHandler originalHandler;
  private final RestResponse unauthorizedResponse = new RestResponse() {
    @Override
    public String contentType() {
      return "application/json";
    }

    @Override
    public BytesReference content() {
      return new BytesArray("");
    }

    @Override
    public RestStatus status() {
      return RestStatus.UNAUTHORIZED;
    }
  };
  private final Map<RestRequest.Method, Stream<Pattern>> authPatterns = new HashMap<RestRequest.Method, Stream<Pattern>>() {
    {
      this.put(RestRequest.Method.POST, Stream.of(
              Pattern.compile("^/testindex(/.*)?$")
      ));
      this.put(RestRequest.Method.PUT, Stream.of(
              Pattern.compile("^/testindex(/.*)?$")
      ));
      // all methods
      this.put(null, Stream.of(
              Pattern.compile("^/adminindex(/.*)?$")
      ));
    }
  };

  AuthRestHandler(RestHandler restHandler) {
    this.originalHandler = restHandler;
  }

  @Override
  public void handleRequest(RestRequest restRequest, RestChannel restChannel, NodeClient nodeClient) throws Exception {
    this.logger.debug(restRequest.path());
    this.logger.debug(restRequest.rawPath());
    if (isOk(restRequest)) {
      this.originalHandler.handleRequest(restRequest, restChannel, nodeClient);
    } else {
      restChannel.sendResponse(unauthorizedResponse);
    }
  }

  private boolean needAuth(RestRequest.Method method, String path) {
    if (authPatterns.containsKey(method)) {
      Stream<Pattern> patterns = authPatterns.get(method);
      boolean match = patterns.anyMatch(
              p -> p.matcher(path).matches()
      );
      return match;
    }
    return false;
  }

  private boolean isOk(RestRequest restRequest) {
    RestRequest.Method method = restRequest.method();
    String path = restRequest.path(); // use rawpath() ?
    boolean needAuth = needAuth(method, path)
                    || needAuth(null, path);
    if (! needAuth) {
      return true;
    }

    for (java.util.Map.Entry<String, String> entry: restRequest.headers()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (key.equals("user") && value.equals("admin")) {
        return true;
      }
    }
    return false;

    // ES 5.4
    // return restRequest.getHeaders().get("user").equals("admin");
  }
}
