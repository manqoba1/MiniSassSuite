package com.sifiso.codetribe.minisasslibrary.services;



import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;

import java.io.Serializable;

/**
 * Created by aubreyM on 14/12/12.
 */
public class RequestCacheEntry implements Serializable, Comparable<RequestCacheEntry> {
    private Long dateRequested, dateUploaded;
    private RequestDTO request;
    private int attemptCount;

    public Long getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Long dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Long getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Long dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public RequestDTO getRequest() {
        return request;
    }

    public void setRequest(RequestDTO request) {
        this.request = request;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(RequestCacheEntry another) {
        return dateRequested.compareTo(another.dateRequested);
    }
}
