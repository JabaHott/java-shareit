package ru.practicum.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.RequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, RequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getAllIRequests(Long userId, Integer from, Integer size) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        if (from != null) {
            parameters.put("from", from);
            sb.append("from={from}&");
        }

        if (size != null) {
            parameters.put("size", size);
            sb.append("size={size}");
        }

        return get("/all?" + sb, userId, parameters);
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}