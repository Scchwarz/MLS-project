package com.edu.zjut.mlsprojectbackend.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * needed-plugin: analysis-ik
 * index-name: file
 * index-mapping:
 * 	    {
 * 	  "settings": {
 * 	    "number_of_shards": 1,
 * 	    "number_of_replicas": 1
 *      },
 * 	  "mappings": {
 * 	    "properties": {
 * 	      "id": {
 * 	        "type": "integer",
 * 	        "fields": {
 * 	            "keyword": {
 * 	                "type": "keyword",
 * 	                "ignore_above": 256
 *                }
 *            }
 *          },
 * 	      "title": {
 * 	        "type": "text",
 * 	        "analyzer": "ik_max_word",
 * 	        "search_analyzer": "ik_smart"
 *          },
 * 	      "content": {
 * 	        "type": "text",
 * 	        "analyzer": "ik_max_word",
 *   	    "search_analyzer": "ik_smart"
 *          },
 * 	      "uploadTime": {
 * 	        "type": "date",
 * 	        "format": ["yyyy-MM-dd HH:mm:ss"]
 *          }
 *        }
 *      }
 *    }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Document(indexName = "file", createIndex = false)
public class FileIndex implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Field(type = FieldType.Integer)
	private Integer id;
	@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
	private String title;
	@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
	private String content;
	@Field(type = FieldType.Date, format = {} ,pattern = "yyyy-MM-dd HH:mm:ss")
	private Date uploadTime;
	@Field(type = FieldType.Integer)
	private Integer likeCounts;
	@Field(type = FieldType.Integer)
	private Integer favCounts;
	@Field(type = FieldType.Integer)
	private Integer downloadCounts;
	@Field(type = FieldType.Text)
	private String checked;
	@Field(type = FieldType.Text)
	private String state;

}