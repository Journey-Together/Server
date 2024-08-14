package Journey.Together.domain.place.service;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataMigrationService {

    private final PlaceRepository placeRepository;

    private final RestHighLevelClient client;

    @Transactional
    public void migrateData() {
        List<Place> places = placeRepository.findAll();

        places.forEach(place -> {
            IndexRequest indexRequest = new IndexRequest("places")
                    .id(place.getId().toString())
                    .source(Map.of("name", place.getName()), XContentType.JSON);

            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }
}
