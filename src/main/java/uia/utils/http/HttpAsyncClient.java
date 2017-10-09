package uia.utils.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class HttpAsyncClient {

    private int retryCount;

    private String url;

    private CloseableHttpAsyncClient client;

    private Map<String, String> headersDefault;

    public HttpAsyncClient(String url) {
        this(url, null);
    }

    public HttpAsyncClient(String url, Map<String, String> headersDefault) {
        this.retryCount = 3;
        this.url = url;
        this.client = HttpAsyncClients.createDefault();
        this.headersDefault = headersDefault;

        this.client.start();
    }

    public void shutdown() throws IOException {
        this.client.close();
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void get(String action, FutureCallback<HttpClientResponse> callback) throws IOException {
        get(action, null, callback);
    }

    public void get(String action, Map<String, String> headersOthers, FutureCallback<HttpClientResponse> callback) throws IOException {
        HttpGet getMethod = new HttpGet(this.url + action);
        getMethod.addHeader("accept", "application/json");
        execute(getMethod, headersOthers, callback);
    }

    public void postJson(String action, String json, FutureCallback<HttpClientResponse> callback) throws IOException {
        postJson(action, json, null, callback);
    }

    public void postJson(String action, String json, Map<String, String> headersOthers, FutureCallback<HttpClientResponse> callback) throws IOException {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(this.url + action);
        postMethod.setEntity(requestEntity);
        execute(postMethod, headersOthers, callback);
    }

    public void postXml(String action, String xml, FutureCallback<HttpClientResponse> callback) throws IOException {
        postXml(action, xml, null, callback);
    }

    public void postXml(String action, String xml, Map<String, String> headersOthers, FutureCallback<HttpClientResponse> callback) throws IOException {
        StringEntity requestEntity = new StringEntity(xml, ContentType.APPLICATION_XML);
        HttpPost postMethod = new HttpPost(this.url + action);
        postMethod.setEntity(requestEntity);
        execute(postMethod, headersOthers, callback);
    }

    public void delete(String action, FutureCallback<HttpClientResponse> callback) throws IOException {
        delete(action, callback);
    }

    public void delete(String action, Map<String, String> headersOthers, FutureCallback<HttpClientResponse> callback) throws IOException {
        HttpDelete deleteMethod = new HttpDelete(this.url + action);
        deleteMethod.addHeader("accept", "application/json");
        execute(deleteMethod, headersOthers, callback);
    }

    private void execute(HttpUriRequest request, Map<String, String> headersOthers, final FutureCallback<HttpClientResponse> callback) throws IOException {
        if (this.headersDefault != null) {
            this.headersDefault.forEach(request::addHeader);
        }
        if (headersOthers != null) {
            headersOthers.forEach(request::addHeader);
        }

        int rc = this.retryCount;
        while (true) {
            rc--;
            try {
                this.client.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void cancelled() {
                        callback.cancelled();
                    }

                    @Override
                    public void completed(HttpResponse response) {
                        callback.completed(new HttpClientResponse(response));
                    }

                    @Override
                    public void failed(Exception ex) {
                        callback.failed(ex);
                    }

                });
                break;
            }
            catch (Exception ex) {
                if (rc < 1) {
                    throw ex;
                }
            }
        }
    }
}
