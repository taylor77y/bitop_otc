package com.bitop.otcapi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bitopotc")
public class BitopOTConfig
{

    private String name;


    private static String profile;


    private static boolean addressEnabled;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        BitopOTConfig.profile = profile;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        BitopOTConfig.addressEnabled = addressEnabled;
    }


    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }


    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }


    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}
