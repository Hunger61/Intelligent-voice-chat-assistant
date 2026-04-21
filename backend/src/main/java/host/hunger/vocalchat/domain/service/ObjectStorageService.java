package host.hunger.vocalchat.domain.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 对象存储领域服务端口，屏蔽 COS/MinIO 等厂商实现差异。
 */
public interface ObjectStorageService {

    /**
     * 上传对象并返回写入结果。
     *
     * @param bucket 存储桶名称
     * @param key 对象键（对象在桶内的唯一标识）
     * @param content 对象内容流
     * @param contentLength 对象字节长度
     * @param contentType 对象 MIME 类型
     * @param metadata 自定义元信息
     */
    UploadResult putObject(String bucket,
                           String key,
                           InputStream content,
                           long contentLength,
                           String contentType,
                           Map<String, String> metadata);

    /**
     * 下载对象内容及其基础属性。
     */
    ObjectResource getObject(String bucket, String key);

    /**
     * 删除单个对象。
     *
     * @return true 表示请求成功执行（对象原本不存在时由实现决定是否视为成功）
     */
    boolean deleteObject(String bucket, String key);

    /**
     * 批量删除对象。
     *
     * @param keys 需要删除的对象键列表
     */
    DeleteObjectsResult deleteObjects(String bucket, List<String> keys);

    /**
     * 检查对象是否存在。
     */
    boolean objectExists(String bucket, String key);

    /**
     * 获取对象元数据，不返回对象内容。
     */
    ObjectMetadata headObject(String bucket, String key);

    /**
     * 按前缀分页列出对象。
     *
     * @param prefix 对象键前缀过滤条件
     * @param continuationToken 分页续传令牌，首轮可为空
     * @param maxKeys 本页最大返回数量
     */
    ListObjectsResult listObjects(String bucket, String prefix, String continuationToken, int maxKeys);

    /**
     * 复制对象到目标位置。
     */
    CopyResult copyObject(String sourceBucket, String sourceKey, String targetBucket, String targetKey);

    /**
     * 生成下载预签名 URL。
     *
     * @param expiresIn 过期时长
     */
    String presignGetUrl(String bucket, String key, Integer expiresIn);

    /**
     * 生成上传预签名 URL。
     *
     * @param expiresIn 过期时长
     * @param contentType 上传时约束的 MIME 类型
     */
    String presignPutUrl(String bucket, String key, Integer expiresIn, String contentType);

    /**
     * 初始化分片上传并返回 uploadId。
     */
    MultipartUploadInitResult initiateMultipartUpload(String bucket,
                                                      String key,
                                                      String contentType,
                                                      Map<String, String> metadata);

    /**
     * 上传单个分片。
     *
     * @param uploadId 分片上传会话 ID
     * @param partNumber 分片序号（通常从 1 开始）
     */
    UploadPartResult uploadPart(String bucket,
                                String key,
                                String uploadId,
                                int partNumber,
                                InputStream content,
                                long contentLength);

    /**
     * 完成分片上传并合并对象。
     *
     * @param uploadId 分片上传会话 ID
     * @param parts 已上传分片信息，通常需按 partNumber 升序传入
     */
    CompleteMultipartUploadResult completeMultipartUpload(String bucket,
                                                          String key,
                                                          String uploadId,
                                                          List<CompletedPart> parts);

    /**
     * 取消分片上传并清理未合并分片。
     */
    boolean abortMultipartUpload(String bucket, String key, String uploadId);

    /** 上传结果。 */
    record UploadResult(String bucket, String key, String eTag, String versionId) {
    }

    /** 对象内容与读取时属性。 */
    record ObjectResource(InputStream content,
                          String contentType,
                          long contentLength,
                          Map<String, String> metadata) {
    }

    /** 对象元数据。 */
    record ObjectMetadata(String bucket,
                          String key,
                          String contentType,
                          long contentLength,
                          String eTag,
                          String versionId,
                          Map<String, String> metadata) {
    }

    /** 列表查询中的对象概要。 */
    record ObjectSummary(String key,
                         long contentLength,
                         String eTag,
                         String lastModified,
                         String contentType) {
    }

    /** 分页列举结果。 */
    record ListObjectsResult(List<ObjectSummary> objects,
                             String nextContinuationToken,
                             boolean truncated) {
    }

    /** 对象复制结果。 */
    record CopyResult(String sourceBucket,
                      String sourceKey,
                      String targetBucket,
                      String targetKey,
                      String eTag,
                      String versionId) {
    }

    /** 批量删除结果。 */
    record DeleteObjectsResult(List<String> deletedKeys,
                               List<DeleteError> errors) {
    }

    /** 单个删除失败信息。 */
    record DeleteError(String key, String code, String message) {
    }

    /** 分片上传初始化结果。 */
    record MultipartUploadInitResult(String bucket, String key, String uploadId) {
    }

    /** 已完成分片信息。 */
    record CompletedPart(int partNumber, String eTag) {
    }

    /** 单个分片上传结果。 */
    record UploadPartResult(int partNumber, String eTag) {
    }

    /** 分片合并完成结果。 */
    record CompleteMultipartUploadResult(String bucket,
                                         String key,
                                         String eTag,
                                         String versionId) {
    }
}
