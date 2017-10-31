package jp.hashiwa.elasticsearch.authplugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.rest.*;

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
      return new BytesArray("{\"error\": \"Unauthorized.\"}");
    }

    @Override
    public RestStatus status() {
      return RestStatus.UNAUTHORIZED;
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

  private boolean isOk(RestRequest restRequest) {
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
