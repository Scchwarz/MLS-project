package com.edu.zjut.mlsprojectbackend;

import com.edu.zjut.mlsprojectbackend.entity.dto.FileIndex;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import com.edu.zjut.mlsprojectbackend.service.FileIndexService;
import com.edu.zjut.mlsprojectbackend.service.UserFileService;
import com.edu.zjut.mlsprojectbackend.utils.BaiduOCRUtils;
import com.edu.zjut.mlsprojectbackend.utils.PDFUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class MlSprojectBackendApplicationTests {

	@Autowired
	UserFileService service;
	@Autowired
	private FileIndexService fileIndexService;

	@Test
	void contextLoads() {
		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	}

	@Test
	public void testUploadUserFile() throws IOException {

		// Create a mock user ID for testing
		Integer userId = 1;

		// Create a mock PDF file for testing
		byte[] fileContent = "Mock PDF File Content".getBytes();
		MockMultipartFile mockPdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", fileContent);

		// Call the uploadUserFile method with the mock file and user ID
		//String result = service.uploadUserFile(new MockMultipartFile[]{mockPdfFile}, userId);

		// Assert the result
		//assertEquals("上传成功", result);
	}

	@Test
	public void testDeleteUserFile() throws IOException {
		//service.deleteUserFile(2);
	}

	@Test
	public void testDownloadFile() throws IOException {
	}

	@Test
	public void testQueryFiles() {
		String filename = "Test1";
		List<UserFile> files = service.getFilesByName(filename);
		files.forEach(System.out::println);
	}
	@Test
	public void test() {
		fileIndexService.initIndex();

		List<FileIndex> list=fileIndexService.matchingQuery("查询");
		for(FileIndex file:list){
			System.out.println(file);
		}
	}
	@Autowired
	PDFUtils utils;
	@Autowired
	ElasticsearchTemplate template;
	@Test
	public void PDFtest() throws Exception {
		List<BufferedImage> images = utils.extractImagesFromPDF("E:\\软件工程\\课设\\mls-project\\MLSproject-backend\\mlsproject-frontend\\src\\assets\\pdfFiles\\COVID_19急性期后的远期肾脏转归：从急性肾损伤到慢性肾脏病.pdf");
		String text = BaiduOCRUtils.OCRImages(images);
		System.out.println(text);
	}
	@Test
	public void fileTest() throws Exception {
		UserFile file = new UserFile();
		file.setFileId(111);
		FileIndex fileIndex = new FileIndex();
		System.out.println(file.getFileId());
		fileIndex.setId(file.getFileId());
		System.out.println(fileIndex.getId());
		template.save(fileIndex);
	}
}
