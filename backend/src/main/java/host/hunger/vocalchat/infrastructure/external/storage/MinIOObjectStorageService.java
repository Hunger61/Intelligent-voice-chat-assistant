package host.hunger.vocalchat.infrastructure.external.storage;

import host.hunger.vocalchat.domain.service.ObjectStorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinIOObjectStorageService implements ObjectStorageService {

    private final MinioClient minioClient;

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
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .method(Method.GET)
                            .expiry(expiresIn, TimeUnit.SECONDS)
                            .build()
            );
        } catch (ServerException | XmlParserException | InternalException | InvalidResponseException |
                 InvalidKeyException | NoSuchAlgorithmException | IOException | ErrorResponseException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String presignPutUrl(String bucket, String key, Integer expiresIn, String contentType) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .method(Method.PUT)
                            .expiry(expiresIn, TimeUnit.SECONDS)
                            .build()
            );
        } catch (ServerException | XmlParserException | InternalException | InvalidResponseException |
                 InvalidKeyException | NoSuchAlgorithmException | IOException | ErrorResponseException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
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
