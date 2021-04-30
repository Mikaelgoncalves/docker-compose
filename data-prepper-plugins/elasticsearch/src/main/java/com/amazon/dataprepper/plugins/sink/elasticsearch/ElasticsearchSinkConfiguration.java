package com.amazon.dataprepper.plugins.sink.elasticsearch;

import com.amazon.dataprepper.model.configuration.PluginSetting;

import static com.google.common.base.Preconditions.checkNotNull;

public class ElasticsearchSinkConfiguration {
  /**
   * TODO: add retryConfiguration
   */
  private final ConnectionConfiguration connectionConfiguration;
  private final IndexConfiguration indexConfiguration;
  private final RetryConfiguration retryConfiguration;

  public ConnectionConfiguration getConnectionConfiguration() {
    return connectionConfiguration;
  }

  public IndexConfiguration getIndexConfiguration() {
    return indexConfiguration;
  }

  public RetryConfiguration getRetryConfiguration() {
    return retryConfiguration;
  }

  private ElasticsearchSinkConfiguration(
          final ConnectionConfiguration connectionConfiguration, final IndexConfiguration indexConfiguration,
          final RetryConfiguration retryConfiguration) {
    checkNotNull(connectionConfiguration, "connectionConfiguration cannot be null");
    checkNotNull(indexConfiguration, "indexConfiguration cannot be null");
    checkNotNull(retryConfiguration, "retryConfiguration cannot be null");
    this.connectionConfiguration = connectionConfiguration;
    this.indexConfiguration = indexConfiguration;
    this.retryConfiguration = retryConfiguration;
  }

  public static ElasticsearchSinkConfiguration readESConfig(final PluginSetting pluginSetting) {
    final ConnectionConfiguration connectionConfiguration =
            ConnectionConfiguration.readConnectionConfiguration(pluginSetting);
    final IndexConfiguration indexConfiguration = IndexConfiguration.readIndexConfig(pluginSetting);
    final RetryConfiguration retryConfiguration = RetryConfiguration.readRetryConfig(pluginSetting);

    return new ElasticsearchSinkConfiguration(connectionConfiguration, indexConfiguration, retryConfiguration);
  }
}
