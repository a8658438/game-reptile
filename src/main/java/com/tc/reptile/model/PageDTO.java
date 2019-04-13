package com.tc.reptile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author loocao
 * @date 2018-07-18
 */
public class PageDTO<T> implements Iterable<T>, Serializable {
    private Long total;
    private List<T> content;

    public PageDTO() {
        this.content = new ArrayList<>();
    }

    public PageDTO(List<T> content) {
        this.content = content;
    }

    public static <T> PageDTO<T> of(long total, List<T> content) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setTotal(total);
        pageDTO.setContent(content);
        return pageDTO;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PageDTO{" +
                ", total=" + total +
                ", content=" + content +
                '}';
    }

    @Override
    public Iterator<T> iterator() {
        return this.content.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.content.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.content.spliterator();
    }
}
