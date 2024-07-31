package com.sparta.myselectshop.naver.service;

import com.sparta.myselectshop.naver.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "NAVER API")
@Service
public class NaverApiService {

	private final String clientId;
	private final String clientSecret;
	private final RestTemplate restTemplate;

	// 생성자 주입
	public NaverApiService(@Value("${naver.client.id}") String clientId,
	                       @Value("${naver.client.secret}") String clientSecret,
	                       RestTemplate restTemplate) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.restTemplate = restTemplate;
	}

	public List<ItemDto> searchItems(String query) throws JSONException {
		// 요청 URL 만들기
		URI uri = UriComponentsBuilder
				.fromUriString("https://openapi.naver.com")
				.path("/v1/search/shop.json")
				.queryParam("display", 15)
				.queryParam("query", query)
				.encode()
				.build()
				.toUri();
		log.info("uri = " + uri);

		RequestEntity<Void> requestEntity = RequestEntity
				.get(uri)
				.header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret)
				.build();

		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

		log.info("NAVER API Status Code : " + responseEntity.getStatusCode());

		return fromJSONtoItems(responseEntity.getBody());
	}

	public List<ItemDto> fromJSONtoItems(String responseEntity) throws JSONException {
		JSONObject jsonObject = new JSONObject(responseEntity);
		JSONArray items  = jsonObject.getJSONArray("items");
		List<ItemDto> itemDtoList = new ArrayList<>();

		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			ItemDto itemDto = new ItemDto(item);
			itemDtoList.add(itemDto);
		}

		return itemDtoList;
	}
}