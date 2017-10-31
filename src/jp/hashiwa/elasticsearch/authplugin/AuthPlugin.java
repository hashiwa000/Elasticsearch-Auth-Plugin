package jp.hashiwa.elasticsearch.authplugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.*;

import java.util.function.UnaryOperator;

public class AuthPlugin extends Plugin implements ActionPlugin {
  private Settings settings;
  private final Logger logger = Loggers.getLogger(AuthPlugin.class);

  public AuthPlugin(Settings settings) {
    this.settings = settings;
    this.logger.info("auth plugin is enabled.");
    this.logger.debug(settings.toString());
  }

//  @Override
//  public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
//    return null;
//  }
//
//  @Override
//  public List<Class<? extends ActionFilter>> getActionFilters() {
//    return null;
//  }
//
//  @Override
//  public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver, Supplier<DiscoveryNodes> nodesInCluster) {
//    return null;
//  }
//
//  @Override
//  public Collection<String> getRestHeaders() {
//    return null;
//  }

  @Override
  public UnaryOperator<RestHandler> getRestHandlerWrapper(ThreadContext threadContext) {
    return restHandler -> {
      this.logger.debug("in getRestHandlerWrapper: " + restHandler);
      RestHandler authRestHandler = new AuthRestHandler(restHandler);
      return authRestHandler;
    };
  }
}
