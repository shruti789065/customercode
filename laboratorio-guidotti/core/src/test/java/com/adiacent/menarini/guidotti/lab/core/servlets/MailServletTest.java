package com.adiacent.menarini.guidotti.lab.core.servlets;
import com.day.cq.mailer.MailService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MailServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private MailServlet servlet;

    @Mock
    MailService mailService;
    @Mock
    private Query query;

    @Mock
    private QueryBuilder qBuilder;
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Test
    void doPost(AemContext context) throws ServletException, IOException, RepositoryException {
        InputStream is = MailServletTest.class.getResourceAsStream("/com/adiacent/menarini/guidotti/lab/core/models/contactsPage.json");
        ctx.load().json(is, "/content/laboratorio-guidotti/it/contattaci.html");

        Resource c = spy(ctx.currentResource("/content/laboratorio-guidotti/it/contattaci.html"));

        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MailServlet.MAIL_PROPERTY,"test@adiacent.com");
        params.put("Telephone","");
        params.put("address","");
        params.put("city","");
        params.put("family-name","");
        params.put("g-recaptcha-response","SAMPLE_TOKEN");
        params.put("information","");
        params.put("job-title","");
        params.put("message","");
        params.put("myfile","");
        params.put("name","");
        params.put("personal-data","");
        params.put("privacy","");
        params.put("zip","");
        params.put(":redirect","home.html");
        params.put("resourcePath","/content/laboratorio-guidotti/it/contattaci/jcr:content/root/container/container/container");
        params.put("_crypted-value_","LNqKzrCnF+YnBpyFWD48mAR9FKdepJmayfFvRfePZj4=");
        request.setParameterMap(params);


        lenient().when(qBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        List<Hit> listHits = new ArrayList<>();
        Hit hit = mock(Hit.class);
        listHits.add(hit);
        SearchResult searchResults = mock(SearchResult.class);
        lenient().when(query.getResult()).thenReturn(searchResults);
        lenient().when(searchResults.getHits()).thenReturn(listHits);
        List<String> paths = new ArrayList<String>();
        paths.add("/content/laboratorio-guidotti/it/contattaci.html/jcr:content/root/container/container/container");
        paths.add("/content/laboratorio-guidotti/it/contattaci.html/jcr:content/root/container/container/container/options");
        paths.add("/content/laboratorio-guidotti/it/contattaci.html/jcr:content/root/container/container/container/options/items/item0");

        final int[] count = {0};
        when(hit.getPath()).thenAnswer(
                new Answer<String>()
                {
                    @Override
                    public String answer(final InvocationOnMock invocation) throws Throwable
                    {
                        count[0]++;
                        return paths.get(count[0] -1);
                    }
                }
        );

        ctx.registerService(MailService.class);
        ctx.registerService(MailService.class,mailService);
        ctx.registerService(QueryBuilder.class,qBuilder);
        servlet = spy(ctx.registerInjectActivateService(new MailServlet()));
        doReturn(params.keySet().iterator()).when(servlet).getRequestParamIterators(any(SlingHttpServletRequest.class));
        doReturn(new ArrayList<Resource>().iterator()).when(servlet).getResourceFormElements(any(SlingHttpServletRequest.class));

        lenient().doReturn(c).when(spy(request)).getResource();
        lenient().doReturn(ctx.resourceResolver()).when(c).getResourceResolver();
        doReturn("SAMPLE_PRIVATE_KEY").when(servlet).getSecretKey(any());
        //oppure:
        CloseableHttpClient httpclient =  mock(CloseableHttpClient.class);
        doReturn(httpclient).when(servlet).getHttpClient();
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        doReturn(httpResponse).when(httpclient).execute(any(HttpUriRequest.class));
        HttpEntity entity = mock(HttpEntity.class);
        doReturn(entity).when(httpResponse).getEntity();
        InputStream inputStream = mock(InputStream.class);
        doReturn(inputStream).when(entity).getContent();
        String inputString = "{\"success\": true}";
        byte[] byteArrray = inputString.getBytes();
        doReturn(byteArrray).when(inputStream).readAllBytes();

        servlet.doPost(request, response);
        boolean res= response.getBufferSize() > 0;
        assertTrue(res);
    }
}
