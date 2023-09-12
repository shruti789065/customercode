package com.adiacent.menarini.mhos.core.business;

import com.adiacent.menarini.mhos.core.models.ContentFragmentModel;

import com.adiacent.menarini.mhos.core.models.ContentFragmentResponseModel;

import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adiacent.menarini.mhos.core.resources.SlingSettingsUtils;
import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentFragmentApi {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentApi.class);

    private  static final int DEFAULT_API_TIMEOUT = 30000;
    private  static final long API_WARNING_TIME = 1000;

    private static final String POST_TYPE = "POST";
    private static final String GET_TYPE = "GET";
    private static final String DELETE_TYPE = "DELETE";
    private static final String PATCH_TYPE = "PATCH";
    private static final String ENDPOINT_PREFIX = "api/assets/";

    protected boolean isLocalRunModeEnabled() {
        return SlingSettingsUtils.get_instance().checkLocalMode();
    }

    protected String performOperation(String methodType, String url, Map<String,String> headers, Map<String,String> params, Object payload, int timeout, String timeLogging)  {

        if (StringUtils.isBlank(url))
            return null;

        String res = null;

        List<NameValuePair> queryParams;


        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout > 0 ? timeout : DEFAULT_API_TIMEOUT)
                .setSocketTimeout(timeout > 0 ? timeout : DEFAULT_API_TIMEOUT).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        HttpRequestBase request = null;

        if(POST_TYPE.equals(methodType))
            request = new HttpPost(url);
        if(GET_TYPE.equals(methodType))
            request = new HttpGet(url);
        if(DELETE_TYPE.equals(methodType))
            request = new HttpDelete(url);
        if(PATCH_TYPE.equals(methodType))
            request = new HttpPatch(url);

        //authentication
        String name = ImportLibraryResource.get_instance().getConfig().getUsername();
        String password = ImportLibraryResource.get_instance().getConfig().getPwd();
        String authString = name + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                authString .getBytes(Charset.forName("US-ASCII")) );
        request.setHeader("Authorization", "Basic " +  new String( encodedAuth ));


        request.setHeader("accept", "application/json");


        if(headers != null)
            for (String key : headers.keySet()) {
                request.setHeader(key, headers.get(key));
            }


        try {

            URI uri = null;
            if (params != null && !params.isEmpty()) {
                queryParams = params.entrySet().stream().map(e -> new BasicNameValuePair(e.getKey(), e.getValue())).collect(Collectors.toList());
                uri = new URIBuilder(request.getURI()).addParameters(queryParams).build();
            } else {
                uri = new URIBuilder(request.getURI()).build();
            }

            request.setURI(uri);

            if (payload != null) {
                request.setHeader("Content-type", "application/json");

                StringEntity body = new StringEntity(payload.toString(),"UTF-8");
                if(PATCH_TYPE.equals(methodType))
                    ((HttpPatch) request).setEntity(body);
                else
                    ((HttpPost) request).setEntity(body);
            }

            HttpResponse response = client.execute(request);
            if(response != null){

                if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode() ||
                        HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()
                )
                    res = EntityUtils.toString(response.getEntity());
                else
                    if(HttpStatus.SC_INTERNAL_SERVER_ERROR == response.getStatusLine().getStatusCode())
                        res=null;
                    else
                        if(HttpStatus.SC_NOT_FOUND == response.getStatusLine().getStatusCode())
                            res = null;
                        else {
                            LOG.info("******" + response.getStatusLine().getStatusCode());
                            LOG.info("******PAYLOAD " + payload) ;
                        }
            }
            LOG.info("Response: {}", res);

        } catch (org.apache.http.ParseException | IOException e) {
            LOG.error(e.getMessage(), e);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return res;

    }

/*
     private Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<ContentFragmentWrapper> deserializer = new JsonDeserializer<ContentFragmentWrapper>() {

            @Override
            public ContentFragmentWrapper deserialize(JsonElement json, Type typeOfT,
                                                    JsonDeserializationContext context) {

                JsonObject jsonObject = json.getAsJsonObject();

                JsonObject properties = jsonObject.get("properties").getAsJsonObject();
                String model = properties.get("cq:model").getAsJsonObject().get("path").getAsString();
                String title = properties.get("title").getAsString();
                String description = properties.get("description").getAsString();

                ContentFragmentWrapper res = new ContentFragmentWrapper();

                res.setDescription(description);
                res.setCqModel(model);
                res.setTitle(title);


                JsonObject elements = properties.get("elements").getAsJsonObject();
                String linkLabel = elements.get("linkLabel").getAsJsonObject().get("value").getAsString();
                return res;

                }
        };
        gsonBuilder.registerTypeAdapter(ContentFragmentWrapper.class, deserializer);
        Gson customGson = gsonBuilder.create();
        return customGson;
    }
*/
   public ContentFragmentModel getByPath(String serverName, int serverPort, String jsonPath) {

        String endpoint = ( isLocalRunModeEnabled() ? "http://localhost:4502" : "https://"+serverName+":"+serverPort ) + "/" + ENDPOINT_PREFIX + jsonPath ;

        String res = performOperation(GET_TYPE, endpoint, null,  null, null ,0, null);

        // ContentFragmentWrapper w = getGson().fromJson(res, ContentFragmentWrapper.class);

        ContentFragmentModel response = new GsonBuilder().create().fromJson(res, ContentFragmentModel.class);

        return response;
    }


    public boolean create(ContentFragmentModel obj, String path) {

        HashMap<String,String> headers = new HashMap<String,String>();
        headers.put("Content-Type", "application/json");
        String res = performOperation(POST_TYPE, path, headers,  null, obj ,0, null);

        ContentFragmentResponseModel response = new GsonBuilder().create().fromJson(res, ContentFragmentResponseModel.class);

        return response != null && response.getProperties()!= null && response.getProperties().getCreated();
    }

}
