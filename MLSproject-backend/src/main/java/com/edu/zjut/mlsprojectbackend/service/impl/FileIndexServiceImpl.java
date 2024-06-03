package com.edu.zjut.mlsprojectbackend.service.impl;

import com.edu.zjut.mlsprojectbackend.entity.dto.FileIndex;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import com.edu.zjut.mlsprojectbackend.service.FileIndexService;
import com.edu.zjut.mlsprojectbackend.utils.PDFUtils;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileIndexServiceImpl implements FileIndexService {
	private static final Logger log = LoggerFactory.getLogger(FileIndexServiceImpl.class);

	@Resource
	private ElasticsearchTemplate template;
	@Resource
	PDFUtils pdfUtils;

	/**
	 * 初始化索引结构和数据
	 */
	@Override
	public void initIndex() {
		//处理索引结构
		IndexOperations indexOps = template.indexOps(FileIndex.class);
		if (!indexOps.exists()) {
			log.info("files_index not exists");
			indexOps.createMapping(FileIndex.class);
		}
		/* maybe not useful
		//同步数据库记录
		List<UserFile> filesList = fileService.getAllUserFiles(1, 10);
		if (!filesList.isEmpty()) {
			List<FileIndex> filesIndexList = new ArrayList<>();
			filesList.forEach(file -> {
				FileIndex fileIndex = new FileIndex();
				fileIndex.setId(file.getFileId());
				fileIndex.setTitle(file.getFileName());
				fileIndex.setContent("text");
				fileIndex.setUploadTime(file.getUploadTime());
				//BeanUtils.copyProperties(file,fileIndex);
				filesIndexList.add(fileIndex);
			});
			template.save(filesIndexList);
		}*/
	}

	/**
	 * 匹配查询：标题或内容中含有关键字(高亮)
	 * @param keywords 关键字
	 * @return List<FileIndex>
	 */
	@Override
	public List<FileIndex> matchingQuery(String keywords) {
		//查询标准构建，匹配字段“content”和“title”中包含keywords的数据
		Criteria criteria=new Criteria("title").matches(keywords).or(new Criteria("content").contains(keywords));
		//高亮查询
		List<HighlightField> highlightFieldList =new ArrayList<>();
		HighlightField highlightField=new HighlightField("title", HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
		highlightFieldList.add(highlightField);
		highlightField = new HighlightField("content", HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
        highlightFieldList.add(highlightField);
		Highlight highlight=new Highlight(highlightFieldList);
		HighlightQuery highlightQuery=new HighlightQuery(highlight, FileIndex.class);
		//构建查询
		CriteriaQueryBuilder builder=new CriteriaQueryBuilder(criteria)
				.withSort(Sort.by(Sort.Direction.DESC,"_score"))
				.withHighlightQuery(highlightQuery);
		CriteriaQuery query=new CriteriaQuery(builder);
		//通过elasticsearchTemplate查询
		SearchHits<FileIndex> result=template.search(query, FileIndex.class);
		//处理结果
		List<SearchHit<FileIndex>> searchHits=result.getSearchHits();
		List<FileIndex> fileIndexList =new ArrayList<>();
		for(SearchHit<FileIndex> hit:searchHits){
			FileIndex fileIndex= hit.getContent();
			//将高亮结果添加到返回的结果类中显示
			var titleHighlight=hit.getHighlightField("title");
			if(!titleHighlight.isEmpty()) {
				fileIndex.setTitle(titleHighlight.get(0));
			}
			var contentHighlight = hit.getHighlightField("content");
            if(!contentHighlight.isEmpty()){
                fileIndex.setContent(contentHighlight.get(0));
            }
			if(fileIndex.getState().equals("公共") && fileIndex.getChecked().equals("已审核")){
				fileIndexList.add(fileIndex);
			}
		}
		return fileIndexList;
	}
	/**
	 * 保存用户文件信息到文件索引。
	 *
	 * @param file 用户文件实体
	 * @throws Exception 异常
	 */
	@Override
	public void saveByUserFile(UserFile file) throws Exception {
		FileIndex fileIndex = new FileIndex();
		BeanUtils.copyProperties(fileIndex,file);
		fileIndex.setId(file.getFileId());
		fileIndex.setTitle(file.getFileName());
		String text = pdfUtils.readPDFFile(file.getPath());
		fileIndex.setContent(text);
		template.save(fileIndex);
	}
	/**
	 * 根据用户文件ID删除文件索引。
	 *
	 * @param id 用户文件ID
	 */
	@Override
	public void deleteByUsrFileId(Integer id) {
		template.delete(id.toString(),FileIndex.class);
	}

	@Override
	public void updateFavCount(Integer id, Integer fav) {
		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(id);
		fileIndex.setFavCounts(fav);
		template.update(fileIndex);
	}

	@Override
	public void updateLikeCount(Integer id, Integer like) {
		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(id);
		fileIndex.setLikeCounts(like);
		template.update(fileIndex);
	}

	@Override
	public void addDownloadCount(Integer id, Integer download) {
		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(id);
		fileIndex.setDownloadCounts(download);
		template.update(fileIndex);
	}

	@Override
	public void setCheck(Integer id, String checked) {
		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(id);
		fileIndex.setChecked(checked);
		template.update(fileIndex);
	}

	@Override
	public void setState(Integer id, String state) {
		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(id);
		fileIndex.setState(state);
		template.update(fileIndex);
	}

}
