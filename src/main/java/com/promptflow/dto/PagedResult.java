package com.promptflow.dto;

import java.util.List;

/**
 * 分页结果封装
 */
public class PagedResult<T> {

    private List<T> list;
    private long total;
    private int totalPages;
    private int page;
    private int size;

    public PagedResult() {}

    public PagedResult(List<T> list, long total, int totalPages, int page, int size) {
        this.list = list;
        this.total = total;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
    }

    // Getters and Setters
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
