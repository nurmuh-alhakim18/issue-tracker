package com.alhakim.issuetracker.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.alhakim.issuetracker.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public PaginatedResponse<IssueResponse> search(IssueSearchRequest request) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        if (isNotBlank(request.getQuery())) {
            boolQuery.must(buildMultiMatchQuery(request.getQuery()));
        }

        if (isNotBlank(request.getStatus())) {
            boolQuery.filter(buildTermQuery(request.getStatus().toUpperCase(), "status"));
        }

        if (isNotBlank(request.getPriority())) {
            boolQuery.filter(buildTermQuery(request.getPriority().toUpperCase(), "priority"));
        }

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            boolQuery.filter(buildTermsQuery(request.getTags()));
        }

        List<SortOptions> sortOptions = new ArrayList<>();
        if (isNotBlank(request.getOrderBy())) {
            String orderBy = request.getOrderBy();
            SortOrder direction = isNotBlank(request.getDirection()) && request.getDirection().equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc;
            sortOptions.add(SortOptions.of(s -> s
                    .field(f -> f
                            .field(orderBy)
                            .order(direction)
                    )
            ));
        }

        int pageIndex = Math.max(0, request.getPage() - 1);
        int pageSize = Math.max(1, request.getSize());
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("issues")
                .query(boolQuery.build())
                .sort(sortOptions)
                .from(pageIndex * pageSize)
                .size(pageSize)
        );

        try {
            co.elastic.clients.elasticsearch.core.SearchResponse<IssueIndex> result = elasticsearchClient.search(searchRequest, IssueIndex.class);
            return mapToPaginatedResponse(result, pageIndex, pageSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.isEmpty();
    }

    private Query buildMultiMatchQuery(String query) {
        return MultiMatchQuery.of(mm -> mm
                .fields("title", "description")
                .query(query)
        )._toQuery();
    }

    private Query buildTermQuery(String term, String field) {
        return TermQuery.of(t -> t
                .field(field)
                .value(term)
        )._toQuery();
    }

    private Query buildTermsQuery(List<String> terms) {
        List<String> capitalizedTerms = terms.stream().map(t -> StringUtils.capitalize(t.toLowerCase())).toList();
        return TermsQuery.of(t -> t
                .field("tags")
                .terms(ts -> ts.value(
                        capitalizedTerms.stream()
                                .map(FieldValue::of)
                                .toList()
                ))
        )._toQuery();
    }

    private PaginatedResponse<IssueResponse> mapToPaginatedResponse(
            co.elastic.clients.elasticsearch.core.SearchResponse<IssueIndex> result,
            int pageIndex,
            int pageSize
    ) {
        List<IssueResponse> issueResponses = result.hits().hits().stream()
                .map(indexHit -> {
                    IssueIndex issueIndex = indexHit.source();
                    if (issueIndex != null) {
                        return IssueResponse.create(issueIndex);
                    }

                    return null;
                })
                .toList();

        long totalElements = result.hits().total() != null ? result.hits().total().value() : 0;
        long totalPages = totalElements == 0 ? 1 : (totalElements + pageSize - 1) / pageSize;
        boolean isLastPage = pageIndex + 1 >= totalPages;
        PaginatedResponse.PageInfo pageInfo = PaginatedResponse.PageInfo.builder()
                .pageNo(pageIndex + 1)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(isLastPage)
                .build();

        return PaginatedResponse.create(issueResponses, pageInfo);
    }
}
