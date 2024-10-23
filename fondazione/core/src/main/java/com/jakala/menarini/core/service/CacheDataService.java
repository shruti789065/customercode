package com.jakala.menarini.core.service;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.service.interfaces.CacheDataServiceInterface;
import com.jakala.menarini.core.service.utils.CacheDataConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import java.util.concurrent.TimeUnit;


@Component(
        service = CacheDataServiceInterface.class,
        immediate = true
)
@Designate(ocd = CacheDataConfiguration.class)
public class CacheDataService implements CacheDataServiceInterface {



  private Cache<String,Object> cache;


    @Activate
    private void activate(CacheDataConfiguration config) {
        this.cache =  Caffeine.newBuilder()
                .maximumSize(config.maxSize())
                .expireAfterAccess(config.maxTtl(), TimeUnit.MINUTES)
                .build();

    }

    @Override
    public RegisteredUserDto getUserCacheData(String userKey) {
        return  (RegisteredUserDto) this.cache.getIfPresent(userKey);
    }

    @Override
    public boolean putUserCacheData(String userKey, RegisteredUserDto userData) {
        try {
            cache.put(userKey,userData);
            return true;
        }catch (NullPointerException e) {
            return false;
        }

    }

    @Override
    public Object getGenericData(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public boolean putGenericData(String key, Object data) {
        try {
            cache.put(key,data);
            return true;
        }catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void clearCacheData(String key) {
        cache.invalidate(key);
    }

}
