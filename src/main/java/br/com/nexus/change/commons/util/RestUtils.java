package br.com.nexus.change.commons.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestUtils {

    public static URI getUri(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri();
    }
}