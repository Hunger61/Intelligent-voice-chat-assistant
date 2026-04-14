package host.hunger.vocalchat.infrastructure.external.storage;

import host.hunger.vocalchat.domain.service.ObjectStorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

//todo
@Service
public class COSObjectStorageService implements ObjectStorageService {
    @Override
    public UploadResult putObject(String bucket, String key, InputStream content, long contentLength, String contentType, Map<String, String> metadata) {
        return null;
    }

    @Override
    public ObjectResource getObject(String bucket, String key) {
        return null;
    }

    @Override
    public boolean deleteObject(String bucket, String key) {
        return false;
    }

    @Override
    public DeleteObjectsResult deleteObjects(String bucket, List<String> keys) {
        return null;
    }

    @Override
    public boolean objectExists(String bucket, String key) {
        return false;
    }

    @Override
    public ObjectMetadata headObject(String bucket, String key) {
        return null;
    }

    @Override
    public ListObjectsResult listObjects(String bucket, String prefix, String continuationToken, int maxKeys) {
        return null;
    }

    @Override
    public CopyResult copyObject(String sourceBucket, String sourceKey, String targetBucket, String targetKey) {
        return null;
    }

    @Override
    public String presignGetUrl(String bucket, String key, Integer expiresIn) {
        return "";
    }

    @Override
    public String presignPutUrl(String bucket, String key, Integer expiresIn, String contentType) {
        return "";
    }

    @Override
    public MultipartUploadInitResult initiateMultipartUpload(String bucket, String key, String contentType, Map<String, String> metadata) {
        return null;
    }

    @Override
    public UploadPartResult uploadPart(String bucket, String key, String uploadId, int partNumber, InputStream content, long contentLength) {
        return null;
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(String bucket, String key, String uploadId, List<CompletedPart> parts) {
        return null;
    }

    @Override
    public boolean abortMultipartUpload(String bucket, String key, String uploadId) {
        return false;
    }
}
