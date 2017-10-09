package jp.hashiwa.elasticsearch.authplugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.component.Lifecycle;
import org.elasticsearch.common.component.LifecycleListener;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

//class AuthLifecycleComponent implements LifecycleComponent {
public class AuthLifecycleComponent extends AbstractLifecycleComponent {
  private final Logger logger = Loggers.getLogger(AuthLifecycleComponent.class);

  @Inject
  public AuthLifecycleComponent(Settings settings) {
    super(settings);
  }

  /**
   * Close the resources opened by this plugin.
   *
   * @throws IOException if the plugin failed to close its resources
   */
  @Override
  public void close() {
    this.logger.info("close");

  }

  @Override
  protected void doClose() throws IOException {
    this.logger.info("doClose");

  }

  @Override
  public Lifecycle.State lifecycleState() {
    this.logger.info("lifecycleState");
    return null;
  }

  @Override
  public void addLifecycleListener(LifecycleListener lifecycleListener) {
    this.logger.info("addLifecycleListener");
  }

  @Override
  public void removeLifecycleListener(LifecycleListener lifecycleListener) {
    this.logger.info("removeLifecycleListener");
  }

  @Override
  public void start() {
    this.logger.info("start");
  }

  @Override
  protected void doStart() {
    this.logger.info("doStart");

  }

  @Override
  public void stop() {
    this.logger.info("stop");
  }

  @Override
  protected void doStop() {
    this.logger.info("doStop");

  }
}