package com.editorial.platform.readmodel.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.editorial.platform.readmodel.model.ContentLookupRow;

public interface ContentLookupRepository extends CassandraRepository<ContentLookupRow, ContentLookupRow.Key> {

    List<ContentLookupRow> findByKeyContentType(String contentType);
}
