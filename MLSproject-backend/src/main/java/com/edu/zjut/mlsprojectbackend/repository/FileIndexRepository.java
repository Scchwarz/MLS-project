package com.edu.zjut.mlsprojectbackend.repository;

import com.edu.zjut.mlsprojectbackend.entity.dto.FileIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileIndexRepository extends ElasticsearchRepository<FileIndex, Long> {

}
