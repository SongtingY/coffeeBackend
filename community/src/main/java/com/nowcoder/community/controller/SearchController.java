package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

//    search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
//        搜索帖子
        Map<String, Object> searchResult = null;
        try {
            searchResult = elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        } catch (IOException e) {
            logger.error("es搜索失败：", e.getMessage());
        }
//        聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            List<DiscussPost> list = (List<DiscussPost>) searchResult.get("list");
            for(DiscussPost post: list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

//        分页
        page.setPath("/search?keyword=" + keyword);
        long total = (long) searchResult.get("total");
        page.setRows(searchResult == null ? 0 : (int) total);

        return "/site/search";
    }
}
