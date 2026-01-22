package kg.edgepos.authservice.mapper;

import kg.edgepos.authservice.dao.entity.FileStoreEntity;
import kg.edgepos.authservice.dto.FileStoreDto;

public class FileStoreMapper {

    public static FileStoreDto map(FileStoreEntity entity) {
        return FileStoreDto.builder()
            .bucket(entity.getBucket())
            .contentType(entity.getContentType())
            .id(entity.getId())
            .name(entity.getFilename())
            .size(entity.getFileSize())
            .build();
    }
}
