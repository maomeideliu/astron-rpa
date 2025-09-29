package com.iflytek.rpa.monitor.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchDo {
    private String searchType;

    private List<String> deptIdPathList;
}
