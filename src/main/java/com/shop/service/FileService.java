package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    // byte[] 이게 진짜 파일
    // uploadPath 경로             C:/shop1/item
    // originalFileName 파일 이름   a.jpg
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData)
            throws Exception {
        UUID uuid = UUID.randomUUID();      // 랜덤으로 UUID를 생성    asdfiqwn
        // 파일 확장자만 추출       .jpg
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;        // asdfiqwn.jpg
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;    // C:/shop1/item/asdfiqwn.jpg
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }
    public void  deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        if (deleteFile.exists()) { // deleteFile 객체 여부를 확인
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
