package com.xuegong.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AiProviderFactory {

    @Value("${ai.provider:zhipu}")
    private String defaultProvider;

    private final Map<String, AiProvider> providers;

    public AiProviderFactory(List<AiProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AiProvider::getName, Function.identity()));
        log.info("已加载 AI 提供者: {}", providers.keySet());
    }

    /**
     * 获取默认提供者
     */
    public AiProvider getProvider() {
        return getProvider(defaultProvider);
    }

    /**
     * 获取指定提供者
     */
    public AiProvider getProvider(String name) {
        AiProvider provider = providers.get(name);
        if (provider == null) {
            log.warn("AI 提供者 '{}' 不存在，使用默认提供者 '{}'", name, defaultProvider);
            provider = providers.get(defaultProvider);
        }
        if (provider != null && !provider.isAvailable()) {
            log.warn("AI 提供者 '{}' 不可用，尝试其他可用提供者", provider.getName());
            provider = providers.values().stream()
                    .filter(AiProvider::isAvailable)
                    .findFirst()
                    .orElse(null);
        }
        return provider;
    }

    /**
     * 获取所有可用的提供者
     */
    public List<AiProvider> getAvailableProviders() {
        return providers.values().stream()
                .filter(AiProvider::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有提供者（包括不可用的）
     */
    public List<AiProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }
}
