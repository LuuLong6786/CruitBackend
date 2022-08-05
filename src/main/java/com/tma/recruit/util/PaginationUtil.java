package com.tma.recruit.util;

import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.response.Pagination;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
public class PaginationUtil {

    private Integer page;

    private Integer pageSize;

    private SortType sortType;

    private String sortBy;

    public Pageable getPageable() {
        return PageRequest.of(page - 1, pageSize,
                SortType.DESC.equals(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
    }

    public Pagination getPagination(Page<?> pages) {
        return new Pagination(pageSize, page, pages.getTotalPages(),
                pages.getTotalElements());
    }
}