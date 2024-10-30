package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.RegisteredUserDto;

public interface CacheDataServiceInterface {


    public RegisteredUserDto getUserCacheData( String userKey);
    public boolean putUserCacheData(String userKey, RegisteredUserDto userData);
    public Object getGenericData(String key);
    public boolean putGenericData(String key, Object data);
    public void clearCacheData(String key);

}
