package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.models.RssItemModel;
import com.adiacent.menarini.menarinimaster.core.models.RssNewModel;
import com.day.cq.mailer.MailService;
import com.day.cq.replication.Replicator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Designate(ocd = RssNewsImporter.Config.class)
@Component(
        service = RssNewsImporter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= MMT - Rss News importer",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
public class RssNewsImporter implements Cloneable{
    private static final Logger logger = LoggerFactory.getLogger(RssNewsImporter.class);

    private static final String RSS_NEWS_IMPORTER_SUBSERVICE = "menarini-user";

    private static final String IMPORTER_USER = "Importer";
    private static final String LAST_IMPORT = "last_import";
    private static final String LAST_PRODUCT = "last_product";




    private SimpleDateFormat DF = new SimpleDateFormat("yyyyMMdd");

    @Reference
    private SlingSettingsService settingsService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile MailService mailService;
    private Config serviceConfig;


    boolean limitedImport;

    long lastRunningImport;

    private List<String> errors;

    @Activate
    @Modified
    protected void activate(final Config config) {
        logger.info("Activating MTT RSS News Importer");
        serviceConfig = config;

        logger.debug("Config rss news root path:{}", serviceConfig.getNewsRootPath());
        logger.debug("Config debug enabled:{}", serviceConfig.isDebugReportEnabled());
        logger.debug("Config debug report recipient{}", serviceConfig.getDebugReportRecipient());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void start() {
        logger.info("start rss feed importer ");
        //Si controlla che l'importer sia abilitato all'esecuzione
        if(serviceConfig.isNewsImportDisabled()){
            addErrors("Procedure not enabled ");
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
        }

        if(StringUtils.isBlank(serviceConfig.getRssFeedUrl())){
            addErrors("Rss Feed file Url is missing ");
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
        }

        RssNewModel data = getRssNewsData();
        if(data != null){
            List<RssItemModel> items = data.getChannel().getItems();
            if( items!= null){
                items.stream().forEach(i-> {
                    try {
                        if(i.getEnclosure() != null && StringUtils.isNotBlank(i.getEnclosure().getUrl()))
                            i.setImage(recoverImageFromUrl(i.getEnclosure().getUrl()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    logger.info(i.getTitle());
                });
            }


        }

    }

    private RssNewModel getRssNewsData() {
        URL url = null;
        try {
            url = new URL(serviceConfig.getRssFeedUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            InputStream inputStream = url.openConnection().getInputStream();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, StandardCharsets.UTF_8))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            //logger.info(textBuilder.toString());
            String txt = textBuilder.toString();
            txt = txt.substring(txt.indexOf("<rss"));
            txt = txt.replace("<dc:creator>","<dc_creator>");
            txt = txt.replace("</dc:creator>","</dc_creator>");
            XmlMapper xmlMapper = new XmlMapper();
            return  xmlMapper.readValue(
                    txt,
                    RssNewModel.class);

        } catch (MalformedURLException e) {
            addErrors(e.getMessage());
            return null;
        } catch (ProtocolException e) {
            addErrors(e.getMessage());
            return null;
        } catch (IOException e) {
            addErrors(e.getMessage());
            return null;
        }

    }


    public byte[] recoverImageFromUrl(String urlText) throws Exception {
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            if(inputStream != null)
                inputStream.close();
        }
        finally{
            if(output != null)
                output.close();
        }
        return output.toByteArray();
    }

    public List<String> getErrors() {
        if(errors == null)
            return null;
        String[] array = errors.toArray(new String[errors.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setErrors(List<String> errors) {
        if(errors== null)
            this.errors = null;
        else {
            String[] array = errors.toArray(new String[errors.size()]);
            String[] clone = array.clone();
            this.errors = Arrays.asList(clone);
        }
    }

    protected MailService getMailService() {
        return mailService;
    }

    public void addErrors(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getErrors() == null)
                this.errors = new ArrayList<String>();
            this.errors.add(txt);
        }
    }

    protected void sendResult() {
        String result = "Result : Ok";
        if(errors != null  && errors.size() > 0){
            result = errors.stream().collect(Collectors.joining("\n"));
        }

        Email mail = new HtmlEmail();
        try {
            mail.setMsg(result);
            mail.setSubject("Fine importazione dnn library ");
            mail.addTo(serviceConfig.getDebugReportRecipient());
        /*for (String copyto : config.getDebugReportRecipientCopyTo()) {
            if (org.apache.commons.lang.StringUtils.isNotEmpty(copyto)) {
                logger.info("sendEmail Send Copy To " + copyto);
                mail.addCc(copyto);
            }
        }*/
            mail.setCharset("UTF-8");
            getMailService().send(mail);
        } catch (Exception e) {
            logger.error("Error sending report email", e);

        }



    }

    @ObjectClassDefinition(name = "RSS News Importer", description = "RSS News  Importer")
    public static @interface Config {

        @AttributeDefinition(name = "News Root Path", description = "Imported news root path")
        String getNewsRootPath() default "/content/menarini-berlinchemie/de/news";

        @AttributeDefinition(name = "Rss feed url", description = "RSS Feed url")
        String getRssFeedUrl() default "";

        @AttributeDefinition(name = "DAM News Images Root Folder", description = "DAM root folder where imported news images will be placed")
        String getNewsImagesDAMFolder() default "/content/dam/menarini-berlinchemie/assets/news";

        @AttributeDefinition(name = "Debug Report Enabled", description = "Enable debug report email")
        boolean isDebugReportEnabled() default false;

        @AttributeDefinition(name = "Debug Report Subject", description = "Debug email report subject")
        String getDebugReportSubject() default "[MTT] RSS News Import Procedure";

        @AttributeDefinition(name = "Debug Report Recipient", description = "Debug email report recipient")
        String getDebugReportRecipient() default "";

        @AttributeDefinition(name = "Debug Report Recipient Copyto", description = "Debug email report recipient Copyto")
        String[] getDebugReportRecipientCopyTo() default {};

        @AttributeDefinition(name = "Number of last days to import news data from", description = "Number of last days to import news data from")
        int getImportNewsDays() default 1;

        @AttributeDefinition(name = "Disable News Import", description = "Disable News Import")
        boolean isNewsImportDisabled() default false;
    }
}
