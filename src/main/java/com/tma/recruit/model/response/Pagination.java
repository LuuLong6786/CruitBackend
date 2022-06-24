package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    private int pageSize = 0;

    private int page = 0;

    private int lastPage = 0;

    private int total = 0;
}
