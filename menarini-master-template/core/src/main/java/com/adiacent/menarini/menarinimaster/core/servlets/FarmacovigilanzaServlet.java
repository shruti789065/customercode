package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.resources.FarmacovigilanzaAPI;
import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.day.cq.wcm.api.NameConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.http.entity.mime.MIME.UTF8_CHARSET;


@Component(service = { Servlet.class }, immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={FarmacovigilanzaServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Call PIM api")

public class FarmacovigilanzaServlet extends SlingSafeMethodsServlet {
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "searchMedicineResult";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            if(request.getParameter("medicineName") != null){
                String medicineName = request.getParameter("medicineName");
                String endpoint = FarmacovigilanzaAPI.get_instanceAPI().getConfigApi().getPimApiEndpoint();
                String username = FarmacovigilanzaAPI.get_instanceAPI().getConfigApi().getApiUsername();
                String password = FarmacovigilanzaAPI.get_instanceAPI().getConfigApi().getApiPassword();
                String tokenKey = FarmacovigilanzaAPI.get_instanceAPI().getConfigApi().getTokenKey();
                JsonObject resultPimApi = callPim(medicineName,endpoint,username,password,tokenKey);
                response.setContentType(Constants.APPLICATION_JSON);
                response.getWriter().print(resultPimApi);
            }
        }catch (Exception e){
            logger.error("Error in search results Get call: ", e);
        }
    }

    private JsonObject callPim(String medicineName, String endpoint, String username, String password, String token) throws IOException, URISyntaxException {
        JsonObject res = null;

        if (StringUtils.isBlank(endpoint)){
            return null;
        }
        int timeout = 3000;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        String endpointLogin = endpoint+"/auth";

        HttpRequestBase request = new HttpPost(endpointLogin);

        URI uri = null;
        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        uriBuilder.addParameter("username", username);
        uriBuilder.addParameter("password", password);
        uri = uriBuilder.build();
        request.setURI(uri);
        request.addHeader("x-api-key",token);
        request.setHeader("content-type", "application/json");
        HttpResponse response = client.execute(request);
        String result = EntityUtils.toString(response.getEntity(), UTF8_CHARSET);
        JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

        if(jsonObject.has("success")){
            boolean isLogedin = jsonObject.get("success").getAsBoolean();
            if(isLogedin){
                HttpRequestBase requestAvailableSyncs = new HttpGet(endpoint+"/find-products");
                URI uriProducts = null;
                URIBuilder uriBuilderProducts = new URIBuilder(requestAvailableSyncs.getURI());
                uriBuilderProducts.addParameter("searchItem", medicineName);
                uriBuilderProducts.addParameter("searchType", "2");
                uriBuilderProducts.addParameter("searchOutput","short");
                uriProducts = uriBuilderProducts.build();
                requestAvailableSyncs.setURI(uriProducts);

                requestAvailableSyncs.addHeader("x-api-key",token);
                requestAvailableSyncs.setHeader("content-type", "application/json");

                HttpResponse responseFindProducts = client.execute(requestAvailableSyncs);
                if(responseFindProducts.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    String responseResult = EntityUtils.toString(responseFindProducts.getEntity(), UTF8_CHARSET);
                    JsonObject jsonProducts = JsonParser.parseString(responseResult).getAsJsonObject();
                    if(jsonProducts.has("pharmaceutical-forms")){
                        JsonArray pharma = jsonProducts.get("pharmaceutical-forms").getAsJsonArray();
                        JsonObject pharmaName = new JsonObject();
                        JsonArray jsonArray = new JsonArray();
                        for (int i = 0; i < pharma.size(); i++) {
                            JsonObject jo =  pharma.get(i).getAsJsonObject();
                            String internalName = jo.get("internalName").getAsString();
                            jsonArray.add(internalName);
                        }
                        pharmaName.add("name",jsonArray);
                        res = pharmaName;
                    }
                }
            }
        }
        return res;
    }
}
