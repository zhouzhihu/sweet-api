package com.egrand.sweetapi.plugin.es.module;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetapi.core.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.IndexField;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class ESModule implements ModuleService {

    private TenantService tenantService;

    public ESModule(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * 指定esKey操作
     * @param esKey
     * @return
     */
    public BoundESModule es(String esKey) {
        return new BoundESModule(tenantService, esKey);
    }

    /**
     * 创建IndexMapping
     * @param indexName index名称
     * @param indexMapping Mapping脚本
     * @return
     */
    public String createIndiceMapping(String indexName, String indexMapping) {
        return this.execute((clientInterface) -> clientInterface.createIndiceMapping(indexName, indexMapping));
    }

    /**
     * 获取IndexMapping
     * @param index index名称
     * @param pretty 是否增加pretty
     * @return
     */
    public String getIndexMapping(String index, boolean pretty) {
        return this.execute(clientInterface -> clientInterface.getIndexMapping(index, pretty));
    }

    /**
     * 获取IndexMapping字段
     * @param index index名称
     * @param indexType index类型
     * @return
     */
    public List<IndexField> getIndexMappingFields(String index, String indexType, String ...key) {
        return this.execute(clientInterface -> clientInterface.getIndexMappingFields(index, indexType));
    }

    /**
     * 判断是否存在index
     * @param indiceName index名称
     * @return
     */
    public boolean existIndice(String indiceName) {
        return this.execute(clientInterface -> clientInterface.existIndice(indiceName));
    }

    /**
     * 删除index
     * @param index index名称
     * @return
     */
    public String dropIndice(String index) {
        return this.execute(clientInterface -> clientInterface.dropIndice(index));
    }

    // -----------------  增加文档 indexType默认为“_doc" ------------------

    /**
     * 添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entity 实体
     * @return
     */
    public String addDocument(String indexName, Map<String, Object> entity) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, entity));
    }

    /**
     * 添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entity 实体
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocument(String indexName, Map<String, Object> entity, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, entity, refreshOption));
    }

    /**
     * 添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entity 实体
     * @param docId 实体ID
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocument(String indexName, Map<String, Object> entity, Object docId, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, entity, docId, refreshOption));
    }

    /**
     * 添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entity 实体
     * @param clientOptions 选项
     * @return
     */
    public String addDocument(String indexName, Map<String, Object> entity, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, entity, clientOptions));
    }

    // -----------------  增加文档 indexType默认为“_doc" ------------------

    // -----------------  指定indexType增加文档  ------------------

    /**
     * 添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entity 实体
     * @return
     */
    public String addDocument(String indexName, String indexType, Map<String, Object> entity) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, indexType, entity));
    }

    /**
     * 添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entity 实体
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocument(String indexName, String indexType, Map<String, Object> entity, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, indexType, entity, refreshOption));
    }

    /**
     * 添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entity 实体
     * @param docId 实体ID
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocument(String indexName, String indexType, Map<String, Object> entity, Object docId, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, indexType, entity, docId, refreshOption));
    }

    /**
     * 添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entity 实体
     * @param clientOptions 选项
     * @return
     */
    public String addDocument(String indexName, String indexType, Map<String, Object> entity, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.addDocument(indexName, indexType, entity, clientOptions));
    }

    // -----------------  指定indexType增加文档  ------------------


    // -----------------  批量增加文档 indexType默认为“_doc" ------------------

    /**
     * 批量添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entityList 实体列表
     * @return
     */
    public String addDocuments(String indexName, List<Map<String, Object>> entityList) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, entityList));
    }

    /**
     * 批量添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entityList 实体列表
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocuments(String indexName, List<Map<String, Object>> entityList, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, entityList, refreshOption));
    }

    /**
     * 批量添加文档(indexType默认为"_doc")
     * @param indexName index名称
     * @param entityList 实体列表
     * @param clientOptions 选项
     * @return
     */
    public String addDocuments(String indexName, List<Map<String, Object>> entityList, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, entityList, clientOptions));
    }

    // -----------------  批量增加文档 indexType默认为“_doc" ------------------

    // -----------------  指定indexType批量增加文档 ------------------

    /**
     * 批量添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entityList 实体列表
     * @return
     */
    public String addDocuments(String indexName, String indexType, List<Map<String, Object>> entityList) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, indexType, entityList));
    }

    /**
     * 批量添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entityList 实体列表
     * @param refreshOption 刷新选项
     * @return
     */
    public String addDocuments(String indexName, String indexType, List<Map<String, Object>> entityList, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, indexType, entityList, refreshOption));
    }

    /**
     * 批量添加文档
     * @param indexName index名称
     * @param indexType index类型
     * @param entityList 实体列表
     * @param clientOptions 选项
     * @return
     */
    public String addDocuments(String indexName, String indexType, List<Map<String, Object>> entityList, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.addDocuments(indexName, indexType, entityList, clientOptions));
    }

    // -----------------  指定indexType批量增加文档 ------------------

    // -----------------  更新文档 Map<String,Object> ------------------

    public String updateDocument(String index, Object id, Map<String, Object> entity) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, entity));
    }

    public String updateDocument(String index, Object id, Map<String, Object> entity, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, entity, refreshOption));
    }

    public String updateDocument(String index, Object id, Map<String, Object> entity, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, entity, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, Object id, Map<String, Object> entity, String refreshOption, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, entity, refreshOption, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, String indexType, Object id, Map<String, Object> entity) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, entity));
    }

    public String updateDocument(String index, String indexType, Object id, Map<String, Object> entity, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, entity, refreshOption));
    }

    public String updateDocument(String index, String indexType, Object id, Map<String, Object> entity, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, entity, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, String indexType, Object id, Map<String, Object> entity, String refreshOption, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, entity, refreshOption, detect_noop, doc_as_upsert));
    }

    // -----------------  更新文档 Map<String,Object> ------------------

    // -----------------  更新文档 Object ------------------

    public String updateDocument(String index, Object id, Object params) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, params));
    }

    public String updateDocument(String index, Object id, Object params, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, params, refreshOption));
    }

    public String updateDocument(String index, Object id, Object params, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, params, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, Object id, Object params, String refreshOption, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, id, params, refreshOption, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, String indexType, Object id, Object params) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, params));
    }

    public String updateDocument(String index, String indexType, Object id, Object params, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, params, refreshOption));
    }

    public String updateDocument(String index, String indexType, Object id, Object params, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, params, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, String indexType, Object id, Object params, String refreshOption, Boolean detect_noop, Boolean doc_as_upsert) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, id, params, refreshOption, detect_noop, doc_as_upsert));
    }

    public String updateDocument(String index, Object entity, ClientOptions updateOptions) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, entity, updateOptions));
    }

    public String updateDocument(String index, String indexType, Object entity, ClientOptions updateOptions) {
        return this.execute(clientInterface -> clientInterface.updateDocument(index, indexType, entity, updateOptions));
    }

    // -----------------  更新文档 Object ------------------

    // -----------------  批量更新文档 List<?> ------------------

    public String updateDocuments(String indexName, List<?> beans) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, beans));
    }

    public String updateDocuments(String indexName, List<?> beans, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, beans, refreshOption));
    }

    public String updateDocuments(String indexName, List<?> beans, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, beans, clientOptions));
    }

    public String updateDocuments(String indexName, String indexType, List<?> beans) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, indexType, beans));
    }

    public String updateDocuments(String indexName, String indexType, List<?> beans, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, indexType, beans, refreshOption));
    }

    public String updateDocuments(String indexName, String indexType, List<?> beans, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, indexType, beans, clientOptions));
    }

    // -----------------  批量更新文档 List<?> ------------------

    // -----------------  批量更新文档 List<Map> ------------------

    public String updateDocuments(String indexName, List<Map> beans, String docIdKey, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, beans, docIdKey, refreshOption));
    }

    public String updateDocuments(String indexName, String indexType, List<Map> beans, String docIdKey, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.updateDocuments(indexName, indexType, beans, docIdKey, refreshOption));
    }

    // -----------------  批量更新文档 List<Map> ------------------

    // -----------------  删除文档 ------------------

    public String deleteDocument(String indexName, String indexType, String id) {
        return this.execute(clientInterface -> clientInterface.deleteDocument(indexName, indexType, id));
    }

    public String deleteDocument(String indexName, String indexType, String id, String refreshOption) {
        return this.execute(clientInterface -> clientInterface.deleteDocument(indexName, indexType, id, refreshOption));
    }

    public String deleteDocuments(String indexName, String[] ids) {
        return this.execute(clientInterface -> clientInterface.deleteDocuments(indexName, ids));
    }

    public String deleteDocuments(String indexName, String indexType, String[] ids) {
        return this.execute(clientInterface -> clientInterface.deleteDocuments(indexName, indexType, ids));
    }

    public String deleteDocuments(String indexName, String indexType, String[] ids, ClientOptions clientOptions) {
        return this.execute(clientInterface -> clientInterface.deleteDocuments(indexName, indexType, ids, clientOptions));
    }

    // -----------------  删除文档 ------------------

    // -----------------  获取文档 ------------------

    public String getDocument(String indexName, String documentId) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, documentId));
    }

    public <T> T getDocument(String indexName, String documentId, Class<T> beanType) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, documentId, beanType));
    }

    public String getDocument(String indexName, String documentId, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, documentId, options));
    }

    public <T> T getDocument(String indexName, String documentId, Map<String, Object> options, Class<T> beanType) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, documentId, options, beanType));
    }

    public String getDocument(String indexName, String indexType, String documentId) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, indexType, documentId));
    }

    public <T> T getDocument(String indexName, String indexType, String documentId, Class<T> beanType) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, indexType, documentId, beanType));
    }

    public String getDocument(String indexName, String indexType, String documentId, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, indexType, documentId, options));
    }

    public <T> T getDocument(String indexName, String indexType, String documentId, Map<String, Object> options, Class<T> beanType) {
        return this.execute(clientInterface -> clientInterface.getDocument(indexName, indexType, documentId, options, beanType));
    }

    // -----------------  获取文档 ------------------

    // -----------------  根据Field获取文档 ------------------

    public String getDocumentByField(String indexName, String fieldName, Object fieldValue) {
        return this.execute(clientInterface -> clientInterface.getDocumentByField(indexName, fieldName, fieldValue));
    }

    public String getDocumentByField(String indexName, String fieldName, Object fieldValue, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocumentByField(indexName, fieldName, fieldValue, options));
    }

    public <T> T getDocumentByField(String indexName, String fieldName, Object fieldValue, Class<T> type) {
        return this.execute(clientInterface -> clientInterface.getDocumentByField(indexName, fieldName, fieldValue, type));
    }

    public <T> T getDocumentByField(String indexName, String fieldName, Object fieldValue, Class<T> type, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocumentByField(indexName, fieldName, fieldValue, type, options));
    }

    public String getDocumentByFieldLike(String indexName, String fieldName, Object fieldValue) {
        return this.execute(clientInterface -> clientInterface.getDocumentByFieldLike(indexName, fieldName, fieldValue));
    }

    public String getDocumentByFieldLike(String indexName, String fieldName, Object fieldValue, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocumentByFieldLike(indexName, fieldName, fieldValue, options));
    }

    public <T> T getDocumentByFieldLike(String indexName, String fieldName, Object fieldValue, Class<T> type) {
        return this.execute(clientInterface -> clientInterface.getDocumentByFieldLike(indexName, fieldName, fieldValue, type));
    }

    public <T> T getDocumentByFieldLike(String indexName, String fieldName, Object fieldValue, Class<T> type, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.getDocumentByFieldLike(indexName, fieldName, fieldValue, type, options));
    }

    // -----------------  根据Field获取文档 ------------------

    // -----------------  分页查询 ------------------

    // -----------------  分页查询 ------------------

    // -----------------  查询 ------------------

    public <T> ESDatas<T> searchList(String path, Class<T> type) {
        return this.execute(clientInterface -> clientInterface.searchList(path, type));
    }

    public <T> ESDatas<T> searchList(String path, String entity, Class<T> type) {
        return this.execute(clientInterface -> clientInterface.searchList(path, entity, type));
    }

    public <T> ESDatas<T> searchListByField(String indexName, String fieldName, Object fieldValue, Class<T> type, int from, int size) {
        return this.execute(clientInterface -> clientInterface.searchListByField(indexName, fieldName, fieldValue, type, from, size));
    }

    public <T> ESDatas<T> searchListByField(String indexName, String fieldName, Object value, Class<T> type, int from, int size, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.searchListByField(indexName, fieldName, value, type, from, size, options));
    }

    public <T> ESDatas<T> searchListByFieldLike(String indexName, String fieldName, Object value, Class<T> type, int from, int size) {
        return this.execute(clientInterface -> clientInterface.searchListByFieldLike(indexName, fieldName, value, type, from, size));
    }

    public <T> ESDatas<T> searchListByFieldLike(String indexName, String fieldName, Object value, Class<T> type, int from, int size, Map<String, Object> options) {
        return this.execute(clientInterface -> clientInterface.searchListByFieldLike(indexName, fieldName, value, type, from, size, options));
    }

    // -----------------  查询 ------------------

    public <T> List<T> mgetDocuments(String index, Class<T> type, Object... ids) {
        return this.execute(clientInterface -> clientInterface.mgetDocuments(index, type, ids));
    }

    public String mgetDocuments(String index, String indexType, Object... ids) {
        return this.execute(clientInterface -> clientInterface.mgetDocuments(index, indexType, ids));
    }

    public <T> List<T> mgetDocuments(String index, String indexType, Class<T> type, Object... ids) {
        return this.execute(clientInterface -> clientInterface.mgetDocuments(index, indexType, type, ids));
    }

    public Object executeRequest(String path, String entity) {
        return this.execute(clientInterface -> clientInterface.executeRequest(path, entity));
    }

    protected <T> T execute(Function<ClientInterface, T> function) {
        return function.apply(this.getClientInterface());
    }

    /**
     * 获取ES ClientInterface
     * @return
     */
    protected ClientInterface getClientInterface(String ...key) {
        String elasticSearch;
        ClientInterface clientInterface;
        if (null == key || key.length == 0)
            elasticSearch = this.tenantService.getTenant();
        else
            elasticSearch = key[0];
        if(StrUtil.isEmpty(elasticSearch)) {
            log.info("ES数据源：{}", "default");
            return ElasticSearchHelper.getRestClientUtil();
        } else {
            clientInterface = ElasticSearchHelper.getRestClientUtil(elasticSearch);
            log.info("ES数据源：{}", elasticSearch);
            return clientInterface;
        }
    }

    @Override
    public String getType() {
        return "es";
    }
}
