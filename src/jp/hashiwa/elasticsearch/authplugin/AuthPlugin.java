package jp.hashiwa.elasticsearch.authplugin;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.bootstrap.BootstrapCheck;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.io.stream.NamedWriteable;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ExecutorBuilder;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * refer:
 * - https://github.com/codelibs/elasticsearch-auth/blob/master/src/main/java/org/codelibs/elasticsearch/auth/AuthPlugin.java
 * Created by hashiwa on 17/07/08.
 */
//public class AuthPlugin implements SearchPlugin {
public class AuthPlugin extends Plugin {
  private Settings settings;
  private final Logger logger = Loggers.getLogger(AuthPlugin.class);

  public AuthPlugin(Settings settings) {
    this.settings = settings;
    this.logger.info(settings.toString());
  }

  /**
   * Node level guice modules.
   */
  public Collection<Module> createGuiceModules() {
    return Collections.emptyList();
  }

  /**
   * Node level services that will be automatically started/stopped/closed. This classes must be constructed
   * by injection with guice.
   */
//  public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
//    return Collections.emptyList();
//  }
  public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
    return Arrays.asList(AuthLifecycleComponent.class);
  }

  /**
   * Returns components added by this plugin.
   *
   * Any components returned that implement {@link LifecycleComponent} will have their lifecycle managed.
   * Note: To aid in the migration away from guice, all objects returned as components will be bound in guice
   * to themselves.
   *
   * @param client AuthLifecycleComponent client to make requests to the system
   * @param clusterService AuthLifecycleComponent service to allow watching and updating cluster state
   * @param threadPool AuthLifecycleComponent service to allow retrieving an executor to run an async action
   * @param resourceWatcherService AuthLifecycleComponent service to watch for changes to node local files
   * @param scriptService AuthLifecycleComponent service to allow running scripts on the local node
   */
  public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
                                             ResourceWatcherService resourceWatcherService, ScriptService scriptService,
                                             NamedXContentRegistry xContentRegistry) {
    return Collections.emptyList();
  }

  /**
   * Additional node settings loaded by the plugin. Note that settings that are explicit in the nodes settings can't be
   * overwritten with the additional settings. These settings added if they don't exist.
   */
  public Settings additionalSettings() {
    return Settings.Builder.EMPTY_SETTINGS;
  }

  /**
   * Returns parsers for {@link NamedWriteable} this plugin will use over the transport protocol.
   * @see NamedWriteableRegistry
   */
  public List<NamedWriteableRegistry.Entry> getNamedWriteables() {
    return Collections.emptyList();
  }

  /**
   * Returns parsers for named objects this plugin will parse from {@link XContentParser#namedObject(Class, String, Object)}.
   * @see NamedWriteableRegistry
   */
  public List<NamedXContentRegistry.Entry> getNamedXContent() {
    return Collections.emptyList();
  }

  /**
   * Called before a new index is created on a node. The given module can be used to register index-level
   * extensions.
   */
  public void onIndexModule(IndexModule indexModule) {}

  /**
   * Returns a list of additional {@link Setting} definitions for this plugin.
   */
  public List<Setting<?>> getSettings() { return Collections.emptyList(); }

  /**
   * Returns a list of additional settings filter for this plugin
   */
  public List<String> getSettingsFilter() { return Collections.emptyList(); }

  /**
   * Provides a function to modify global custom meta data on startup.
   * <p>
   * Plugins should return the input custom map via {@link UnaryOperator#identity()} if no upgrade is required.
   * <p>
   * The order of custom meta data upgraders calls is undefined and can change between runs so, it is expected that
   * plugins will modify only data owned by them to avoid conflicts.
   * <p>
   * @return Never {@code null}. The same or upgraded {@code MetaData.Custom} map.
   * @throws IllegalStateException if the node should not start because at least one {@code MetaData.Custom}
   *                               is unsupported
   */
  public UnaryOperator<Map<String, MetaData.Custom>> getCustomMetaDataUpgrader() {
    return UnaryOperator.identity();
  }

  /**
   * Provides a function to modify index template meta data on startup.
   * <p>
   * Plugins should return the input template map via {@link UnaryOperator#identity()} if no upgrade is required.
   * <p>
   * The order of the template upgrader calls is undefined and can change between runs so, it is expected that
   * plugins will modify only templates owned by them to avoid conflicts.
   * <p>
   * @return Never {@code null}. The same or upgraded {@code IndexTemplateMetaData} map.
   * @throws IllegalStateException if the node should not start because at least one {@code IndexTemplateMetaData}
   *                               cannot be upgraded
   */
  public UnaryOperator<Map<String, IndexTemplateMetaData>> getIndexTemplateMetaDataUpgrader() {
    return UnaryOperator.identity();
  }

  /**
   * Provides a function to modify index meta data when an index is introduced into the cluster state for the first time.
   * <p>
   * Plugins should return the input index metadata via {@link UnaryOperator#identity()} if no upgrade is required.
   * <p>
   * The order of the index upgrader calls for the same index is undefined and can change between runs so, it is expected that
   * plugins will modify only indices owned by them to avoid conflicts.
   * <p>
   * @return Never {@code null}. The same or upgraded {@code IndexMetaData}.
   * @throws IllegalStateException if the node should not start because the index is unsupported
   */
  public UnaryOperator<IndexMetaData> getIndexMetaDataUpgrader() {
    return UnaryOperator.identity();
  }

  /**
   * Provides the list of this plugin's custom thread pools, empty if
   * none.
   *
   * @param settings the current settings
   * @return executors builders for this plugin's custom thread pools
   */
  public List<ExecutorBuilder<?>> getExecutorBuilders(Settings settings) {
    return Collections.emptyList();
  }

  /**
   * Returns a list of checks that are enforced when a node starts up once a node has the transport protocol bound to a non-loopback
   * interface. In this case we assume the node is running in production and all bootstrap checks must pass. This allows plugins
   * to provide a better out of the box experience by pre-configuring otherwise (in production) mandatory settings or to enforce certain
   * configurations like OS settings or 3rd party resources.
   */
  public List<BootstrapCheck> getBootstrapChecks() { return Collections.emptyList(); }

  /**
   * Close the resources opened by this plugin.
   *
   * @throws IOException if the plugin failed to close its resources
   */
  @Override
  public void close() throws IOException {

  }
}

