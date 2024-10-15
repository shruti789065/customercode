package com.jakala.menarini.core.service;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.aswLambdaDto.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaGetFileDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaImageDataDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaPutFileDto;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;
import io.jsonwebtoken.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

@Component(
        service = ImageProfileServiceInterface.class,
        immediate = true
)
public class ImageProfileService implements ImageProfileServiceInterface {


    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProfileService.class);

    private String apiUrl;
    private long maxHeight;
    private long maxWidth;
    private long maxWeight;

    @Activate
    private void activate(Map<String, Object> properties) {
        this.apiUrl = (String) properties.get("api_url");
        this.maxHeight = Long.parseLong((String)properties.get("max_height"));
        this.maxWidth = Long.parseLong((String)properties.get("max_width"));
        this.maxWeight = Long.parseLong((String)properties.get("max_weight"));
    }


    @Override
    public ImageProfileServiceResponseDto getImageProfile(LambdaGetFileDto getFileDto, String token) {
        Gson gson = new Gson();
        HttpUriRequest getRequest = RequestBuilder.get(this.apiUrl)
                .setHeader("Authorization", "Bearer " + token)
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setEntity(new StringEntity(gson.toJson(getFileDto), StandardCharsets.UTF_8))
                .build();
        ImageProfileServiceResponseDto imgResponse = this.parseResponse(getRequest);
        Map imageResponseMap = gson.fromJson(imgResponse.getImageData(), Map.class);
        String body = imageResponseMap.get("body").toString();
        Map imageBodyMap = gson.fromJson(body, Map.class);
        if(imageBodyMap.get("imageData") == null) {
            imgResponse.setImageData("");
            return imgResponse;
        }
        LambdaImageDataDto imageDataDto = gson.fromJson(imageBodyMap.get("imageData").toString(), LambdaImageDataDto.class);
        imgResponse.setImageData(new String(imageDataDto.getData(), StandardCharsets.UTF_8));
        return imgResponse;
    }

    @Override
    public ImageProfileServiceResponseDto saveImageProfile(LambdaPutFileDto putFileDto, String token) {
        Gson gson = new Gson();
        ImageProfileServiceResponseDto response = new ImageProfileServiceResponseDto();
        response.setSuccess(Boolean.TRUE);
        String imageData = putFileDto.getImageData();
        HashMap<Boolean, String> imageCheck = (HashMap<Boolean, String>) this.checkIntegrityImage(imageData);
        if(imageCheck.containsKey(false)) {
            response.setSuccess(Boolean.FALSE);
            response.setErrorMessage(imageCheck.get(false));
            return response;
        }
        HttpPost postImage = new HttpPost(this.apiUrl);
        postImage.setHeader("Authorization", "Bearer " + token);
        postImage.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        postImage.setEntity(new StringEntity(gson.toJson(putFileDto), StandardCharsets.UTF_8));
        return this.parseResponse(postImage);
    }

    private ImageProfileServiceResponseDto parseResponse(HttpUriRequest request)  {
        ImageProfileServiceResponseDto response = new ImageProfileServiceResponseDto();
        response.setSuccess(Boolean.TRUE);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        try(CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig).build()) {

            final CloseableHttpResponse awsResponse = httpClient.execute(request);
            StringBuffer responseString = this.readHttpResponse(awsResponse);
            if(awsResponse.getStatusLine().getStatusCode() != 200) {
                response.setSuccess(Boolean.FALSE);
                response.setErrorMessage(responseString.toString());
                return response;
            }

            response.setImageData(responseString.toString());
        }catch (java.io.IOException e) {
            response.setSuccess(Boolean.FALSE);
            response.setErrorMessage(e.getMessage());
            return response;
        }
        return response;
    }

    private StringBuffer readHttpResponse(CloseableHttpResponse httpResponse) throws IOException, java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputBuffer;
        StringBuffer responseString = new StringBuffer();

        while ((inputBuffer = reader.readLine()) != null) {
            responseString.append(inputBuffer);
        }
        return responseString;
    }

    private Map<Boolean,String> checkIntegrityImage(String image) {
        HashMap<Boolean,String> checkResult = new HashMap<>();
        final byte[] imageBufferData = DatatypeConverter.parseBase64Binary(image);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBufferData);
        int size = imageBufferData.length;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            if(bufferedImage == null) {
                checkResult.put(Boolean.FALSE,"Image is not valid");
                return checkResult;
            }
            if(bufferedImage.getWidth() > this.maxWidth || 
                    bufferedImage.getHeight() > this.maxHeight) {
                checkResult.put(Boolean.FALSE,"Image must be " + this.maxWidth + " x " + this.maxHeight);
            } else if (size > this.maxWeight) {
                checkResult.put(Boolean.FALSE,"Image great than " + this.maxWeight);
            } else {
                checkResult.put(Boolean.TRUE,"Image is valid");
            }
        } catch (java.io.IOException e) {
            checkResult.put(Boolean.FALSE, e.getMessage());
            return checkResult;
        }
        return checkResult;


    }


    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public long getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(long maxHeight) {
        this.maxHeight = maxHeight;
    }

    public long getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(long maxWidth) {
        this.maxWidth = maxWidth;
    }

    public long getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }
}
