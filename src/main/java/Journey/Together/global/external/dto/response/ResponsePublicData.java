package Journey.Together.global.external.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePublicData<T> {
    private Response<T> response;

    @Data
    public static class Response<T> {
        private Header header;
        private Body<T> body;

        @Data
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Data
        public static class Body<T> {
            private Items<T> items;
            private int numOfRows;
            private int pageNo;
            private int totalCount;

            @Data
            public static class Items<T> {
                private List<T> item;
            }
        }
    }
}