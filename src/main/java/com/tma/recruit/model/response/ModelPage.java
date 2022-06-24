package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelPage<T> {

    private List<T> data;

    private Pagination pagination;
}
