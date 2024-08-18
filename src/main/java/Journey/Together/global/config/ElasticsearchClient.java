package Journey.Together.global.config;

import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ElasticsearchClient {

    public static RestHighLevelClient createClient() {
        // Elasticsearch 클러스터 호스트와 포트 설정
        HttpHost host = new HttpHost("localhost", 9200, "http");
        
        // 인증 정보를 설정
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));

        // RestClientBuilder 생성
        RestClientBuilder builder = RestClient.builder(host)
            .setHttpClientConfigCallback(httpClientBuilder -> {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });

        // RestHighLevelClient 생성
        return new RestHighLevelClient(builder);
    }
}
