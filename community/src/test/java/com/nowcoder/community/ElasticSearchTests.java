//package com.nowcoder.community;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import com.alibaba.fastjson2.JSONObject;
//import com.nowcoder.community.dao.DiscussPostMapper;
//import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
//import com.nowcoder.community.entity.DiscussPost;
//import org.checkerframework.checker.units.qual.A;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.client.erhlc.RestClients;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//
//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//public class ElasticSearchTests {
//
//    @Autowired
//    private DiscussPostMapper discussMapper;
//
//    @Autowired
//    private DiscussPostRepository discussRepository;
//
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;
//
//    @Autowired
//    private ElasticsearchClient elasticsearchClient;
//
//
//    @Test
//    void testInsert() {
//        discussRepository.save(discussMapper.selectDiscussPostById(241));
//        discussRepository.save(discussMapper.selectDiscussPostById(242));
//        discussRepository.save(discussMapper.selectDiscussPostById(243));
//    }
//
//    @Test
//    void testInsertList() {
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100,0));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100,0));
//    }
//
//    @Test
//    void testUpdate() {
//        DiscussPost post = discussMapper.selectDiscussPostById(231);
//        post.setContent("我是新人，使劲灌水。");
//        discussRepository.save(post);
//    }
//
//    @Test
//    void testDelete() {
////        discussRepository.deleteById(231);
//        // 删除所有数据
//        discussRepository.deleteAll();
//    }
//
//    @Test
//    public void testSearchByRepository() throws IOException {
//        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名
//        Map<String,Object> res = new HashMap<>();
//
//        //高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("title");
//        highlightBuilder.field("content");
//        highlightBuilder.requireFieldMatch(false);
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
//
//        //构建搜索条件
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
//                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
//                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .from(0)// 指定从哪条开始查询
//                .size(10)// 需要查出的总记录条数
//                .highlighter(highlightBuilder);//高亮
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, DiscussPost.class);
//
//        //System.out.println(JSONObject.toJSON(searchResponse));
//        List<DiscussPost> list = new ArrayList<>();
//        long total = searchResponse.getHits().getTotalHits().value;
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
//
//            // 处理高亮显示的结果
//            HighlightField titleField = hit.getHighlightFields().get("title");
//            if (titleField != null) {
//                discussPost.setTitle(titleField.getFragments()[0].toString());
//            }
//            HighlightField contentField = hit.getHighlightFields().get("content");
//            if (contentField != null) {
//                discussPost.setContent(contentField.getFragments()[0].toString());
//            }
////            System.out.println(discussPost);
//            list.add(discussPost);
//        }
//        res.put("list",list);
//        res.put("total",total);
//        if(res.get("list")!= null){
//            for (DiscussPost post : list = (List<DiscussPost>) res.get("list")) {
//                System.out.println(post);
//            }
//            System.out.println(res.get("total"));
//        }
//    }
//}
